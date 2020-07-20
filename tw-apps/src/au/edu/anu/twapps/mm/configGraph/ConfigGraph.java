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
import javax.tools.ToolProvider;

import au.edu.anu.rscs.aot.errorMessaging.ErrorList;
import au.edu.anu.rscs.aot.errorMessaging.ErrorMessagable;
import au.edu.anu.rscs.aot.errorMessaging.impl.SpecificationErrorMsg;
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
		// Don't validate here as the graph is not yet built
	}

	public static TreeGraph<TreeGraphDataNode, ALEdge> getGraph() {
		return graph;
	}

	public static void validateGraph() {
		// clears ui message display and disables ui button and displays 'checking...' label
		ErrorList.startCheck();
		/**
		 * Because of the thread below, execution now leaves this method with buttons
		 * states as set by clear() above.
		 * 
		 * The last method "SignalState", simple causes a Platform.runLater to restore
		 * button states
		 */
		Runnable checkTask = () -> {
			Iterable<ErrorMessagable> specErrors = TWA.checkSpecifications(graph);
			if (specErrors != null) {
				for (ErrorMessagable e : specErrors) {
					SpecificationErrorMsg se = (SpecificationErrorMsg) e;
					ModelBuildErrorMsg mbem = new ModelBuildErrorMsg(ModelBuildErrors.SPECIFICATION, se, graph);
					if (!mbem.ignore())
						/**
						 * Here is where context is shifted from whatever the specs say to whatever the
						 * user can do. Sounds good but not really fully satisfactory. There are still
						 * many confusing msgs.
						 */
						ErrorList.add(mbem);
				}
			}
			if (!ErrorList.haveErrors()) {
				boolean haveCompiler = !(ToolProvider.getSystemJavaCompiler() == null);
				if (!haveCompiler)
					ErrorList.add(new ModelBuildErrorMsg(ModelBuildErrors.COMPILER_MISSING));
			}

			if (!ErrorList.haveErrors()) {
				CodeGenerator gen = new CodeGenerator(graph);
				gen.generate();
			}

			if (!ErrorList.haveErrors()) {
				ProjectJarGenerator gen = new ProjectJarGenerator();
				gen.generate(graph);
			}
			
			if (!ErrorList.haveErrors()) {
				File file = new File(TwPaths.TW_ROOT + File.separator + TwPaths.TW_DEP_JAR);
				if (!file.exists())
					ErrorList.add(new ModelBuildErrorMsg(ModelBuildErrors.DEPLOY_RESOURCE_MISSING, file,
							"Run TwSetup or obtain file from developers."));

				if (GraphState.changed())
					ErrorList.add(new ModelBuildErrorMsg(ModelBuildErrors.DEPLOY_PROJECT_UNSAVED));
			}
			ErrorList.endCheck();

		};
		new Thread(checkTask).start();
	}

	public static void onParentChanged() {
		graph.onParentChanged();
	}

}
