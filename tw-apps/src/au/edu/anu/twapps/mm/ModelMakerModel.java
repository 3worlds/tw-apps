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
import au.edu.anu.twcore.project.Project;


/**
 * Author Ian Davies
 *
 * Date 10 Dec. 2018
 */
public class ModelMakerModel {
	//List<ModelListener> listeners = new ArrayList<>;
	public ModelMakerModel() {

	}

	public void checkGraph() {
		// TODO Auto-generated method stub

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

	public void openProject(File file) {
		// TODO Auto-generated method stub

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

	public void newProject() {
		if (!canClose())
			return;
		String name = Dialogs.getText("New project","","New project name:","my Project");

		if (name==null)
			return;
		if (Project.isOpen()) {
			//we need modelListeners
			//onProjectClosing();
			Project.close();
		}
		name = Project.create(name);		
	}

	public void save() {
		// TODO Auto-generated method stub

	}

	public void saveAs() {
		// TODO Auto-generated method stub

	}

	public void importProject() {
		// TODO Auto-generated method stub

	}

}
