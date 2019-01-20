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

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import au.edu.anu.rscs.aot.AotException;
import au.edu.anu.rscs.aot.collections.QuickListOfLists;
import au.edu.anu.rscs.aot.graph.AotNode;
import au.edu.anu.rscs.aot.graph.ConfigurableGraph;
import au.edu.anu.rscs.aot.graph.NodeExceptionList;
import au.edu.anu.rscs.aot.graph.NodeInitialiser;
import au.edu.anu.twapps.exceptions.TwAppsException;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.Edge;
import fr.cnrs.iees.graph.EdgeFactory;
import fr.cnrs.iees.graph.Graph;
import fr.cnrs.iees.graph.Node;
import fr.cnrs.iees.graph.NodeFactory;
import fr.cnrs.iees.properties.ExtendablePropertyList;
import fr.cnrs.iees.properties.ReadOnlyPropertyList;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import fr.cnrs.iees.tree.Tree;
import fr.cnrs.iees.tree.TreeNode;
import fr.cnrs.iees.tree.TreeNodeFactory;
import fr.cnrs.iees.tree.impl.DefaultTreeFactory;

public class AotGraph2 implements //
		Graph<AotNode2, AotEdge2>, //
		Tree<AotNode2>, //
		NodeFactory, //
		EdgeFactory, //
		TreeNodeFactory, //
		ConfigurableGraph {

	private Set<AotNode2> nodes;
	private AotNode2 root;

	protected AotGraph2() {
		super();
		this.nodes = new HashSet<>();
	}

	// This constructor is required by the initialisation process
	public AotGraph2(Iterable<AotNode2> nodeList) {
		this();
		for (AotNode2 n: nodeList)
			nodes.add(n);
		// order is undefined so must search
		root = root();
	}

	protected AotGraph2(AotNode2 root) {
		this();
		this.root = root;
		insertChildren(root);
	}

	private void insertChildren(TreeNode parent) {
		for (TreeNode child : parent.getChildren()) {
			nodes.add((AotNode2) child);
			insertChildren(child);
		}
	}

	// ----------------------Graph<AotNode2, AotEdge2>
	@Override
	public Iterable<AotNode2> leaves() {
		List<AotNode2> result = new ArrayList<>(nodes.size());
		for (AotNode2 n : nodes)
			if (n.isLeaf())
				result.add(n);
		return result;
	}

	@Override
	public Iterable<AotNode2> nodes() {
		return nodes;
	}

	@Override
	public int size() {
		return nodes.size();
	}

	@Override
	public boolean contains(AotNode2 arg0) {
		return nodes.contains(arg0);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Iterable<AotEdge2> edges() {
		QuickListOfLists<AotEdge2> edges = new QuickListOfLists<>();
		for (AotNode2 n : nodes)
			edges.addList((Iterable<AotEdge2>) n.getEdges(Direction.OUT));
		return edges;
	}

	@Override
	public Iterable<AotNode2> roots() {
		List<AotNode2> result = new ArrayList<>(nodes.size());
		for (AotNode2 n : nodes)
			if (n.getParent() == null)
				result.add(n);
		return result;
	}

//---------------------------------Tree<AotNode2>
	@Override
	public Iterable<AotNode2> findNodesByReference(String arg0) {
		List<AotNode2> found = new ArrayList<>(nodes.size());
		for (AotNode2 n : nodes)
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
	public AotNode2 root() {
		if (root == null)
			root = findRoot();
		return root;
	}

	private AotNode2 findRoot() {
		List<AotNode2> roots = (List<AotNode2>) roots();
		if (roots.size() == 1)
			return roots.get(0);
		return null;
	}

	@Override
	public Tree<AotNode2> subTree(AotNode2 arg0) {
		return new AotGraph2(arg0);
	}
	// ---------------------------------NodeFactory

	// could have an abstract ancestor class with this as final

	@Override
	public Node makeNode(String arg0, String arg1, ReadOnlyPropertyList arg2) {
		throw new TwAppsException("Attempt to instantiate an AotNode outside of the tree context.");
	}
	// --------------------------------EdgeFactory

	@Override
	public Edge makeEdge(Node start, Node end, String label, String name, ReadOnlyPropertyList properties) {
		if (properties == null)
			properties = new ExtendablePropertyListImpl();
		return new AotEdge2(start, end, label, name, (ExtendablePropertyList) properties, this);
	}
	// --------------------------------TreeNodeFactory

	@Override
	public TreeNode makeTreeNode(TreeNode parent, String label, String name, SimplePropertyList properties) {
		if (properties == null)
			properties = new ExtendablePropertyListImpl();
		AotNode2 node = new AotNode2(label, name, DefaultTreeFactory.makeSimpleTreeNode(null, this),
				(ExtendablePropertyList) properties, this);
		if (!nodes.add(node))
			throw new TwAppsException("Attempt to add duplicate node: " + node.toDetailedString());
		node.setParent(parent);
		if (parent != null)
			parent.addChild(node);
		return node;
	}
	// --------------------------------ConfigurableGraph

	@SuppressWarnings("unchecked")
	@Override
	public NodeExceptionList castNodes() {
		NodeExceptionList castFailList = new NodeExceptionList();
		List<AotNode2> removedNodes = new ArrayList<>(nodes.size());
		List<AotNode2> addedNodes = new ArrayList<>(nodes.size());
		for (AotNode2 n : nodes) {
			try {
				String className;
				className = (String) n.getPropertyValue("class");
//				I dont like this, so try to remove it
//				if (className != null && !n.getLabel().equals("defaultPropertyList")) {				
				if (className != null) {
					AotNode2 newNode = n;
					try {
						ClassLoader c = Thread.currentThread().getContextClassLoader();
						Class<? extends AotNode2> nodeClass = (Class<? extends AotNode2>) Class.forName(className,
								false, c);
						Constructor<? extends AotNode2> nodeConstructor = nodeClass.getConstructor();
						// NOTE: a node constructor always has a factory as argument...
						newNode = nodeConstructor.newInstance(this);
						newNode.setLabel(n.getLabel());
						newNode.setName(n.getName());
						newNode.connectLike(n);
						newNode.setProperties(n);
						n.disconnect();
						removedNodes.add(n);
						addedNodes.add(newNode);
					} catch (Exception e) {
						throw new AotException("Cannot clone " + this + " with class " + className, e);
					}
				}
			} catch (Exception e) {
				castFailList.add(n, e);
//				log.warning("AotGraph: Node " + n + " could not be cast.", e);
			}
		}
		nodes.removeAll(removedNodes);
		nodes.addAll(addedNodes);
		return castFailList;
	}

	@Override
	public NodeExceptionList initialise() {
		// ok huge dump of annotation  here!!
		return null;
//		
//		NodeInitialiser initialiser = new NodeInitialiser(this);
//		initialiser.showInitialisationOrder();
//		return initialiser.initialise();
	}

}
