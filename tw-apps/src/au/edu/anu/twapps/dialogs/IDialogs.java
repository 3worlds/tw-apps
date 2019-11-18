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

package au.edu.anu.twapps.dialogs;

import java.io.File;
import java.util.List;

//import javafx.stage.FileChooser.ExtensionFilter;

/**
 * Author Ian Davies
 *
 * Date 11 Dec. 2018
 */
/**
 * 
 * Interface for implementation independent dialogs
 */
public interface IDialogs {
	public void errorAlert(String title, String header, String content);

	public void infoAlert(String title, String header, String content);

	public void warnAlert(String title, String header, String content);

	public File selectDirectory(String title, String currentPath);
	
	public File exportFile(String title, String promptDir, String promptFileName);

	public YesNoCancel yesNoCancel(String title, String header, String content);

	public String getText(String title, String header, String content, String prompt);

	public File getExternalProjectFile();

	public boolean confirmation(String title, String header, String content);

	// ExtenstionFilter is javafx!!
	public File getOpenFile(File directory, String title, Object extList);

	public boolean editList(String title, String header, String content, Object listView);

	public int getListChoice(String[] list, String title, String header, String content);

	public List<String> getRadioButtonChoices(String title, String header, String content, List<String[]> entries);

	public Object owner();
}
