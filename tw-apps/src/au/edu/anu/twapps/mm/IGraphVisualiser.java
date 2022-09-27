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
import fr.cnrs.iees.graph.impl.TreeGraph;
import au.edu.anu.twcore.root.World;

/**
 * 
 * Interface for the "view" of <a href=
 * "https://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93controller">Model-View-Controller</a>
 * 
 * @author Ian Davies - 9 Aug 2019
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
	 * @param edge     Edge that has been added.
	 * @param duration Duration in ms of animation.
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
	 * @param child The child {@link VisualNode} of the relationship.
	 */
	public void onNewParent(VisualNode child);

	/**
	 * Re-apply the layout algorithm.
	 * 
	 * @param root          Root of the layout (if {@link LayoutType} is a tree
	 *                      algorithm).
	 * @param jitterFaction Amount of random displacement to apply (relative to
	 *                      drawing dimension)
	 * @param layoutType    Choice of algorithm {@link LayoutType}.
	 * @param pcShowing     Show parent-child relationships.
	 * @param xlShowing     Show cross-links.
	 * @param sideline      Place all isolated nodes to one side.
	 * @param duration      Duration in ms of animation.
	 */
	public void doLayout(VisualNode root, double jitterFaction, LayoutType layoutType, boolean pcShowing,
			boolean xlShowing, boolean sideline, double duration);

	/**
	 * Remove visual elements between this child {@link VisualNode} and its parent.
	 * 
	 * @param child The child node.
	 */
	public void onRemoveParentLink(VisualNode child);

	/**
	 * Update {@link VisualNode} after being renamed.
	 * 
	 * @param node The {@link VisualNode} to update.
	 */
	public void onNodeRenamed(VisualNode node);

	/**
	 * Update {@link VisualEdge} after being renamed.
	 * 
	 * @param edge The {@link VisualEdge} to update.
	 */
	public void onEdgeRenamed(VisualEdge edge);

	/**
	 * Highlight all parts of the graph within the given depth of the given root
	 * node.
	 * 
	 * @param centerNode The node at the center of the local graph.
	 * @param depth      Maximum path depth in the display.
	 */
	public void onHighlightLocalGraph(VisualNode centerNode, int depth);

	/**
	 * Return view to normal after highlighting a local portion.
	 */
	public void onHighlightAll();

	/**
	 * Collapses (hides) the pre-defined sub-tree. This is use when a configuration
	 * is first created to remove clutter.
	 */
	public void collapsePredef();

	/**
	 * Update the view after rollback to a previous state of the configuration graph
	 * and the ModelMaker controls.
	 * 
	 * @param layoutGraph The layout graph of {@link VisualNode} and
	 *                    {@link VisualEdge}.
	 */
	public void onRollback(TreeGraph<VisualNode, VisualEdge> layoutGraph);

	/**
	 * Update the view when a new root is chosen for the layout. This method should
	 * not re-apply the layout. If the root is null the true root of the graph is
	 * used ({@link World}).
	 * 
	 * @param root {@link VisualNode} that is now the root of the layout (can be
	 *             null).
	 */
	public void setLayoutRoot(VisualNode root);
}
