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

import au.edu.anu.twapps.mm.layout.LayoutType;
import au.edu.anu.twapps.mm.visualGraph.VisualEdge;
import au.edu.anu.twapps.mm.visualGraph.VisualNode;
import fr.cnrs.iees.graph.impl.TreeGraph;

/**
 * Author Ian Davies - 17 Dec. 2018
 * <p>
 * Interface for the controller (cf: <a href=
 * "https://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93controller">MVC</a>)
 * of ModelMaker ({@link MMModel}).
 */
// 
public interface IMMController {

	/**
	 * Owners of the controller interface ({@link MMModel}) have a need to directly
	 * call methods of the visualisation interface. This will be hidden in a future
	 * release by making all such calls via the controller itself.
	 * 
	 * @return interface to the configuration graph visualisation.
	 */
	public IGraphVisualiser visualiser();

	/**
	 * Inform the controller that a project is closing. Typically, a controller will
	 * ask the view to clear elements in the display.
	 */
	public void onProjectClosing();

	/**
	 * Actions to take when a new project is opened. The controller will ask the
	 * view to build itself based upon information in the layout graph.
	 * <p>
	 * 
	 * @param layoutGraph The graph that maintains layout information.
	 */
	public void onProjectOpened(TreeGraph<VisualNode, VisualEdge> layoutGraph);

	/**
	 * Actions to take when a node has been selected by the user.
	 * <p>
	 * 
	 * @param n The user selected node.
	 */
	public void onNodeSelected(VisualNode n);

	/**
	 * Actions to take when a new node has been added to the configuration graph.
	 * <p>
	 * 
	 * @param n The newly constructed node
	 */
	public void onNewNode(VisualNode n);

	/**
	 * Actions to take when a new edge has been added to the configuration graph.
	 * <p>
	 * 
	 * @param e The newly constructed edge.
	 */
	public void onNewEdge(VisualEdge e);

	/**
	 * Actions to take when a configuration node has been deleted.
	 */
	public void onNodeDeleted();

	/**
	 * Actions to take when a configuration edge has been deleted.
	 */
	public void onEdgeDeleted();

	/**
	 * Actions to take when a configuration element (node or edge) has been renamed.
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
	 * <p>
	 * 
	 * @param item The element that has had changes to its properties. Its class
	 *             will be implementation specific.
	 */
	public void onItemEdit(Object item);

	/**
	 * Apply the current layout algorithm to the graph visualisation.
	 * <p>
	 * 
	 * @param duration Time in milliseconds for animation of the action
	 */
	public void doLayout(double duration);

	/**
	 * Apply the given layout algorithm using given node as the root.
	 * <p>
	 * 
	 * @param root     The node that will form the root of the layout (ignored if
	 *                 the current layout algorithm is not a tree layout).
	 * @param layout   The {@link LayoutType} to be used.
	 * @param duration Time in milliseconds for animation of the action.
	 */
	public void doFocusedLayout(VisualNode root, LayoutType layout, double duration);

	/**
	 * Set a default title for the ModelMaker window.
	 */
	public void setDefaultTitle();

	/**
	 * Actions to take when the configuration graph has been rolled back to some
	 * other previously saved state.
	 * <p>
	 * 
	 * @param layoutGraph The new layout graph.
	 */
	public void onRollback(TreeGraph<VisualNode, VisualEdge> layoutGraph);

	/**
	 * Retrieve controller settings from a preferences system.
	 */
	public void getPreferences();

	/**
	 * Store controller settings to preferences system.
	 */
	public void putPreferences();

	// TODO This should be paired with the same process for edge properties.
	/**
	 * Queries {@link MMModel} to return all property keys for nodes with the given
	 * classId. The {@link IGraphVisualiser} requires this to disable editing of
	 * immutable properties.
	 * <p>
	 * 
	 * @param classId The class of node.
	 * @return All keys of immutable properties of nodes of this class (can be
	 *         empty).
	 */
	public Collection<String> getUnEditablePropertyKeys(String classId);

	// TODO This should be paired with the same process for edge properties.
	/**
	 * Actions to take when optional properties are added or removed from a node.
	 * <p>
	 * 
	 * @param vn the node containing the relevant properties.
	 */
	public void onAddRemoveProperty(VisualNode vn);

	/**
	 * Set the current layout root. This only has meaning when a tree
	 * {@link LayoutType} is used. If layoutRoot is null, the new root will be the
	 * true root of the tree, even if the tree is broken.
	 * <p>
	 * 
	 * @param layoutRoot The new layout root. If null, the layout root will be the
	 *                   true root of the tree. If
	 * @return the previous layout root.
	 */
	public VisualNode setLayoutRoot(VisualNode layoutRoot);

	/**
	 * Gets the current layout root.
	 * <p>
	 * 
	 * @return current layout root.
	 */
	public VisualNode getLayoutRoot();

	/**
	 * Actions to take if the layout root name has changed (i.e uptake controls as
	 * required).
	 */
	public void onRootNameChange();

	/**
	 * The model interface {@link IMMModel} controlled by this controller.
	 * <p>
	 * 
	 * @return The model interface.
	 */
	public IMMModel model();
}
