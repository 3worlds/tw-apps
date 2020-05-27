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

import au.edu.anu.twapps.mm.visualGraph.VisualEdge;
import au.edu.anu.twapps.mm.visualGraph.VisualNode;
import au.edu.anu.twcore.project.Project;
import fr.cnrs.iees.graph.impl.ALEdge;
import fr.cnrs.iees.graph.impl.TreeGraph;
import fr.cnrs.iees.graph.impl.TreeGraphDataNode;
import fr.cnrs.iees.graph.io.impl.OmugiGraphExporter;
import fr.cnrs.iees.identity.IdentityScope;
import fr.cnrs.iees.identity.impl.LocalScope;
import fr.cnrs.iees.io.FileImporter;
import fr.cnrs.iees.io.GraphFileFormats;
import fr.ens.biologie.generic.utils.Duple;

/**
 * @author Ian Davies
 *
 * @date 27 May 2020
 */
public class Memento {

	private Duple<File, File> filePair;

	public Memento(TreeGraph<TreeGraphDataNode, ALEdge> a, TreeGraph<VisualNode, VisualEdge> b) {
		filePair = next();
		new OmugiGraphExporter(filePair.getFirst()).exportGraph(a);
		new OmugiGraphExporter(filePair.getSecond()).exportGraph(b);
	}

	@SuppressWarnings("unchecked")
	public Duple<TreeGraph<TreeGraphDataNode, ALEdge>, TreeGraph<VisualNode, VisualEdge>> restore() {

		TreeGraph<TreeGraphDataNode, ALEdge> a = (TreeGraph<TreeGraphDataNode, ALEdge>) FileImporter
				.loadGraphFromFile(filePair.getFirst());

		TreeGraph<VisualNode, VisualEdge> b = (TreeGraph<VisualNode, VisualEdge>) FileImporter
				.loadGraphFromFile(filePair.getSecond());

		Duple<TreeGraph<TreeGraphDataNode, ALEdge>, TreeGraph<VisualNode, VisualEdge>> result = new Duple<TreeGraph<TreeGraphDataNode, ALEdge>, TreeGraph<VisualNode, VisualEdge>>(
				a, b);
		return result;
	}

	private static Duple<File, File> next() {
		List<Duple<File, File>> filePairs = UndoRedo.getFiles();
		IdentityScope scope = new LocalScope("UNDO");
		for (Duple<File, File> filePair : filePairs) {
			String filename = filePair.getFirst().getName();
			String name = filename.substring(0, filename.indexOf("."));
			scope.newId(true, name);
		}
		String newName1 = scope.newId(true, "$undoA$1").id();
		String newName2 = newName1.replace(UndoRedo.configName, UndoRedo.layoutName);
		File f1 = Project.makeFile(newName1, GraphFileFormats.TGOMUGI.extension().split(" ")[0]);
		File f2 = Project.makeFile(newName2, GraphFileFormats.TGOMUGI.extension().split(" ")[0]);
		return new Duple<File, File>(f1, f2);
	}


}
