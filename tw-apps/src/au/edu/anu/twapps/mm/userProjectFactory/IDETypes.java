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

/**
 * These enums define the IDE support for user projects: location of java, class
 * and library files.
 * 
 * @author Ian Davies - 23 Aug 2019
 */
public enum IDETypes {
	/**
	 * The <a href =
	 * "https://www.eclipse.org/downloads/packages/release/oxygen/3a/eclipse-ide-java-developers">Eclipse
	 * </a> IDE for java developers.
	 */
	eclipse,
	/**
	 * The <a href = "https://www.jetbrains.com/idea/">IntelliJ </a> IDE for java
	 * developers. Not currently supported.
	 */
	IntelliJ,
	/**
	 * The <a href = "https://netbeans.apache.org/">Apache NetBeans </a> IDE for
	 * java developers. Not currently supported.
	 */
	NetBeans;

}
