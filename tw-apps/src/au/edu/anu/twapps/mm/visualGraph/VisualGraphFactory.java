package au.edu.anu.twapps.mm.visualGraph;

import java.util.HashMap;
import java.util.Map;

import au.edu.anu.rscs.aot.graph.property.Property;
import fr.cnrs.iees.graph.Node;
import fr.cnrs.iees.graph.impl.TreeGraphFactory;
import fr.cnrs.iees.properties.PropertyListFactory;
import fr.cnrs.iees.properties.ReadOnlyPropertyList;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.SharedPropertyListImpl;

/**
 * A factory for 3Worlds specifications - has predefined labels matching nodes
 * and edges. .makePropertyList returns an extendable property list.
 * 
 * @author Jacques Gignoux - 27 mai 2019
 *
 */
public class VisualGraphFactory extends TreeGraphFactory {

	private static Map<String, String> vgLabels = new HashMap<>();

	// Property list factory for nodes (anonymous class)
	private static PropertyListFactory nodePLF = new PropertyListFactory () {
		@Override
		public ReadOnlyPropertyList makeReadOnlyPropertyList(Property... properties) {
			// TODO: replace with your own code
			return null;
		}
		@Override
		public SimplePropertyList makePropertyList(Property... properties) {
			// TODO: replace with your own code
			return null;
		}
		@Override
		public SimplePropertyList makePropertyList(String... propertyKeys) {
			return new SharedPropertyListImpl(propertyKeys);
		}
	};
	
	// Property list factory for edges (anonymous class)
	private static PropertyListFactory edgePLF = new PropertyListFactory () {
		@Override
		public ReadOnlyPropertyList makeReadOnlyPropertyList(Property... properties) {
			// TODO: replace with your own code
			return null;
		}
		@Override
		public SimplePropertyList makePropertyList(Property... properties) {
			// TODO: replace with your own code
			return null;
		}
		@Override
		public SimplePropertyList makePropertyList(String... propertyKeys) {
			return new SharedPropertyListImpl(propertyKeys);
		}
	};

	public VisualGraphFactory() {
		super("VisualGraph");
	}

	public VisualGraphFactory(String scopeName) {
		this();
	}

	@Override
	public VisualNode makeNode(String proposedId) {
		VisualNode result = new VisualNode(scope.newId(proposedId), this);
		addNodeToGraphs(result);
		return result;
	}

	@Override
	public VisualEdge makeEdge(Node start, Node end, String proposedId) {
		VisualEdge result = new VisualEdge(scope.newId(proposedId), start, end, this);
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

}
