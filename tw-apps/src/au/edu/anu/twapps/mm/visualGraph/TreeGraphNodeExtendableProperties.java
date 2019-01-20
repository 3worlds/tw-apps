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

import java.util.List;
import java.util.Set;

import au.edu.anu.rscs.aot.graph.property.Property;
import fr.cnrs.iees.graph.NodeFactory;
import fr.cnrs.iees.properties.ExtendablePropertyList;
import fr.cnrs.iees.properties.PropertyListSetters;
import fr.cnrs.iees.properties.ReadOnlyPropertyList;
import fr.cnrs.iees.properties.ResizeablePropertyList;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.tree.TreeNode;
import fr.ens.biologie.generic.Sealable;

public class TreeGraphNodeExtendableProperties extends TreeGraphNode implements ExtendablePropertyList {

	private ExtendablePropertyList properties;

	protected TreeGraphNodeExtendableProperties(String label, String name, TreeNode treenode, NodeFactory factory) {
		super(label, name, treenode, factory);

	}

	protected TreeGraphNodeExtendableProperties(String label, String name, TreeNode treenode,
			ExtendablePropertyList properties, NodeFactory factory) {
		super(label, name, treenode, factory);
		this.properties = properties;
	}

	protected ResizeablePropertyList getProperties() {
		return properties;
	}

	@Override
	public SimplePropertyList clone() {
		return properties.clone();
	}

	@Override
	public PropertyListSetters setProperty(String key, Object value) {
		return properties.setProperty(key, value);
	}

	@Override
	public Set<String> getKeysAsSet() {
		return properties.getKeysAsSet();
	}

	@Override
	public Object getPropertyValue(String arg0) {
		return properties.getPropertyValue(arg0);
	}

	@Override
	public boolean hasProperty(String arg0) {
		return properties.hasProperty(arg0);
	}

	@Override
	public int size() {
		return properties.size();
	}

	@Override
	public ResizeablePropertyList addProperties(List<String> arg0) {
		return properties.addProperties(arg0);
	}

	@Override
	public ResizeablePropertyList addProperties(String... arg0) {
		properties.addProperties(arg0);
		return properties;
	}

	@Override
	public ResizeablePropertyList addProperties(ReadOnlyPropertyList arg0) {
		properties.addProperties(arg0);
		return properties;
	}

	@Override
	public ResizeablePropertyList addProperty(Property arg0) {
		properties.addProperty(arg0);
		return properties;
	}

	@Override
	public ResizeablePropertyList addProperty(String arg0) {
		properties.addProperty(arg0);
		return properties;
	}

	@Override
	public ResizeablePropertyList addProperty(String arg0, Object arg1) {
		return properties.addProperty(arg0, arg1);
	}

	@Override
	public Object getPropertyValue(String arg0, Object arg1) {
		return properties.getPropertyValue(arg0, arg1);
	}

	@Override
	public ResizeablePropertyList removeAllProperties() {
		return properties.removeAllProperties();
	}

	@Override
	public ResizeablePropertyList removeProperty(String arg0) {
		return properties.removeProperty(arg0);
	}

	@Override
	public boolean isSealed() {
		return properties.isSealed();
	}

	@Override
	public Sealable seal() {
		return properties.seal();
	}

}
