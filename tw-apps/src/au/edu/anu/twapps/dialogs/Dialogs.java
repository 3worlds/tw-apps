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
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

	public static File exportFile(String title, String promptDir, String promptFileName) {
		return impl.exportFile(title, promptDir, promptFileName);

	}

	public static YesNoCancel yesNoCancel(String title, String header, String content) {
		return impl.yesNoCancel(title, header, content);
	}

	public static String getText(String title, String header, String content, String prompt, String strValidation) {
		return impl.getText(title, header, content, prompt, strValidation);
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

	public static File promptForSaveFile(File directory, String title, String[]... exts) {
		return impl.promptForSaveFile(directory, title, exts);
	};

	public static File promptForOpenFile(File directory, String title, String[]... exts) {
		return impl.promptForOpenFile(directory, title, exts);
	}

	public static Object owner() {
		return impl.owner();
	}

	public static int editISFiles(List<File> files, int idx) {
		return impl.editISFiles(files, idx);
	}

	public static List<String> getCBSelections(String title, String header,List<String> items, List<Boolean> selected) {
		return impl.getCBSelections(title, header,items, selected);
	}

	public static boolean isValid(String s, String regex) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(s);
		return matcher.matches();
	}

	public static final String vsInteger = "([0-9]*)?";
	public static final String vsReal = "[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?";
	public static final String vsReal2 = "^-?\\d+(,\\d+)*(\\.\\d+(e\\d+)?)?$";
	public static final String vsAlpha = "([a-zA-Z]*)?";
	public static final String vsAlphaNumeric = "([a-zA-Z0-9]*)?";
	public static final String vsAlphaAlphaNumericSpace = "([a-zA-Z][a-zA-Z0-9 ]*)?";// Need the " " space to allow for
																						// null string
	public static final String vsAlphaCapAlphaNumeric = "([A-Z][a-zA-Z0-9]*)?";

	public static final String test = "([*a-zA-Z][a-zA-Z0-9 *]*)?";

	public static void main(String[] args) {

		String regex = vsReal;

		List<String> names = new ArrayList<String>();
		names.add("prj1"); // 1
		names.add("1prj"); // 2
		names.add("*)(*"); // 3
		names.add("123"); // 4
		names.add("1"); // 5
		names.add("abc"); // 6
		names.add("Abc"); // 7
		names.add("ABC"); // 8
		names.add("a"); // 9
		names.add("A"); // 10
		names.add(""); // 11
		names.add("BlahAndBlah");// 12
		names.add("Blah And              Blah");// 13
		names.add(" Blah And Blah");// 14
		names.add(" Blah");// 15
		names.add("*Blah");// 16
		names.add("*Blah*");// 17
		names.add("*Bl*ah*");// 18
		names.add("0.0");// 19
		names.add(".1");// 20
		names.add("");// 21
		names.add(".");// 22
		names.add("0.");// 23

		Pattern pattern = Pattern.compile(regex);
		System.out.println(regex);
		for (int i = 0; i < names.size(); i++) {
			String name = names.get(i);
			Matcher matcher = pattern.matcher(name);
			System.out.println((i + 1) + ") " + matcher.matches() + "\t'" + name + "'");
		}
	}
}
