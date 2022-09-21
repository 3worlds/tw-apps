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
 * A static class to call dialogs for the assigned implementation.
 * </p>
 * This class should provide a method for every method defined in
 * {@link IDialogs} interface. Author Ian Davies
 *
 * @author Ian Davies - 12 Dec. 2018
 * 
 */
public class Dialogs {
	private static IDialogs impl;

	/**
	 * Private constructor to prevent instantiation.
	 */
	private Dialogs() {
	};

	/**
	 * Sets the current implementation of the dialogs to be used. This must be done
	 * when application first starts.
	 * 
	 * @param impl The implementation of {@link IDialogs}.
	 */
	public static void setImplementation(IDialogs impl) {
		Dialogs.impl = impl;
	}

	/**
	 * Present dialog to inform user of an error condition.
	 * 
	 * @param title   Dialog title
	 * @param header  Dialog header (can be null)
	 * @param content Dialog content (can be null)
	 */
	public static void errorAlert(String title, String header, String content) {
		impl.errorAlert(title, header, content);
	}

	/**
	 * Present dialog to supply information to the user.
	 * 
	 * @param title   Dialog title
	 * @param header  Dialog header (can be null)
	 * @param content Dialog content (can be null)
	 */
	public static void infoAlert(String title, String header, String content) {
		impl.infoAlert(title, header, content);
	}

	/**
	 * Present dialog to indicate a warning to the user.
	 * 
	 * @param title   Dialog title
	 * @param header  Dialog header (can be null)
	 * @param content Dialog content (can be null)
	 */
	public static void warnAlert(String title, String header, String content) {
		impl.warnAlert(title, header, content);
	}

	/**
	 * Present a dialog for directory selection.
	 * 
	 * @param title       Dialog title
	 * @param initialPath Initial directory to show.
	 * @return Selected directory (null if cancelled).
	 */
	public static File selectDirectory(String title, String initialPath) {
		return impl.selectDirectory(title, initialPath);
	}

	/**
	 * Dialog to export a file.
	 * 
	 * @param title          Dialog title
	 * @param promptDir      Initial directory.
	 * @param promptFileName Suggested file name.
	 * @return File or null if cancelled.
	 */
	public static File exportFile(String title, String promptDir, String promptFileName) {
		return impl.exportFile(title, promptDir, promptFileName);

	}

	/**
	 * A triple-response dialog. {@link YesNoCancel} is exposed to prevent
	 * dependency on any Javafx classes.
	 * 
	 * @param title   Dialog title
	 * @param header  Dialog header (can be null)
	 * @param content Dialog content (can be null)
	 * @return YesNoCancel enum value according to user selection.
	 */
	public static YesNoCancel yesNoCancel(String title, String header, String content) {
		return impl.yesNoCancel(title, header, content);
	}

	/**
	 * Prompts the user for a validated string.
	 * 
	 * @param title         Dialog title
	 * @param header        Dialog header (can be null)
	 * @param content       Dialog content (can be null)
	 * @param prompt        Suggested text
	 * @param strValidation regex validation string.
	 * @return user string or null if cancelled.
	 */
	public static String getText(String title, String header, String content, String prompt, String strValidation) {
		return impl.getText(title, header, content, prompt, strValidation);
	}

	/**
	 * Dialog to prompt for a file to import into a project.
	 * 
	 * @return File or null if cancelled.
	 */
	public static File getExternalProjectFile() {
		return impl.getExternalProjectFile();
	}

	/**
	 * Creates a confirmation dialog using the given implementation (Javafx or
	 * other).
	 * <p>
	 * 
	 * @param title   Dialog title text
	 * @param header  Dialog header text (can be null)
	 * @param content Dialog content text (can be null)
	 * @return true if action is confirmed by user
	 */
	public static boolean confirmation(String title, String header, String content) {
		return impl.confirmation(title, header, content);
	}

	/**
	 * Get a user selected file.
	 * 
	 * @param directory  Directory to open with
	 * @param title      Dialog title
	 * @param extensions Extensions used by the implementation.
	 * @return User selected file or null if none selected.
	 */
	public static File getOpenFile(File directory, String title, Object extensions) {
		return impl.getOpenFile(directory, title, extensions);
	}

	/**
	 * Presents user with a list of enum options that can be selected or deselected.
	 * 
	 * @param title   Dialog title
	 * @param header  Dialog header (can be null)
	 * @param content Dialog content (can be null)
	 * @param control The control object.
	 * @return True if ok clicked, false otherwise
	 */
	public static boolean editList(String title, String header, String content, Object control) {
		return impl.editList(title, header, content, control);
	}

	/**
	 * Enables the user to select one string from a list of strings.
	 * 
	 * @param list    List of strings
	 * @param title   Dialog title
	 * @param header  Dialog header (can be null)
	 * @param content Dialog content (can be null)
	 * @return index of selected string in the list (-1 of user cancels)
	 */
	public static int getListChoice(String[] list, String title, String header, String content) {
		return impl.getListChoice(list, title, header, content);
	}

	/**
	 * Presents the user with any number of groups of toggles allowing selection of
	 * one from each group.
	 * 
	 * @param title   Dialog title
	 * @param header  Dialog header (can be null)
	 * @param content Dialog content (can be null)
	 * @param entries Group entries with associated options.
	 * @return The list of groups with the single selected toggle.
	 */
	public static List<String> getRadioButtonChoices(String title, String header, String content,
			List<String[]> entries) {
		return impl.getRadioButtonChoices(title, header, content, entries);
	}

	/**
	 * Show a file save dialog.
	 * 
	 * @param directory Initial directory
	 * @param title     Dialog title
	 * @param exts      List of valid file extensions
	 * @return The file or null if cancelled.
	 */
	public static File promptForSaveFile(File directory, String title, String[]... exts) {
		return impl.promptForSaveFile(directory, title, exts);
	};

	/**
	 * Show a file open dialog.
	 * 
	 * @param directory Initial directory
	 * @param title     Dialog title
	 * @param exts      List of valid file extensions
	 * @return The file or null if cancelled.
	 */
	public static File promptForOpenFile(File directory, String title, String[]... exts) {
		return impl.promptForOpenFile(directory, title, exts);
	}

	/**
	 * Get the owning window of the dialog implementation.
	 * 
	 * @return Owning object.
	 */
	public static Object owner() {
		return impl.owner();
	}

	/**
	 * Select one file from a list of files.
	 * 
	 * @param files         The file list
	 * @param initSelection zero-based index of initial selection.
	 * @return index of new selection.
	 */
	public static int selectFile(List<File> files, int initSelection) {
		return impl.selectFile(files, initSelection);
	}

	/**
	 * Select any number of entries from a list of strings.
	 * 
	 * @param title    Dialog title
	 * @param header   Dialog header (may be null).
	 * @param items    Items from which to select.
	 * @param selected Selection status of items.
	 * @return The new selection status (may be unchanged)
	 */
	public static List<String> getCBSelections(String title, String header, List<String> items,
			List<Boolean> selected) {
		return impl.getCBSelections(title, header, items, selected);
	}

//	public static boolean isValid(String s, String regex) {
//		Pattern pattern = Pattern.compile(regex);
//		Matcher matcher = pattern.matcher(s);
//		return matcher.matches();
//	}

//	public static final String vsInteger = "([0-9]*)?";
	/**
	 * regex for all chars of a floating point number
	 */
	public static final String vsReal = "[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?";
//	public static final String vsReal2 = "^-?\\d+(,\\d+)*(\\.\\d+(e\\d+)?)?$";
//	public static final String vsAlpha = "([a-zA-Z]*)?";
	/**
	 * regex string for alpha-numeric characters.
	 */
	public static final String vsAlphaNumeric = "([a-zA-Z0-9]*)?";
	/**
	 * regex string for alpha-numeric chars or blank space. This may need to change!
	 * It is used to allow the user to return an empty string but this is bad
	 * practice. The user should choose cancel!
	 */
	public static final String vsAlphaAlphaNumericSpace = "([a-zA-Z][a-zA-Z0-9 ]*)?";
	/**
	 * regex for Alpha-numeric string starting with uppercase.
	 */
	public static final String vsAlphaCapAlphaNumeric = "([A-Z][a-zA-Z0-9]*)?";

//	public static final String test = "([*a-zA-Z][a-zA-Z0-9 *]*)?";

//	public static void main(String[] args) {
//
//		String regex = vsReal;
//
//		List<String> names = new ArrayList<String>();
//		names.add("prj1"); // 1
//		names.add("1prj"); // 2
//		names.add("*)(*"); // 3
//		names.add("123"); // 4
//		names.add("1"); // 5
//		names.add("abc"); // 6
//		names.add("Abc"); // 7
//		names.add("ABC"); // 8
//		names.add("a"); // 9
//		names.add("A"); // 10
//		names.add(""); // 11
//		names.add("BlahAndBlah");// 12
//		names.add("Blah And              Blah");// 13
//		names.add(" Blah And Blah");// 14
//		names.add(" Blah");// 15
//		names.add("*Blah");// 16
//		names.add("*Blah*");// 17
//		names.add("*Bl*ah*");// 18
//		names.add("0.0");// 19
//		names.add(".1");// 20
//		names.add("");// 21
//		names.add(".");// 22
//		names.add("0.");// 23
//
//		Pattern pattern = Pattern.compile(regex);
//		System.out.println(regex);
//		for (int i = 0; i < names.size(); i++) {
//			String name = names.get(i);
//			Matcher matcher = pattern.matcher(name);
//			System.out.println((i + 1) + ") " + matcher.matches() + "\t'" + name + "'");
//		}
//	}
}
