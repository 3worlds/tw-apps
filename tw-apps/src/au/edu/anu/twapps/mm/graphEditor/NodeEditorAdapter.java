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

import static au.edu.anu.qgraph.queries.CoreQueries.*;
import static au.edu.anu.qgraph.queries.base.SequenceQuery.*;
import java.util.*;

import fr.cnrs.iees.omugi.collections.tables.StringTable;
import au.edu.anu.omhtk.util.IntegerRange;
import au.edu.anu.twapps.mm.layoutGraph.*;
import au.edu.anu.twcore.archetype.TWA;
import fr.cnrs.iees.omugi.OmugiClassLoader;
import fr.cnrs.iees.omugi.graph.Direction;
import fr.cnrs.iees.omugi.graph.TreeNode;
import fr.cnrs.iees.omugi.graph.impl.*;
import fr.cnrs.iees.omugi.identity.Identity;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.*;

/**
 * @author Ian Davies - 10 Jan. 2019
 */
public class NodeEditorAdapter implements //
		NodeEditor {
	private final LayoutNode visualNode;

	/**
	 * @param visualNode  The node which is the focus of the editing operation.
	 */
	public NodeEditorAdapter(LayoutNode visualNode) {
		this.visualNode = visualNode;
	}

	@Override
	public LayoutNode layoutNode() {
		return visualNode;
	}

//	@Override
//	public TreeGraph<LayoutNode, LayoutEdge> visualGraph() {
//		return visualGraph;
//	}

	@Override
	public boolean moreChildrenAllowed(IntegerRange range, String childLabel) {
		List<LayoutNode> lst = new ArrayList<>();
		for (LayoutNode child : visualNode.getChildren()) {
			String label = child.configNode().classId();
			if (label.equals(childLabel))
				lst.add(child);
		}
		return range.inRange(lst.size() + 1);
	}

	@Override
	public boolean hasOutEdges() {
		return visualNode.edges(Direction.OUT).iterator().hasNext();
	}

	@Override
	public String proposeAnId(String proposedName) {
		Identity id = visualNode.scope().newId(false, proposedName);
		return id.id();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<? extends TreeGraphNode> getSubClass() {
		ClassLoader classLoader = OmugiClassLoader.getAppClassLoader();
		if (visualNode.configHasProperty(TWA.SUBCLASS)) {
			String result = (String) visualNode.configGetPropertyValue(TWA.SUBCLASS);
			try {
				return (Class<? extends TreeGraphNode>) Class.forName(result, true, classLoader);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		} else {
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Iterable<LayoutEdge> getOutEdges() {
		return (Iterable<LayoutEdge>) visualNode.edges(Direction.OUT);
	}

	private static boolean ignoreDuplicateEdgesBetween(TreeGraphNode start, TreeGraphNode end, String edgeLabel) {
		if (end.classId().equals(N_DIMENSIONER.label()) && start.classId().equals(N_TABLE.label())
				&& E_SIZEDBY.label().equals(edgeLabel))
			return true;
		return false;
	}

	@Override
	public boolean hasOutEdgeTo(LayoutNode vEnd, String edgeLabel) {

		TreeGraphNode cStart = visualNode.configNode();
		TreeGraphNode cEnd = vEnd.configNode();
		if (ignoreDuplicateEdgesBetween(cStart, cEnd, edgeLabel))
			return false;
		for (ALEdge cEdge : cStart.edges(Direction.OUT)) {
			ALNode cEndNode = cEdge.endNode();
			if (cEndNode.id().equals(cEnd.id()))
				if ((cEdge.classId()).equals(edgeLabel))
					return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Iterable<LayoutNode> getOutNodes() {
		return (Iterable<LayoutNode>) get(visualNode.edges(Direction.OUT), selectZeroOrMany(), edgeListEndNodes());
	}

	@Override
	public boolean references(StringTable parents) {
		TreeNode node = visualNode.configNode();
		for (int i = 0; i < parents.size(); i++)
			if (LayoutNode.referencedBy(node, parents.getWithFlatIndex(i)))
				return true;
		return false;
	}

	@Override
	public String getLabel() {
		return visualNode.configNode().classId();
	}

	@Override
	public String getName() {
		return visualNode.configNode().id();
	}

	@Override
	public boolean is3Wroot() {
		return visualNode.configNode().classId().equals(N_ROOT.label());
	}

	@Override
	public String toString() {
		return visualNode.configNode().toShortString();
	}

	@Override
	public boolean hasProperty(String key) {
		return visualNode.configNode().properties().hasProperty(key);
	}

	@Override
	public boolean isPredefined() {
		return visualNode.isPredefined();
	}

	@Override
	public StringTable getParentTable() {
		return visualNode.parentTable();
	}

	@Override
	public Collection<LayoutNode> getChildren() {
		return visualNode.getChildren();
	}

	@Override
	public boolean isCollapsed() {
		return visualNode.isCollapsed();
	}

	@Override
	public Collection<? extends ALEdge> getConfigOutEdges() {
		return visualNode.configNode().edges(Direction.OUT);
	}

	@Override
	public LayoutEdge newEdge(String name, String label, LayoutNode endNode) {
		return visualNode.newEdge(name, label, endNode);
	}

	@Override
	public LayoutNode newChild(String label, String proposedName) {
		return visualNode.newChild(label, proposedName);
	}

	@Override
	public TreeGraphDataNode getConfigNode() {
		return visualNode.configNode();
	}

	@Override
	public void connectChild(LayoutNode child) {
		visualNode.reconnectChild(child);
	}

	@Override
	public boolean hasChildren() {
		return visualNode.hasChildren();
	}

	@Override
	public boolean hasCollapsedChild() {
		return visualNode.hasCollapsedChild();
	}

	@Override
	public boolean hasUncollapsedChildren() {
		return visualNode.hasUncollapsedChildren();
	}

	@Override
	public void deleteParentLink(LayoutNode child) {
		visualNode.disconnectFrom(child);
		visualNode.configNode().disconnectFrom(child.configNode());
	}

}
