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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;
import au.edu.anu.omhtk.preferences.Preferences;
import au.edu.anu.rscs.aot.archetype.ArchetypeArchetypeConstants;
import au.edu.anu.rscs.aot.collections.tables.StringTable;
import au.edu.anu.rscs.aot.errorMessaging.ErrorMessageManager;
import au.edu.anu.twapps.dialogs.Dialogs;
import au.edu.anu.twapps.exceptions.TwAppsException;
import au.edu.anu.twapps.mm.visualGraph.VisualGraphFactory;
import au.edu.anu.twapps.mm.configGraph.ConfigGraph;
import au.edu.anu.twapps.mm.visualGraph.ElementDisplayText;
import au.edu.anu.twapps.mm.visualGraph.VisualEdge;
import au.edu.anu.twapps.mm.visualGraph.VisualNode;
import au.edu.anu.twcore.archetype.TWA;
import au.edu.anu.twcore.archetype.TwArchetypeConstants;
import au.edu.anu.twcore.archetype.tw.CheckSubArchetypeQuery;
import au.edu.anu.twcore.errorMessaging.ModelBuildErrorMsg;
import au.edu.anu.twcore.errorMessaging.ModelBuildErrors;
import au.edu.anu.twcore.graphState.GraphState;
import au.edu.anu.twcore.project.Project;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.Node;
import fr.cnrs.iees.graph.Tree;
import fr.cnrs.iees.graph.TreeNode;
import fr.cnrs.iees.graph.impl.ALEdge;
import fr.cnrs.iees.graph.impl.SimpleDataTreeNode;
import fr.cnrs.iees.graph.impl.TreeGraph;
import fr.cnrs.iees.graph.impl.TreeGraphDataNode;
import fr.cnrs.iees.graph.io.impl.OmugiGraphExporter;
import fr.cnrs.iees.identity.IdentityScope;
import fr.cnrs.iees.identity.impl.LocalScope;
import fr.cnrs.iees.io.FileImporter;
import fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels;
import fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames;
import fr.cnrs.iees.twcore.generators.ProjectJarGenerator;
import fr.cnrs.iees.twmodels.LibraryTable;
import fr.ens.biologie.generic.utils.Duple;
import fr.ens.biologie.generic.utils.Logging;
import fr.ens.biologie.generic.utils.NameUtils;

import static au.edu.anu.rscs.aot.queries.CoreQueries.*;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.get;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;

/**
 * Author Ian Davies
 *
 * Date 10 Dec. 2018
 */
public class MMModel implements IMMModel, ArchetypeArchetypeConstants {
	// Interface supplied to the controller
	private TreeGraph<VisualNode, VisualEdge> visualGraph;
	private IMMController controller;
	public static String[] mmArgs;

	private static Logger log = Logging.getLogger(MMModel.class);

	public MMModel(IMMController controller) {
		this.controller = controller;
		buildNonEditableList();
	}

	@Override
	public void addState(String desc) {
		if (!Project.getProjectFile().exists()) {
			doSave();
		}
		Preferences.flush();
		controller.putPreferences();

		Caretaker.addState(
				new MMMemento(desc, ConfigGraph.getGraph(), visualGraph, Project.makeProjectPreferencesFile()));

	}

	@Override
	public void doNewProject(String proposedName,TreeGraph<TreeGraphDataNode, ALEdge> templateConfig) {

		/** Does user want to continue if there is unsaved work */
		if (!canClose()) {
			return;
		}
		// collect all relevant ids into a temporary scope.

		IdentityScope prjScope = getProjectScope(templateConfig);
		String newId = getNewProjectName(prjScope, proposedName, "New project", "", "New project name:");
		/** Still not to late. User cancelled */
		if (newId == null)
			return;

		/** now committed to the new project */
		if (Project.isOpen()) {
			onProjectClosing();
			Project.close();
		}

		/** Create and open the project */
		newId = Project.create(newId);

		/** Rename the root id to the project name */
		TreeGraphDataNode twRoot = findTwRoot(templateConfig);
		if (!twRoot.id().equals(newId))
			twRoot.rename(twRoot.id(), newId);

		/** Build a visual graph to shadow the config graph */
		TreeGraph<VisualNode, VisualEdge> templateVisual = buildVisualGraph(templateConfig);

		/** Make these the current graphs */
		ConfigGraph.setGraph(templateConfig);
		visualGraph = templateVisual;

		/** The visual graph parent/child require setting */
		setupParentReferences(visualGraph.root());

		/**
		 * Do all that is required of the ui for a newly created project. Currently,
		 * this just calls the controller to build the ui.
		 */
		onProjectOpened();

		final double duration = 1.0;

		/** Create the default layout. */
		controller.doLayout(duration);

		/** hide the predefined nodes for new models */
		controller.collapsePredef();

		/** Re apply layout after collapsing predefined tree. */
		controller.doLayout(duration);

		/**
		 * Save Config and layout graphs and call config validation. Validation updates
		 * the ui buttons and message display.
		 */

		addState("init");

		doSave();
	}

	private static IdentityScope getProjectScope(TreeGraph<TreeGraphDataNode, ALEdge> graph) {
		LocalScope result = new LocalScope("Projects");
		/**
		 * If a project is open, the project name is the same as the root node name. If
		 * so, this would force an increment in the name meaning this function will inc
		 * by 2 instead of 1. Well this is no good. Prj1 always produces Prj1 for ever.
		 */
		for (String prjName : Project.getAllProjectNames())
			result.newId(true, prjName);

		for (Node n : graph.nodes()) { // check its not already there because project name is the same as root.id()
			if (result.newId(false, n.id()).id().equals(n.id()))
				result.newId(true, n.id());
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void doOpenProject(File file) {

		/** Does user want to continue if there is unsaved work */
		if (!canClose())
			return;

		/** committed to open project */
		if (Project.isOpen()) {
			onProjectClosing();
			Project.close();
		}

		Project.open(file);

		File cFile = Project.makeConfigurationFile();
		File vFile = Project.makeLayoutFile();
		if (!cFile.exists()) {
			Dialogs.errorAlert("File not found", cFile.getName(), "");
			Project.close();
			controller.setDefaultTitle();
			return;
		}
		if (!vFile.exists()) {
			Dialogs.errorAlert("File not found", vFile.getName(), "");
			Project.close();
			controller.setDefaultTitle();
			return;
		}

		TreeGraph<TreeGraphDataNode, ALEdge> newGraph = (TreeGraph<TreeGraphDataNode, ALEdge>) FileImporter
				.loadGraphFromFile(cFile);
		TreeGraph<VisualNode, VisualEdge> importVisual = (TreeGraph<VisualNode, VisualEdge>) FileImporter
				.loadGraphFromFile(vFile);

		if (importVisual.nNodes() != newGraph.nNodes()) {
			Dialogs.errorAlert("File error", file.getName(), "Files '" + cFile.getName() + "' and '" + vFile.getName()
					+ "' do not match. Possibly due to a parsing error in '" + cFile.getName() + "'.");
			Project.close();
			return;
		}

		ConfigGraph.setGraph(newGraph);
		visualGraph = importVisual;
		shadowGraph();

		onProjectOpened();

		addState("init");
	
		ConfigGraph.validateGraph();

	}

	@Override
	@SuppressWarnings("unchecked")
	public void restore(MMMemento m) {
		TreeGraph<TreeGraphDataNode, ALEdge> a = (TreeGraph<TreeGraphDataNode, ALEdge>) FileImporter
				.loadGraphFromFile(m.getState().getFirst());

		TreeGraph<VisualNode, VisualEdge> b = (TreeGraph<VisualNode, VisualEdge>) FileImporter
				.loadGraphFromFile(m.getState().getSecond());

		try {
			Files.copy(m.getState().getThird().toPath(), Project.makeProjectPreferencesFile().toPath(),
					StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}

		controller.getPreferences();

		ConfigGraph.setGraph(a);
		visualGraph = b;

		shadowGraph();

		controller.onRollback(visualGraph);

		ConfigGraph.validateGraph();

	}

	@SuppressWarnings("unchecked")
	@Override
	public void doImport() {

		/** Does user want to continue if there is unsaved work */
		if (!canClose())
			return;

		File file = Dialogs.getExternalProjectFile();
		if (file == null)
			return;

		log.info("Import: " + file);
		TreeGraph<TreeGraphDataNode, ALEdge> importGraph = (TreeGraph<TreeGraphDataNode, ALEdge>) FileImporter
				.loadGraphFromFile(file);

//		Iterator<TreeGraphDataNode> iter = importGraph.roots().iterator();
//
//		int nRoots = 0;
//		while (iter.hasNext()) {
//			iter.next();
//			nRoots++;
//		}
//
		if (importGraph.roots().size() > 1) {
			Dialogs.errorAlert("Import error", "Tree has more than one root.",
					"Graphs with multiple roots cannot be imported");
			return;
		}

		TreeGraphDataNode twRoot = findTwRoot(importGraph);

		if (twRoot == null) {
			Dialogs.errorAlert("Import error", file.getName(),
					"This file does not have a root node called '" + N_ROOT.label() + "'");
			return;
		}

		// (IdentityScope scope, String proposedId, String title, String header, String
		// content)
		IdentityScope prjScope = getProjectScope(importGraph);
		String newId = getNewProjectName(prjScope, "Prj1", "Import '" + file.getName() + "'", "", "New project name:");
		/** Still not to late. User cancelled */
		if (newId == null)
			return;

		if (Project.isOpen()) {
			onProjectClosing();
			Project.close();
		}

		newId = Project.create(newId);
		if (!twRoot.id().equals(newId))
			twRoot.rename(twRoot.id(), newId);

		TreeGraph<VisualNode, VisualEdge> importVisual = buildVisualGraph(importGraph);

		ConfigGraph.setGraph(importGraph);
		visualGraph = importVisual;
		setupParentReferences(visualGraph.root());

		onProjectOpened();

		final double duration = 1.0;
		controller.doLayout(duration);

		/** hide the predefined nodes for imported graphs */
		controller.collapsePredef();

		/** Re apply layout after collapsing predefined tree. */
		controller.doLayout(duration);


		addState("init");

		doSave();
	}

	private void onProjectClosing() {
		controller.onProjectClosing();
		ConfigGraph.terminateChecks();
		ConfigGraph.setGraph(null);
		visualGraph = null;
		Caretaker.finalise();
	}

	private void onProjectOpened() {
		controller.onProjectOpened(visualGraph);
		Caretaker.initialise();
		/** Cleanup stranded undo file */
		MMMemento.deleteStrandedFiles();
	}

	@Override
	public void doSave() {
		File pf = Project.getProjectFile();
		/**
		 * User may have deleted their project during a session but has asked to save or
		 * this has been called following a rollback file error.
		 */
		if (!pf.exists()) {
			pf.mkdirs();
			Caretaker.initialise();
			addState("init");
		}
		new OmugiGraphExporter(Project.makeConfigurationFile()).exportGraph(ConfigGraph.getGraph());
		new OmugiGraphExporter(Project.makeLayoutFile()).exportGraph(visualGraph);
		
		GraphState.clear();
		
		ConfigGraph.validateGraph();
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
			keys.add(TwArchetypeConstants.twaSubclass);
			keys.add("generatedClassName");
			keys.add(P_FUNCTIONCLASS.key());
			keys.add(P_DRIVERCLASS.key());
			keys.add(P_PARAMETERCLASS.key());
			keys.add(P_DECORATORCLASS.key());
			keys.add(P_DATAELEMENTTYPE.key());
			keys.add(P_CONSTANTCLASS.key());

			nonEditableMap.put(key.label(), keys);
		}
		addEntry(N_SPACE, P_SPACETYPE);
		addEntry(N_FIELD, P_FIELD_TYPE);
		addEntry(N_TABLE, P_FIELD_TYPE);// done by P_DATAELEMENTTYPE above
		addEntry(N_COMPONENT, P_PARAMETERCLASS);
		addEntry(N_SYSTEM, P_PARAMETERCLASS);
		addEntry(N_FUNCTION, P_FUNCTIONCLASS);
		addEntry(N_FUNCTION, P_FUNCTIONTYPE);
		addEntry(N_INITFUNCTION,P_FUNCTIONTYPE);
		addEntry(N_COMPONENTTYPE, P_RELOCATEFUNCTION);
		addEntry(N_RECORD,P_DYNAMIC);
		addEntry(N_ROOT,P_MODEL_BUILTBY);

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
	
	@Override
	public Collection<String> unEditablePropertyKeys(String classId) {
		List<String> pKeys = nonEditableMap.get(classId);
		if (pKeys==null)
			pKeys = new ArrayList<>();
		return Collections.unmodifiableCollection(pKeys);		
	}

	private static int nInstances = 0;

	@Override
	public void doDeploy() {

		ProjectJarGenerator gen = new ProjectJarGenerator();
		gen.generate(ConfigGraph.getGraph());
		ErrorMessageManager.endCheck();

		List<String> commands = new ArrayList<>();
		commands.add("java");
		commands.add("-jar");
		commands.add(Project.getProjectUserName() + ".jar");
		commands.add("" + nInstances);// runTimeId??
		nInstances++;
		commands.add(Project.getProjectFile().getName());
		for (String s : mmArgs)
			commands.add(s);

		ProcessBuilder builder = new ProcessBuilder(commands);
		builder.directory(Project.getProjectFile());
		builder.inheritIO();
		try {
			builder.start();
		} catch (Exception e) {
			ErrorMessageManager.dispatch(new ModelBuildErrorMsg(ModelBuildErrors.DEPLOY_EXCEPTION, e, commands));
		}
	}


	private List<String> getQueryStringTableEntries(SimpleDataTreeNode constraint) {
		List<String> result = new ArrayList<>();
		if (constraint == null)
			return result;
		for (String key : constraint.properties().getKeysAsArray()) {
			if (constraint.properties().getPropertyValue(key) instanceof StringTable) {
				StringTable t = (StringTable) constraint.properties().getPropertyValue(key);
				for (int i = 0; i < t.size(); i++)
					result.add(t.getWithFlatIndex(i));
			}
		}
		return result;
	}

	// This uses the twa archetype. If the archetype changes this function may
	// crash;
	private void setupParentReferences(VisualNode vn) {
		Map<String, List<StringTable>> classParentMap = new HashMap<>();
		Set<String> discoveredFiles = new HashSet<>();
		fillClassParentMap(classParentMap, TWA.getRoot(), discoveredFiles);
		vn.setupParentReference(classParentMap);
	}

	private void fillClassParentMap(Map<String, List<StringTable>> classParentMap, TreeNode root,
			Set<String> discoveredFiles) {
		for (TreeNode childSpec : root.getChildren()) {
			String key = (String) ((SimpleDataTreeNode) childSpec).properties().getPropertyValue(aaIsOfClass);
			List<StringTable> value = classParentMap.get(key);
			if (value == null)
				value = new ArrayList<>();
			StringTable item = (StringTable) ((SimpleDataTreeNode) childSpec).properties()
					.getPropertyValue(aaHasParent);
			value.add(item);
			classParentMap.put(key, value);
			// search subA

			@SuppressWarnings("unchecked")
			List<SimpleDataTreeNode> saConstraints = (List<SimpleDataTreeNode>) get(childSpec.getChildren(),
					selectZeroOrMany(hasProperty(aaClassName, CheckSubArchetypeQuery.class.getName())));
			for (SimpleDataTreeNode constraint : saConstraints) {
				List<String> pars = getQueryStringTableEntries(constraint);
				if (pars.get(0).equals(P_SA_SUBCLASS.key())) {
					String fname = pars.get(pars.size() - 1);
					// prevent infinite recursion
					if (!discoveredFiles.contains(fname)) {
						discoveredFiles.add(fname);
						Tree<?> tree = (Tree<?>) TWA.getSubArchetype(fname);
						fillClassParentMap(classParentMap, tree.root(), discoveredFiles);
					}
				}
			}
		}
	}

	private void shadowGraph() {
		for (VisualNode vn : visualGraph.nodes())
			vn.shadowElements(ConfigGraph.getGraph());
	}

	private static Duple<VisualNode, VisualNode> getMatchingPair(Iterable<VisualNode> visualNodes, Node node1,
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

	private static TreeGraphDataNode findTwRoot(TreeGraph<TreeGraphDataNode, ALEdge> graph) {
		for (TreeGraphDataNode root : graph.roots())
			if (root.classId().equals(N_ROOT.label()))
				return root;
		return null;
	}

	private static TreeGraph<VisualNode, VisualEdge> buildVisualGraph(
			TreeGraph<TreeGraphDataNode, ALEdge> importGraph) {
		TreeGraph<VisualNode, VisualEdge> newVisualGraph = new TreeGraph<VisualNode, VisualEdge>(
				new VisualGraphFactory());
		for (TreeGraphDataNode importNode : importGraph.nodes()) {
			log.info("Creating visual node: " + importNode.id());
			Node node = newVisualGraph.nodeFactory().makeNode(importNode.id());
			VisualNode visualNode = (VisualNode) node;
			visualNode.setConfigNode(importNode);
		}

		for (TreeGraphDataNode importNode : importGraph.nodes()) {
			log.info("Creating visual node: " + importNode.id());
			TreeNode parent = importNode.getParent();
			if (parent != null) {
				Duple<VisualNode, VisualNode> vNodes = getMatchingPair(newVisualGraph.nodes(), parent, importNode);
				// child ---------------------- parent
				vNodes.getSecond().connectParent(vNodes.getFirst());
			}

		}

		VisualGraphFactory vf = (VisualGraphFactory) newVisualGraph.edgeFactory();
		for (TreeGraphDataNode importNode : importGraph.nodes()) {
			for (ALEdge edge : importNode.edges(Direction.OUT)) {
				log.info("Creating visual edge: " + edge.id());
				String visualId = edge.id();
				Duple<VisualNode, VisualNode> vNodes = getMatchingPair(newVisualGraph.nodes(), edge.startNode(),
						edge.endNode());
				VisualEdge visualEdge = vf.makeEdge(vNodes.getFirst(), vNodes.getSecond(), visualId);
				visualEdge.setConfigEdge(edge);
				visualEdge.setVisible(true);
			}
		}

		Random rnd = new Random();
		for (VisualNode visualNode : newVisualGraph.nodes()) {
			log.info("Initialising " + visualNode.getDisplayText(ElementDisplayText.RoleName));
			visualNode.setVisible(true);
			visualNode.setCategory();
			visualNode.setPosition(rnd.nextDouble(), rnd.nextDouble());
			visualNode.setCollapse(false);
		}

		return newVisualGraph;
	}

	@Override
	public boolean canClose() {
		if (Project.isOpen()) {
			File pf = Project.getProjectFile();
			if (!pf.exists()) {
				// user has deltled project during a session.
				pf.mkdirs();
				doSave();
				return true;
			}
		}
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

	@Override
	public void doSaveAs() {
		TreeGraphDataNode cRoot = findTwRoot(ConfigGraph.getGraph());
		VisualNode vRoot = null;
		for (VisualNode root : visualGraph.roots())
			if (root.id().equals(cRoot.id()))
				vRoot = root;
		IdentityScope prjScope = getProjectScope(ConfigGraph.getGraph());
		String newId = getNewProjectName(prjScope, vRoot.id(), "Save as", "", "New project name:");
		if (newId == null)
			return;
		if (Project.isOpen()) {
			Project.close();
		}
		String oldId = vRoot.id();
		vRoot.rename(oldId, newId);
		vRoot.getConfigNode().rename(oldId, newId);
		
		Project.create(newId);
		
		doSave();
		
		Preferences.initialise(Project.makeProjectPreferencesFile());
	}

	private String getNewProjectName(IdentityScope scope, String proposedId, String title, String header,
			String content) {
		boolean modified = true;
		String result = scope.newId(false, proposedId).id();
		while (modified) {
			String userName = Dialogs.getText(title, header, content, result, Dialogs.vsAlphaCapAlphaNumeric);
			if (userName == null)
				return null;

			if (userName.equals(""))
				return null;

			userName = NameUtils.validJavaName(userName);

			String newName = scope.newId(false, userName).id();
			modified = !newName.equals(userName);
			result = newName;
		}
		return result;

	}

	@Override
	public LibraryTable[] getLibrary() {
		return LibraryTable.values();
	}


}
