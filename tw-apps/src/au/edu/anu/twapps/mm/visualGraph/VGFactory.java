package au.edu.anu.twapps.mm.visualGraph;

import java.util.HashMap;
import java.util.Map;

import au.edu.anu.rscs.aot.graph.property.Property;
import fr.cnrs.iees.graph.Node;
import fr.cnrs.iees.graph.impl.TreeGraphFactory;
import fr.cnrs.iees.properties.ExtendablePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import fr.cnrs.iees.properties.impl.SharedPropertyListImpl;
import fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels;
import fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels;

/**
 * A factory for 3Worlds specifications - has predefined labels matching nodes and edges.
 * .makePropertyList returns an extendable property list.
 * 
 * @author Jacques Gignoux - 27 mai 2019
 *
 */
public class VGFactory extends TreeGraphFactory implements VisualKeys{

	
	public VGFactory() {
		super("VisualGraph");
	}

	public VGFactory(String scopeName) {
		this();
	}

	// TODO probably can't work with graphimporter!
	@Override
	public VisualNode makeNode(String proposedId) {
		VisualNode result = new VisualNode(scope.newId(proposedId),this);
		addNodeToGraphs(result);
		return result;
	}
	
	@Override
	public VisualEdge makeEdge(Node start,Node end,String proposedId) {
		VisualEdge result = new VisualEdge(scope.newId(proposedId),start,end,this);
		return result;
		
	}
	
	
}
