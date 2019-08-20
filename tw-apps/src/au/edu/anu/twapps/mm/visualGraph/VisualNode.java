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

import au.edu.anu.twapps.exceptions.TwAppsException;
import au.edu.anu.twcore.archetype.PrimaryTreeLabels;
import au.edu.anu.twcore.root.ExpungeableFactory;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.graph.NodeFactory;
import fr.cnrs.iees.graph.impl.ALEdge;
import fr.cnrs.iees.graph.impl.TreeGraph;
import fr.cnrs.iees.graph.impl.TreeGraphDataNode;
import fr.cnrs.iees.graph.impl.TreeGraphNode;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.ExtendablePropertyList;
import fr.cnrs.iees.properties.ReadOnlyPropertyList;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.SharedPropertyListImpl;

public class VisualNode extends TreeGraphDataNode implements VisualKeys {

	private TreeGraphDataNode configNode;
	/**
	 * These Objects are constructed at startup time. Thus, there is no need to have
	 * them stored in a property list. To store them in a property list would cause
	 * problems when reloading the file with the omugiImporter.
	 */

	private Object vnSymbol;
	private Object vnText;
	private Object vnParentLine;

	public VisualNode(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
		//setCategory();
	}

	public VisualNode(Identity id, GraphFactory factory) {
		super(id, new SharedPropertyListImpl(VisualGraphFactory.getNodeKeys()), factory);
		//setCategory();
	}

	public VisualNode(Identity newId, ReadOnlyPropertyList props, VisualGraphFactory factory) {
		super(newId, (SimplePropertyList) props, factory);
		//setCategory();
	}

	protected void setConfigNode(TreeGraphDataNode configNode) {
		this.configNode = configNode;
	}

	public TreeGraphDataNode getConfigNode() {
		return configNode;
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
	public Iterable<VisualNode> getChildren() {
		return (Iterable<VisualNode>) super.getChildren();
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

	public String getDisplayText(boolean classOnly) {
		if (classOnly)
			return configNode.classId();
		else
			return configNode.classId() + ":" + configNode.id();
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

	public void setParentLine(Object l) {
		if (vnParentLine != null)
			throw new TwAppsException("Attempt to overwrite line to parent symbol " + id());
		vnParentLine = l;
	}

	public void removeParentLine() {
		if (vnParentLine != null)
			vnParentLine = null;
		else
			throw new TwAppsException("Attempt to remove non-existant line to parent symbol " + id());
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

	public Object getParentLine() {
		return vnParentLine;
	}

	public boolean isCollapsed() {
		return (Boolean) properties().getPropertyValue(vnCollapsed);
	}

	public boolean isCollapsedParent() {
		if (isCollapsed())
			return false;
		for (VisualNode n : getChildren()) {
			if (n.isCollapsed())
				return true;
		}
		return false;
	}

	public String getCreatedBy() {
		return (String) properties().getPropertyValue(vnCreatedBy);
	}

	public void setCreatedBy(String label) {
		properties().setProperty(vnCreatedBy, label);
	}

	private ExtendablePropertyList getExtendablePropertyList() {
		return (ExtendablePropertyList) configNode.properties();
	}

	public void addProperty(String key, Object value) {
		getExtendablePropertyList().addProperty(key, value);
	}

	public void addProperty(String key) {
		getExtendablePropertyList().addProperty(key);
	}

	public boolean configHasProperty(String key) {
		return getExtendablePropertyList().hasProperty(key);
	};

	public Object configGetPropertyValue(String key) {
		return getExtendablePropertyList().getPropertyValue(key);
	}

	public void shadowElements(TreeGraph<TreeGraphNode, ALEdge> configGraph) {
		for (TreeGraphNode cNode:configGraph.nodes()) {
			if (cNode.id().equals(id())) {
				configNode = (TreeGraphDataNode) cNode;
				for (ALEdge edge :edges(Direction.OUT)) {
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
		ExpungeableFactory vf = (ExpungeableFactory) factory();
		ExpungeableFactory cf = (ExpungeableFactory) configNode.factory();
		vf.expungeNode(this);
		cf.expungeNode(configNode);
		disconnect();
		configNode.disconnect();
	}
	
	public VisualNode newChild(String label,String proposedId) {
		NodeFactory cf = configNode.factory();
		TreeGraphDataNode cChild = (TreeGraphDataNode) cf.makeNode(cf.nodeClass(label), proposedId);
		cChild.connectParent(configNode);	 
		proposedId = cChild.id();
		VisualNode vChild = (VisualNode) factory().makeNode(proposedId);
		vChild.connectParent(this);
		vChild.setConfigNode(cChild);
		vChild.setCreatedBy(cChild.classId());
		vChild.setCategory();
		if (!cChild.id().equals(vChild.id()))
			throw new TwAppsException("Ids must be the same -[config: "+cChild.id()+"; visual: "+vChild.id());
		return vChild;
	}
}
