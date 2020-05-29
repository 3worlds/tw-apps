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
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import au.edu.anu.twapps.mm.visualGraph.VisualEdge;
import au.edu.anu.twapps.mm.visualGraph.VisualNode;
import au.edu.anu.twcore.project.Project;
import fr.cnrs.iees.graph.impl.ALEdge;
import fr.cnrs.iees.graph.impl.TreeGraph;
import fr.cnrs.iees.graph.impl.TreeGraphDataNode;
import fr.ens.biologie.generic.utils.Duple;

/**
 * @author Ian Davies
 *
 * @date 27 May 2020
 */

/**
 * Rollover system:
 * 
 * This is an ordered list that we can add to (at each edit) and move along in
 * either direction to undo or redo a change by reloading the given file and
 * making it the current config and layout.
 * 
 * States are stored in sets of unique tmp files and deleted on closing/opening
 * of a project.
 * 
 * Undo/redo is not effected by saving so we should follow that pattern.
 * 
 * Therefore these tmp files should be deleted on project closing/opening (in
 * case of crashing).
 * 
 * 
 * This could get trashed if someone deletes the project dir during a session.
 * 
 * Well the only way to manage that is to keep the graphs in memeory which we
 * don't want to do so if you delete the dir you loose your undo abiltiy but
 * still should be able to avoid crashing.
 * 
 * cf https://en.wikipedia.org/wiki/Memento_pattern
 * 
 * caretaker class, Memento class (create(state), restore())
 * 
 */

// caretaker class
public class Rollover {

	/**
	 * file names with underscores can't be used by the project's main file so these
	 * names should be safe.
	 */
	final static String configName = "__stateA";
	final static String layoutName = "__stateB";

	private static List<Memento> mementos;// should be a linked list
	private static int index;

	// NB: No error checking yet

	/**
	 * NB: This class does not deal with updating the ui with graphs returned from
	 * prev(); Therefore, while saveState can be called from anywhere, getPrevState
	 * and getSuccState can only be called by the controller in the first instance
	 * which then delegates to MMModel to effect updates.
	 */

	public static void initialise() {
		clear();
		mementos = new LinkedList<>();
	}

	public static void finalise() {
		if (Project.isOpen())
			clear();
		mementos = null;
	}

	// go back
	public static boolean canUndo() {
		return index > 0;
	}

	// go forward
	public static boolean canRedo() {
//		if (mementos == null)
//			return false;
		return index < (mementos.size() - 1);
	}

	public static String getUndoText() {
		return mementos.get(index).getDesc();
	}

	public static String getRedoText() {
		return mementos.get(index + 1).getDesc();
	}

	public static void preserveState(String desc, TreeGraph<TreeGraphDataNode, ALEdge> a,
			TreeGraph<VisualNode, VisualEdge> b) {
		Memento m = new Memento(desc, a, b);
		index++;
		mementos.add(index, m);
//		show("preserve");
	}

	public static Duple<TreeGraph<TreeGraphDataNode, ALEdge>, TreeGraph<VisualNode, VisualEdge>> getPrevState() {
		index--;
		Memento m = mementos.get(index);
//		show("getPrev");
		return m.restore();
	}

	public static Duple<TreeGraph<TreeGraphDataNode, ALEdge>, TreeGraph<VisualNode, VisualEdge>> getSuccState() {
		index++;
		Memento m = mementos.get(index);
//		show("getSucc");
		return m.restore();
	}

//	public static Duple<TreeGraph<TreeGraphDataNode, ALEdge>, TreeGraph<VisualNode, VisualEdge>> getStateAt(int i){
//		//TODO
//		return mementos.get(i).restore();
//	}

	private static void deleteFile(File file) {
		if (file.exists())
			file.delete();
	}

	private static void clear() {
		for (Duple<File, File> filePair : getFiles()) {
			deleteFile(filePair.getFirst());
			deleteFile(filePair.getSecond());
		}
		index = -1;
	}

	static List<Duple<File, File>> getFiles() {
		File[] files = Project.getProjectFile().listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {

				return name.startsWith(configName);
			}

		});
		List<Duple<File, File>> result = new ArrayList<>();
		for (File f : files) {
			String b = f.getAbsolutePath().replace(configName, layoutName);
			result.add(new Duple<File, File>(f, new File(b)));
		}
		return result;
	}

//	private static void show(String msg) {
//		System.out.println(msg + "------");
//		for (int i = 0; i < mementos.size(); i++) {
//			if (i == index)
//				System.out.println(i + ") ->" + mementos.get(i).getDesc() + " [" + mementos.get(i).getFilename() + "]");
//			else
//				System.out.println(i + ") " + mementos.get(i).getDesc() + " [" + mementos.get(i).getFilename() + "]");
//		}
//	}

}
