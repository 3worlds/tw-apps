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

import au.edu.anu.rscs.aot.graph.AotNode;
import au.edu.anu.twapps.exceptions.TwAppsException;
import au.edu.anu.twcore.archetype.PrimaryTreeLabels;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.Edge;
import fr.cnrs.iees.graph.NodeFactory;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.tree.TreeNode;
import fr.cnrs.iees.tree.TreeNodeFactory;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

public class VisualNode extends TreeGraphNodeSimplePropertyList implements VisualKeys {

	private AotNode configNode;

	protected VisualNode(String label, String name, TreeNode treenode, SimplePropertyList properties,
			NodeFactory factory) {
		super(label, name, treenode, properties, factory);
		setCollapse(false);
	}

	public void setConfigNode(AotNode configNode) {
		this.configNode = configNode;
	}

	public AotNode getConfigNode() {
		return configNode;
	}

	public void setX(double x) {
		this.setProperty(vnx, x);
	}

	public void setY(double y) {
		setProperty(vny, y);
	}

	public void setPosition(double x, double y) {
		setX(x);
		setY(y);
	}

	public double getX() {
		return (Double) getPropertyValue(vnx);
	}

	public double getY() {
		return (Double) getPropertyValue(vny);
	}

	private void setSymbol(Object symbol) {
		if (getPropertyValue(vnSymbol) != null)
			throw new TwAppsException("Attempt to overwrite node symbol " + uniqueId());
		setProperty(vnSymbol, symbol);
	}

	private void setText(Object text) {
		if (getPropertyValue(vnText) != null)
			throw new TwAppsException("Attempt to overwrite node text " + uniqueId());
		setProperty(vnText, text);
	}

	public void setCategory() {
		if (PrimaryTreeLabels.contains(getLabel()))
			setProperty(vnCategory, getLabel());
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

	@Override
	public String toDetailedString() {
		StringBuilder sb = new StringBuilder(toUniqueString());
		sb.append("=[");
		if (getParent() != null)
			sb.append("↑").append(getParent().toUniqueString());
		else
			sb.append("ROOT");
		if (hasChildren()) {
			for (TreeNode n : getChildren()) {
				sb.append(" ↓").append(n.toUniqueString());
			}
		}
		if (getEdges(Direction.IN).iterator().hasNext()) {
			for (Edge e : getEdges(Direction.IN))
				sb.append(" ←").append(e.startNode().toUniqueString());
		}
		if (getEdges(Direction.OUT).iterator().hasNext()) {
			for (Edge e : getEdges(Direction.OUT))
				sb.append(" →").append(e.endNode().toUniqueString());
		}
		if (size() > 0)
			sb.append(' ').append(getProperties().toString());
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

	public void setVisualElements(Object c, Object t) {
		setSymbol(c);
		setText(t);
	}

	public void setParentLine(Object l) {
		if (getPropertyValue(vnParentLine) != null) {
			throw new TwAppsException("Attempt to overwrite line to parent symbol " + uniqueId());
		}
		setProperty(vnParentLine, l);
	}

	public String getCategory() {
		return (String) getPropertyValue(vnCategory);
	}

	public Object getSymbol() {
		return getPropertyValue(vnSymbol);
	}

	@Override
	public VisualGraph nodeFactory() {
		return (VisualGraph) super.nodeFactory();
	}

	@Override
	public VisualGraph treeNodeFactory() {
		return (VisualGraph) super.treeNodeFactory();
	}

	@Override
	public VisualNode getParent() {
		return  (VisualNode) super.getParent();
	}

	public boolean isCollapsed() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setCollapse(boolean b) {
		setProperty(vnCollapsed, b);
	}

	public boolean getCollapsed() {
		return (Boolean) getPropertyValue(vnCollapsed);
	}

	public boolean isCollapsedParent() {
		if (getCollapsed())
			return false;
		for (TreeNode n : getChildren()) {
			if (((VisualNode) n).isCollapsed())
				return true;
		}
		return false;

	}

}
