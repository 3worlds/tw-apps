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

import java.util.HashMap;
import java.util.Map;

import au.edu.anu.rscs.aot.graph.property.Property;
import au.edu.anu.rscs.aot.graph.property.PropertyKeys;
import fr.cnrs.iees.graph.Node;
import fr.cnrs.iees.graph.impl.TreeGraphFactory;
import fr.cnrs.iees.graph.impl.TreeGraphNode;
import fr.cnrs.iees.properties.PropertyListFactory;
import fr.cnrs.iees.properties.ReadOnlyPropertyList;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.SharedPropertyListImpl;

/**
 * @author Ian Davies
 *
 * @Date 11 Jul 2019
 */
public class VisualGraphFactory extends TreeGraphFactory implements VisualKeys {

	private static Map<String, String> vgLabels = new HashMap<>();

	// Property list factory for nodes (anonymous class)
	private static PropertyListFactory nodePLF = new PropertyListFactory() {
		@Override
		public ReadOnlyPropertyList makeReadOnlyPropertyList(Property... properties) {
			return new SharedPropertyListImpl(getNodeKeys());
		}

		@Override
		public SimplePropertyList makePropertyList(Property... properties) {
			SimplePropertyList pl = new SharedPropertyListImpl(getNodeKeys());
			for (Property p:properties) 
				pl.setProperty(p);
			return pl;
		}

		@Override
		public SimplePropertyList makePropertyList(String... propertyKeys) {
			return new SharedPropertyListImpl(propertyKeys);
		}
	};

	// Property list factory for edges (anonymous class)
	private static PropertyListFactory edgePLF = new PropertyListFactory() {
		@Override
		public ReadOnlyPropertyList makeReadOnlyPropertyList(Property... properties) {
			return new SharedPropertyListImpl(getEdgeKeys());
		}

		@Override
		public SimplePropertyList makePropertyList(Property... properties) {
			SimplePropertyList pl = new SharedPropertyListImpl(getEdgeKeys());
			for (Property p:properties) 
				pl.setProperty(p);
			return pl;
		}

		@Override
		public SimplePropertyList makePropertyList(String... propertyKeys) {
			return new SharedPropertyListImpl(propertyKeys);
		}
	};

	public VisualGraphFactory() {
		super("VGF");
	}

	public VisualGraphFactory(String scopeName) {
		this();
	}
	public VisualGraphFactory(String scopeName, Map<String, String> labels) {
		this();
	}

	@Override
	public VisualNode makeNode(String proposedId) {
		VisualNode result = new VisualNode(scope.newId(true,proposedId), this);
		addNodeToGraphs(result);
		return result;
	}
	@Override
	public VisualNode makeNode(String proposedId, ReadOnlyPropertyList props) {
		VisualNode result = new VisualNode(scope.newId(true,proposedId),props,this);
		addNodeToGraphs(result);
		return result;
	}
		
	@Override
	public VisualEdge makeEdge(Node start, Node end, String proposedId) {
		VisualEdge result = new VisualEdge(scope.newId(true,proposedId), start, end, this);
		return result;
	}

	@Override
	public PropertyListFactory nodePropertyFactory() {
		return nodePLF;
	}

	@Override
	public PropertyListFactory edgePropertyFactory() {
		return edgePLF;
	}

	static {
		vgLabels.put(VisualNode.class.getSimpleName(), VisualNode.class.getName());
		vgLabels.put(VisualEdge.class.getSimpleName(), VisualEdge.class.getName());
	}
	
	private static PropertyKeys nodeKeys = new PropertyKeys(vnx, vny,/* vnText, vnSymbol, vnParentLine.*/ vnCategory,
			vnCollapsed);
	private static PropertyKeys edgeKeys = new PropertyKeys(/*veText, veSymbol*/);

	public static PropertyKeys getNodeKeys() {
		return nodeKeys;
	}

	public static PropertyKeys getEdgeKeys() {
		return edgeKeys;
	}

}
