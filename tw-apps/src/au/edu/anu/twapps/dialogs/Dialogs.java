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

/**
 * Author Ian Davies
 *
 * Date 12 Dec. 2018
 */

// static methods for dialogs
public class Dialogs {
	private static IDialogs impl;

	// prevent instantiation
	private Dialogs() {
	};

	public static void initialise(IDialogs impl) {
		Dialogs.impl = impl;
	}

	public static void errorAlert(String title, String header, String content) {
		impl.errorAlert(title, header, content);
	}

	public static void infoAlert(String title, String header, String content) {
		impl.infoAlert(title, header, content);
	}

	public static void warnAlert(String title, String header, String content) {
		impl.warnAlert(title, header, content);
	}

	public static File selectDirectory(String title, String currentPath) {
		return impl.selectDirectory(title, currentPath);
	}

	public static YesNoCancel yesNoCancel(String title, String header, String content) {
		return impl.yesNoCancel(title, header, content);
	}

	public static String getText(String title, String header, String content, String prompt) {
		return impl.getText(title, header, content, prompt);
	}

	public static File getExternalProjectFile() {
		return impl.getExternalProjectFile();
	}

	public static boolean confirmation(String title, String header, String content) {
		return impl.confirmation(title, header, content);
	}

	public static File getOpenFile(File directory, String title, Object extensions) {
		return impl.getOpenFile(directory, title, extensions);
	}

	public static boolean editList(String title, String header, String content, Object listView) {
		return impl.editList(title, header, content, listView);
	}

	public static int getListChoice(String[] list, String title, String header, String content) {
		return impl.getListChoice(list, title, header, content);
	}

	public static List<String> getRadioButtonChoices(String title, String header, String content,
			List<String[]> entries) {
		return impl.getRadioButtonChoices(title, header, content, entries);
	}

	public static Object owner() {
		return impl.owner();
	}
}
