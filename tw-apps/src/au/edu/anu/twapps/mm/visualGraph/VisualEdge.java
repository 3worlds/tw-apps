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

import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.EdgeFactory;
import fr.cnrs.iees.graph.Node;
import fr.cnrs.iees.graph.impl.ALDataEdge;
import fr.cnrs.iees.graph.impl.ALEdge;
import fr.cnrs.iees.graph.impl.TreeGraphDataNode;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.SharedPropertyListImpl;
import fr.ens.biologie.generic.utils.Duple;

/**
 * Author Ian Davies - 11 Jul 2019
 * <p>
 * Visualization of configuration graph edges constructed by the
 * {@link VisualGraphFactory}.
 * 
 */
public class VisualEdge extends ALDataEdge  {
	/**
	 * Edge is visible.
	 */
	final static String IS_VISIBLE = "visible";
	private ALEdge configEdge;
	/**
	 * These Objects are constructed at startup time. Thus, there is no need to have
	 * them stored in a property list. To store them in a property list would cause
	 * problems when reloading the file with the omugiImporter.
	 */
	private Object veText;
	private Object veSymbol;
	private Object veArrowhead;

	/**
	 * @param id      Edge {@link Identity}
	 * @param start   The {@link VisualNode} start node.
	 * @param end     The {@link VisualNode} end node.
	 * @param props   The property list which will be a
	 *                {@link SharedPropertyListImpl}.
	 * @param factory The {@link VisualGraphFactory}
	 */
	public VisualEdge(Identity id, Node start, Node end, SimplePropertyList props, EdgeFactory factory) {
		super(id, start, end, props, factory);
	}

	/**
	 * @param id      Edge {@link Identity}
	 * @param start   The {@link VisualNode} start node.
	 * @param end     The {@link VisualNode} end node.
	 * @param factory The {@link VisualGraphFactory}
	 */
	public VisualEdge(Identity id, Node start, Node end, EdgeFactory factory) {
		super(id, start, end, new SharedPropertyListImpl(VisualGraphFactory.getEdgeKeys()), factory);
	}

	/**
	 * Setter fpr the configuration {@link ALEdge}.
	 * 
	 * @param configEdge {@link ALEdge}.
	 */
	public void setConfigEdge(ALEdge configEdge) {
		this.configEdge = configEdge;
	}

	/**
	 * Getter for the wrapped configuration graph edge.
	 * 
	 * @return the wrapped edge.
	 */
	public ALEdge getConfigEdge() {
		return configEdge;
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
			return configEdge.toShortString();
		}
		case Role: {
			return configEdge.classId();
		}
		case Name: {
			return configEdge.id();
		}
		default: {
			return "";
		}
		}
	}

	/**
	 * Sets the configuration edge if found in this configuration node.
	 * 
	 * @param configNode the configuration node that is the start node of the
	 *                   required edge.
	 */
	public void shadowElements(TreeGraphDataNode configNode) {
		for (ALEdge edge : configNode.edges(Direction.OUT))
			if (edge.id().equals(id())) {
				configEdge = edge;
				return;
			}
	}

	/**
	 * @return true if a text object exists, false otherwise.
	 */
	public boolean hasText() {
		return veText != null;
	}

	/**
	 * Get the text display object. The class of this object is implementation
	 * specific.
	 * 
	 * @return The text object.
	 * @throws NullPointerException if the object is null.
	 */
	public Object getText() {
		if (veText == null)
			throw new NullPointerException(
					"Attempt to access null edge text object [" + getDisplayText(ElementDisplayText.RoleName) + "]");
		return veText;
	}

	/**
	 * Getter for the edge view objects.
	 * 
	 * @return a duple of symbols to represent the edge and arrowhead.
	 */
	public Duple<Object, Object> getSymbol() {
		return new Duple<Object, Object>(veSymbol, veArrowhead);
	}

	private void setSymbol(Object line, Object arrowhead){
		if (veSymbol != null)
			throw new IllegalStateException("Attempt to overwrite edge line " + id());
		veSymbol = line;
		if (veArrowhead != null)
			throw new IllegalStateException("Attempt to overwrite edge line arrowhead" + id());
		veArrowhead = arrowhead;
	}

	private void setText(Object t) {
		if (veText != null)
			throw new IllegalStateException("Attempt to overwrite edge text " + id());
		veText = t;
	}

	/**
	 * Sets the implementation-specific object to view the {@link ALEdge}.
	 * 
	 * @param line      Line drawing object.
	 * @param arrowhead Arrowhead object.
	 * @param text      Text display object.
	 */
	public void setVisualElements(Object line, Object arrowhead, Object text) {
		setSymbol(line, arrowhead);
		setText(text);
	}

	/**
	 * Getter for the visible property value of the VisualEdge. This property is set
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
	 * Setter for the visible property of the VisualEdge.
	 * 
	 * @param value true if visible, false otherwise.
	 */
	public void setVisible(boolean value) {
		properties().setProperty(IS_VISIBLE, value);
	}

}
