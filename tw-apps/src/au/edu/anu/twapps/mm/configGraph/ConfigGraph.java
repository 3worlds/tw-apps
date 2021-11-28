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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.tools.ToolProvider;

import au.edu.anu.rscs.aot.errorMessaging.ErrorMessageManager;
import au.edu.anu.rscs.aot.errorMessaging.ErrorMessagable;
import au.edu.anu.rscs.aot.errorMessaging.impl.SpecificationErrorMsg;
import au.edu.anu.twapps.exceptions.TwAppsException;
import au.edu.anu.twcore.TextTranslations;
import au.edu.anu.twcore.archetype.TWA;
import au.edu.anu.twcore.errorMessaging.ModelBuildErrorMsg;
import au.edu.anu.twcore.errorMessaging.ModelBuildErrors;
import au.edu.anu.twcore.graphState.GraphState;
import au.edu.anu.twcore.project.TwPaths;
import fr.cnrs.iees.graph.impl.ALEdge;
import fr.cnrs.iees.graph.impl.TreeGraph;
import fr.cnrs.iees.graph.impl.TreeGraphDataNode;
import fr.cnrs.iees.twcore.generators.CodeGenerator;
import fr.cnrs.iees.twcore.generators.ProjectJarGenerator;

/**
 * @author Ian Davies
 *
 * @Date 13 Aug 2019
 */
public class ConfigGraph {
	private static TreeGraph<TreeGraphDataNode, ALEdge> graph;

	private ConfigGraph() {
	}

	public static void setGraph(TreeGraph<TreeGraphDataNode, ALEdge> graph) {
		ConfigGraph.graph = graph;
		// Don't validate here as the graph may not be built yet
	}

	public static TreeGraph<TreeGraphDataNode, ALEdge> getGraph() {
		return graph;
	}

	private static ExecutorService executor;

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
					throw new TwAppsException("Graph is null in ValidateGraph");
				ProjectJarGenerator gen = new ProjectJarGenerator();
				gen.generate(graph);
			}

			if (!ErrorMessageManager.haveErrors()) {
				File file = new File(TwPaths.TW_ROOT + File.separator + TwPaths.TW_DEP_JAR);
				if (!file.exists())
					ErrorMessageManager.dispatch(new ModelBuildErrorMsg(ModelBuildErrors.DEPLOY_RESOURCE_MISSING,
							TwPaths.TW_DEP_JAR, TwPaths.TW_ROOT));

			}
			if (!ErrorMessageManager.haveErrors() && GraphState.changed())
				ErrorMessageManager.dispatch(new ModelBuildErrorMsg(ModelBuildErrors.DEPLOY_PROJECT_UNSAVED));
			
			if (!ErrorMessageManager.haveErrors()) {
				List<String> commands = new ArrayList<>();
				commands.add("Rscript");
				commands.add("--version");
				ProcessBuilder b = new ProcessBuilder(commands);
				b.inheritIO();
				try {
					b.start();
				} catch (IOException e) {
					ErrorMessageManager.dispatch(new ModelBuildErrorMsg(ModelBuildErrors.DEPLOY_RSCRIPT_MISSING));		
				}

				
			}

			ErrorMessageManager.endCheck();
			
			} catch (Throwable e) {
				e.printStackTrace();
			}

		};

		if (executor == null)
			executor = Executors.newSingleThreadExecutor();
		executor.submit(checkTask);

	}

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

	public static void onParentChanged() {
		graph.onParentChanged();
	}

}
