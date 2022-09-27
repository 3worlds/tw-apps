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

import au.edu.anu.rscs.aot.collections.tables.StringTable;
import au.edu.anu.rscs.aot.util.IntegerRange;
import au.edu.anu.twapps.mm.visualGraph.VisualEdge;
import au.edu.anu.twapps.mm.visualGraph.VisualNode;
import fr.cnrs.iees.graph.impl.TreeGraph;
import fr.cnrs.iees.graph.impl.TreeGraphNode;

/**
 * 
 * Interface to provide a wrapper class to manage the currently selected node
 * for editing for the graph structure editor.
 * 
 * @author Ian Davies - 10 Jan. 2019
 */
public interface VisualNodeEditable {

	/**
	 * The underlying {@link VisualNode}. Ideally this should not be exposed!
	 * 
	 * @return The {@link VisualNode} currently selected for editing.
	 */
	public VisualNode visualNode();

	/**
	 * The layout graph. Ideally this should not be exposed!
	 * 
	 * @return The layout graph.
	 */
	public TreeGraph<VisualNode, VisualEdge> visualGraph();

	/* true if the this node can have more children of this label */
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
	 * @return List of {@link VisualEdge} out-edges.
	 */
	public Iterable<VisualEdge> getOutEdges();

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
	public boolean hasOutEdgeTo(VisualNode endNode, String edgeLabel);

	/**
	 * Get a list of all {@link VisualNode}s which have edges to them from the
	 * currently selected node.
	 * 
	 * @return List of {@link VisualNode}s than satisfy (can be empty).
	 */
	public Iterable<VisualNode> getOutNodes();

	/**
	 * Query to ask if the currently selected node is referenced in the given table
	 * of parents.
	 * 
	 * @param parents {@link StringTable}
	 * @return true if reference found, false otherwise.
	 */
	public boolean references(StringTable parents);

//	public String extractParentReference(StringTable parents);

}
