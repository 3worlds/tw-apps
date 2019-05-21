package au.edu.anu.twapps.mm.visualGraph;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import fr.cnrs.iees.graph.impl.ALEdge;
import fr.cnrs.iees.graph.impl.TreeGraph;
import fr.cnrs.iees.graph.impl.TreeGraphFactory;
import fr.cnrs.iees.graph.impl.TreeGraphNode;
import fr.cnrs.iees.identity.impl.PairIdentity;
import fr.cnrs.iees.twcore.constants.Configuration;

class VisualGraphTest {

	@Test
	void test() {
		VisualGraph vg = new VisualGraph(new TreeGraphFactory("VisualGraph"));
		TreeGraph<TreeGraphNode,ALEdge> cg = new TreeGraph<>(new TreeGraphFactory("test"));
		String name = "crap";
		TreeGraphNode croot = (TreeGraphNode) cg.nodeFactory().makeNode(Configuration.N_ROOT+PairIdentity.LABEL_NAME_STR_SEPARATOR+name);
		VisualNode vroot = vg.makeNode(Configuration.N_ROOT+PairIdentity.LABEL_NAME_STR_SEPARATOR+name);
		
		TreeGraphNode csrcNode = (TreeGraphNode) cg.nodeFactory().makeNode(Configuration.N_DATADEFINITION+PairIdentity.LABEL_NAME_STR_SEPARATOR+name);
		csrcNode.connectParent(croot);
		VisualNode vsrcNode = vg.makeNode(Configuration.N_DATADEFINITION+PairIdentity.LABEL_NAME_STR_SEPARATOR+name);
		vsrcNode.connectParent(vroot);
		assertTrue(croot!=null);
		assertTrue(croot.classId().equals(Configuration.N_ROOT));
		vroot.setConfigNode(croot);
		vsrcNode.setConfigNode(csrcNode);
		for (VisualNode n: vg.nodes())
			System.out.println(n.toString());

		//fail("Not yet implemented");
	}

}
