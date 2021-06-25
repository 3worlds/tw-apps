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

	public VisualNode(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	public VisualNode(Identity id, GraphFactory factory) {
		super(id, new SharedPropertyListImpl(VisualGraphFactory.getNodeKeys()), factory);
	}

	public VisualNode(Identity newId, ReadOnlyPropertyList props, VisualGraphFactory factory) {
		super(newId, (SimplePropertyList) props, factory);
	}

	public void setConfigNode(TreeGraphDataNode configNode) {
		this.configNode = configNode;
	}

	public TreeGraphDataNode getConfigNode() {
		return configNode;
	}

	public boolean isVisible() {
		if (properties().getPropertyValue(vnVisible) == null)
			properties().setProperty(vnVisible, true);
		return (Boolean) properties().getPropertyValue(vnVisible);
	}

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

	public void setCollapse(boolean b) {
		properties().setProperty(vnCollapsed, b);
	}

	public void setCategory() {
		if (PrimaryTreeLabels.contains(configNode.classId()))
			properties().setProperty(vnCategory, configNode.classId());
		else
			setCategory(getParent());
	}

	private void setCategory(VisualNode parent) {
		if (parent != null) {
			if (PrimaryTreeLabels.contains(parent.configNode.classId()))
				setCategory(parent.configNode.classId());
			else
				setCategory(parent.getParent());
		}
	}

	private void setCategory(String category) {
		properties().setProperty(vnCategory, category);
	}

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

	public void setX(double x) {
		properties().setProperty(vnx, x);
	}

	public void setY(double y) {
		properties().setProperty(vny, y);
	}

	public void setPosition(double x, double y) {
		setX(x);
		setY(y);
	}

	public double getX() {
		return (Double) properties().getPropertyValue(vnx);
	}

	public double getY() {
		return (Double) properties().getPropertyValue(vny);
	}

	private void setSymbol(Object symbol) {
		if (vnSymbol != null)
			throw new TwAppsException("Attempt to overwrite node symbol " + id());
		vnSymbol = symbol;
	}

	private void setText(Object text) {
		if (vnText != null)
			throw new TwAppsException("Attempt to overwrite node text " + id());
		vnText = text;
	}

	public void setVisualElements(Object c, Object t) {
		setSymbol(c);
		setText(t);
	}

	public void setParentLine(Object line, Object arrowhead) {
		if (vnParentLine != null)
			throw new TwAppsException("Attempt to overwrite line to parent line " + id());
		vnParentLine = line;
		if (vnArrowhead != null)
			throw new TwAppsException("Attempt to overwrite arrowhead of parent line " + id());
		vnArrowhead = arrowhead;
	}

	public void removeParentLine() {
		if (vnParentLine != null)
			vnParentLine = null;
		else
			throw new TwAppsException("Attempt to remove non-existant line to parent line " + id());
		if (vnArrowhead != null)
			vnArrowhead = null;
		else
			throw new TwAppsException("Attempt to remove non-existant arrowhead of parent line " + id());

	}

	public String getCategory() {
		return (String) properties().getPropertyValue(vnCategory);
	}

	public Object getSymbol() {
		return vnSymbol;
	}

	public Object getText() {
		return vnText;
	}

	public Duple<Object, Object> getParentLine() {
		return new Duple<Object, Object>(vnParentLine, vnArrowhead);
	}

	public boolean isCollapsed() {
		return (Boolean) properties().getPropertyValue(vnCollapsed);
	}

	public boolean hasCollaspedChild() {
		if (isCollapsed())
			return false;
		for (VisualNode n : getChildren()) {
			if (n.isCollapsed())
				return true;
		}
		return false;
	}

	public boolean hasUncollapsedChildren() {
		if (isCollapsed())
			return false;
		for (VisualNode n : getChildren()) {
			if (!n.isCollapsed())
				return true;
		}
		return false;
	}

	public StringTable getParentTable() {
		return (StringTable) properties().getPropertyValue(vnParentRef);
	}

	public void setParentRef(StringTable table) {
		properties().setProperty(vnParentRef, table);
	}

	private ExtendablePropertyList getExtendablePropertyList() {
		return (ExtendablePropertyList) configNode.properties();
	}

	public SimplePropertyList cProperties() {
		return configNode.properties();
	}

	public void addProperty(String key, Object value) {
		getExtendablePropertyList().addProperty(key, value);
	}

	public void addProperty(String key) {
		getExtendablePropertyList().addProperty(key);
	}

	public boolean configHasProperty(String key) {
		return configNode.properties().hasProperty(key);
	};

	public Object configGetPropertyValue(String key) {
		return configNode.properties().getPropertyValue(key);
	}

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

	public String cClassId() {
		return configNode.classId();
	}

	public void remove() {
		EditableFactory vf = (EditableFactory) factory();
		EditableFactory cf = (EditableFactory) configNode.factory();
		vf.expungeNode(this);
		cf.expungeNode(configNode);
		disconnect();
		configNode.disconnect();
	}

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

	public void reconnectChild(VisualNode vChild) {
		TreeGraphNode cChild = vChild.configNode;
		configNode.connectChild(cChild);
		connectChild(vChild);
	}

	public boolean isPredefined() {
		return ConfigurationReservedNodeId.isPredefined(id());
	}

	public boolean isRoot() {
		return cClassId().equals(N_ROOT.label());
	}

	public void setupParentReference(Map<String, List<StringTable>> map) {
		setupParentReference(this, map);
	}

	private static void setupParentReference(VisualNode parent, Map<String, List<StringTable>> map) {
		if (parent == null)
			throw new TwAppsException("Parent is null.");
		if (map == null)
			throw new TwAppsException("Map is null when processing parent " + parent.getDisplayText(ElementDisplayText.RoleName));
		List<StringTable> parentList = map.get(parent.cClassId());
		if (parentList == null)
			throw new TwAppsException("ParentList is null for parent " + parent.getDisplayText(ElementDisplayText.RoleName));

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
		if (parent.getParentTable() == null)
			parent.setParentRef(parentList.get(0));
		for (VisualNode child : parent.getChildren())
			setupParentReference(child, map);

	}

	public boolean treeMatchesTable(StringTable parents) {
		TreeNode node = getConfigNode().getParent();
		for (int i = 0; i < parents.size(); i++) {
			if (referencedBy(node, parents.getWithFlatIndex(i)))
				return true;
		}
		return false;
	}

	public static boolean referencedBy(TreeNode node, String ref) {
		if (ref==null)
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
