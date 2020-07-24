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

import java.io.InputStream;

import au.edu.anu.twapps.mm.layout.LayoutType;
import au.edu.anu.twapps.mm.visualGraph.VisualEdge;
import au.edu.anu.twapps.mm.visualGraph.VisualNode;
import fr.cnrs.iees.graph.impl.TreeGraph;

/**
 * Author Ian Davies
 *
 * Date 17 Dec. 2018
 */
// ModelMaker calls these Controller methods
// ModelMaker HAS one of these
// Controller IS one of these: Controller implements
public interface IMMController {
	public void onProjectClosing();

	public void onProjectOpened(TreeGraph<VisualNode,VisualEdge> layoutGraph);
	
	public void onNodeSelected(VisualNode n);
	
	public void onNewNode(VisualNode n);
	
	public void onNewEdge(VisualEdge e);
	
	public void onNodeDeleted();
	
	public void onEdgeDeleted();
	
	public void onElementRenamed();
	
	public void onTreeCollapse();
	
	public void onTreeExpand();

	public void onItemEdit(Object item);
	
	public void doLayout(double duration);
	
	public void doFocusedLayout(VisualNode root, LayoutType layout,double duration);
	
	public void setDefaultTitle();
	
	public void collapsePredef();
	
	public LayoutType getCurrentLayout();
	
	public void onRollback(TreeGraph<VisualNode, VisualEdge> layoutGraph);
	
	public void getPreferences();
	
	public void putPreferences();

//	public void redirectOutputToUI(InputStream errorStream);

}
