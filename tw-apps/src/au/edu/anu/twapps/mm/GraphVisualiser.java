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
import au.edu.anu.twapps.mm.layoutGraph.LayoutEdge;
import au.edu.anu.twapps.mm.layoutGraph.LayoutNode;
import fr.cnrs.iees.graph.impl.TreeGraph;
import au.edu.anu.twcore.root.World;

/**
 * 
 * Interface for the "view" component of <a href=
 * "https://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93controller">Model-View-Controller</a>
 * pattern for displaying an interactive graph.
 * 
 * @author Ian Davies - 9 Aug 2019
 */
public interface GraphVisualiser {
	/**
	 * Build the initial view from the graph.
	 * 
	 * @param duration Duration in ms of animations.
	 */
	public void initialiseView(double duration);

	/**
	 * Add visual elements when adding a new node to the graph.
	 * 
	 * @param node The node to add to the view.
	 */
	public void onNewNode(LayoutNode node);

	/**
	 * Add visual elements when adding a new edge to the graph.
	 * 
	 * @param edge     Edge that has been added.
	 * @param duration Duration in ms of animation.
	 */
	public void onNewEdge(LayoutEdge edge, double duration);

	/**
	 * Collapse a sub-tree to the given root.
	 * 
	 * @param root     Root of the sub-tree to be hidden.
	 * @param duration Duration in ms of animation.
	 */
	public void collapseTreeFrom(LayoutNode root, double duration);

	/**
	 * Expand a sub-tree from the given root.
	 * 
	 * @param root     Root of the sub-tree to be expanded.
	 * @param duration Duration in ms of animation.
	 */
	public void expandTreeFrom(LayoutNode root, double duration);

	/**
	 * Get the underlying graph (the layout graph) of the visualisation.
	 * 
	 * @return layout graph.
	 */
	public TreeGraph<LayoutNode, LayoutEdge> getLayoutGraph();

	/**
	 * Clear all elements of the view.
	 */
	public void close();

	/**
	 * Remove all visual elements associated with the given node.
	 * 
	 * @param node The node to remove.
	 */
	public void removeView(LayoutNode node);

	/**
	 * Remove all visual elements associated with a given edge.
	 * 
	 * @param edge The edge to remove
	 */
	public void removeView(LayoutEdge edge);

	/**
	 * Create visual elements depicting a new parent-child relationship.
	 * 
	 * @param child The child {@link LayoutNode} of the relationship.
	 */
	public void onNewParent(LayoutNode child);

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
	public void doLayout(LayoutNode root, double jitterFaction, LayoutType layoutType, boolean pcShowing,
			boolean xlShowing, boolean sideline, double duration);

	/**
	 * Remove visual elements between this child {@link LayoutNode} and its parent.
	 * 
	 * @param child The child node.
	 */
	public void onRemoveParentLink(LayoutNode child);

	/**
	 * Update {@link LayoutNode} after being renamed.
	 * 
	 * @param node The {@link LayoutNode} to update.
	 */
	public void onNodeRenamed(LayoutNode node);

	/**
	 * Update {@link LayoutEdge} after being renamed.
	 * 
	 * @param edge The {@link LayoutEdge} to update.
	 */
	public void onEdgeRenamed(LayoutEdge edge);

	/**
	 * Highlight all parts of the graph within the given depth of the given root
	 * node.
	 * 
	 * @param centerNode The node at the center of the local graph.
	 * @param depth      Maximum path depth in the display.
	 */
	public void onHighlightLocalGraph(LayoutNode centerNode, int depth);

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
	 * @param layoutGraph The layout graph of {@link LayoutNode} and
	 *                    {@link LayoutEdge}.
	 */
	public void onRollback(TreeGraph<LayoutNode, LayoutEdge> layoutGraph);

	/**
	 * Update the view when a new root is chosen for the layout. This method should
	 * not re-apply the layout. If the root is null the true root of the graph is
	 * used ({@link World}).
	 * 
	 * @param root {@link LayoutNode} that is now the root of the layout (can be
	 *             null).
	 */
	public void setLayoutRoot(LayoutNode root);
}
