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
import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.graph.TreeNode;
import fr.cnrs.iees.graph.impl.TreeGraphDataNode;
import fr.cnrs.iees.graph.impl.TreeGraphNode;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.identity.impl.PairIdentity;
import fr.cnrs.iees.properties.ReadOnlyPropertyList;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.SharedPropertyListImpl;

public class VisualNode extends TreeGraphDataNode implements VisualKeys{

	private TreeGraphNode configNode;

	public VisualNode(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
		setCollapse(false);
		setCategory();
	}

	public VisualNode(Identity id, GraphFactory factory) {
		super(id, new SharedPropertyListImpl(VisualGraphFactory.getNodeKeys()), factory);
		setCollapse(false);
		setCategory();
	}

	public VisualNode(Identity newId, ReadOnlyPropertyList props, VisualGraphFactory factory) {
		super(newId,(SimplePropertyList) props,factory);
		setCollapse(false);
		setCategory();
	}

	public void setConfigNode(TreeGraphNode configNode) {
		this.configNode = configNode;
	}

	public TreeGraphNode getConfigNode() {
		return configNode;
	}

	@Override
	public String toDetailedString() {
		StringBuilder sb = new StringBuilder(super.toDetailedString());
		sb.append(" [");
		sb.append("Config: ");
		if (getConfigNode() == null)
			sb.append("null");
		else
			sb.append(getConfigNode().toDetailedString());
		sb.append("]");
		sb.append("]");
		return sb.toString();

	}

	// helper methods
	@Override
	public VisualNode getParent() {
		return (VisualNode) super.getParent();
	}

	public void setCollapse(boolean b) {
		properties().setProperty(vnCollapsed, b);
	}

	public void setCategory() {
		if (PrimaryTreeLabels.contains(getLabel()))
			properties().setProperty(vnCategory, getLabel());
		else
			setCategory(getParent());
	}

	private void setCategory(VisualNode parent) {
		if (parent != null) {
			if (PrimaryTreeLabels.contains(parent.getLabel()))
				setCategory(parent.getLabel());
			else
				setCategory(parent.getParent());
		}
	}

	private void setCategory(String category) {
		properties().setProperty(vnCategory, category);
	}

	public String getLabel() {
		return this.id().split(PairIdentity.LABEL_NAME_STR_SEPARATOR)[0];
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
		if (properties().getPropertyValue(vnSymbol) != null)
			throw new TwAppsException("Attempt to overwrite node symbol " + id());
		properties().setProperty(vnSymbol, symbol);
	}

	private void setText(Object text) {
		if (properties().getPropertyValue(vnText) != null)
			throw new TwAppsException("Attempt to overwrite node text " + id());
		properties().setProperty(vnText, text);
	}

	public void setVisualElements(Object c, Object t) {
		setSymbol(c);
		setText(t);
	}

	public void setParentLine(Object l) {
		if (properties().getPropertyValue(vnParentLine) != null) {
			throw new TwAppsException("Attempt to overwrite line to parent symbol " + id());
		}
		properties().setProperty(vnParentLine, l);
	}

	public String getCategory() {
		return (String) properties().getPropertyValue(vnCategory);
	}

	public Object getSymbol() {
		return properties().getPropertyValue(vnSymbol);
	}

	public boolean isCollapsed() {
		return (Boolean) properties().getPropertyValue(vnCollapsed);
	}

	public boolean isCollapsedParent() {
		if (isCollapsed())
			return false;
		for (TreeNode n : getChildren()) {
			if (((VisualNode) n).isCollapsed())
				return true;
		}
		return false;
	}

}
