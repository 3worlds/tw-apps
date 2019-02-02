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
package au.edu.anu.twapps.mm.visualGraph;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.logging.Logger;

import au.edu.anu.twcore.project.Project;
import fr.cnrs.iees.graph.MinimalGraph;
import fr.cnrs.iees.graph.io.impl.OmugiGraphExporter;
import static fr.cnrs.iees.io.parsing.impl.TreeGraphTokens.COMMENT;

/**
 * An Exporter for aot graphs.
 * 
 * @author Jacques Gignoux - 11 janv. 2019
 *
 */
public class VisualGraphExporter extends OmugiGraphExporter {


	// Constructors
	public VisualGraphExporter(File file) {
		super(file);
	}
	
	
	@Override
	public void exportGraph(MinimalGraph<?> graph) {
		if (VisualGraph.class.isAssignableFrom(graph.getClass())) {
			header = "aot";
			exportTreeGraph((VisualGraph)graph);
		}
	}

	public static void saveGraphToFile(File makeLayoutFile, VisualGraph layoutGraph) {
		new VisualGraphExporter(Project.makeLayoutFile()).exportGraph(layoutGraph);
	}

}
