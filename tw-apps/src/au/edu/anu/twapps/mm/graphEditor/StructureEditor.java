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

import java.util.List;

import au.edu.anu.twapps.mm.layoutGraph.LayoutEdge;
import au.edu.anu.twapps.mm.layoutGraph.LayoutNode;
import fr.cnrs.iees.omugi.graph.impl.SimpleDataTreeNode;
import fr.cnrs.iees.omhtk.utils.*;

/**
 * Interface for the graph structure editor for ModelMaker. All these methods
 * are in the context of a single user-selected node. This node, represented by
 * {@link VisualNodeEditor} is assumed available in implementations of this
 * interface.
 * 
 * @author Ian Davies - 11 Jan. 2019
 * 
 */
public interface StructureEditor {

	/**
	 * Filters the list of potential child specifications to just those applicable
	 * to the current state of the configuration graph (e.g. specifications for
	 * children that have 1..1 multiplicity and already exist are removed from the
	 * returned list.
	 * 
	 * @param childNodeSpecs List of all potential child specifications.
	 * @return List of child specifications relevant to the current state of the
	 *         configuration graph.
	 */
	public List<SimpleDataTreeNode> filterChildSpecs(Iterable<SimpleDataTreeNode> childNodeSpecs);

	/**
	 * Filters the list of potential out-edge specifications to just those
	 * applicable to the current state of the configuration graph (e.g.
	 * specifications for out-edges that have a 1..1 multiplicity and already exist
	 * are not included in the result.
	 * 
	 * @param edgeSpecs List of all potential out-edge specifications.
	 * @return A list (can be empty) of tuples the out-edge label, the end-node and
	 *         the out-edge specification.
	 */
	public List<Tuple<String, LayoutNode, SimpleDataTreeNode>> filterEdgeSpecs(Iterable<SimpleDataTreeNode> edgeSpecs);

	/**
	 * Returns a list of nodes without parents whose specifications appear in the
	 * childSpecs list. This situation can arise when editing the configuration
	 * graph when a user wants to delete a parent or reassign the parent of a node
	 * to a different parent.
	 * 
	 * @param childSpecs List of relevant child specifications of the node being
	 *                   edited.
	 * @return List of valid child {@link LayoutNode}s.
	 */
	public List<LayoutNode> orphanedChildList(Iterable<SimpleDataTreeNode> childSpecs);

	/**
	 * Actions required to construct a new child node.
	 * 
	 * @param childLabel    The child's label
	 * @param childId       A suggested name (can be changed by the user but its
	 *                      uniqueness within the scope of the configuration graph
	 *                      will be enforced.
	 * @param childBaseSpec The specification of the new child node.
	 */
	void onNewChild(String childLabel, String childId, SimpleDataTreeNode childBaseSpec);

	/**
	 * Actions required to construct a new out-edge.
	 * 
	 * @param details  A tuple contains the edge label, {@link LayoutNode}
	 *                 (end-node) to which the edge connects and the edge
	 *                 specifications.
	 * @param duration Duration in ms of animation.
	 */
	public void onNewEdge(Tuple<String, LayoutNode, SimpleDataTreeNode> details, double duration);

	/**
	 * Actions required when user asks to delete an out-edge.
	 * 
	 * @param edge {@link LayoutEdge} to delete
	 */
	public void onDeleteEdge(LayoutEdge edge);

	/**
	 * Actions required when user asks to delete a node.
	 * 
	 * @param duration Duration in ms of animation.
	 */
	public void onDeleteNode(double duration);

	/**
	 * Actions required to rename a node. This action can be cancelled by the user.
	 * 
	 * @return true if renamed, false otherwise.
	 */
	public boolean onRenameNode();

	/**
	 * Actions required to rename an out-edge of the currently selected node. This
	 * action can be cancelled by the user.
	 * 
	 * @param edge Out-edge of the currently selected node.
	 * @return true if renamed, false otherwise.
	 */
	public boolean onRenameEdge(LayoutEdge edge);

	/**
	 * Actions required to delete a sub-tree of the currently selected node.
	 * 
	 * @param childRoot The root of the sub-tree to be deleted.
	 * @param duration  Duration in ms of animation.
	 */
	public void onDeleteTree(LayoutNode childRoot, double duration);

	/**
	 * Actions required when collapsing a sub-tree to the currently selected node.
	 * 
	 * @param childRoot The node that identifies one sub-tree amoung possible many
	 *                  as the focus of the operation.
	 * @param duration  Duration in ms of animation.
	 */
	public void onCollapseTree(LayoutNode childRoot, double duration);

	/**
	 * Collapse all sub-trees of the currently selected node.
	 * 
	 * @param duration Duration in ms of animation.
	 */
	public void onCollapseTrees(double duration);

	/**
	 * Expand a sub-tree of the given childRoot from the currently selected node.
	 * 
	 * @param childRoot root of the particular sub-tree;
	 * @param duration  Duration in ms of animation.
	 */
	public void onExpandTree(LayoutNode childRoot, double duration);

	/**
	 * Expand all sub-trees of the currently selected node
	 * 
	 * @param duration Duration in ms of animation.
	 */
	public void onExpandTrees(double duration);

	/* connect node as child of this node */
	/**
	 * Make the currently selected node the parent of the given child node.
	 * 
	 * @param childNode The proposed child node.
	 */
	public void onReconnectChild(LayoutNode childNode);

	/**
	 * Build implementation specific gui (e.g javafx)
	 */
	public void buildgui();

	/**
	 * Export a sub-tree to a file.
	 * 
	 * @param root The root of the sub-tree to export.
	 */
	void onExportTree(LayoutNode root);

	/**
	 * Import a sub-tree to the currently selected node. The root of the imported
	 * tree must satisfy the child specifications.
	 * 
	 * @param childSpec The child specifications.
	 * @param duration  Duration in ms of animation.
	 */
	void onImportTree(SimpleDataTreeNode childSpec, double duration);

	/**
	 * Remove the relationship between a child and its parent. The tree graph will
	 * have more than one root until either the child is deleted or a parent
	 * assigned.
	 * 
	 * @param child The child whose parent-child relationship to the currently
	 *              select node is to be removed.
	 */
	void onDeleteParentLink(LayoutNode child);

	/**
	 * Actions require to add/remove optional properties.
	 * 
	 * @param propertySpecs             List of optional property specifications for
	 *                                  the currently selected node.
	 * @param optionalEdgePropertySpecs List of optional property specifications for
	 *                                  all out-edges of the currently selected
	 *                                  node.
	 * @return true is options have changed, false otherwise.
	 */
	boolean onOptionalProperties(List<SimpleDataTreeNode> propertySpecs,
			List<Duple<LayoutEdge, SimpleDataTreeNode>> optionalEdgePropertySpecs);

}
