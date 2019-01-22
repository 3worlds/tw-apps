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

package au.edu.anu.twapps.mm;

import java.io.File;
import java.util.ArrayList;
import au.edu.anu.rscs.aot.graph.AotGraph;
import au.edu.anu.rscs.aot.graph.AotNode;
import au.edu.anu.rscs.aot.graph.io.AotGraphExporter;
import au.edu.anu.twapps.dialogs.Dialogs;
import au.edu.anu.twapps.exceptions.TwAppsException;
import au.edu.anu.twapps.mm.visualGraph.VisualGraph;
import au.edu.anu.twapps.mm.visualGraph.VisualGraphExporter;
import au.edu.anu.twapps.mm.visualGraph.VisualKeys;
import au.edu.anu.twapps.mm.visualGraph.VisualNode;
import au.edu.anu.twcore.project.Project;
import au.edu.anu.twcore.specificationCheck.CheckImpl;
import au.edu.anu.twcore.specificationCheck.Checkable;
import fr.cnrs.iees.Identifiable;
import fr.cnrs.iees.io.FileImporter;
import fr.cnrs.iees.twcore.constants.Configuration;

/**
 * Author Ian Davies
 *
 * Date 10 Dec. 2018
 */
public class ModelMaker  implements Modelable {
	// Interface supplied to the controller
	private AotGraph currentGraph;
	private VisualGraph layoutGraph;
	private Controllable controller;
	// Should we avoid using javafx.beans.property? - make ModelMaker a boolean change listener??

	public ModelMaker(Controllable controller) {
		this.controller = controller;
	}

	private void onProjectClosing() {
		controller.onProjectClosing();
		Project.close();
	}

	private void onProjectOpened() {
		controller.onProjectOpened(layoutGraph);
	}

	@Override
	public boolean validateGraph() {
		Checkable checker = new CheckImpl(currentGraph); 
		return checker.validateGraph();

	}

	@Override
	public void doClearJavaProject() {
		// TODO Auto-generated method stub

	}

	@Override
	public void doSetJavaProject() {
		// TODO Auto-generated method stub

	}

	@Override
	public void doMenuExit() {
		// TODO Auto-generated method stub

	}

	@Override
	public void doLayout() {
		// TODO Auto-generated method stub

	}

	@Override
	public void doDeploy() {
		// TODO Auto-generated method stub

	}

	@Override
	public void doNewProject() {
		if (!canClose())
			return;
		String name = Dialogs.getText("New project", "", "New project name:", "my Project");
		if (name == null)
			return;
		if (Project.isOpen()) {
			onProjectClosing();
			Project.close();
		}
		name = Project.create(name);
		currentGraph = new AotGraph(new ArrayList<AotNode>());
		currentGraph.makeTreeNode(null, Configuration.N_ROOT, name);
		layoutGraph = new VisualGraph();
		layoutGraph.makeTreeNode(null, Configuration.N_ROOT, name);
		layoutGraph.root().setPosition(0.1, 0.5);
		connectConfigToVisual();
		onProjectOpened();
		doSave();
	}

	//TODO deal with out nodes
	private void connectConfigToVisual() {
		for (VisualNode vn : layoutGraph.nodes()) {
			AotNode n = currentGraph.findNodeByReference(vn.uniqueId());
			if (n == null)
				throw new TwAppsException("Unable to find " + vn.uniqueId() + " in currentGraph");
			vn.setConfigNode(n);
		}
	}

	@Override
	public void doOpenProject(File file) {
		// TODO Auto-generated method stub
		if (!canClose())
			return;
		if (Project.isOpen())
			onProjectClosing();
		Project.open(file);
		currentGraph = (AotGraph) FileImporter.loadGraphFromFile(Project.makeConfigurationFile());
		layoutGraph = (VisualGraph) FileImporter.loadGraphFromFile(Project.makeLayoutFile());
		connectConfigToVisual();
		onProjectOpened();
	}

	@Override
	public void doSave() {
		new AotGraphExporter(Project.makeConfigurationFile()).exportGraph(currentGraph);
		VisualGraphExporter.saveGraphToFile(Project.makeLayoutFile(), layoutGraph);
		GraphState.isChanged(false);
	}

	@Override
	public void doSaveAs() {
		// TODO Auto-generated method stub

	}

	@Override
	public void doImport() {
		// TODO Auto-generated method stub
		File file = Dialogs.getExternalProjectFile();
		if (file == null)
			return;
//		importGraph.resolveReferences();
//		Utilities.enforceNameProperty(importGraph);
//		AotNode importRoot = Utilities.get3worldsroot(importGraph);
//		String tmpName = null;
//		if (importRoot != null)
//			tmpName = importRoot.getName();
//		if (tmpName == null) {
//			Dialogs.errorAlert("Import project", "Unable to import project.",
//					"The file " + file.getName() + " has no root labelled " + N_GRAPHROOT.toString());
//			return;
//		}
//		final String name = NameUtils.validJavaName(NameUtils.wordUpperCaseName(tmpName));
//		if (!canClose("closing"))
//			return;
//		StatusText.message("Importing " + file.getName());
//		Cursor oldCursor = controller.setCursor(Cursor.WAIT);
//		Runnable task = () -> {
//			if (Project.isOpen())
//				onProjectClosing();
//			Project.create(name);
//			currentGraph = importGraph;
//			AotNode root = Utilities.get3worldsroot(currentGraph);
//			root.setName(name);
//			layoutGraph = Utilities.createLayoutGraph(currentGraph);
//			onProjectOpened();
//			Utilities.save(currentGraph, layoutGraph);
//			log.debug("Project importted: " + Project.getCurrentProjectTitle());
//			StatusText.clear();
//			controller.setCursor(oldCursor);
//		};
//		ExecutorService executor = Executors.newSingleThreadExecutor();
//		executor.execute(task);

	}

	@Override
	public boolean canClose() {
		if (!GraphState.hasChanged())
			return true;
		switch (Dialogs.yesNoCancel("Project has changed", "Save changes before closing projecct?", "")) {
		case yes:
			doSave();
			return true;
		case no:
			return true;
		default:
			return false;
		}
	}

}
