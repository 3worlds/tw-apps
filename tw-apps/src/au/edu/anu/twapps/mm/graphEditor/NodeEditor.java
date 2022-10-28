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

import fr.cnrs.iees.omugi.collections.tables.StringTable;

import java.util.Collection;

import au.edu.anu.omhtk.util.IntegerRange;
import au.edu.anu.twapps.mm.layoutGraph.LayoutEdge;
import au.edu.anu.twapps.mm.layoutGraph.LayoutNode;
import fr.cnrs.iees.omugi.graph.impl.ALEdge;
import fr.cnrs.iees.omugi.graph.impl.TreeGraphDataNode;
import fr.cnrs.iees.omugi.graph.impl.TreeGraphNode;

/**
 * 
 * Interface to provide a wrapper class to manage the currently selected node
 * for editing for the graph structure editor.
 * 
 * @author Ian Davies - 10 Jan. 2019
 */
public interface NodeEditor {

	/**
	 * The underlying {@link LayoutNode}. Ideally this should not be exposed!
	 * 
	 * @return The {@link LayoutNode} currently selected for editing.
	 */
	public LayoutNode layoutNode();
	// remove

	/**
	 * @return All Layout nodes that are children of this node.
	 */
	public Collection<LayoutNode> getChildren();

	/**
	 * @return true if <em>any</em> sub-trees of this node are collapsed; false otherwise.
	 */
	public boolean isCollapsed();

	
	/**
	 * Query to ask if more children of the given node label are allowed within the
	 * given {@link IntegerRange}.
	 * 
	 * @param range      Allowed number of children
	 * @param childLabel Label of the child type.
	 * @return true if allowed, false otherwise.
	 */
	public boolean moreChildrenAllowed(IntegerRange range, String childLabel);

	/**
	 * Query to ask if the currently selected node have out-edges.
	 * 
	 * @return true if out-edges exist, false otherwise.
	 */
	public boolean hasOutEdges();

	/**
	 * Get the list of out-edges of the currently selected node.
	 * 
	 * @return List of {@link LayoutEdge} out-edges.
	 */
	public Iterable<LayoutEdge> getOutEdges();

	/**
	 * Get the out edges from the configuration node.
	 * 
	 * @return Collection of out edges.
	 */
	public Collection<? extends ALEdge> getConfigOutEdges();

	/**
	 * Ensure that the proposed node name (id) is unique within the scope of the
	 * configuration graph.
	 * 
	 * @param proposedName Proposed name.
	 * @return actual name, modified if required, to ensure it is unique within the
	 *         scope.
	 */
	public String proposeAnId(String proposedName);

	/**
	 * Get the underlying java class of the currently selected configuration.
	 * 
	 * @return java class of the selected node.
	 */
	public Class<? extends TreeGraphNode> getSubClass();

	/**
	 * Query to ask if the currently selected node has an out-edge to the given
	 * node.
	 * 
	 * @param endNode   Edge destination node.
	 * @param edgeLabel Edge label.
	 * @return true is such an edge exists, false otherwise.
	 */
	public boolean hasOutEdgeTo(LayoutNode endNode, String edgeLabel);

	/**
	 * Get a list of all {@link LayoutNode}s which have edges to them from the
	 * currently selected node.
	 * 
	 * @return List of {@link LayoutNode}s than satisfy (can be empty).
	 */
	public Iterable<LayoutNode> getOutNodes();

	/**
	 * Query to ask if the currently selected node is referenced in the given table
	 * of parents.
	 * 
	 * @param parents {@link StringTable}
	 * @return true if reference found, false otherwise.
	 */
	public boolean references(StringTable parents);

	/**
	 * @return The label or classId component of the configuration node (i.e the
	 *         node being edited).
	 */
	public String getLabel();

	/**
	 * @return The name of id component of the configuration node (i.e the node
	 *         being edited).
	 */
	public String getName();

	/**
	 * @return true if configuration node (i.e the node being edited) label is the
	 *         3Worlds root; false otherwise.
	 */
	public boolean is3Wroot();

	/**
	 * @param key Key of the property being queried.
	 * @return true if the node being edited has the property; false otherwise.
	 */
	public boolean hasProperty(String key);

	/**
	 * @return true if the node being edited is in the predefined sub-tree; false
	 *         otherwise.
	 */
	public boolean isPredefined();

	/**
	 * This table persists after a parent link has been deleted. It provides the
	 * valid list of possible parents allowed when reassigning the parent after
	 * editing.
	 * 
	 * @return the parent table of the Layout node
	 */
	public StringTable getParentTable();

	/**
	 * @param name    The unique name of this edge
	 * @param label   The label or classId of this edge
	 * @param endNode The end node.
	 * @return the newly created edge.
	 */
	public LayoutEdge newEdge(String name, String label, LayoutNode endNode);

	/**
	 * Create a new layout node
	 * 
	 * @param label
	 * @param promptName
	 * @return new child of this node.
	 */
	public LayoutNode newChild(String label, String promptName);

	/**
	 * Get the configuration node that is being edited.
	 * 
	 * @return The configuration node.
	 */
	public TreeGraphDataNode getConfigNode();

	/**
	 * @param make the given node a child of this node.
	 */
	public void connectChild(LayoutNode child);

	/**
	 * @return true if this node is not a leaf node.
	 */
	public boolean hasChildren();

	public boolean hasCollapsedChild();

	/**
	 * @return true if there is at least on child in the sub-tree of this graph that
	 *         is collapsed; false otherise.
	 */
	public boolean hasUncollapsedChildren();

	public void deleteParentLink(LayoutNode child);
	
//	public Collection<LayoutNode> getAllRoots();

}
