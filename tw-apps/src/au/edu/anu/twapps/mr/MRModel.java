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
import java.util.List;

import fr.cnrs.iees.omugi.graph.impl.ALEdge;
import fr.cnrs.iees.omugi.graph.impl.TreeGraph;
import fr.cnrs.iees.omugi.graph.impl.TreeGraphDataNode;
import au.edu.anu.omhtk.preferences.Preferences;

//ModelRunner methods called by the Controller
//The controller HAS one of these
//ModelRunner IS on of these: ModelRunner implements
/**
 * 
 * Interface implemented by {@link MRModelImpl} is part of the <a href=
 * "https://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93controller">Model-View-Controller</a>
 * pattern.
 * 
 * @author Ian Davies 31 Dec 2019
 * 
 */
public interface MRModel {
	/**
	 * Create a file with the run-time dynamic graph.
	 */
	public void doISGenerate();

	/**
	 * Setter of the index one of a list of files of the dynamic graph as the one to
	 * load when simulation begins.
	 * 
	 * @param idx index in the list of files.
	 */
	public void setISSelection(int idx);

	/**
	 * Getter of the index one of a list of files of the dynamic graph as the one to
	 * load when simulation begins.
	 * 
	 * @return file index.
	 */
	public int getISSelection();

	/**
	 * Get a list of all available dynamic graph files.
	 * 
	 * @return The list of files.
	 */
	public List<File> getISFiles();

	/**
	 * Reload the dynamic graph during a simulation (i.e during a pause).
	 */
	public void doISReload();

	/**
	 * Zero all dynamic data and remove ephemeral nodes.
	 */
	public void doISClear();

	/**
	 * Save the dynamic graph to a file.
	 * 
	 * @param file The file.
	 */
	public void doISSaveAs(File file);

	/**
	 * Setter for the configuration graph.
	 * 
	 * @param graph Configuration graph.
	 */
	public void setGraph(TreeGraph<TreeGraphDataNode, ALEdge> graph);

	/**
	 * Getter of the configuration graph.
	 * 
	 * @return Configuration graph.
	 */
	public TreeGraph<TreeGraphDataNode, ALEdge> getGraph();

	/**
	 * Set the ModelRunner controls from the {@link Preferences} system.
	 */
	public void getPreferences();

	/**
	 * Save the ModelRunner controls to the {@link Preferences} system.
	 */
	public void putPreferences();
}
