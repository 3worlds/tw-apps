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

package au.edu.anu.twapps.mr;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import au.edu.anu.omhtk.preferences.*;
import au.edu.anu.twapps.dialogs.DialogService;
import fr.cnrs.iees.omugi.graph.impl.ALEdge;
import fr.cnrs.iees.omugi.graph.impl.TreeGraph;
import fr.cnrs.iees.omugi.graph.impl.TreeGraphDataNode;

/**
 * Implementation of {@IMRModel} to manage model logic for GUI implementation
 * independent tasks.
 * 
 * @author Ian Davies date 31 Dec 2019
 */
public class MRModelImpl implements MRModel {
	private TreeGraph<TreeGraphDataNode, ALEdge> graph;
	// private IMRController controller; // TODO Not used
	private List<File> ISFiles;
	private int currentIdx;

	/**
	 * TODO
	 */
	public MRModelImpl(/* IMRController controller */) {
		// this.controller = controller;
	}

	@Override
	public void doISGenerate() {
		// output of this is a saved *.isf file
		DialogService.getImplementation().infoAlert("Info", "Not implemented", "Show dlg to generated bootstrap driver values and populations");

	}

	@Override
	public void doISReload() {
		// doISClear();
		if (currentIdx >= 0)
			DialogService.getImplementation().infoAlert("Info", "Not implemented",
					"Load drivers and populations from " + ISFiles.get(currentIdx).getName());
	}

	@Override
	public void doISClear() {
		DialogService.getImplementation().infoAlert("Info", "Not implemented", "Clear all drivers and populations");
		// TODO set all data and populations to appropriate zero values

	}

	@Override
	public void doISSaveAs(File file) {
		DialogService.getImplementation().infoAlert("Info", "Not implemented", "Save current drivers and populations to " + file);

	}

	private static final String ISCurrentFileIndex = "ISCurrentFileIndex";
	private static final String ISFileName = "ISFileName";
	private static final String ISFileCount = "ISFileCount";

	@Override
	public void getPreferences() {
		ArrayPreferences prefs = PreferenceService.getImplementation();
		currentIdx = prefs.getInt(ISCurrentFileIndex, -1);
		int n = prefs.getInt(ISFileCount, 0);
		ISFiles = new ArrayList<>();
		for (int i = 0; i < n; i++) {
			String s = prefs.getString(ISFileName + "_" + i, "");
			ISFiles.add(new File(s));
		}
	}

	@Override
	public void putPreferences() {
		ArrayPreferences prefs = PreferenceService.getImplementation();
		prefs.putInt(ISCurrentFileIndex, currentIdx);
		prefs.putInt(ISFileCount, ISFiles.size());
		for (int i = 0; i < ISFiles.size(); i++)
			prefs.putString(ISFileName + "_" + i, ISFiles.get(i).getAbsolutePath());
	}

	@Override
	public int getISSelection() {
		return currentIdx;
	}

	@Override
	public void setISSelection(int idx) {
		currentIdx = idx;
	}

	@Override
	public List<File> getISFiles() {
		return ISFiles;
	}

	@Override
	public TreeGraph<TreeGraphDataNode, ALEdge> getGraph() {
		return graph;
	}

	@Override
	public void setGraph(TreeGraph<TreeGraphDataNode, ALEdge> graph) {
		this.graph = graph;
	}

}
