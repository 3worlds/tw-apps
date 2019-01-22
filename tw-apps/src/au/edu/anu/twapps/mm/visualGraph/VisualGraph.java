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

package au.edu.anu.twapps.mm.visualGraph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import au.edu.anu.rscs.aot.collections.QuickListOfLists;
import au.edu.anu.rscs.aot.graph.property.PropertyKeys;
import au.edu.anu.twapps.exceptions.TwAppsException;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.Edge;
import fr.cnrs.iees.graph.EdgeFactory;
import fr.cnrs.iees.graph.Graph;
import fr.cnrs.iees.graph.Node;
import fr.cnrs.iees.graph.NodeFactory;
import fr.cnrs.iees.properties.ReadOnlyPropertyList;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.SharedPropertyListImpl;
import fr.cnrs.iees.tree.Tree;
import fr.cnrs.iees.tree.TreeNode;
import fr.cnrs.iees.tree.TreeNodeFactory;
import fr.cnrs.iees.tree.impl.DefaultTreeFactory;

public class VisualGraph implements //
		Graph<VisualNode, VisualEdge>, //
		Tree<VisualNode>, //
		NodeFactory, //
		EdgeFactory, //
		TreeNodeFactory, //
		VisualKeys
/*
 * PropertyListFactory
 */ {
	private Set<VisualNode> nodes;
	private VisualNode root;
	private PropertyKeys nodeKeys;
	private PropertyKeys edgeKeys;

	// Constructors
	public VisualGraph() {
		super();
		this.nodes = new HashSet<>();
		this.nodeKeys = getNodeKeys();
		this.edgeKeys = getEdgeKeys();
	}

	// ------------------- Tree<VisualNode2>
	protected VisualGraph(VisualNode root) {
		this();
		this.root = root;
		insertChildren(root);
	}

	private void insertChildren(TreeNode parent) {
		for (TreeNode child : parent.getChildren()) {
			nodes.add((VisualNode) child);
			insertChildren(child);
		}
	}

	// ------------------Graph<VisualNode2, VisualEdge2>
	@Override
	public boolean contains(VisualNode arg0) {
		return nodes.contains(arg0);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Iterable<VisualEdge> edges() {
		QuickListOfLists<VisualEdge> edges = new QuickListOfLists<>();
		for (VisualNode n : nodes)
			edges.addList((Iterable<VisualEdge>) n.getEdges(Direction.OUT));
		return edges;
	}

	@Override
	public Iterable<VisualNode> roots() {
		List<VisualNode> result = new ArrayList<>(nodes.size());
		for (VisualNode n : nodes)
			if (n.getParent() == null)
				result.add(n);
		return result;
	}

	// ------------------- Tree<VisualNode2>
	@Override
	public Iterable<VisualNode> leaves() {
		List<VisualNode> result = new ArrayList<>(nodes.size());
		for (VisualNode n : nodes)
			if (n.isLeaf())
				result.add(n);
		return result;
	}

	@Override
	public Iterable<VisualNode> nodes() {
		return nodes;
	}

	@Override
	public int size() {
		return nodes.size();
	}

	@Override
	public Iterable<VisualNode> findNodesByReference(String arg0) {
		List<VisualNode> found = new ArrayList<>(nodes.size());
		for (VisualNode n : nodes)
			if (Tree.matchesReference(n, arg0))
				found.add(n);
		return found;
	}

	@Override
	public int maxDepth() {
		throw new TwAppsException("Method not implemented.");
	}

	@Override
	public int minDepth() {
		throw new TwAppsException("Method not implemented.");
	}

	@Override
	public VisualNode root() {
		if (root == null)
			root = findRoot();
		return root;
	}

	private VisualNode findRoot() {
		List<VisualNode> roots = (List<VisualNode>) roots();
		if (roots.size() == 1)
			return roots.get(0);
		return null;
	}

	@Override
	public Tree<VisualNode> subTree(VisualNode arg0) {
		return new VisualGraph(arg0);
	}

// -------------------------------NodeFactory
	@Override
	public Node makeNode(String label, String name, ReadOnlyPropertyList properties) {
		throw new TwAppsException("Attempt to instantiate an VisualNode outside of the tree context.");
	}

	// -------------------------------EdgeFactory

	/*
	 * I hope we can handle null label and edge - nulls are defined as unique and
	 * also setable. If not null they can't be later changed
	 */
	@Override
	public VisualEdge makeEdge(Node start, Node end, String label, String name, ReadOnlyPropertyList properties) {
		if (properties == null)
			properties = new SharedPropertyListImpl(edgeKeys);
		return new VisualEdge(start, end, label, name, (SimplePropertyList) properties, this);
	}


	@Override
	public VisualNode makeTreeNode(TreeNode parent) {
		return makeTreeNode(parent,null,null,null);
	}

	// ------------------------- TreeNodeFactory
	@Override
	public VisualNode makeTreeNode(TreeNode parent, String label, String name, SimplePropertyList properties) {
		if (properties == null)
			properties = new SharedPropertyListImpl(nodeKeys);
		VisualNode node = new VisualNode(label, name, DefaultTreeFactory.makeSimpleTreeNode(null,this),properties, this);
		if (!nodes.add(node))
			throw new TwAppsException("Attempt to add duplicate node: " + node.toDetailedString());
		node.setParent(parent);
		if (parent != null)
			parent.addChild(node);
		return node;
	}
}