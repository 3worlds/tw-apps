package au.edu.anu.twapps.mm.visualGraph;

import java.util.HashMap;
import java.util.Map;

import au.edu.anu.rscs.aot.graph.property.Property;
import fr.cnrs.iees.graph.EdgeFactory;
import fr.cnrs.iees.graph.Node;
import fr.cnrs.iees.graph.impl.ALEdge;
import fr.cnrs.iees.graph.impl.TreeGraph;
import fr.cnrs.iees.graph.impl.TreeGraphFactory;
import fr.cnrs.iees.graph.impl.TreeGraphNode;
import fr.cnrs.iees.properties.ExtendablePropertyList;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import fr.cnrs.iees.properties.impl.SharedPropertyListImpl;
import fr.cnrs.iees.properties.impl.SimplePropertyListImpl;
import fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels;
import fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels;

/**
 * A factory for 3Worlds specifications - has predefined labels matching nodes
 * and edges. .makePropertyList returns an extendable property list.
 * 
 * @author Jacques Gignoux - 27 mai 2019
 *
 */
public class VisualGraphFactory extends TreeGraphFactory implements  VisualKeys {

	public VisualGraphFactory() {
		super("VisualGraph");
	}

	public VisualGraphFactory(String scopeName) {
		this();
	}

	// TODO Assume this will crash with graphimporter!
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
	public SimplePropertyList makePropertyList(Property... properties) {
		// this is no good here! How can we know if its edge or node properties.
		return new SharedPropertyListImpl(VisualKeys.getNodeKeys());
	}

	@Override
	public SimplePropertyList makePropertyList(String... propertyKeys) {
		return new SharedPropertyListImpl(propertyKeys);
	}

}
