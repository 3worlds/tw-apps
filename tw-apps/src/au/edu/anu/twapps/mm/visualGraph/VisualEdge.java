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

import au.edu.anu.rscs.aot.graph.AotEdge;
import au.edu.anu.twapps.exceptions.TwAppsException;
import fr.cnrs.iees.graph.EdgeFactory;
import fr.cnrs.iees.graph.Node;
import fr.cnrs.iees.properties.SimplePropertyList;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

public class VisualEdge extends EdgeSimpleProperty implements VisualKeys{
	private AotEdge configEdge;

	protected VisualEdge(Node start, Node end, String label, String name, SimplePropertyList properties,
			EdgeFactory factory) {
		super(start, end, label, name, properties, factory);
	}

	public void setConfigEdge(AotEdge configEdge) {
		this.configEdge = configEdge;
	}

	public AotEdge getConfigEdge() {
		return configEdge;
	}
	
	public Object getText() {
		return this.getPropertyValue(veText);
	}
	private void setSymbol(Object s) {
		if (getPropertyValue(veSymbol) != null) 
			throw new TwAppsException("Attempt to overwrite edge symbol " + uniqueId());
		setProperty(veSymbol, s);
	}
	private void setText(Object t) {
		if (getPropertyValue(veText) != null) 
			throw new TwAppsException("Attempt to overwrite edge text " + uniqueId());
		setProperty(veText, t);
	}

	public void setVisualElements(Object l, Text t) {
		setSymbol(l);
		setText(t);
	}

}
