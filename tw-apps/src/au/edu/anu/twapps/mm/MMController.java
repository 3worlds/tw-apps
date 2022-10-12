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

import java.util.Collection;

import au.edu.anu.omhtk.preferences.Preferenceable;
import au.edu.anu.twapps.mm.layout.LayoutType;
import au.edu.anu.twapps.mm.layoutGraph.LayoutEdge;
import au.edu.anu.twapps.mm.layoutGraph.LayoutNode;
import fr.cnrs.iees.graph.impl.TreeGraph;

/**
 * Interface for the 'controller' component of a (cf: <a href=
 * "https://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93controller">Model-View-Controller</a>)
 * of ModelMaker.
 * 
 * @see MMModelImpl
 * 
 * @author Ian Davies - 17 Dec. 2018
 */
public interface MMController extends Preferenceable {

	/**
	 * Owners of the controller interface ({@link MMModelImpl}) have a need to
	 * directly call methods of the visualisation interface. This will be hidden in
	 * a future release by making all such calls via the controller itself.
	 * 
	 * @return interface to the configuration graph visualisation.
	 */
	public GraphVisualiser visualiser();

	/**
	 * Inform the controller that a project is closing. Typically, a controller will
	 * ask the view to clear elements in the display.
	 */
	public void onProjectClosing();

	/**
	 * Once a project is opened, the controller will ask the view to build itself
	 * based upon information in the layout graph.
	 * 
	 * @param layoutGraph The graph that maintains layout information.
	 */
	public void onProjectOpened(TreeGraph<LayoutNode, LayoutEdge> layoutGraph);

	/**
	 * Actions to take when a node has been selected by the user.
	 * 
	 * @param selectedNode The user-selected node.
	 */
	public void onNodeSelected(LayoutNode selectedNode);

	/**
	 * Actions to take when a new node has been added to the configuration graph.
	 * 
	 * @param newNode The newly constructed node
	 */
	public void onNewNode(LayoutNode newNode);

	/**
	 * Actions to take when a new edge has been added to the configuration graph.
	 * <p>
	 * 
	 * @param newEdge The newly constructed edge.
	 */
	public void onNewEdge(LayoutEdge newEdge);

	/**
	 * Actions to take when any configuration node has been deleted.
	 */
	public void onNodeDeleted();

	/**
	 * Actions to take when any configuration edge has been deleted.
	 */
	public void onEdgeDeleted();

	/**
	 * Actions to take when any configuration element (node or edge) has been
	 * renamed.
	 */
	public void onElementRenamed();

	/**
	 * Actions to take when a sub-tree of a node has been collapsed.
	 */
	public void onTreeCollapse();

	/**
	 * Actions to take when a sub-tree of a node has been expanded.
	 */
	public void onTreeExpand();

	/**
	 * Actions to take when the properties of an element (node or edge) have
	 * changed.
	 * 
	 * @param item The element that has had changes to its properties. Its class
	 *             will be implementation specific.
	 */
	public void onItemEdit(Object item);

	/**
	 * Apply the current layout algorithm to the graph visualisation.
	 * 
	 * @param durationInMilliSec Time in milliseconds for animation of the action
	 */
	public void doLayout(double durationInMilliSec);

	/**
	 * Apply the given layout algorithm using given node as the root.
	 * <p>
	 * 
	 * @param root               The node that will form the root of the layout
	 *                           (ignored if the current layout algorithm is not a
	 *                           tree layout).
	 * @param layoutType         The {@link LayoutType} to be used.
	 * @param durationInMilliSec Time in milliseconds for animation of the action.
	 */
	public void doFocusedLayout(LayoutNode root, LayoutType layoutType, double durationInMilliSec);

	/**
	 * Set a default title for the ModelMaker window.
	 */
	public void setDefaultTitle();

	/**
	 * Actions to take when the configuration graph has been rolled back to some
	 * other previously saved state.
	 * 
	 * @param layoutGraph The new layout graph.
	 */
	public void onRollback(TreeGraph<LayoutNode, LayoutEdge> layoutGraph);

	/**
	 * Queries {@link MMModelImpl} to return all property keys for nodes with the
	 * given classId. The {@link GraphVisualiser} requires this to disable editing
	 * of immutable properties.
	 * <p>
	 * 
	 * @param classId The class of node.
	 * @return All keys of immutable properties of nodes of this class (can be
	 *         empty).
	 */
	public Collection<String> getUnEditablePropertyKeys(String classId);

	/**
	 * Actions to take when optional properties are added or removed from a node.
	 * <p>
	 * 
	 * @param propertyOwner the node containing the relevant properties.
	 */
	public void onAddRemoveProperty(LayoutNode propertyOwner);

	/**
	 * Set the current layout root. This only has meaning when a tree
	 * {@link LayoutType} is used. If layoutRoot is null, the new root will be the
	 * true root of the tree, even if the tree is broken.
	 * 
	 * @param layoutRoot The new layout root. If null, the layout root will be the
	 *                   true root of the tree.
	 * @return the previous layout root.
	 */
	public LayoutNode setLayoutRoot(LayoutNode layoutRoot);

	/**
	 * Gets the current layout root.
	 * 
	 * @return current layout root.
	 */
	public LayoutNode getLayoutRoot();

	/**
	 * Actions to take if the layout root name has changed (i.e uptake controls as
	 * required).
	 */
	public void onRootNameChange();

	/**
	 * The model interface {@link MMModel} controlled by this controller.
	 * <p>
	 * 
	 * @return The model interface.
	 */
	public MMModel model();
}
