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

import java.util.Collection;
import java.util.List;
import java.util.Map;

import au.edu.anu.rscs.aot.collections.tables.StringTable;
import au.edu.anu.twapps.exceptions.TwAppsException;
import au.edu.anu.twcore.archetype.PrimaryTreeLabels;
import au.edu.anu.twcore.root.EditableFactory;
import au.edu.anu.twcore.root.TwConfigFactory;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.graph.NodeFactory;
import fr.cnrs.iees.graph.TreeNode;
import fr.cnrs.iees.graph.impl.ALEdge;
import fr.cnrs.iees.graph.impl.TreeGraph;
import fr.cnrs.iees.graph.impl.TreeGraphDataNode;
import fr.cnrs.iees.graph.impl.TreeGraphNode;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.ExtendablePropertyList;
import fr.cnrs.iees.properties.ReadOnlyPropertyList;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.SharedPropertyListImpl;
import fr.cnrs.iees.twcore.constants.ConfigurationReservedNodeId;
import fr.ens.biologie.generic.SaveableAsText;
import fr.ens.biologie.generic.utils.Duple;

import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;

/**
 * Author Ian Davies - 11 Jul 2019
 * <p>
 * Visualization of configuration graph nodes constructed by the
 * {@link VisualGraphFactory}.
 */
public class VisualNode extends TreeGraphDataNode implements VisualKeys, SaveableAsText {

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
	 * @param gfactory The graph factory ({@link VisualGraphFactory})
	 */
	public VisualNode(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	/**
	 * @param id      The unique {@link Identity} of this node.
	 * @param factory The graph factory ({@link VisualGraphFactory})
	 */
	public VisualNode(Identity id, GraphFactory factory) {
		super(id, new SharedPropertyListImpl(VisualGraphFactory.getNodeKeys()), factory);
	}

	/**
	 * @param id      The unique {@link Identity} of this node.
	 * @param props   Node property list.
	 * @param factory The graph factory ({@link VisualGraphFactory})
	 */
	public VisualNode(Identity id, ReadOnlyPropertyList props, VisualGraphFactory factory) {
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
		if (properties().getPropertyValue(vnVisible) == null)
			properties().setProperty(vnVisible, true);
		return (Boolean) properties().getPropertyValue(vnVisible);
	}

	/**
	 * Setter for the visible property of the VisualNode.
	 * 
	 * @param value true if visible, false otherwise.
	 */
	public void setVisible(boolean value) {
		properties().setProperty(vnVisible, value);
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
	public Collection<VisualNode> getChildren() {
		return (Collection<VisualNode>) super.getChildren();
	}

	@Override
	public VisualNode getParent() {
		return (VisualNode) super.getParent();
	}

	/**
	 * Setter for the collapsed property.
	 * 
	 * @param b true or false.
	 */
	public void setCollapse(boolean b) {
		properties().setProperty(vnCollapsed, b);
	}

	/**
	 * Setter for the category property. Nodes are coloured according the the
	 * category (in this case the sub-tree) they belong to.
	 */
	public void setCategory() {
		if (PrimaryTreeLabels.contains(configNode.classId()))
			properties().setProperty(vnCategory, configNode.classId());
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
	private void setCategory(VisualNode parent) {
		if (parent != null) {
			if (PrimaryTreeLabels.contains(parent.configNode.classId()))
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
		properties().setProperty(vnCategory, category);
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
		properties().setProperty(vnx, x);
	}

	/**
	 * Setter to wrap the node's y position property.
	 * 
	 * @param y Y position.
	 */
	public void setY(double y) {
		properties().setProperty(vny, y);
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
		return (Double) properties().getPropertyValue(vnx);
	}

	/**
	 * Getter for the node's y property.
	 * 
	 * @return Y position.
	 */
	public double getY() {
		return (Double) properties().getPropertyValue(vny);
	}

	/**
	 * Set the implementation specific symbol drawing object.
	 * 
	 * @param symbol The symbol drawing object.
	 * @throws TwAppsException If symbol has already been set.
	 */
	private void setSymbol(Object symbol) throws TwAppsException {
		if (vnSymbol != null)
			throw new TwAppsException("Attempt to overwrite node symbol " + id());
		vnSymbol = symbol;
	}

	private void setText(Object text) throws TwAppsException {
		if (vnText != null)
			throw new TwAppsException("Attempt to overwrite node text " + id());
		vnText = text;
	}

	/**
	 * Set drawing objects for this node.
	 * 
	 * @param c The symbol object.
	 * @param t The text object.
	 * @throws TwAppsException If objects have already been set.
	 */
	public void setVisualElements(Object c, Object t) throws TwAppsException {
		setSymbol(c);
		setText(t);
	}

	/**
	 * Set the drawing objects for the parent-child edge (maintained by the child).
	 * 
	 * @param line      The edge drawing object
	 * @param arrowhead The arrowhead drawing object.
	 * @throws TwAppsException If the objects already exist.
	 */
	public void setParentLine(Object line, Object arrowhead) throws TwAppsException {
		if (vnParentLine != null)
			throw new TwAppsException("Attempt to overwrite line to parent line " + id());
		vnParentLine = line;
		if (vnArrowhead != null)
			throw new TwAppsException("Attempt to overwrite arrowhead of parent line " + id());
		vnArrowhead = arrowhead;
	}

	/**
	 * Remove drawing objects for the parent-child edge.
	 * 
	 * @throws TwAppsException If objects do not exist.
	 */
	public void removeParentLine() throws TwAppsException {
		if (vnParentLine != null)
			vnParentLine = null;
		else
			throw new TwAppsException("Attempt to remove non-existant line to parent line " + id());
		if (vnArrowhead != null)
			vnArrowhead = null;
		else
			throw new TwAppsException("Attempt to remove non-existant arrowhead of parent line " + id());

	}

	/**
	 * Getter for the category of this node. Nodes are coloured according the the
	 * category (in this case the sub-tree) they belong to.
	 * 
	 * @return The category.
	 */
	public String getCategory() {
		return (String) properties().getPropertyValue(vnCategory);
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
		return (Boolean) properties().getPropertyValue(vnCollapsed);
	}

	/**
	 * @return true if this node is not collapsed and has children that are
	 *         collapse, false otherwise.
	 */
	public boolean hasCollaspedChild() {
		if (isCollapsed())
			return false;
		for (VisualNode n : getChildren()) {
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
		for (VisualNode n : getChildren()) {
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
		return (StringTable) properties().getPropertyValue(vnParentRef);
	}

	/**
	 * Setter for the parent table. This is used to provide correct options to the
	 * user when a parent-child edge is to been restored after having been removed.
	 * 
	 * @param table {@link StringTable} of potential parents.
	 */
	public void setParentRef(StringTable table) {
		properties().setProperty(vnParentRef, table);
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
					VisualEdge vEdge = (VisualEdge) edge;
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
	 * @return The newly constructed {@link VisualNode}.
	 */
	public VisualNode newChild(String label, String proposedId) {
		NodeFactory cf = configNode.factory();
		TreeGraphDataNode cChild = (TreeGraphDataNode) cf.makeNode(cf.nodeClass(label), proposedId);
		cChild.connectParent(configNode);
		proposedId = cChild.id();
		VisualNode vChild = (VisualNode) factory().makeNode(proposedId);
		vChild.connectParent(this);
		vChild.setConfigNode(cChild);
		if (!cChild.id().equals(vChild.id()))
			throw new TwAppsException("Ids must match -[config: " + cChild.id() + "; visual: " + vChild.id());
		return vChild;
	}

	/**
	 * Construct a new cross-link edge.
	 * 
	 * @param id    Unique name of the edge
	 * @param label The edge label (aka classId)
	 * @param vEnd  The end node.
	 * @return The new {@link VisualEdge}
	 */
	public VisualEdge newEdge(String id, String label, VisualNode vEnd) {
		VisualGraphFactory vf = (VisualGraphFactory) factory();
		VisualEdge result = vf.makeEdge(this, vEnd, id);
		id = result.id();

		TreeGraphNode cEnd = vEnd.configNode;
		TwConfigFactory cf = (TwConfigFactory) configNode.factory();
		ALEdge cEdge = (ALEdge) cf.makeEdge(cf.edgeClass(label), configNode, cEnd, id);
		result.setConfigEdge(cEdge);
		result.setVisible(true);
		if (!cEdge.id().equals(result.id()))
			throw new TwAppsException("Ids must match -[config: " + cEdge.id() + "; visual: " + result.id());
		return result;
	}

	/**
	 * Set this node as parent to the given child node.
	 * 
	 * @param vChild The child node.
	 */
	public void reconnectChild(VisualNode vChild) {
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
	public void setupParentReference(Map<String, List<StringTable>> map) {
		setupParentReference(this, map);
	}

	private static void setupParentReference(VisualNode parent, Map<String, List<StringTable>> map) {
		if (parent == null)
			throw new TwAppsException("Parent is null.");
		if (map == null)
			throw new TwAppsException(
					"Map is null when processing parent " + parent.getDisplayText(ElementDisplayText.RoleName));
		List<StringTable> parentList = map.get(parent.configNode.classId());
		if (parentList == null)
			throw new TwAppsException("Archetype error: ParentList is null for parent "
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
		for (VisualNode child : parent.getChildren())
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
