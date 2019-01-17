package au.edu.anu.twapps.mm.visualGraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import au.edu.anu.rscs.aot.AotException;
import au.edu.anu.rscs.aot.collections.QuickListOfLists;
import au.edu.anu.rscs.aot.graph.AotEdge;
import au.edu.anu.rscs.aot.graph.AotNode;
import au.edu.anu.rscs.aot.graph.property.PropertyKeys;
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

public class VisualGraph
		implements Tree<VisualNode>, Graph<VisualNode, VisualEdge>, NodeFactory, EdgeFactory, TreeNodeFactory {

	private Set<VisualNode> nodes;
	private VisualNode root;
	private PropertyKeys nodeKeys;
	private final static String vnx = "x";
	private final static String vny = "y";
	private final static String vnLabel = "label";
	private final static String vnSymbol = "symbol";

	private PropertyKeys edgeKeys;
	private final static String veText = "text";
	private final static String veSymbol = "symbol";

	public VisualGraph(Iterable<VisualNode> list) {
		super();
		this.nodes = new HashSet<VisualNode>();
		this.root = null;
		this.nodeKeys = new PropertyKeys(vnx, vny, vnLabel, vnSymbol);
		this.edgeKeys = new PropertyKeys(veText, veSymbol);
		for (VisualNode n : list)
			nodes.add(n);
		if (list.iterator().hasNext())
			root = list.iterator().next();
	}

	protected VisualGraph(PropertyKeys keys) {
		this(new ArrayList<VisualNode>());
	}

	public VisualGraph(VisualNode root) {
		this(new ArrayList<VisualNode>());
		this.root = root;
		insertOnlyChildren(root, nodes);
	}

	@Override
	public Iterable<VisualNode> leaves() {
		List<VisualNode> result = new ArrayList<>(nodes.size());
		for (VisualNode n : nodes)
			if (n.isLeaf())
				result.add(n);
		return result;
	}

	private void insertOnlyChildren(TreeNode parent, Collection<VisualNode> list) {
		for (TreeNode child : parent.getChildren()) {
			list.add((VisualNode) child);
			insertOnlyChildren(child, list);
		}
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
	public boolean contains(VisualNode n) {
		return nodes.contains(n);
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

	@Override
	public Iterable<VisualNode> findNodesByReference(String reference) {
		List<VisualNode> found = new ArrayList<>(nodes.size()); // this may be a bad idea for big graphs
		for (VisualNode n : nodes)
			if (Tree.matchesReference(n, reference))
				found.add(n);
		return found;
	}

	@Override
	public int maxDepth() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int minDepth() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public VisualNode root() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Tree<VisualNode> subTree(VisualNode parent) {
		return new VisualGraph(parent);
	}

	@Override
	public VisualNode makeTreeNode(TreeNode parent, String label, String name, SimplePropertyList props) {
		VisualNode node = new VisualNode(label, name, new SharedPropertyListImpl(nodeKeys), this);
		if (!nodes.add(node)) {
//			log.warning(()->"Duplicate Node insertion: "+node.toDetailedString());
			return null;
		} else {
			node.setParent(parent);
			if (parent != null)
				parent.addChild(node);
			return node;
		}
	}

	@Override
	public TreeNode makeTreeNode(TreeNode parent, String label, String name) {
		return makeTreeNode(parent, label, name, null);
	}

	@Override
	public Edge makeEdge(Node arg0, Node arg1, String arg2, String arg3, ReadOnlyPropertyList arg4) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node makeNode(String arg0, String arg1, ReadOnlyPropertyList arg2) {
		// TODO Auto-generated method stub
		return null;
	}

}
