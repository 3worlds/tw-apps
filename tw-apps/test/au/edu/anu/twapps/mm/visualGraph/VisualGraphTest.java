package au.edu.anu.twapps.mm.visualGraph;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import au.edu.anu.rscs.aot.graph.AotGraph;
import au.edu.anu.rscs.aot.graph.AotNode;
import fr.cnrs.iees.identity.impl.PairIdentity;
import fr.cnrs.iees.twcore.constants.Configuration;

class VisualGraphTest {

	@Test
	void test() {
		VisualGraph vg = new VisualGraph();
		AotGraph cg = new AotGraph();
		String name = "crap";
		AotNode croot = cg.makeTreeNode(null, Configuration.N_ROOT+PairIdentity.LABEL_NAME_STR_SEPARATOR+name);
		VisualNode vroot = vg.makeTreeNode(null, Configuration.N_ROOT+PairIdentity.LABEL_NAME_STR_SEPARATOR+name);
		
		AotNode csrcNode = cg.makeTreeNode(croot,Configuration.N_DATADEFINITION+PairIdentity.LABEL_NAME_STR_SEPARATOR+name);
		VisualNode vsrcNode = vg.makeTreeNode(vroot,Configuration.N_DATADEFINITION+PairIdentity.LABEL_NAME_STR_SEPARATOR+name);
		assertTrue(croot!=null);
		assertTrue(croot.getLabel().equals(Configuration.N_ROOT));
		vroot.setConfigNode(croot);
		vsrcNode.setConfigNode(csrcNode);
		for (VisualNode n: vg.nodes())
			System.out.println(n.toString());

		//fail("Not yet implemented");
	}

}
