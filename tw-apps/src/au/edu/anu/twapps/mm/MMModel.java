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
import java.io.InputStream;
import java.util.Collection;

import au.edu.anu.twapps.mm.undo.MMMemento;
import au.edu.anu.twapps.mm.undo.Originator;
import fr.cnrs.iees.graph.impl.ALEdge;
import fr.cnrs.iees.graph.impl.TreeGraph;
import fr.cnrs.iees.graph.impl.TreeGraphDataNode;
import fr.cnrs.iees.twmodels.LibraryTable;
import au.edu.anu.twcore.project.Project;

/**
 * @author Ian Davies - 10 Jan. 2019
 *         <p>
 *         Interface implemented by {@link MMModelImpl} is part of the <a href=
 *         "https://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93controller">Model-View-Controller</a>
 *         pattern.
 *         </p>
 *         <p>
 *         This interface extends {@link Originator} as part of the
 *         <a href= "https://en.wikipedia.org/wiki/Memento_pattern">Momento</a>
 *         pattern to restore model state as part of the undo/redo system.
 */
public interface MMModel extends Originator {
	/**
	 * Prompt the user to confirm closing an open {@link Project}.
	 * 
	 * @return true if confirmed (file is saved or user does not want the file
	 *         saved) and false if user cancels the operation.
	 */
	public boolean canClose();

	/**
	 * Actions required when creating a new {@link Project}. The parameters are
	 * supplied from the {@link LibraryTable}.
	 * <p>
	 * 
	 * @param proposedName  Default name of the project.
	 * @param templateGraph Project graph from the model.
	 * @param archiveStream Project artifacts (may be null).
	 */
	public void doNewProject(String proposedName, TreeGraph<TreeGraphDataNode, ALEdge> templateGraph,
			InputStream archiveStream);

	/**
	 * Actions required when proposing to open a {@link Project} from disk.
	 * 
	 * @param directory the {@link Project} directory.
	 */
	public void doOpenProject(File directory);

	/**
	 * Actions required to deploy the current {@link Project} as a running model.
	 * Assumes the configuration is verified.
	 */
	public void doDeploy();

	/**
	 * Actions required to save the current {@link Project}.
	 */
	public void doSave();

	/**
	 * Actions required to save the current {@link Project} under a new name.
	 */
	public void doSaveAs();

	/**
	 * Actions required to construct a {@link Project} from an external graph.
	 */
	public void doImport();

	/**
	 * Query to determine if a property is immutable.
	 * 
	 * @param classId Class name of the element (node/edge) containing the property.
	 * @param key     Property key
	 * @return true if property is immutable, false otherwise.
	 */
	public boolean propertyEditable(String classId, String key);

	/**
	 * A collection of all immutable property keys for elements of this class name.
	 * 
	 * @param classId The element (node/edge) class name.
	 * @return List of keys (can be empty).
	 */
	public Collection<String> unEditablePropertyKeys(String classId);

	/**
	 * Actions required to retore the configuration graph to a previous state.
	 * 
	 * @param m {@link MMMemento} containing state information.
	 */
	public void restore(MMMemento m);

	/**
	 * @return All entries in the {@link LibraryTable}.
	 */
	public LibraryTable[] getLibrary();

	/**
	 * Actions required to save the current state as an {@link MMMemento}.
	 */
	public void saveAndReload();

}
