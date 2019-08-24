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

package au.edu.anu.twapps.mm.jars;

import java.io.File;
import java.util.Set;

import au.edu.anu.omhtk.jars.Jars;

/**
 * @author Ian Davies
 *
 * @date 24 Aug 2019
 */
public class DataJar extends Jars{
	/**
	 * locates file in project directory in the .3w repo.
	 * 
	 * @param file
	 * @return the directory in which the file was found
	 */
	private static File locateFile(File file) {
		File f = null;
		File dir = null;
		// 3Worlds root (.3w)
/*		f = new File(System.getProperty("user.home") + File.separator + TW_ROOT + File.separator + file.getPath());
		if (f.exists())
			dir = new File(System.getProperty("user.home") + File.separator + TW_ROOT);
		// 3Worlds project root
		f = new File(Project.getProjectRoot() + File.separator + file.getPath());
		if (f.exists())
			dir = Project.getProjectRoot();*/
		return dir;
	}

	public DataJar(Set<File> dataFiles) {
//		File dir = null;
/**		File file = Project.getProjectFile();
		addFile(file.getAbsolutePath(),"");*/
//		String projectName = Project.getProjectName();
//		if (projectName != null) {
//			String[] extList = TWG.extensions();
//			File projectDsl = null;
//			for (int i = 0; i < extList.length; i++) {
//				projectDsl = new File(projectName + extList[i]);
//				dir = locateFile(projectDsl);
//				if (dir != null)
//					break;
//			}
//			addFile(dir.getAbsolutePath() + File.separator + projectDsl.getPath(), "");
//		}
/**
		String prjDir = Project.getProjectRoot().getAbsolutePath() + File.separator;
		for (File s : dataFiles) {
			String fileName = s.getAbsolutePath();
			String resourceName = fileName.replace(prjDir, "");
			resourceName = resourceName.replace(s.getName(), "");
			resourceName = resourceName.replace("\\", Jar.separator);
			if (resourceName.endsWith(Jar.separator))
				resourceName = resourceName.substring(0, resourceName.length() - 1);
			addFile(s.getAbsolutePath(), resourceName);
		}
		*/
	}

}
