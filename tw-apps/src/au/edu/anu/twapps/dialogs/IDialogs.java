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
 * <p>
 * Interface for implementation-independent dialogs
 * 
 * @author Ian Davies - 11 Dec. 2018
 */
public interface IDialogs {
	/**
	 * Show dialog to inform user of an error condition.
	 * 
	 * @param title   Dialog title
	 * @param header  Dialog header (can be null)
	 * @param content Dialog content (can be null)
	 */
	public void errorAlert(String title, String header, String content);

	/**
	 * Present dialog to supply information to the user.
	 * 
	 * @param title   Dialog title
	 * @param header  Dialog header (can be null)
	 * @param content Dialog content (can be null)
	 */
	public void infoAlert(String title, String header, String content);

	/**
	 * Present dialog to indicate a warning to the user.
	 * 
	 * @param title   Dialog title
	 * @param header  Dialog header (can be null)
	 * @param content Dialog content (can be null)
	 */
	public void warnAlert(String title, String header, String content);

	/**
	 * Present a dialog for directory selection.
	 * 
	 * @param title       Dialog title
	 * @param initialPath Initial directory to show.
	 * @return Selected directory (null if cancelled).
	 */
	public File selectDirectory(String title, String initialPath);

	/**
	 * Dialog to export a file.
	 * 
	 * @param title          Dialog title
	 * @param promptDir      Initial directory.
	 * @param promptFileName Suggested file name.
	 * @return File or null if cancelled.
	 */
	public File exportFile(String title, String promptDir, String promptFileName);

	/**
	 * A triple-response dialog. {@link YesNoCancel} is exposed to prevent
	 * dependency on any Javafx classes.
	 * 
	 * @param title   Dialog title
	 * @param header  Dialog header (can be null)
	 * @param content Dialog content (can be null)
	 * @return YesNoCancel enum value according to user selection.
	 */
	public YesNoCancel yesNoCancel(String title, String header, String content);

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
	public String getText(String title, String header, String content, String prompt, String strValidation);

	/**
	 * Dialog to prompt for a file to import into a project.
	 * 
	 * @return File or null if cancelled.
	 */
	public File getExternalProjectFile();

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
	public boolean confirmation(String title, String header, String content);

	/**
	 * Get a user selected file.
	 * 
	 * @param directory  Directory to open with
	 * @param title      Dialog title
	 * @param extensions Extensions used by the implementation.
	 * @return User selected file or null if none selected.
	 */
	public File getOpenFile(File directory, String title, Object extensions);

	/**
	 * Presents user with a list of enum options that can be selected or deselected.
	 * 
	 * @param title   Dialog title
	 * @param header  Dialog header (can be null)
	 * @param content Dialog content (can be null)
	 * @param control The control object.
	 * @return True if ok clicked, false otherwise
	 */
	public boolean editList(String title, String header, String content, Object control);

	/**
	 * Enables the user to select one string from a list of strings.
	 * 
	 * @param list    List of strings
	 * @param title   Dialog title
	 * @param header  Dialog header (can be null)
	 * @param content Dialog content (can be null)
	 * @return index of selected string in the list (-1 of user cancels)
	 */
	public int getListChoice(String[] list, String title, String header, String content);

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
	public List<String> getRadioButtonChoices(String title, String header, String content, List<String[]> entries);

	/**
	 * Get the owning window of the dialog implementation.
	 * 
	 * @return Owning object.
	 */
	public Object owner();

	/**
	 * Show a file save dialog.
	 * 
	 * @param directory Initial directory
	 * @param title     Dialog title
	 * @param exts      List of valid file extensions
	 * @return The file or null if cancelled.
	 */
	public File promptForSaveFile(File directory, String title, String[]... exts);

	/**
	 * Show a file open dialog.
	 * 
	 * @param directory Initial directory
	 * @param title     Dialog title
	 * @param exts      List of valid file extensions
	 * @return The file or null if cancelled.
	 */
	public File promptForOpenFile(File directory, String title, String[]... exts);

	/**
	 * Select one file from a list of files.
	 * 
	 * @param files         The file list
	 * @param initSelection zero-based index of initial selection.
	 * @return index of new selection.
	 */
	public int selectFile(List<File> files, int initSelection);

	/**
	 * Select any number of entries from a list of strings.
	 * 
	 * @param title    Dialog title
	 * @param header   Dialog header (may be null).
	 * @param items    Items from which to select.
	 * @param selected Selection status of items.
	 * @return The new selection status (may be unchanged)
	 */
	public List<String> getCBSelections(String title, String header, List<String> items, List<Boolean> selected);
}
