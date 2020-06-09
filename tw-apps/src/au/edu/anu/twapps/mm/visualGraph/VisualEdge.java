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

public class VisualEdge extends ALDataEdge implements VisualKeys {
	private ALEdge configEdge;
	/**
	 * These Objects are constructed at startup time. Thus, there is no need to have
	 * them stored in a property list. To store them in a property list would cause
	 * problems when reloading the file with the omugiImporter.
	 */
	private Object veText;
	private Object veSymbol;
	private Object veArrowhead;

	public VisualEdge(Identity id, Node start, Node end, SimplePropertyList props, EdgeFactory factory) {
		super(id, start, end, props, factory);
	}

	public VisualEdge(Identity id, Node start, Node end, EdgeFactory factory) {
		super(id, start, end, new SharedPropertyListImpl(VisualGraphFactory.getEdgeKeys()), factory);
	}

	public void setConfigEdge(ALEdge configEdge) {
		this.configEdge = configEdge;
	}

	public String getDisplayText(boolean classOnly) {
		if (classOnly)
			return configEdge.classId();
		else
			return configEdge.toShortString();
	}
	
	public String getDisplayText() {
		return configEdge.toShortString();
	}

	public ALEdge getConfigEdge() {
		return configEdge;
	}

	public boolean hasText() {
		return veText != null;
	}

	public Object getText() {
		if (veText == null)
			throw new TwAppsException("Attempt to access null edge text object [" + getDisplayText() + "]");
		return veText;
	}

	public Duple<Object, Object> getSymbol() {
		return new Duple<Object, Object>(veSymbol, veArrowhead);
	}

	private void setSymbol(Object line, Object arrowhead) {
		if (veSymbol != null)
			throw new TwAppsException("Attempt to overwrite edge line " + id());
		veSymbol = line;
		if (veArrowhead != null)
			throw new TwAppsException("Attempt to overwrite edge line arrowhead" + id());
		veArrowhead = arrowhead;
	}

	private void setText(Object t) {
		if (veText != null)
			throw new TwAppsException("Attempt to overwrite edge text " + id());
		veText = t;
	}

	public void setVisualElements(Object line, Object arrowhead, Object text) {
		setSymbol(line, arrowhead);
		setText(text);
	}

	public boolean isVisible() {
		if (properties().getPropertyValue(veVisible) == null)
			properties().setProperty(veVisible, true);
		return (Boolean) properties().getPropertyValue(veVisible);
	}

	public void setVisible(boolean value) {
		properties().setProperty(veVisible, value);
	}

	public void shadowElements(TreeGraphDataNode configNode) {
		for (ALEdge edge : configNode.edges(Direction.OUT))
			if (edge.id().equals(id())) {
				configEdge = edge;
				return;
			}
	}
}
