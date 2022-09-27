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

package au.edu.anu.twapps.mm.userProjectFactory;

import java.io.File;

import au.edu.anu.twapps.dialogs.Dialogs;
import au.edu.anu.twcore.userProject.UPLEclipse;
import au.edu.anu.twcore.userProject.UserProjectLink;
import au.edu.anu.twcore.project.Project;

/**
 * Factory to initialize a link between an IDE java project and a 3Worlds
 * {@link Project}.
 * 
 * @author Ian Davies - 23 Aug 2019
 */
public class UserProjectLinkFactory {
	private UserProjectLinkFactory() {
	};

	/**
	 * Initialize a link between an IDE and a 3Worlds {@link Project}. An error
	 * alert is produced if the requested IDE is not supported.
	 * 
	 * @param projectRoot The File path to the Java project.
	 * @param type        The {@link IDETypes} in use.
	 * @return true if initialization was successful, false otherwise.
	 */
	public static boolean makeEnv(File projectRoot, IDETypes type) {
		switch (type) {
		case eclipse: {
			if (new File(projectRoot.getAbsoluteFile() + File.separator + "src").exists())
				if (new File(projectRoot.getAbsoluteFile() + File.separator + "bin").exists()) {
					UserProjectLink.initialise(new UPLEclipse(projectRoot));
					return true;
				}
			Dialogs.errorAlert(type.name() + "[" + projectRoot.getName() + "]",
					"Project is missing or non-standard directory structure", "Expected 'src' and 'bin' directories");
			return false;
		}
		default: {
			Dialogs.errorAlert("IDE", type.name(), "This IDE is not yet supported.");
			return false;
		}
		}
	}

}
