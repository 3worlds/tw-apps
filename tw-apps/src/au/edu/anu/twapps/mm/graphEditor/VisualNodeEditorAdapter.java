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
import au.edu.anu.twapps.mm.layoutGraph.LayoutEdge;
import au.edu.anu.twapps.mm.layoutGraph.LayoutNode;
import au.edu.anu.twcore.archetype.TWA;
import fr.cnrs.iees.omugi.OmugiClassLoader;
import fr.cnrs.iees.omugi.graph.Direction;
import fr.cnrs.iees.omugi.graph.TreeNode;
import fr.cnrs.iees.omugi.graph.impl.ALEdge;
import fr.cnrs.iees.omugi.graph.impl.ALNode;
import fr.cnrs.iees.omugi.graph.impl.TreeGraph;
import fr.cnrs.iees.omugi.graph.impl.TreeGraphNode;
import fr.cnrs.iees.omugi.identity.Identity;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.*;

/**
 * @author Ian Davies - 10 Jan. 2019
 */
public class VisualNodeEditorAdapter implements //
		VisualNodeEditor {
	private LayoutNode visualNode;
	private TreeGraph<LayoutNode, LayoutEdge> visualGraph;

	/**
	 * @param visualNode  Node for editing.
	 * @param visualGraph The layout graph.
	 */
	public VisualNodeEditorAdapter(LayoutNode visualNode, TreeGraph<LayoutNode, LayoutEdge> visualGraph) {
		this.visualNode = visualNode;
		this.visualGraph = visualGraph;
	}

	@Override
	public LayoutNode visualNode() {
		return visualNode;
	}

	@Override
	public TreeGraph<LayoutNode, LayoutEdge> visualGraph() {
		return visualGraph;
	}

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

//	@Override
//	public String extractParentReference(StringTable parents) {
//		TreeNode node = visualNode.configNode();
//		for (int i = 0; i < parents.size(); i++)
//			if (VisualNode.referencedBy(node, parents.getWithFlatIndex(i)))
//				return parents.getWithFlatIndex(i);
//
//		return null;
//	}

}
