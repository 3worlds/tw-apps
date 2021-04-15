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
import java.util.Collection;

import fr.cnrs.iees.graph.impl.ALEdge;
import fr.cnrs.iees.graph.impl.TreeGraph;
import fr.cnrs.iees.graph.impl.TreeGraphDataNode;
import fr.cnrs.iees.twmodels.LibraryTable;
//import fr.cnrs.iees.twmodels.*;

/**
 * Author Ian Davies
 *
 * Date 10 Jan. 2019
 */
//ModelMaker methods called by the Controller
// The controller HAS one of these
// ModelMaker IS on of these: ModelMaker implements
// Effectively a singleton listener pattern
public interface IMMModel extends Originator{
	public boolean canClose();

	public void doNewProject(String proposedName, TreeGraph<TreeGraphDataNode, ALEdge> templateGraph);

	public void doOpenProject(File file);

	public void doDeploy();

	public void doSave();

	public void doSaveAs();

	public void doImport();

	public boolean propertyEditable(String label, String key);
	
	public Collection<String> unEditablePropertyKeys(String label);
	
	public void restore(MMMemento m);
	
	public LibraryTable[] getLibrary();
	

}
