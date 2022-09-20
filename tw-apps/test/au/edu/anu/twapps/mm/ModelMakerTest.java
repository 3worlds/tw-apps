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

import org.junit.jupiter.api.*;

import au.edu.anu.twapps.dialogs.Dialogs;
import au.edu.anu.twcore.graphState.GraphState;

class ModelMakerTest {
	MockController c;
	//https://junit.org/junit5/docs/current/user-guide/
	@BeforeAll
	public void init() {
		Dialogs.setImplementation(new MockDialogs());
		c = new MockController();
		GraphState.setImplementation(null);
	}

//	@Test
//	public void test() {
//		c.handleNewProject();
//		assertTrue(Project.makeConfigurationFile().exists());
//		// TODO write visual graph assertTrue(Project.makeLayoutFile().exists());
//		File pf = Project.getProjectFile();
//		Project.close();
//		// awaiting AotGraphImporter
//		c.handleOpenProject(pf);
//	}

}
