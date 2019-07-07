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

import java.util.Set;

import au.edu.anu.twapps.exceptions.TwAppsException;
import au.edu.anu.twcore.archetype.PrimaryTreeLabels;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.Edge;
import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.graph.TreeNode;
import fr.cnrs.iees.graph.impl.ALDataNode;
import fr.cnrs.iees.graph.impl.ALNode;
import fr.cnrs.iees.graph.impl.TreeGraphDataNode;
import fr.cnrs.iees.graph.impl.TreeGraphNode;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.identity.impl.PairIdentity;
import fr.cnrs.iees.properties.PropertyListSetters;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.SharedPropertyListImpl;

public class VisualNode extends TreeGraphNode //
		implements VisualKeys {
	private TreeGraphNode configNode;

	public VisualNode(Identity id, GraphFactory gfactory) {
		super(id, gfactory);
	}

//	public VisualNode(Identity id, SimplePropertyList props, GraphFactory factory) {
//		super(id, props, factory);
//		setCollapse(false);
//		setCategory();
//	}
//
//	public void setCollapse(boolean b) {
//		properties().setProperty(vnCollapsed, b);
//	}

	public void setCategory() {

		if (PrimaryTreeLabels.contains(getLabel()))
			properties().setProperty(vnCategory, getLabel());
		else
			setCategory(getParent());
	}

	private void setCategory(VisualNode parent) {
		if (parent != null) {
			if (PrimaryTreeLabels.contains(parent.getLabel()))
				setProperty(vnCategory, parent.getLabel());
			else
				setCategory(parent.getParent());
		}
	}

	public String getLabel() {
		return this.id().split(PairIdentity.LABEL_NAME_STR_SEPARATOR)[0];
	}

//

//
//	public VisualNode(Identity id, SimplePropertyList properties, GraphFactory gf) {
//		super(id, properties, gf);
//		setCollapse(false);
//		setCategory();
//	}
//
//	public VisualNode(Identity id, GraphFactory gf) {
//		super(id,new SharedPropertyListImpl(VisualKeys.getNodeKeys()), gf);
//		setCollapse(false);
//		setCategory();
//	}
//
//	@Override
//	public VisualNode getParent() {
//		return (VisualNode) super.getParent();
//	}
//
//	@Override
//	public PropertyListSetters setProperty(String key, Object value) {
//		return ((PropertyListSetters) properties).setProperty(key, value);
//	}
//
//	@Override
//	public Object getPropertyValue(String key) {
//		return properties.getPropertyValue(key);
//	}
//
//	@Override
//	public boolean hasProperty(String key) {
//		return properties.hasProperty(key);
//	}
//
//	@Override
//	public Set<String> getKeysAsSet() {
//		return properties.getKeysAsSet();
//	}
//
//	@Override
//	public int size() {
//		return properties.size();
//	}
//
//	@Override
//	public SimplePropertyList clone() {
//		// temporary - check this later
//		return (SimplePropertyList) properties.clone();
//	}

//	@Override
//	public VisualGraph nodeFactory() {
////		return (VisualGraph) super.treeNodeFactory();
//		return nodeFactory();
//	}

//	@Override
//	public String toDetailedString() {
//		StringBuilder sb = new StringBuilder(toUniqueString());
//		sb.append("=[");
//		if (getParent() != null)
//			sb.append("↑").append(getParent().toUniqueString());
//		else
//			sb.append("ROOT");
//		if (hasChildren()) {
//			for (TreeNode n : getChildren()) {
//				sb.append(" ↓").append(n.toUniqueString());
//			}
//		}
//		if (edges(Direction.IN).iterator().hasNext()) {
//			for (Edge e : edges(Direction.IN))
//				sb.append(" ←").append(e.startNode().toUniqueString());
//		}
//		if (edges(Direction.OUT).iterator().hasNext()) {
//			for (Edge e : edges(Direction.OUT))
//				sb.append(" →").append(e.endNode().toUniqueString());
//		}
////		if (size() > 0)
////			sb.append(' ').append(properties.toString());
//		sb.append(" [");
//		sb.append("Config: ");
//		if (getConfigNode() == null)
//			sb.append("null");
//		else
//			sb.append(getConfigNode().toDetailedString());
//		sb.append("]");
//		sb.append("]");
//		return sb.toString();
//	}
//
//	public void setConfigNode(TreeGraphNode configNode) {
//		this.configNode = configNode;
//	}
//
//	public TreeGraphNode getConfigNode() {
//		return configNode;
//	}
//
//	public void setX(double x) {
//		this.setProperty(vnx, x);
//	}
//
//	public void setY(double y) {
//		setProperty(vny, y);
//	}
//
//	public void setPosition(double x, double y) {
//		setX(x);
//		setY(y);
//	}
//
//	public double getX() {
//		return (Double) getPropertyValue(vnx);
//	}
//
//	public double getY() {
//		return (Double) getPropertyValue(vny);
//	}
//
//	private void setSymbol(Object symbol) {
//		if (getPropertyValue(vnSymbol) != null)
//			throw new TwAppsException("Attempt to overwrite node symbol " + id());
//		setProperty(vnSymbol, symbol);
//	}
//
//	private void setText(Object text) {
//		if (getPropertyValue(vnText) != null)
//			throw new TwAppsException("Attempt to overwrite node text " + id());
//		setProperty(vnText, text);
//	}
//
//
//
//	public void setVisualElements(Object c, Object t) {
//		setSymbol(c);
//		setText(t);
//	}
//
//	public void setParentLine(Object l) {
//		if (getPropertyValue(vnParentLine) != null) {
//			throw new TwAppsException("Attempt to overwrite line to parent symbol " + id());
//		}
//		setProperty(vnParentLine, l);
//	}
//
//	public String getCategory() {
//		return (String) getPropertyValue(vnCategory);
//	}
//
//	public Object getSymbol() {
//		return getPropertyValue(vnSymbol);
//	}
//
//	public boolean isCollapsed() {
//		return (Boolean) getPropertyValue(vnCollapsed);
//	}
//
//
//	public boolean isCollapsedParent() {
//		if (isCollapsed())
//			return false;
//		for (TreeNode n : getChildren()) {
//			if (((VisualNode) n).isCollapsed())
//				return true;
//		}
//		return false;
//
//	}
//
//	@Override
//	public PropertyListSetters setProperty(String key, Object value) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public Object getPropertyValue(String key) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public boolean hasProperty(String key) {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public Set<String> getKeysAsSet() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public int size() {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//
//	@Override
//	public SimplePropertyList clone() {
//		// TODO Auto-generated method stub
//		return null;
//	}

}
