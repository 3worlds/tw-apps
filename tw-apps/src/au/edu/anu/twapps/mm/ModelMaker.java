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

import au.edu.anu.rscs.aot.graph.AotGraph;
import au.edu.anu.twapps.dialogs.Dialogs;
import au.edu.anu.twapps.graphviz.GraphVisualisation;
import au.edu.anu.twcore.errorMessaging.archetype.ArchComplianceManager;
import au.edu.anu.twcore.errorMessaging.codeGenerator.CodeComplianceManager;
import au.edu.anu.twcore.errorMessaging.deploy.DeployComplianceManager;
import au.edu.anu.twcore.project.Project;
import fr.cnrs.iees.io.FileImporter;

/**
 * Author Ian Davies
 *
 * Date 10 Dec. 2018
 */
public class ModelMaker implements Modelable {
	// not really a listener but rather an
	// Inteface to controller
	private AotGraph currentGraph;
	private AotGraph layoutGraph;
	private Controllable controller;
	private boolean graphValid;

	public ModelMaker(Controllable controller) {
		this.controller = controller;
		graphValid = false;
	}




	private void onProjectClosing() {
		controller.onProjectClosing(layoutGraph);
		Project.close();
	}

	private void onProjectOpened() {
		graphValid = validateGraph();
		controller.onProjectOpened(layoutGraph, graphValid);
	}

	@Override
	public boolean validateGraph() {
		// run the checker
		return true; // TODO Auto-generated method stub

	}

	@Override
	public void doDisconnectJavaProject() {
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

//	@Override
//	public void onMouseClicked(double x, double y, double w, double h) {

		// TODO this does not belong here as runlater is a javafx method.
		// This means a lot of code in modelMakerModel must be factored elsewhere.
//		if (placing) {
//			Platform.runLater(() -> {
//				AotNode n = popupEditor.locate(event, pane.getWidth(), pane.getHeight());
//				VisualNode.insertCircle(n, controller.childLinksProperty(), controller.xLinksProperty(), pane, this);
//				// add parent edge. There must be one in this circumstance
//				AotEdge inEdge = (AotEdge) get(n.getEdges(Direction.IN), selectOne(hasTheLabel(Trees.CHILD_LABEL)));
//				VisualNode.createChildLine(inEdge, controller.childLinksProperty(), pane);
//				popupEditor = null;
//				placing = false;
//				pane.setCursor(Cursor.DEFAULT);
//				reBuildAllElementsPropertySheet();
//				checkGraph();
//			});
//		}

//	}

//	@Override
//	public void onMouseMoved(double x, double y, double w, double h) {
//		// TODO Auto-generated method stub
//
//	}

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
		currentGraph = (AotGraph) Project.newConfiguration();
		layoutGraph = (AotGraph) GraphVisualisation.initialiseLayout(Project.newLayout());
		onProjectOpened();
		doSave();
	}

	@Override
	public void doOpenProject(File file) {
		// TODO Auto-generated method stub
		if (!canClose())
			return;
		controller.onStartWaiting();
		if (Project.isOpen())
			onProjectClosing();
		Project.open(file);
		currentGraph = (AotGraph) FileImporter.loadGraphFromFile(Project.makeConfigurationFile());
		layoutGraph = (AotGraph) FileImporter.loadGraphFromFile(Project.makeLayoutFile());
		GraphVisualisation.linkGraphs(currentGraph, layoutGraph);
		onProjectOpened();
		controller.onEndWaiting();
	}

	@Override
	public void doSave() {
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
