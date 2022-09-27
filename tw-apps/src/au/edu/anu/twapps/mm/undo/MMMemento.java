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

package au.edu.anu.twapps.mm.undo;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import au.edu.anu.twapps.mm.visualGraph.VisualEdge;
import au.edu.anu.twapps.mm.visualGraph.VisualNode;
import au.edu.anu.twcore.project.Project;
import fr.cnrs.iees.graph.impl.ALEdge;
import fr.cnrs.iees.graph.impl.TreeGraph;
import fr.cnrs.iees.graph.impl.TreeGraphDataNode;
import fr.cnrs.iees.graph.io.impl.OmugiGraphExporter;
import fr.cnrs.iees.identity.IdentityScope;
import fr.cnrs.iees.identity.impl.LocalScope;
import fr.cnrs.iees.io.GraphFileFormats;
import fr.ens.biologie.generic.utils.Duple;
import fr.ens.biologie.generic.utils.Tuple;
import au.edu.anu.twapps.mm.MMModel;

/**
 * Implementation of {@link IMemento} for use by {@link MMModel}.
 * 
 * @author Ian Davies 1 Jun 2020
 */
public class MMMemento implements IMemento {
	private final static String configName = "__stateA";
	private final static String layoutName = "__stateB";
	private final static String prefName = "__stateC";

	private Tuple<File, File, File> state;
	private String desc;

	/**
	 * Construct a ModelMaker memento.
	 * 
	 * @param desc Descrption of the state to appear in application controls.
	 * @param a    The configuration graph.
	 * @param b    The layout graph.
	 * @param c    The state of ModelMaker controls.
	 */
	public MMMemento(String desc, TreeGraph<TreeGraphDataNode, ALEdge> a, TreeGraph<VisualNode, VisualEdge> b, File c) {
		this.state = nextState();
		this.desc = desc;
		new OmugiGraphExporter(state.getFirst()).exportGraph(a);
		new OmugiGraphExporter(state.getSecond()).exportGraph(b);
		try {
			Files.copy(c.toPath(), state.getThird().toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return a tuple of artifacts for this state (configuration, layout,
	 *         application controls).
	 */
	public final Tuple<File, File, File> getState() {
		return state;
	}

	private Tuple<File, File, File> nextState() {
		List<Duple<File, File>> filePairs = getFiles();
		IdentityScope scope = new LocalScope("UNDO");
		for (Duple<File, File> filePair : filePairs) {
			String filename = filePair.getFirst().getName();
			String name = filename.substring(0, filename.indexOf("."));
			scope.newId(true, name);
		}
		String newName1 = scope.newId(true, configName + 1).id();
		String newName2 = newName1.replace(configName, layoutName);
		String newName3 = newName1.replace(configName, prefName);
		File f1 = Project.makeFile(newName1 + GraphFileFormats.TGOMUGI.extension().split(" ")[0]);
		File f2 = Project.makeFile(newName2 + GraphFileFormats.TGOMUGI.extension().split(" ")[0]);
		File f3 = Project.makeFile(newName3 + ".xml");
		return new Tuple<File, File, File>(f1, f2, f3);
	}

	private static List<Duple<File, File>> getFiles() {
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

	private static void deleteFile(File f) {
		if (f.exists())
			f.delete();
	}

	@Override
	public void finalise() {
		deleteFile(state.getFirst());
		deleteFile(state.getSecond());
		deleteFile(state.getThird());
	}

	@Override
	public String getDescription() {
		return desc;
	}

	/**
	 * Delete all state files in the current {@link Project} directory.
	 */
	public static void deleteStrandedFiles() {

		File[] files = Project.getProjectFile().listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return (name.startsWith(configName) || name.startsWith(layoutName) || name.startsWith(prefName));
			}

		});
		for (File f : files)
			f.delete();

	}

}
