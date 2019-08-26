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
import au.edu.anu.twcore.jars.ThreeWorldsJar;
import au.edu.anu.twcore.project.TwPaths;

/**
 * @author Ian Davies
 *
 * @date 25 Aug 2019
 */
public class SimulatorJar extends Jars{
	public SimulatorJar(String mainClass,Set<File> dataFiles, Set<File> srcFiles, Set<File> resFiles,Set<String> userLibraries) {
		// This jar contains only a manifest.
		// set ModelRunner as main class: could be called germane ha ha.
		setMainClass(mainClass);
		// dependencies
		addDependencyOnJar(".." + Jars.separator + TwPaths.TW_DEP_JAR);
		addDependencyOnJar("data.jar");
		//addDependencyOnJar(".." + Jars.separator + ThreeWorldsJar.TW_JAR);
		for (String userLibrary : userLibraries)
			addDependencyOnJar(userLibrary);
		for (File f : userCodeJars)
			addDependencyOnJar(f.getName());
	}

}
