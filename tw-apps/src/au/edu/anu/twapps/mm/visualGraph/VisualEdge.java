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
import fr.cnrs.iees.graph.EdgeFactory;
import fr.cnrs.iees.graph.Node;
import fr.cnrs.iees.graph.impl.ALDataEdge;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.SharedPropertyListImpl;

public class VisualEdge extends ALDataEdge implements VisualKeys {
	private ALDataEdge configEdge;
	private SimplePropertyList properties;
	public VisualEdge(Identity id, Node start, Node end, SimplePropertyList props, EdgeFactory factory) {
		super(id, start, end, props, factory);
	}
	public VisualEdge(Identity id, Node start, Node end, EdgeFactory factory) {
		super(id,start,end, new SharedPropertyListImpl(VisualKeys.getEdgeKeys()),factory);
	}

	public void setConfigEdge(ALDataEdge configEdge) {
		this.configEdge = configEdge;
	}

	public ALDataEdge getConfigEdge() {
		return configEdge;
	}

	public Object getText() {
		return properties().getPropertyValue(veText);
	}

	private void setSymbol(Object s) {
		if (properties().getPropertyValue(veSymbol) != null)
			throw new TwAppsException("Attempt to overwrite edge symbol " + id());
		properties().setProperty(veSymbol, s);
	}

	private void setText(Object t) {
		if (properties().getPropertyValue(veText) != null)
			throw new TwAppsException("Attempt to overwrite edge text " + id());
		properties().setProperty(veText, t);
	}

	public void setVisualElements(Object line, Object text) {
		setSymbol(line);
		setText(text);
	}

}
