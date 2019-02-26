package au.edu.anu.twapps.mm.visualGraph;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import fr.cnrs.iees.identity.impl.PairIdentity;
import fr.cnrs.iees.twcore.constants.Configuration;

class VisualGraphTest {

	@Test
	void test() {
		String rootId = Configuration.N_ROOT+PairIdentity.LABEL_NAME_STR_SEPARATOR+"Crap";
		VisualGraph<VisualNode,VisualEdge> graph = new VisualGraph<>();
		graph.makeTreeNode(null, rootId);
		for (VisualNode n: graph.nodes())
			System.out.println(n.toString());

		fail("Not yet implemented");
	}

}
