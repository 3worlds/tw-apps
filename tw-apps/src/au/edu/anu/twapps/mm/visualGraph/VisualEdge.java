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

import au.edu.anu.rscs.aot.graph.AotEdge;
import au.edu.anu.twapps.exceptions.TwAppsException;
import fr.cnrs.iees.graph.Node;
import fr.cnrs.iees.graph.impl.SimpleEdgeImpl;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.PropertyListSetters;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.SharedPropertyListImpl;
import javafx.scene.text.Text;

public class VisualEdge extends SimpleEdgeImpl implements SimplePropertyList,VisualKeys{
	private AotEdge configEdge;
	private SimplePropertyList properties;

	protected VisualEdge(Identity id,Node start, Node end, String label, String name, SimplePropertyList props,
			VisualGraph factory) {
		super(id,start, end, factory);
		
		this.properties = props;
		if (properties==null)
			properties = new SharedPropertyListImpl(getEdgeKeys());
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
			throw new TwAppsException("Attempt to overwrite edge symbol " + id());
		setProperty(veSymbol, s);
	}
	private void setText(Object t) {
		if (getPropertyValue(veText) != null) 
			throw new TwAppsException("Attempt to overwrite edge text " + id());
		setProperty(veText, t);
	}

	public void setVisualElements(Object l, Text t) {
		setSymbol(l);
		setText(t);
	}

	@Override
	public PropertyListSetters setProperty(String key, Object value) {
		return ((PropertyListSetters) properties).setProperty(key, value);
	}

	@Override
	public Object getPropertyValue(String key) {
		return properties.getPropertyValue(key);
	}

	@Override
	public boolean hasProperty(String key) {
		return properties.hasProperty(key);
	}

	@Override
	public Set<String> getKeysAsSet() {
		return properties.getKeysAsSet();
	}

	@Override
	public int size() {
		return properties.size();
	}

	@Override
	public SimplePropertyList clone() {
		return (SimplePropertyList) properties.clone();
	}

}
