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

package au.edu.anu.twapps.project;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

import org.junit.jupiter.api.Test;

import au.edu.anu.twapps.exceptions.TwAppsException;

class ProjectTest {

	@Test
	void test() {
		Project.create("a*()*(^^:b\t\n");
		assertTrue(Project.getProjectName().equals("aB"));
		Project.close();
		try {
			Project.getProjectName();
			fail("Closed but getProjectName() succeeded");
		} catch (TwAppsException e) {
			assertTrue(true);
		}
		try {
			Project.close();
			fail("Closed and already closed Project");
		} catch (TwAppsException e) {
			assertTrue(true);
		}
		try {
			Project.getProjectDateTime();
			fail("Datetime of closed Project");
		} catch (TwAppsException e) {
			assertTrue(true);
		}
		try {
			for (int i = 0; i < 2; i++) {
				Project.create("quick");
				Project.close();
			}
		} catch (TwAppsException e) {
			assertTrue(true);
		}

		assertFalse(Project.isOpen());

		File[] files = Project.getAllProjectPaths();
		for (File f : files) {
			try {
				Project.open(f);
				Project.close();
				assertTrue(true);
			} catch (TwAppsException e) {
				fail("Failed to open " + Project.getProjectDirectory());
			}
		}
		Project.create(" The cat sat on the mat");
		assertTrue(Project.getProjectName().equals("theCatSatOnTheMat"));
		Project.close();

		try {
			Project.create("([{*~!'_^)");
			fail("Should not have created project ([{*~!'_^)");
		} catch (TwAppsException e) {
			assertTrue(true);
		}

		File crap = new File("SDFCRap");
		assertTrue(!Project.isValidProjectFile(crap));

		try {
			Project.extractDateTime(crap);
			fail("extracted datetime from nonsense file");
		} catch (TwAppsException e) {
			assertTrue(true);
		}

		try {
			Project.extractDisplayName(crap);
			fail("extracted displayname from nonsense file");
		} catch (TwAppsException e) {
			assertTrue(true);
		}
		
		try{
			Project.extractDisplayNames(Project.getAllProjectPaths());
		} catch (TwAppsException e) {
			fail("extractDisplayNames fail on current .3w projects");
		}

	}

}
