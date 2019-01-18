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

import fr.cnrs.iees.graph.NodeFactory;
import fr.cnrs.iees.properties.PropertyListSetters;
import fr.cnrs.iees.properties.SimplePropertyList;

public class TreeGraphNodeSimplePropertyList extends TreeGraphNode implements SimplePropertyList{
	private SimplePropertyList properties;

	protected TreeGraphNodeSimplePropertyList(String label, String name, NodeFactory factory) {
		super(label, name, factory);
	}

	protected TreeGraphNodeSimplePropertyList(String label, String name, SimplePropertyList properties,
			NodeFactory factory) {
		this(label, name, factory);
		this.properties = properties;
	}
	@Override
	public PropertyListSetters setProperty(String key, Object value) {
		properties.setProperty(key,value);
		return properties;
	}

	@Override
	public Set<String> getKeysAsSet() {
		return properties.getKeysAsSet();
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
	public int size() {
		return properties.size();
	}

	@Override
	public SimplePropertyList clone() {
		// can this work? with shared kesy??
		return properties.clone();
	}

}
