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
import au.edu.anu.twcore.root.EditableFactory;
import fr.cnrs.iees.graph.Edge;
import fr.cnrs.iees.graph.Node;
import fr.cnrs.iees.graph.impl.ALEdge;
import fr.cnrs.iees.graph.impl.TreeGraph;
import fr.cnrs.iees.graph.impl.TreeGraphFactory;
import fr.cnrs.iees.graph.impl.TreeGraphNode;
import fr.cnrs.iees.properties.PropertyListFactory;
import fr.cnrs.iees.properties.ReadOnlyPropertyList;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.SharedPropertyListImpl;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.identity.impl.LocalScope;

/**
 * Factory to construct nodes and edges for the layout graph. All elements use
 * the {@link SharedPropertyListImpl} as all property keys are the same.
 * 
 * @author Ian Davies - 11 Jul 2019
 */
public class VisualGraphFactory extends TreeGraphFactory implements EditableFactory {

	private static Map<String, String> vgLabels = new HashMap<>();

	/**
	 * Removes the edge {@link Identity} from the graphs {@link LocalScope}. This is
	 * necessary if an edge is to be renamed or deleted.
	 * 
	 * @param edge The {@link VisualEdge} whose {@link Identity} is to be removed.
	 */
	public void removeEdgeId(VisualEdge edge) {
		scope.removeId(edge.id());
	}

	// Property list factory for nodes (anonymous class)
	private static PropertyListFactory nodePLF = new PropertyListFactory() {
		@Override
		public ReadOnlyPropertyList makeReadOnlyPropertyList(Property... properties) {
			return new SharedPropertyListImpl(getNodeKeys());
		}

		@Override
		public SimplePropertyList makePropertyList(Property... properties) {
			SimplePropertyList pl = new SharedPropertyListImpl(getNodeKeys());
			for (Property p : properties)
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
			for (Property p : properties)
				pl.setProperty(p);
			return pl;
		}

		@Override
		public SimplePropertyList makePropertyList(String... propertyKeys) {
			return new SharedPropertyListImpl(propertyKeys);
		}
	};

	/**
	 * Constructor with name of the {@link LocalScope} and a map of edge labels and
	 * their associated java class names.
	 */
	public VisualGraphFactory() {
		super("VGF", vgLabels);
	}

	/**
	 * @param scopeName The name of the {@link LocalScope}.
	 */
	public VisualGraphFactory(String scopeName) {
		this();
	}

	/**
	 * @param scopeName Name of the {@link LocalScope}
	 * @param labels    map of edge labels and their associated java class names.
	 */
	public VisualGraphFactory(String scopeName, Map<String, String> labels) {
		this();
	}

	@Override
	public VisualNode makeNode(String proposedId) {
		VisualNode result = new VisualNode(scope.newId(true, proposedId), this);
		addNodeToGraphs(result);
		return result;
	}

	@Override
	public VisualNode makeNode(String proposedId, ReadOnlyPropertyList props) {
		VisualNode result = new VisualNode(scope.newId(true, proposedId), props, this);
		addNodeToGraphs(result);
		return result;
	}

	@Override
	public VisualEdge makeEdge(Node start, Node end, String proposedId) {
		// Edge e = inherited makeEdge(start,end,proposedId);
		VisualEdge result = new VisualEdge(scope.newId(true, proposedId), start, end, this);
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

	private static PropertyKeys nodeKeys = new PropertyKeys(VisualNode.LOCATION_X, VisualNode.LOCATION_Y,
			VisualNode.SUB_TREE, VisualNode.IS_COLLAPSED, VisualNode.PARENT_REFERENCE, VisualNode.IS_VISIBLE);
	private static PropertyKeys edgeKeys = new PropertyKeys(VisualEdge.IS_VISIBLE);

	/**
	 * @return {@link PropertyKeys} shared by all nodes of this graph.
	 */
	public static PropertyKeys getNodeKeys() {
		return nodeKeys;
	}

	/**
	 * @return {@link PropertyKeys} shared by all edges of this graph.
	 */
	public static PropertyKeys getEdgeKeys() {
		return edgeKeys;
	}

	@Override
	public void expungeNode(Node node) {
		scope.removeId(node.id());
		for (Edge edge : node.edges())
			expungeEdge(edge);
		for (TreeGraph<TreeGraphNode, ALEdge> g : graphs)
			g.removeNode((TreeGraphNode) node);
	}

	@Override
	public void expungeEdge(Edge edge) {
		scope.removeId(edge.id());

	}

}
