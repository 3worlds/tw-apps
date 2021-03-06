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

package au.edu.anu.twapps.mm.graphEditor;

import au.edu.anu.twapps.mm.layout.LayoutType;
import au.edu.anu.twapps.mm.visualGraph.VisualEdge;
import au.edu.anu.twapps.mm.visualGraph.VisualNode;
// TODO move to tw-apps later
import fr.cnrs.iees.graph.impl.TreeGraph;

/**
 * @author Ian Davies 9 Aug 2019
 */
public interface IGraphVisualiser {
	public void initialiseView(double duration);

	public void onNewNode(VisualNode node);

	public void onNewEdge(VisualEdge edge, double duration);

	public void collapseTreeFrom(VisualNode childRoot, double duration);

	public void expandTreeFrom(VisualNode childRoot, double duration);

	public TreeGraph<VisualNode, VisualEdge> getVisualGraph();

	public void close();

	public void removeView(VisualNode node);

	public void removeView(VisualEdge edge);

	public void onNewParent(VisualNode child);

	public void doLayout(VisualNode root, double jitterFaction, LayoutType layoutType, boolean pcShowing,
			boolean xlShowing, boolean sideline, double duration);

	public void onRemoveParentLink(VisualNode vnChild);

	public void onNodeRenamed(VisualNode vNode);

	public void onEdgeRenamed(VisualEdge vEdge);

	public void onHighlightLocalGraph(VisualNode root, int depth);

	public void onHighlightAll();
	
	public void onShowLocalGraph(VisualNode root, int depth);
	
	public void onShowAll();

	public void collapsePredef();

	public void onRollback(TreeGraph<VisualNode, VisualEdge> layoutGraph);

	public void setLayoutNode(VisualNode newRoot);
}
