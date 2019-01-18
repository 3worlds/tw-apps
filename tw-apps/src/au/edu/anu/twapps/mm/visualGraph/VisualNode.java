package au.edu.anu.twapps.mm.visualGraph;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;

import au.edu.anu.rscs.aot.graph.AotNode;
import au.edu.anu.twapps.exceptions.TwAppsException;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.Edge;
import fr.cnrs.iees.graph.impl.SimpleNodeImpl;
import fr.cnrs.iees.properties.PropertyListSetters;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.tree.TreeNode;
import fr.cnrs.iees.tree.TreeNodeFactory;
import fr.cnrs.iees.tree.impl.DefaultTreeFactory;
import fr.ens.biologie.generic.Labelled;
import fr.ens.biologie.generic.Named;
import fr.ens.biologie.generic.NamedAndLabelled;

public class VisualNode extends SimpleNodeImpl implements TreeNode, SimplePropertyList, NamedAndLabelled {
// As for AotNode - difference is this is a shared property list
	
	private String name;
	private String label;
	private SimplePropertyList properties;
	private AotNode configNode;
	private TreeNode treenode;

	protected VisualNode(String label, String name, SimplePropertyList props, VisualGraph factory) {
		super(factory);
		this.name = name;
		this.label = label;
		this.properties = props;
		this.treenode = DefaultTreeFactory.makeSimpleTreeNode(null, factory);
	}
	@Override 
	public String classId() {
		return label;
	}
	@Override
	public String instanceId() {
		return name;
	}

	public void setConfigNode(AotNode configNode) {
		this.configNode = configNode;
	}

	public AotNode getConfigNode() {
		return configNode;
	}

	@Override
	public PropertyListSetters setProperty(String key, Object value) {
		return properties.setProperty(key,value);
	}

	@Override
	public Set<String> getKeysAsSet() {
		return properties.getKeysAsSet();
	}

	@Override
	public Object getPropertyValue(String key) {
		return properties.getPropertyValue(key);
	}

	@Override
	public boolean hasProperty(String key) {
		return properties.hasProperty(key);
	}

	@Override
	public int size() {
		return properties.size();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean hasName(String name) {
		return Objects.equals(this.name, name);
	}

	@Override
	public boolean sameName(Named namedItem) {
		return this.hasName(namedItem.getName());
	}

	/**
	 * NOTE: name can only be set once, since it is used as unique ID in equality tests,
	 * on which sets base their unicity of element constraint.
	 */
	// Programmer's error - should throw exception
	@Override
	public Named setName(String name) {
		if (this.name==null)
			this.name = name;
		else
			throw new TwAppsException("Attempt to rename node "+this.name+" to "+name);
		return this;
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public boolean hasLabel(String label) {
		return Objects.equals(this.label, label);
	}

	@Override
	public boolean sameLabel(Labelled labelledItem) {
		return hasLabel(labelledItem.getLabel());

	}

	@Override
	public Labelled setLabel(String label) {
		if (this.label==null)
			this.label = label;
		else
			throw new TwAppsException("Attempt to relabel node "+this.label+" to "+label);
		return this;
	}

	@Override
	public SimplePropertyList clone() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addChild(TreeNode child) {
		treenode.addChild(child);

	}

	@Override
	public Iterable<TreeNode> getChildren() {
		return treenode.getChildren();
	}

	@Override
	public TreeNode getParent() {
		// TODO Auto-generated method stub
		return treenode.getParent();
	}

	@Override
	public boolean hasChildren() {
		// TODO Auto-generated method stub
		return treenode.hasChildren();
	}

	@Override
	public int nChildren() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setChildren(TreeNode... arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setChildren(Iterable<TreeNode> arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setChildren(Collection<TreeNode> arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setParent(TreeNode parent) {
		if (treenode.getParent()==null)
			treenode.setParent(parent);

	}

	@Override
	public TreeNodeFactory treeNodeFactory() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String toUniqueString() {
		return uniqueId();
	}

	@Override
	public String toDetailedString() {
		StringBuilder sb = new StringBuilder(toUniqueString());
		sb.append("=[");
		if (treenode.getParent()!=null)
			sb.append("↑").append(treenode.getParent().toUniqueString());
		else
			sb.append("ROOT");
		if (treenode.hasChildren()) {
			for (TreeNode n:treenode.getChildren()) {
				sb.append(" ↓").append(n.toUniqueString());
			}
		}
		if (getEdges(Direction.IN).iterator().hasNext()) {
			for (Edge e:getEdges(Direction.IN))
				sb.append(" ←").append(e.startNode().toUniqueString());
		}
		if (getEdges(Direction.OUT).iterator().hasNext()) {
			for (Edge e:getEdges(Direction.OUT))
				sb.append(" →").append(e.endNode().toUniqueString());
		}		
		if (properties.size()>0)
			sb.append(" Prop ").append(properties.toString());
		
		if (this.getConfigNode()!=null)
			sb.append(" configNode: "+getConfigNode().toDetailedString());
		sb.append("]");
		return sb.toString();
	}
//	@Override
//	public String toShortString() {
//		StringBuilder sb = new StringBuilder(toUniqueString());
//		if (getConfigNode()!=null) {
//			sb.append(" hosting ").append(getConfigNode().toShortString());
//		return sb.toString();
//		}		
//	}

}
