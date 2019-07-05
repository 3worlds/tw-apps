package au.edu.anu.twapps.mm.visualGraph;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import fr.cnrs.iees.graph.impl.ALEdge;
import fr.cnrs.iees.graph.impl.TreeGraph;
import fr.cnrs.iees.graph.impl.TreeGraphFactory;
import fr.cnrs.iees.graph.impl.TreeGraphNode;
import fr.cnrs.iees.identity.impl.PairIdentity;
import fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels;

class VisualGraphTest {

	@Test
	void test() {
		VisualGraph vg = new VisualGraph(new TreeGraphFactory("VisualGraph"));
		TreeGraph<TreeGraphNode,ALEdge> cg = new TreeGraph<>(new TreeGraphFactory("test"));
		String name = "crap";
		TreeGraphNode croot = (TreeGraphNode) cg.nodeFactory().makeNode(ConfigurationNodeLabels.N_ROOT.label()+PairIdentity.LABEL_NAME_STR_SEPARATOR+name);
		VisualNode vroot = vg.makeNode(ConfigurationNodeLabels.N_ROOT.label()+PairIdentity.LABEL_NAME_STR_SEPARATOR+name);
		
		TreeGraphNode csrcNode = (TreeGraphNode) cg.nodeFactory().makeNode(ConfigurationNodeLabels.N_DATADEFINITION.label()+PairIdentity.LABEL_NAME_STR_SEPARATOR+name);
		csrcNode.connectParent(croot);
		VisualNode vsrcNode = vg.makeNode(ConfigurationNodeLabels.N_DATADEFINITION.label()+PairIdentity.LABEL_NAME_STR_SEPARATOR+name);
		vsrcNode.connectParent(vroot);
		assertTrue(croot!=null);
		assertTrue(croot.classId().equals(ConfigurationNodeLabels.N_ROOT.label()));
		vroot.setConfigNode(croot);
		vsrcNode.setConfigNode(csrcNode);
		for (VisualNode n: vg.nodes())
			System.out.println(n.toString());

		//fail("Not yet implemented");
	}

}
