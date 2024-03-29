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

package au.edu.anu.twapps.mm;

import java.io.File;
import java.util.List;

import au.edu.anu.twapps.dialogs.Dialogs;
import au.edu.anu.twapps.dialogs.YesNoCancel;

public class MockDialogs implements Dialogs {

	@Override
	public void errorAlert(String title, String header, String content) {
		System.out.println("errorAlert " + title + " " + header + " " + content);
	}

	@Override
	public void infoAlert(String title, String header, String content) {
		System.out.println("infoAlert " + title + " " + header + " " + content);

	}

	@Override
	public void warnAlert(String title, String header, String content) {
		System.out.println("warnAlert " + title + " " + header + " " + content);

	}

	@Override
	public File selectDirectory(String title, String currentPath) {
		// TODO Auto-generated method stub
		return new File(currentPath);
	}

	@Override
	public YesNoCancel yesNoCancel(String title, String header, String content) {
		// TODO Auto-generated method stub
		return YesNoCancel.yes;
	}

	@Override
	public String getText(String title, String header, String content, String prompt, String validFormat) {
		// TODO Auto-generated method stub
		return "getText user string";
	}

	@Override
	public File getExternalProjectFile() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean confirmation(String title, String header, String content) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public File getOpenFile(File directory, String title, Object extensions) {
		// TODO List<ExtensionFilter> extensionsAuto-generated method stub
		return null;
	}

	@Override
	public boolean editList(String title, String header, String content, Object listView) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getListChoice(String[] list, String title, String header, String content) {
		// TODO Auto-generated method stub
		return -1;
	}

	@Override
	public List<String> getRadioButtonChoices(String title, String header, String content, List<String[]> entries) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object owner() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public File exportFile(String title, String promptDir, String promptFileName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int selectFile(List<File> files, int idx) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public File promptForSaveFile(File directory, String title, String[]... exts) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public File promptForOpenFile(File directory, String title, String[]... exts) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getCBSelections(String title, String header, List<String> items, List<Boolean> selected) {
		// TODO Auto-generated method stub
		return null;
	}

}
