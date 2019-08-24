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
import au.edu.anu.twcore.project.Project;
import au.edu.anu.twcore.project.ProjectPaths;

public class UserProjectJar extends Jars{
	public UserProjectJar(Set<File> srcFiles, Set<File> resFiles) {
		this.version="0.0.0";//TODO jar version
	
		String codeRoot = Project.makeFile(ProjectPaths.CODE).getAbsolutePath();
		for (File file : srcFiles) {
			String fileName = file.getAbsolutePath();
			String jarDirectory = file.getAbsolutePath().replace(codeRoot, "");
			jarDirectory = jarDirectory.replace(file.getName(), "");
			jarDirectory = formatJarDirectory(jarDirectory);
			addFile(fileName, jarDirectory);
		}
		String resRoot = Project.makeFile(ProjectPaths.RES).getAbsolutePath();
		for (File file : resFiles) {
			String fileName = file.getAbsolutePath();
			String jarDirectory = file.getAbsolutePath().replace(resRoot, "");
			jarDirectory = jarDirectory.replaceAll(file.getName(), "");
			jarDirectory = formatJarDirectory(jarDirectory);
			addFile(fileName, jarDirectory);
		}
	}
	private String formatJarDirectory(String s) {
		String r = s.replace("\\", "/");
		if (r.endsWith("/"))
			r = r.substring(0, r.lastIndexOf("/"));
		return r;
	}


}
