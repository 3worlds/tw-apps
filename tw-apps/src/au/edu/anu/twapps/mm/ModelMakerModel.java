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
import au.edu.anu.twapps.dialogs.Dialogs;
import au.edu.anu.twapps.graphviz.GraphVisualisation;
import au.edu.anu.twcore.errorMessaging.archetype.ArchComplianceManager;
import au.edu.anu.twcore.errorMessaging.codeGenerator.CodeComplianceManager;
import au.edu.anu.twcore.errorMessaging.deploy.DeployComplianceManager;
import au.edu.anu.twcore.project.Project;
import fr.cnrs.iees.graph.generic.Graph;

/**
 * Author Ian Davies
 *
 * Date 10 Dec. 2018
 */
public class ModelMakerModel {
	// not really a listener but rather an
	// Inteface to controller
	private Graph<?, ?> currentGraph;
	private Graph<?, ?> layoutGraph;
	private ModelController mctrl;

	public ModelMakerModel(ModelController mctrl) {
		this.mctrl = mctrl;
	}

	public void newProject() {
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
		currentGraph = Project.newConfiguration();
		layoutGraph = GraphVisualisation.initialiseLayout(Project.newLayout());
		onProjectOpened();
		save();
	}

	public void openProject(File file) {
		// TODO Auto-generated method stub
		if (!canClose())
			return;
		// wait cursor
		if (Project.isOpen())
			onProjectClosing();
		Project.open(file);
		currentGraph = Project.loadConfiguration();
		layoutGraph = Project.loadLayout();
		GraphVisualisation.linkGraphs(currentGraph, layoutGraph);
		onProjectOpened();
		// restore cursor
	}

	public void importProject() {
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

	private void clearMessages() {
		ArchComplianceManager.clear();
		CodeComplianceManager.clear();
		DeployComplianceManager.clear();
	}

	public boolean checkGraph() {
		clearMessages();
		// run the checker
		return true; // TODO Auto-generated method stub

	}

	public boolean canClose() {
		if (!GraphState.hasChanged())
			return true;
		switch (Dialogs.yesNoCancel("Project has changed", "Save changes before closing projecct?", "")) {
		case yes:
			save();
			return true;
		case no:
			return true;
		default:
			return false;
		}
	}

	public void onPaneMouseClicked(double x, double y, double width, double height) {
		// TODO Auto-generated method stub

	}

	public void onPaneMouseMoved(double x, double y, double width, double height) {
		// TODO Auto-generated method stub
	}

	public void runLayout() {
		// TODO Auto-generated method stub

	}

	public void createSimulatorAndDeploy() {
		// TODO Auto-generated method stub

	}

	private void onProjectClosing() {
		mctrl.onProjectClosing();
		Project.close();
	}

	private void onProjectOpened() {
		boolean ok = checkGraph();
		mctrl.onProjectOpened(layoutGraph, ok);
		mctrl.onStartDrawing();

		GraphVisualisation.createVisualElements(layoutGraph);

		mctrl.onEndDrawing();

	}

	public void save() {
		GraphState.isChanged(false);
	}

	public void saveAs() {
		// TODO Auto-generated method stub

	}

}
