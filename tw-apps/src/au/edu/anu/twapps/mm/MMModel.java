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

package au.edu.anu.twapps.mm;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

import au.edu.anu.rscs.aot.archetype.CheckMessage;
import au.edu.anu.twapps.dialogs.Dialogs;
import au.edu.anu.twapps.exceptions.TwAppsException;
import au.edu.anu.twapps.mm.visualGraph.VisualGraphFactory;
import au.edu.anu.twapps.mm.configGraph.ConfigGraph;
import au.edu.anu.twapps.mm.errorMessages.archetype.UnknownErr;
import au.edu.anu.twapps.mm.layout.TreeLayout;
import au.edu.anu.twapps.mm.visualGraph.VisualEdge;
import au.edu.anu.twapps.mm.visualGraph.VisualNode;
import au.edu.anu.twcore.errorMessaging.ComplianceManager;
import au.edu.anu.twcore.graphState.GraphState;
import au.edu.anu.twcore.project.Project;
import au.edu.anu.twcore.root.TwConfigFactory;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.Node;
import fr.cnrs.iees.graph.NodeFactory;
import fr.cnrs.iees.graph.TreeNode;
import fr.cnrs.iees.graph.impl.ALEdge;
import fr.cnrs.iees.graph.impl.TreeGraph;
import fr.cnrs.iees.graph.impl.TreeGraphDataNode;
import fr.cnrs.iees.graph.io.impl.OmugiGraphExporter;
import fr.cnrs.iees.io.FileImporter;
import fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels;
import fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames;
import fr.ens.biologie.generic.utils.Duple;
import fr.ens.biologie.generic.utils.Logging;

import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;

/**
 * Author Ian Davies
 *
 * Date 10 Dec. 2018
 */
public class MMModel implements IMMModel {
	// Interface supplied to the controller
	private TreeGraph<VisualNode, VisualEdge> visualGraph;
	private IMMController controller;

	private static Logger log = Logging.getLogger(MMModel.class);

	public MMModel(IMMController controller) {
		this.controller = controller;
		buildNonEditableList();

	}

	private Map<String, List<String>> nonEditableMap = new HashMap<>();

	private void addEntry(ConfigurationNodeLabels nl, ConfigurationPropertyNames pn) {
		String nLabel = nl.label();
		String pKey = pn.key();
		List<String> lst = nonEditableMap.get(nLabel);
		if (lst == null) {
			lst = new ArrayList<>();
			nonEditableMap.put(nLabel, lst);
		}
		lst.add(pKey);
	}

	private void buildNonEditableList() {
		for (ConfigurationNodeLabels key : ConfigurationNodeLabels.values()) {
			List<String> keys = new ArrayList<>();
			// default - subclass is never editable
			keys.add("subclass");
			nonEditableMap.put(key.label(), keys);
		}
		addEntry(ConfigurationNodeLabels.N_COMPONENT, ConfigurationPropertyNames.P_PARAMETERCLASS);
		addEntry(ConfigurationNodeLabels.N_SYSTEM, ConfigurationPropertyNames.P_PARAMETERCLASS);
		addEntry(ConfigurationNodeLabels.N_FUNCTION, ConfigurationPropertyNames.P_FUNCTIONCLASS);
	}

	@Override
	public boolean propertyEditable(String classId, String key) {
		if (!nonEditableMap.containsKey(classId))
			return true;
		if (!nonEditableMap.get(classId).contains(key))
			return true;
		// TODO build this when the archetype is ready
		return false;
	}

	private void onProjectClosing() {
		controller.onProjectClosing();
	}

	private void onProjectOpened() {
		ConfigGraph.validateGraph();
		controller.onProjectOpened(visualGraph);
	}

	@Override
	public void doClearJavaProject() {
		// TODO Auto-generated method stub

	}

	@Override
	public void doSetJavaProject() {
		// TODO Auto-generated method stub

	}

	@Override
	public void doMenuExit() {
		// TODO Auto-generated method stub

	}

	@Override
	public void doDeploy() {
		String arg1 = Project.getProjectFile().getName();
		ProcessBuilder experimentUI = new ProcessBuilder("java", "-jar", Project.getProjectUserName() + ".jar", arg1);
		experimentUI.directory(Project.getProjectFile());
		experimentUI.inheritIO();
		try {
			experimentUI.start();
		} catch (Exception e) {
			ComplianceManager.add(new UnknownErr(CheckMessage.code20Deploy, e));
		}
	}

	@Override
	public void doNewProject() {
		if (!canClose())
			return;
		String promptId = "project1";
		boolean modified = true;
		promptId = Project.proposeId(promptId);
		while (modified) {
			String userName = Dialogs.getText("New project", "", "New project name:", promptId);
			if (userName == null)
				return;
			if (userName.equals(""))
				return;
			userName = Project.formatName(userName);
			String newName = Project.proposeId(userName);
			modified = !newName.equals(userName);
			promptId = newName;
		}
		if (Project.isOpen()) {
			onProjectClosing();
			Project.close();
		}
		promptId = Project.create(promptId);
		ConfigGraph.setGraph(new TreeGraph<TreeGraphDataNode, ALEdge>(new TwConfigFactory()));
		NodeFactory cf = ConfigGraph.getGraph().nodeFactory();
		cf.makeNode(cf.nodeClass(N_ROOT.label()), promptId);
		visualGraph = new TreeGraph<VisualNode, VisualEdge>(new VisualGraphFactory());
		visualGraph.nodeFactory().makeNode(promptId);

		shadowGraph();

		visualGraph.root().setCategory();
		visualGraph.root().setPosition(0.5, 0.5);
		visualGraph.root().setCollapse(false);

		onProjectOpened();
		doSave();
	}

	private void shadowGraph() {
		for (VisualNode vn : visualGraph.nodes())
			vn.shadowElements(ConfigGraph.getGraph());
	}

	@SuppressWarnings("unchecked")
	@Override
	public void doOpenProject(File file) {
		// TODO Auto-generated method stub
		if (!canClose())
			return;
		if (Project.isOpen()) {
			onProjectClosing();
			Project.close();
		}
		Project.open(file);
		ConfigGraph.setGraph(
				(TreeGraph<TreeGraphDataNode, ALEdge>) FileImporter.loadGraphFromFile(Project.makeConfigurationFile()));
		visualGraph = (TreeGraph<VisualNode, VisualEdge>) FileImporter.loadGraphFromFile(Project.makeLayoutFile());
		shadowGraph();
		onProjectOpened();
	}

	@Override
	public void doSave() {
		new OmugiGraphExporter(Project.makeConfigurationFile()).exportGraph(ConfigGraph.getGraph());
		new OmugiGraphExporter(Project.makeLayoutFile()).exportGraph(visualGraph);
		GraphState.clear();
		ConfigGraph.validateGraph();
	}

	@Override
	public void doSaveAs() {
		// TODO Auto-generated method stub

	}

	private Duple<VisualNode, VisualNode> getMatchingPair(Iterable<VisualNode> visualNodes, Node node1,
			Node node2) {
		VisualNode resultNode1 = null;
		VisualNode resultNode2 = null;

		for (VisualNode node : visualNodes) {
			if (node.id().equals(node1.id()))
				resultNode1 = node;
			else if (node.id().equals(node2.id()))
				resultNode2 = node;
			if (resultNode1 != null && resultNode2 != null)
				return new Duple<VisualNode, VisualNode>(resultNode1, resultNode2);
		}
		throw new TwAppsException(
				"Matching node pair not found in visual graph [" + node1.id() + "," + node2.id() + "]");
	}

	@SuppressWarnings("unchecked")
	@Override
	public void doImport() {
		if (!canClose())
			return;
		File file = Dialogs.getExternalProjectFile();
		if (file == null)
			return;
		log.info("Import: " + file);
		TreeGraph<TreeGraphDataNode, ALEdge> importGraph = (TreeGraph<TreeGraphDataNode, ALEdge>) FileImporter
				.loadGraphFromFile(file);
		// This could be in any state so search for the 3worlds node
		TreeGraphDataNode twroot = null;
		for (TreeGraphDataNode root : importGraph.roots()) {
			if (root.classId().equals(N_ROOT.label()))
				twroot = root;
		}
		if (twroot == null) {
			Dialogs.errorAlert("Import error", file.getName(),
					"This file does not a root node called '" + N_ROOT.label() + "'");
			return;
		}
		log.info("Root: " + twroot);
		TreeGraph<VisualNode, VisualEdge> importVisual = new TreeGraph<VisualNode, VisualEdge>(
				new VisualGraphFactory());
		for (TreeGraphDataNode importNode : importGraph.nodes()) {
			log.info("Creating visual node: " + importNode.id());
			Node node = importVisual.nodeFactory().makeNode(importNode.id());
			VisualNode visualNode = (VisualNode) node;
			visualNode.setConfigNode(importNode);
		}

		for (TreeGraphDataNode importNode : importGraph.nodes()) {
			log.info("Creating visual node: " + importNode.id());
			TreeNode parent = importNode.getParent();
			if (parent != null) {
				Duple<VisualNode, VisualNode> vNodes = getMatchingPair(importVisual.nodes(), parent,importNode);
				vNodes.getSecond().connectParent(vNodes.getFirst());
			}
		}

		VisualGraphFactory vf = (VisualGraphFactory) importVisual.edgeFactory();
		for (TreeGraphDataNode importNode : importGraph.nodes()) {
			for (ALEdge edge : importNode.edges(Direction.OUT)) {
				log.info("Creating visual edge: " + edge.id());
				String visualId = edge.id();
				Duple<VisualNode, VisualNode> vNodes = getMatchingPair(importVisual.nodes(), edge.startNode(),
						edge.endNode());
				VisualEdge visualEdge = vf.makeEdge(vNodes.getFirst(), vNodes.getSecond(), visualId);
				visualEdge.setConfigEdge(edge);
			}
		}

		Random rnd = new Random();
		for (VisualNode visualNode : importVisual.nodes()) {
			log.info("Initialising " + visualNode.getDisplayText(false));
			visualNode.setCategory();
			visualNode.setPosition(rnd.nextDouble(), rnd.nextDouble());
			visualNode.setCollapse(false);
		}
		if (Project.isOpen()) {
			onProjectClosing();
			Project.close();
		}

		// problem here if the id is not unique within the project scope
		Project.create(twroot.id());
		ConfigGraph.setGraph(importGraph);
		visualGraph = importVisual;
		new TreeLayout(visualGraph).compute();	
		doSave();
		onProjectOpened();
	}

	@Override
	public boolean canClose() {
		if (!GraphState.changed())
			return true;
		switch (Dialogs.yesNoCancel("Project has changed",
				"Save '" + Project.getProjectUserName() + "' before closing?", "")) {
		case yes:
			doSave();
			return true;
		case no:
			return true;
		default:
			return false;
		}
	}

}
