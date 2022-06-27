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

import au.edu.anu.twapps.mm.layout.LayoutType;
import au.edu.anu.twapps.mm.visualGraph.VisualEdge;
import au.edu.anu.twapps.mm.visualGraph.VisualNode;
// TODO move to tw-apps later
import fr.cnrs.iees.graph.impl.TreeGraph;

/**
 * Author Ian Davies - 9 Aug 2019
 * 
 * Interface for the "view" of <a href=
 * "https://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93controller">Model-View-Controller</a>
 */
public interface IGraphVisualiser {
	/**
	 * Actions required to build the initial view from the graph.
	 * 
	 * @param duration Duration in ms of animations.
	 */
	public void initialiseView(double duration);

	/**
	 * Actions required to add visualisation elements for a node newly added to the
	 * graph.
	 * 
	 * @param node The node to add to the view.
	 */
	public void onNewNode(VisualNode node);

	/**
	 * Actions required to add visualisation elements for an edge newly added to the
	 * graph.
	 * 
	 * @param edge
	 * @param duration
	 */
	public void onNewEdge(VisualEdge edge, double duration);

	/**
	 * Actions required to collapse a sub-tree to the given root.
	 * 
	 * @param root     Root of the sub-tree to be hidden.
	 * @param duration Duration in ms of animation.
	 */
	public void collapseTreeFrom(VisualNode root, double duration);

	/**
	 * Actions required to expand a sub-tree from the given root.
	 * 
	 * @param root     Root of the sub-tree to be expanded.
	 * @param duration Duration in ms of animation.
	 */
	public void expandTreeFrom(VisualNode root, double duration);

	/**
	 * Get the underlying graph (the layout graph) of the visualisation.
	 * 
	 * @return layout graph.
	 */
	public TreeGraph<VisualNode, VisualEdge> getVisualGraph();

	/**
	 * Clear all elements of the view.
	 */
	public void close();

	/**
	 * Actions required to remove all visual elements of a node.
	 * 
	 * @param node The node to remove.
	 */
	public void removeView(VisualNode node);

	/**
	 * Actions required to remove all visual elements of an edge.
	 * 
	 * @param edge The edge to remove
	 */
	public void removeView(VisualEdge edge);

	/**
	 * Actions required to create visual elements depicting a new parent-child
	 * relationship.
	 * 
	 * @param child The child node of the relationship.
	 */
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
