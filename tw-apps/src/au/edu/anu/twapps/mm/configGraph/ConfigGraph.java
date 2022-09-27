/**************************************************************************
 *  TW-APPS - Applications used by 3Worlds                                *
 *                                                                        *
 *  Copyright 2018: Jacques Gignoux & Ian D. Davies                       *
 *       jacques.gignoux@upmc.fr                                          *
 *       ian.davies@anu.edu.au                                            * 
 *                                                                        *
 *  TW-APPS contains ModelMaker and ModelRunner, programs used to         *
 *  construct and run 3Worlds configuration graphs. All code herein is    *
 *  independent of UI implementation.                                     *
 *                                                                        *
 **************************************************************************                                       
 *  This file is part of TW-APPS (3Worlds applications).                  *
 *                                                                        *
 *  TW-APPS is free software: you can redistribute it and/or modify       *
 *  it under the terms of the GNU General Public License as published by  *
 *  the Free Software Foundation, either version 3 of the License, or     *
 *  (at your option) any later version.                                   *
 *                                                                        *
 *  TW-APPS is distributed in the hope that it will be useful,            *
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *  GNU General Public License for more details.                          *                         
 *                                                                        *
 *  You should have received a copy of the GNU General Public License     *
 *  along with TW-APPS.                                                   *
 *  If not, see <https://www.gnu.org/licenses/gpl.html>                   *
  **************************************************************************/

package au.edu.anu.twapps.mm.configGraph;

import java.io.File;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.tools.ToolProvider;

import au.edu.anu.rscs.aot.errorMessaging.ErrorMessageManager;
import au.edu.anu.rscs.aot.errorMessaging.ErrorMessagable;
import au.edu.anu.rscs.aot.errorMessaging.impl.SpecificationErrorMsg;
import au.edu.anu.twcore.archetype.TWA;
import au.edu.anu.twcore.errorMessaging.ModelBuildErrorMsg;
import au.edu.anu.twcore.errorMessaging.ModelBuildErrors;
import au.edu.anu.twcore.graphState.GraphState;
import fr.cnrs.iees.graph.impl.ALEdge;
import fr.cnrs.iees.graph.impl.TreeGraph;
import fr.cnrs.iees.graph.impl.TreeGraphDataNode;
import fr.cnrs.iees.twcore.generators.CodeGenerator;
import fr.cnrs.iees.twcore.generators.ProjectJarGenerator;
import au.edu.anu.twcore.project.Project;

/**
 * A static singleton class to manage the configuration graph
 * 
 * @author Ian Davies - 13 Aug 2019
 */
public class ConfigGraph {
	/**
	 * The configuration graph.
	 */
	private static TreeGraph<TreeGraphDataNode, ALEdge> graph;

	/**
	 * Private constructor to prevent instantiation.
	 */
	private ConfigGraph() {
	}

	/**
	 * Set the graph. The graph construction may not be complete at this stage.
	 * 
	 * @param graph The configuration graph.
	 */
	public static void setGraph(TreeGraph<TreeGraphDataNode, ALEdge> graph) {
		ConfigGraph.graph = Objects.requireNonNull(graph);
	}

	/**
	 * Set the graph to null when closing a project
	 */
	public static void close() {
		graph = null;
	}

	/**
	 * Get the configuration graph. The graph verification is not guaranteed here.
	 * 
	 * @return The configuration graph.
	 */
	public static TreeGraph<TreeGraphDataNode, ALEdge> getGraph() {
		return Objects.requireNonNull(graph);
	}

	/**
	 * The executor for running the graph verification thread.
	 */
	private static ExecutorService executor;

	/**
	 * Execute graph verification.
	 */
	public static void verifyGraph() {
		// calls listeners ie. mm controller to set buttons and clear display
		// ErrorList is poorly named: its not a list but a dispatcher of messages to
		// listeners. Rename to ErrorMessageManager?
		ErrorMessageManager.startCheck();
		/**
		 * Because of the thread below, execution now leaves this method with buttons
		 * states as set by clear() above.
		 * 
		 * The last method "SignalState", simple causes a Platform.runLater to restore
		 * button states
		 */
		Runnable checkTask = () -> {
			try {
				Iterable<ErrorMessagable> specErrors = TWA.checkSpecifications(graph);
				if (specErrors != null) {
					for (ErrorMessagable e : specErrors) {
						SpecificationErrorMsg se = (SpecificationErrorMsg) e;
						ModelBuildErrorMsg mbem = new ModelBuildErrorMsg(ModelBuildErrors.SPECIFICATION, se, graph);
						ErrorMessageManager.dispatch(mbem);
					}
				}
				if (!ErrorMessageManager.haveErrors()) {
					boolean haveCompiler = !(ToolProvider.getSystemJavaCompiler() == null);
					if (!haveCompiler)
						ErrorMessageManager.dispatch(new ModelBuildErrorMsg(ModelBuildErrors.COMPILER_MISSING));
				}

				if (!ErrorMessageManager.haveErrors()) {
					CodeGenerator gen = new CodeGenerator(graph);
					gen.generate();
				}

				if (!ErrorMessageManager.haveErrors()) {
					if (graph == null)
						throw new NullPointerException("Graph is null in ValidateGraph");
					ProjectJarGenerator gen = new ProjectJarGenerator();
					gen.generate(graph);
				}

				if (!ErrorMessageManager.haveErrors()) {
					File file = new File(Project.USER_ROOT_TW_ROOT + File.separator + Project.TW_DEP_JAR);
					if (!file.exists())
						ErrorMessageManager.dispatch(new ModelBuildErrorMsg(ModelBuildErrors.DEPLOY_RESOURCE_MISSING,
								Project.TW_DEP_JAR, Project.USER_ROOT_TW_ROOT));

				}
				if (!ErrorMessageManager.haveErrors() && GraphState.changed())
					ErrorMessageManager.dispatch(new ModelBuildErrorMsg(ModelBuildErrors.DEPLOY_PROJECT_UNSAVED));

				ErrorMessageManager.endCheck();

			} catch (Throwable e) {
				e.printStackTrace();
			}

		};

		if (executor == null)
			executor = Executors.newSingleThreadExecutor();
		executor.submit(checkTask);

	}

	/**
	 * Terminate all threads currently performing verification. This is needed when
	 * closing the {@link Project}.
	 */
	public static void terminateChecks() {
		if (executor != null) {
			try {
				executor.awaitTermination(1, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Called from graph visualization interface when a parent child relationship
	 * has been changed or deleted.
	 */
	public static void onParentChanged() {
		graph.onParentChanged();
	}

}
