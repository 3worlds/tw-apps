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

package au.edu.anu.twapps.mm.layoutGraph;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import fr.cnrs.iees.omugi.collections.tables.StringTable;
import au.edu.anu.twcore.root.EditableFactory;
import au.edu.anu.twcore.root.TwConfigFactory;
import fr.cnrs.iees.omugi.graph.Direction;
import fr.cnrs.iees.omugi.graph.GraphFactory;
import fr.cnrs.iees.omugi.graph.NodeFactory;
import fr.cnrs.iees.omugi.graph.TreeNode;
import fr.cnrs.iees.omugi.graph.impl.ALEdge;
import fr.cnrs.iees.omugi.graph.impl.TreeGraph;
import fr.cnrs.iees.omugi.graph.impl.TreeGraphDataNode;
import fr.cnrs.iees.omugi.graph.impl.TreeGraphNode;
import fr.cnrs.iees.omugi.identity.Identity;
import fr.cnrs.iees.omugi.properties.ExtendablePropertyList;
import fr.cnrs.iees.omugi.properties.ReadOnlyPropertyList;
import fr.cnrs.iees.omugi.properties.SimplePropertyList;
import fr.cnrs.iees.omugi.properties.impl.SharedPropertyListImpl;
import fr.cnrs.iees.twcore.constants.ConfigurationReservedNodeId;
import fr.cnrs.iees.omhtk.SaveableAsText;
import fr.cnrs.iees.omhtk.utils.Duple;

import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;

/**
 * Visualization of configuration graph nodes constructed by the
 * {@link LayoutGraphFactory}.
 * 
 * @author Ian Davies - 11 Jul 2019
 */
public class LayoutNode extends TreeGraphDataNode implements SaveableAsText {
	/**
	 * x location of node in unit space ]0.0..1.0[
	 */
	final static String LOCATION_X = "x";
	/**
	 * y location of node in unit space ]0.0..1.0[
	 */
	final static String LOCATION_Y = "y";
	/**
	 * the sub-tree the node belongs to (for purposes of colour schemes).
	 */
	final static String SUB_TREE = "category";
	/**
	 * True if node is collapsed (hidden).
	 */
	final static String IS_COLLAPSED = "collapsed";
	/**
	 * The parent reference for this node (maintained for purpose of graph editing).
	 */
	final static String PARENT_REFERENCE = "parentRef";
	/**
	 * Node is visible.
	 */
	final static String IS_VISIBLE = "visible";

	private TreeGraphDataNode configNode;
	/**
	 * These Objects are constructed at startup time. Thus, there is no need to have
	 * them stored in a property list. To store them in a property list would cause
	 * problems when reloading the file with the omugiImporter.
	 */
	private Object vnSymbol;
	private Object vnText;
	private Object vnParentLine;
	private Object vnArrowhead;

	/**
	 * @param id       The unique {@link Identity} of this node.
	 * @param props    {@link SimplePropertyList} of node properties.
	 * @param gfactory The graph factory ({@link LayoutGraphFactory})
	 */
	public LayoutNode(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	/**
	 * @param id      The unique {@link Identity} of this node.
	 * @param factory The graph factory ({@link LayoutGraphFactory})
	 */
	public LayoutNode(Identity id, GraphFactory factory) {
		super(id, new SharedPropertyListImpl(LayoutGraphFactory.getNodeKeys()), factory);
	}

	/**
	 * @param id      The unique {@link Identity} of this node.
	 * @param props   Node property list.
	 * @param factory The graph factory ({@link LayoutGraphFactory})
	 */
	public LayoutNode(Identity id, ReadOnlyPropertyList props, LayoutGraphFactory factory) {
		super(id, (SimplePropertyList) props, factory);
	}

	/**
	 * Setter for the configuration node.
	 * 
	 * @param configNode The configuration {@link TreeGraphDataNode}.
	 */
	public void setConfigNode(TreeGraphDataNode configNode) {
		this.configNode = configNode;
	}

	/**
	 * Getter for the configuration node.
	 * 
	 * @return {@link TreeGraphDataNode} configuration node.
	 */
	public TreeGraphDataNode configNode() {
		return configNode;
	}

	/**
	 * Getter for the visible property value of the VisualNode. This property is set
	 * by graph display controls such as collapsing/expanding sub-trees or hiding
	 * all edges.
	 * 
	 * @return true if visible, false otherwise.
	 */
	public boolean isVisible() {
		if (properties().getPropertyValue(IS_VISIBLE) == null)
			properties().setProperty(IS_VISIBLE, true);
		return (Boolean) properties().getPropertyValue(IS_VISIBLE);
	}

	/**
	 * Setter for the visible property of the VisualNode.
	 * 
	 * @param value true if visible, false otherwise.
	 */
	public void setVisible(boolean value) {
		properties().setProperty(IS_VISIBLE, value);
	}

	@Override
	public String toDetailedString() {
		StringBuilder sb = new StringBuilder(super.toDetailedString());
		sb.append(" [");
		sb.append("Config: ");
		if (configNode == null)
			sb.append("null");
		else
			sb.append(configNode.toDetailedString());
		sb.append("]");
		sb.append("]");
		return sb.toString();

	}

	// helper methods
	@SuppressWarnings("unchecked")
	@Override
	public Collection<LayoutNode> getChildren() {
		return (Collection<LayoutNode>) super.getChildren();
	}

	@Override
	public LayoutNode getParent() {
		return (LayoutNode) super.getParent();
	}

	/**
	 * Setter for the collapsed property.
	 * 
	 * @param b true or false.
	 */
	public void setCollapse(boolean b) {
		properties().setProperty(IS_COLLAPSED, b);
	}

	/**
	 * Setter for the category property. Nodes are coloured according the the
	 * category (in this case the sub-tree) they belong to.
	 */
	public void setCategory() {
		if (ConfigSubtreeRootLabels.contains(configNode.classId()))
			properties().setProperty(SUB_TREE, configNode.classId());
		else
			setCategory(getParent());
	}

	/**
	 * Setter for the category property. Nodes are coloured according the the
	 * category (in this case the sub-tree) they belong to. This setter inherited
	 * category of parent if present.
	 * 
	 * @param parent Parent whose category is to be inherited.
	 */
	private void setCategory(LayoutNode parent) {
		if (parent != null) {
			if (ConfigSubtreeRootLabels.contains(parent.configNode.classId()))
				setCategory(parent.configNode.classId());
			else
				setCategory(parent.getParent());
		}
	}

	/**
	 * Setter for the category property. Nodes are coloured according the the
	 * category (in this case the sub-tree) they belong to.
	 * 
	 * @param category Category value to set in the node's property list.
	 */
	private void setCategory(String category) {
		properties().setProperty(SUB_TREE, category);
	}

	/**
	 * Return the text to display given the display option.
	 * 
	 * @param option the {@link ElementDisplayText}.
	 * @return string to display.
	 */
	public String getDisplayText(ElementDisplayText option) {
		switch (option) {
		case RoleName: {
			return configNode.toShortString();
		}
		case Role: {
			return configNode.classId();
		}
		case Name: {
			return configNode.id();
		}
		default: {
			return "";
		}
		}

	}

	/**
	 * Setter to wrap the node's x position property.
	 * 
	 * @param x X position.
	 */
	public void setX(double x) {
		properties().setProperty(LOCATION_X, x);
	}

	/**
	 * Setter to wrap the node's y position property.
	 * 
	 * @param y Y position.
	 */
	public void setY(double y) {
		properties().setProperty(LOCATION_Y, y);
	}

	/**
	 * Setter for the node's x,y position.
	 * 
	 * @param x X position.
	 * @param y Y position.
	 */
	public void setPosition(double x, double y) {
		setX(x);
		setY(y);
	}

	/**
	 * Getter for the node's x property.
	 * 
	 * @return X position.
	 */
	public double getX() {
		return (Double) properties().getPropertyValue(LOCATION_X);
	}

	/**
	 * Getter for the node's y property.
	 * 
	 * @return Y position.
	 */
	public double getY() {
		return (Double) properties().getPropertyValue(LOCATION_Y);
	}

	/**
	 * Set the implementation specific symbol drawing object.
	 * 
	 * @param symbol The symbol drawing object.
	 * @throws IllegalStateException If symbol has already been set.
	 */
	private void setSymbol(Object symbol) {
		if (vnSymbol != null)
			throw new IllegalStateException("Attempt to overwrite node symbol " + id());
		vnSymbol = symbol;
	}

	private void setText(Object text) {
		if (vnText != null)
			throw new IllegalStateException("Attempt to overwrite node text " + id());
		vnText = text;
	}

	/**
	 * Set drawing objects for this node.
	 * 
	 * @param c The symbol object.
	 * @param t The text object.
	 */
	public void setVisualElements(Object c, Object t) {
		setSymbol(c);
		setText(t);
	}

	/**
	 * Set the drawing objects for the parent-child edge (maintained by the child).
	 * 
	 * @param line      The edge drawing object
	 * @param arrowhead The arrowhead drawing object.
	 * @throws IllegalStateException If the objects already exist.
	 */
	public void setParentLine(Object line, Object arrowhead) {
		if (vnParentLine != null)
			throw new IllegalStateException("Attempt to overwrite line to parent line " + id());
		vnParentLine = line;
		if (vnArrowhead != null)
			throw new IllegalStateException("Attempt to overwrite arrowhead of parent line " + id());
		vnArrowhead = arrowhead;
	}

	/**
	 * Remove drawing objects for the parent-child edge.
	 * 
	 * @throws NullPointerException If objects do not exist.
	 */
	public void removeParentLine() {
		if (vnParentLine != null)
			vnParentLine = null;
		else
			throw new NullPointerException("Attempt to remove non-existant line to parent line " + id());
		if (vnArrowhead != null)
			vnArrowhead = null;
		else
			throw new NullPointerException("Attempt to remove non-existant arrowhead of parent line " + id());

	}

	/**
	 * Getter for the category of this node. Nodes are coloured according the the
	 * category (in this case the sub-tree) they belong to.
	 * 
	 * @return The category.
	 */
	public String getCategory() {
		return (String) properties().getPropertyValue(SUB_TREE);
	}

	/**
	 * Getter for the node drawing object.
	 * 
	 * @return the node drawing object.
	 */
	public Object getSymbol() {
		return vnSymbol;
	}

	/**
	 * Getter for the node text drawing object.
	 * 
	 * @return the node text drawing object.
	 */
	public Object getText() {
		return vnText;
	}

	/**
	 * Getter for the parent-child edge drawing objects.
	 * 
	 * @return duple of line and arrowhead drawing objects.
	 */
	public Duple<Object, Object> getParentLine() {
		return new Duple<Object, Object>(vnParentLine, vnArrowhead);
	}

	/**
	 * Getter for the isCollapse node property.
	 * 
	 * @return true if collapsed, false otherwise.
	 */
	public boolean isCollapsed() {
		return (Boolean) properties().getPropertyValue(IS_COLLAPSED);
	}

	/**
	 * @return true if this node is not collapsed and has children that are
	 *         collapse, false otherwise.
	 */
	public boolean hasCollapsedChild() {
		if (isCollapsed())
			return false;
		for (LayoutNode n : getChildren()) {
			if (n.isCollapsed())
				return true;
		}
		return false;
	}

	/**
	 * @return true if this node is not collapsed but has at least one child that is
	 *         not collapsed.
	 */
	public boolean hasUncollapsedChildren() {
		if (isCollapsed())
			return false;
		for (LayoutNode n : getChildren()) {
			if (!n.isCollapsed())
				return true;
		}
		return false;
	}

	/**
	 * Getter for the parent table. This is used to provide correct options to the
	 * user when a parent-child edge is to been restored after having been removed.
	 * 
	 * @return the table {@link StringTable} of potential parents.
	 */
	public StringTable parentTable() {
		return (StringTable) properties().getPropertyValue(PARENT_REFERENCE);
	}

	/**
	 * Setter for the parent table. This is used to provide correct options to the
	 * user when a parent-child edge is to been restored after having been removed.
	 * 
	 * @param table {@link StringTable} of potential parents.
	 */
	public void setParentRef(StringTable table) {
		properties().setProperty(PARENT_REFERENCE, table);
	}

	/**
	 * Getter for the property list of the underlying configuration graph node.
	 * 
	 * @return The {@link ExtendablePropertyList}.
	 */
	private ExtendablePropertyList configProperties() {
		return (ExtendablePropertyList) configNode.properties();
	}

	/**
	 * Getter for the property list of the underlying configuration graph node.
	 * 
	 * @return The {@link SimplePropertyList}.
	 */
	public SimplePropertyList cProperties() {
		return configNode.properties();
	}

	/**
	 * Add a property to the configuration node's {@link ExtendablePropertyList}.
	 * This is to allow optional properties to be added or removed from the node.
	 * 
	 * @param key   Property key.
	 * @param value Property value.
	 */
	public void addProperty(String key, Object value) {
		configProperties().addProperty(key, value);
	}

//	public void addProperty(String key) {
//		configProperties().addProperty(key);
//	}

	/**
	 * Query to check of the underlying configuration node has a property with the
	 * given key.
	 * 
	 * @param key Property key
	 * @return true if present, false otherwise.
	 */
	public boolean configHasProperty(String key) {
		return configNode.properties().hasProperty(key);
	};

	/**
	 * Getter for the property value of the underlying configuration node.
	 * 
	 * @param key The property key.
	 * @return the value.
	 */
	public Object configGetPropertyValue(String key) {
		return configNode.properties().getPropertyValue(key);
	}

	/**
	 * Search the configuration graph to match this node with the configuration
	 * node. They are matched by ids.
	 * 
	 * @param configGraph The configuration graph.
	 */
	public void shadowElements(TreeGraph<TreeGraphDataNode, ALEdge> configGraph) {
		for (TreeGraphDataNode cNode : configGraph.nodes()) {
			if (cNode.id().equals(id())) {
				configNode = cNode;
				for (ALEdge edge : edges(Direction.OUT)) {
					LayoutEdge vEdge = (LayoutEdge) edge;
					vEdge.shadowElements(configNode);
				}
				return;

			}
		}
	}

	/**
	 * Remove the layout node and the underlying configuration node from both
	 * graphs. This includes all edges and ids.
	 */
	public void remove() {
		EditableFactory vf = (EditableFactory) factory();
		EditableFactory cf = (EditableFactory) configNode.factory();
		vf.expungeNode(this);
		cf.expungeNode(configNode);
		disconnect();
		configNode.disconnect();
	}

	/**
	 * Construct a new child node for both the layout and the configuration graphs.
	 * 
	 * @param label      The child label (aka classId).
	 * @param proposedId The proposed id. The given value may be changed to ensure
	 *                   uniqueness.
	 * @return The newly constructed {@link LayoutNode}.
	 * 
	 * @throws IllegalStateException if visual and configuration node Id's don't
	 *                               match.
	 */
	public LayoutNode newChild(String label, String proposedId) {
		NodeFactory cf = configNode.factory();
		TreeGraphDataNode cChild = (TreeGraphDataNode) cf.makeNode(cf.nodeClass(label), proposedId);
		cChild.connectParent(configNode);
		proposedId = cChild.id();
		LayoutNode vChild = (LayoutNode) factory().makeNode(proposedId);
		vChild.connectParent(this);
		vChild.setConfigNode(cChild);
		if (!cChild.id().equals(vChild.id()))
			throw new IllegalStateException("Ids must match -[config: " + cChild.id() + "; visual: " + vChild.id());
		return vChild;
	}

	/**
	 * Construct a new cross-link edge.
	 * 
	 * @param id    Unique name of the edge
	 * @param label The edge label (aka classId)
	 * @param vEnd  The end node.
	 * @return The new {@link LayoutEdge}
	 * 
	 * Throws {@link IllegalStateException} if visual and configuration Id's don't match.
	 */
	public LayoutEdge newEdge(String id, String label, LayoutNode vEnd) {
		LayoutGraphFactory vf = (LayoutGraphFactory) factory();
		LayoutEdge result = vf.makeEdge(this, vEnd, id);
		id = result.id();

		TreeGraphNode cEnd = vEnd.configNode;
		TwConfigFactory cf = (TwConfigFactory) configNode.factory();
		ALEdge cEdge = (ALEdge) cf.makeEdge(cf.edgeClass(label), configNode, cEnd, id);
		result.setConfigEdge(cEdge);
		result.setVisible(true);
		if (!cEdge.id().equals(result.id()))
			throw new IllegalStateException("Ids must match -[config: " + cEdge.id() + "; visual: " + result.id());
		return result;
	}

	/**
	 * Set this node as parent to the given child node.
	 * 
	 * @param vChild The child node.
	 */
	public void reconnectChild(LayoutNode vChild) {
		TreeGraphNode cChild = vChild.configNode;
		configNode.connectChild(cChild);
		connectChild(vChild);
	}

	/**
	 * @return true if this is an immutable node in the configuration graph scheme.
	 */
	public boolean isPredefined() {
		return ConfigurationReservedNodeId.isPredefined(id());
	}

	/**
	 * returns true of this node has the label '3worlds'.
	 */
	@Override
	public boolean isRoot() {
		return configNode.classId().equals(N_ROOT.label());
	}

	/**
	 * Builds a look-up table of node labels and the parents options they can have.
	 * 
	 * @param map The look-up table.
	 */
	public void setParentReference(Map<String, List<StringTable>> map) {
		setupParentReference(this, map);
	}

	private static void setupParentReference(LayoutNode parent, Map<String, List<StringTable>> map) {
		if (parent == null)
			throw new NullPointerException("Parent is null.");
		if (map == null)
			throw new NullPointerException(
					"Map is null when processing parent " + parent.getDisplayText(ElementDisplayText.RoleName));
		List<StringTable> parentList = map.get(parent.configNode.classId());
		if (parentList == null)
			throw new NullPointerException("Archetype error: ParentList is null for parent "
					+ parent.getDisplayText(ElementDisplayText.RoleName));

		/**
		 * Check each table and take the first that corresponds to the current set of
		 * parents. If none (e.g. root) the entry will be null. It follows that this
		 * method can only be used for a tree with a single root.
		 */
		for (StringTable table : parentList) {
			if (parent.treeMatchesTable(table)) {
				parent.setParentRef(table);
				break;
			}
		}
		// This is the empty parent table for the root!
		if (parent.parentTable() == null)
			parent.setParentRef(parentList.get(0));
		for (LayoutNode child : parent.getChildren())
			setupParentReference(child, map);

	}

	/**
	 * Check if the place of this node in it's tree matches to parents listed in the
	 * table.
	 * 
	 * @param parents {@link StringTable} of parent references.
	 * @return true if match found, false otherwise.
	 */
	public boolean treeMatchesTable(StringTable parents) {
		TreeNode node = configNode().getParent();
		for (int i = 0; i < parents.size(); i++) {
			if (referencedBy(node, parents.getWithFlatIndex(i)))
				return true;
		}
		return false;
	}

	/**
	 * This should probably be private and other classes use treeMatchesTable().
	 * 
	 * @param node The node to check
	 * @param ref  The string parent reference.
	 * @return true if match, flase otherwise.
	 */
	public static boolean referencedBy(TreeNode node, String ref) {
		if (ref == null)
			return false;
		String[] parts = ref.split("" + SLASH);
		for (int i = parts.length - 1; i >= 0; i--) {
			if (node != null) {
				String[] pair = parts[i].split("" + COLON);

				if (!node.classId().equals(pair[0]))
					return false;
				else if (pair.length > 1) {
					if (!node.id().equals(pair[1]))
						return false;
				}
				node = node.getParent();
			} else
				return false;
		}
		return true;
	}

}
