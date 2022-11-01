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
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.logging.Logger;

import au.edu.anu.omhtk.preferences.*;
import au.edu.anu.aot.archetype.Archetypes;
import fr.cnrs.iees.omugi.collections.tables.StringTable;
import au.edu.anu.aot.errorMessaging.ErrorMessageManager;
import au.edu.anu.omhtk.util.FileUtilities;
import au.edu.anu.twapps.dialogs.DialogsFactory;
import au.edu.anu.twapps.mm.configGraph.ConfigGraph;
import au.edu.anu.twapps.mm.layoutGraph.*;
import au.edu.anu.twapps.mm.undo.Caretaker;
import au.edu.anu.twapps.mm.undo.MMMemento;
import au.edu.anu.twcore.archetype.TWA;
import au.edu.anu.twcore.archetype.tw.CheckSubArchetypeQuery;
import au.edu.anu.twcore.errorMessaging.*;
import au.edu.anu.twcore.graphState.*;
import au.edu.anu.twcore.project.Project;
import au.edu.anu.twcore.userProject.UserProjectLink;
import fr.cnrs.iees.omugi.graph.*;
import fr.cnrs.iees.omugi.graph.impl.*;
import fr.cnrs.iees.omugi.graph.io.impl.OmugiGraphExporter;
import fr.cnrs.iees.omugi.identity.IdentityScope;
import fr.cnrs.iees.omugi.identity.impl.LocalScope;
import fr.cnrs.iees.omugi.io.FileImporter;
import fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels;
import fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames;
import fr.cnrs.iees.twcore.generators.ProjectJarGenerator;
import fr.cnrs.iees.twmodels.LibraryTable;
import fr.cnrs.iees.omhtk.utils.*;

import static au.edu.anu.qgraph.queries.CoreQueries.*;
import static au.edu.anu.qgraph.queries.base.SequenceQuery.get;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;

/**
 * @author Ian Davies - 10 Dec. 2018
 */
public class MMModelImpl implements MMModel {
	/**
	 * Graph containing layout information for the configuration graph.
	 */
	private TreeGraph<LayoutNode, LayoutEdge> layoutGraph;
	/**
	 * Reference to the controller interface ({@link MMController}).
	 */
	private final MMController controller;
	/**
	 * Arguments (such as logging requests), for this instance of ModelMaker, passed
	 * on from the main class that are also passed to ModelRunner when deployed from
	 * ModelMaker.
	 */
	private static String[] mmArgs;
	/**
	 * A lookup table of all 3Worlds node classes and their possible parent tables.
	 * This is used to correctly identify the parent of a visual node even if the
	 * tree is broken leading to possible ambiguity.
	 */
	private final Map<String, List<StringTable>> classParentTableMapping;

	/**
	 * Static logger for this class.
	 */
	private static Logger log = Logging.getLogger(MMModelImpl.class);

	/**
	 * Constructor associates a controller with this model (<a href=
	 * "https://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93controller">Model-View-Controller</a>).
	 * 
	 * @param controller The controller interface
	 */
	public MMModelImpl(MMController controller) {
		this.controller = controller;
		buildNonEditablePropertyList();
		classParentTableMapping = getLabelParentTableMapping();
	}

	/**
	 * Helper method to state creation of {@link classParentTableMapping}.
	 * 
	 * @return The constructed mapping of node class and parent table entries.
	 */
	private Map<String, List<StringTable>> getLabelParentTableMapping() {
		Set<String> discoveredFiles = new HashSet<>();
		Map<String, List<StringTable>> map = new HashMap<>();
		fillClassParentMap(map, TWA.getRoot(), discoveredFiles);
		return map;
	}

	/**
	 * Set the command line args passed from the main class. This usually include
	 * logging requests.
	 * 
	 * @param args Command line arguments.
	 */
	public static void setMMArgs(String[] args) {
		mmArgs = args;
	}

	/**
	 * Save the current state to the {@link Caretaker} for the undo/redo memento
	 * pattern.
	 */
	@Override
	public final void addState(String desc) {
		if (!Project.getProjectFile().exists()) {
			doSave();
		}
		// Store current preferences so they can be restored with this state.
		PreferenceService.getImplementation().flush();
		controller.putPreferences();

		Caretaker.addState(
				new MMMemento(desc, ConfigGraph.getGraph(), layoutGraph, Project.makeProjectPreferencesFile()));

	}

	@Override
	public void doNewProject(String proposedName, TreeGraph<TreeGraphDataNode, ALEdge> templateConfig,
			InputStream archiveStream) {

		/** Does user want to continue if there is unsaved work */
		if (!canClose()) {
			return;
		}

		/** collect all relevant ids into a temporary scope to enfore a unique name. */
		IdentityScope projectScope = getProjectScope(templateConfig);
		String newId = getNewProjectName(projectScope, proposedName, "New project", "", "New project name:");
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
		TreeGraph<LayoutNode, LayoutEdge> templateVisual = buildLayoutGraph(templateConfig);

		/** Make these the current graphs */
		ConfigGraph.setGraph(templateConfig);
		layoutGraph = templateVisual;

		/** The visual graph parent/child require setting */
		layoutGraph.root().setParentReference(classParentTableMapping);
//		setupParentReferences(visualGraph.root());

		/**
		 * Do all that is required of the ui for a newly created project. Currently,
		 * this just calls the controller to build the ui.
		 */
		onProjectOpened();

		final double duration = 1.0;

		/** Create the default layout. */
		controller.doLayout(duration);

		/** hide the predefined nodes for new models */
//		controller.collapsePredef();
		controller.visualiser().collapsePredef();

		/** Re apply layout after collapsing predefined tree. */
		controller.doLayout(duration);

		/**
		 * Save Config and layout graphs and call config validation. Validation updates
		 * the ui buttons and message display.
		 */

		addState("init");

		if (archiveStream != null) {
			// extract to project root. NB something must be done if importing.
			String destDirectory = Project.getProjectDirectory();
			UnzipUtility unzipper = new UnzipUtility();
			try {
				unzipper.unzip(archiveStream, destDirectory);
			} catch (Exception ex) {
				ex.printStackTrace();
			}

		}

		doSave();
	}

	/**
	 * Builds a local scope of project and graph element ids to ensure the root id
	 * will be unique.
	 * 
	 * @param graph The configuration graph for the proposed new project.
	 * @return The scope entries of unique names in use.
	 */
	private static IdentityScope getProjectScope(TreeGraph<TreeGraphDataNode, ALEdge> graph) {
		LocalScope result = new LocalScope("Projects");
		/**
		 * Collect all project names (i.e the "name" part of project_<name>_date)
		 */
		for (String prjName : Project.getAllProjectNames())
			result.newId(true, prjName);

		/**
		 * Add the ids of all elements except the root id - preserve this as a possible
		 * proposed id.
		 */
		for (Node n : graph.nodes()) {
			if (!n.classId().equals(N_ROOT.label()))
				/** check its not already there so we don't accidently add incremented ids! */
				if (result.newId(false, n.id()).id().equals(n.id()))
					result.newId(true, n.id());
			/**
			 * add outEdge ids! E.g. we don't want a project called "Trk1"!!(Caps are not
			 * enforced)
			 */
			for (Edge e : n.edges(Direction.OUT)) {
				if (result.newId(false, e.id()).id().equals(e.id()))
					result.newId(true, e.id());
			}
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
			DialogsFactory.errorAlert("File not found", cFile.getName(), "");
			Project.close();
			controller.setDefaultTitle();
			return;
		}
		if (!vFile.exists()) {
			DialogsFactory.errorAlert("File not found", vFile.getName(), "");
			Project.close();
			controller.setDefaultTitle();
			return;
		}

		TreeGraph<TreeGraphDataNode, ALEdge> newGraph = (TreeGraph<TreeGraphDataNode, ALEdge>) FileImporter
				.loadGraphFromFile(cFile);
		TreeGraph<LayoutNode, LayoutEdge> importVisual = (TreeGraph<LayoutNode, LayoutEdge>) FileImporter
				.loadGraphFromFile(vFile);

		if (importVisual.nNodes() != newGraph.nNodes()) {
			DialogsFactory.errorAlert("File error", file.getName(), "Files '" + cFile.getName() + "' and '" + vFile.getName()
					+ "' do not match. Possibly due to a parsing error in '" + cFile.getName() + "'.");
			Project.close();
			return;
		}

		ConfigGraph.setGraph(newGraph);
		layoutGraph = importVisual;
		shadowGraph();

		onProjectOpened();

		addState("init");

		ConfigGraph.verifyGraph();

	}

	@Override
	@SuppressWarnings("unchecked")
	public final void restore(MMMemento m) {
		// get the prev config graph
		TreeGraph<TreeGraphDataNode, ALEdge> a = (TreeGraph<TreeGraphDataNode, ALEdge>) FileImporter
				.loadGraphFromFile(m.getState().getFirst());

		// get the prev layout graph
		TreeGraph<LayoutNode, LayoutEdge> b = (TreeGraph<LayoutNode, LayoutEdge>) FileImporter
				.loadGraphFromFile(m.getState().getSecond());

		// get the prev preferences data
		try {
			Files.copy(m.getState().getThird().toPath(), Project.makeProjectPreferencesFile().toPath(),
					StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// set prev config graph
		ConfigGraph.setGraph(a);

		// set prev layout graph
		layoutGraph = b;

		// load prev preferences
		controller.getPreferences();

		// link both graphs
		shadowGraph();

		// update the ui
		controller.onRollback(layoutGraph);

		// verify
		ConfigGraph.verifyGraph();

	}

	@SuppressWarnings("unchecked")
	@Override
	public void saveAndReload() {
		String tmpdir = System.getProperty("java.io.tmpdir");
		File f;
		f = new File(tmpdir + File.separator + "tmp.utg");
		new OmugiGraphExporter(f).exportGraph(ConfigGraph.getGraph());

		TreeGraph<TreeGraphDataNode, ALEdge> a = (TreeGraph<TreeGraphDataNode, ALEdge>) FileImporter
				.loadGraphFromFile(f);

		new OmugiGraphExporter(f).exportGraph(layoutGraph);

		TreeGraph<LayoutNode, LayoutEdge> b = (TreeGraph<LayoutNode, LayoutEdge>) FileImporter.loadGraphFromFile(f);

		ConfigGraph.setGraph(a);

		// set prev layout graph
		layoutGraph = b;

		shadowGraph();

		// update the ui
		controller.onRollback(layoutGraph);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void doImport() {

		/** Does user want to continue if there is unsaved work */
		if (!canClose())
			return;

		File file = DialogsFactory.getExternalProjectFile();
		if (file == null)
			return;

		log.info("Import: " + file);
		TreeGraph<TreeGraphDataNode, ALEdge> importGraph = (TreeGraph<TreeGraphDataNode, ALEdge>) FileImporter
				.loadGraphFromFile(file);

		if (importGraph.roots().size() > 1) {
			DialogsFactory.errorAlert("Import error", "Tree has more than one root.",
					"Graphs with multiple roots cannot be imported");
			return;
		}

		TreeGraphDataNode twRoot = findTwRoot(importGraph);

		if (twRoot == null) {
			DialogsFactory.errorAlert("Import error", file.getName(),
					"This file does not have a root node called '" + N_ROOT.label() + "'");
			return;
		}

		String proposedId = twRoot.id();

		IdentityScope prjScope = getProjectScope(importGraph);
		String newId = getNewProjectName(prjScope, proposedId, "Import '" + file.getName() + "'", "",
				"New project name:");
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

		TreeGraph<LayoutNode, LayoutEdge> importVisual = buildLayoutGraph(importGraph);

		ConfigGraph.setGraph(importGraph);
		layoutGraph = importVisual;
		layoutGraph.root().setParentReference(classParentTableMapping);
//		setupParentReferences(visualGraph.root());

		onProjectOpened();

		final double duration = 1.0;
		controller.doLayout(duration);

		/** hide the predefined nodes for imported graphs */
//		controller.collapsePredef();
		controller.visualiser().collapsePredef();

		/** Re apply layout after collapsing predefined tree. */
		controller.doLayout(duration);
		/**
		 * check for any code dependencies. TODO need to check for 3rd party jars
		 */
		List<File> depFiles = getDependentJavaFiles(importGraph.root());
		for (File outfile : depFiles) {
			File infile = DialogsFactory.getOpenFile(new File(System.getProperty("user.home")), "Import " + outfile.getName(),
					new String("Java files,*.java"));
			if (infile != null)
				FileUtilities.copyFileReplace(infile, outfile);
		}

		addState("init");

		doSave();
	}

	/**
	 * Searches the {@code importSnippet} property for any import statements that
	 * begins with the key word 'code'. These entries indicate a dependency on user
	 * code.
	 * 
	 * This method assumes the Project is open.
	 * 
	 * @param root The configuration root node containing the {@code importSnippet}
	 *             property.
	 * @return list of destination files.
	 */
	private List<File> getDependentJavaFiles(TreeGraphDataNode root) {
		List<File> result = new ArrayList<>();
		// e.g. static code.utilities.Utilities3A.*
		StringTable importTable = (StringTable) root.properties().getPropertyValue(P_MODEL_IMPORTSNIPPET.key());
		for (int i = 0; i < importTable.size(); i++) {
			String s = importTable.getByInt(i);
			s = s.replace("static", "");
			s = s.replace(".*", "").trim();
			if (s.startsWith(Project.CODE)) {
				s = Project.LOCAL_JAVA_PKG + "." + s;
				String[] parts = s.split("\\.");
				parts[parts.length - 1] += ".java";
				result.add(Project.makeFile(parts));
			}
		}
		return result;

	}

	/**
	 * Action to take when closing a {@link Project}.
	 */
	private void onProjectClosing() {
		controller.onProjectClosing();
		ConfigGraph.terminateChecks();
		ConfigGraph.close();
		layoutGraph = null;
		Caretaker.finalise();
	}

	/**
	 * Actions required when after opening a {@link Project}.
	 */
	private void onProjectOpened() {
		controller.onProjectOpened(layoutGraph);
		Caretaker.initialise();
		/** Cleanup stranded undo file */
		MMMemento.deleteStrandedFiles();
	}

	@Override
	public void doSave() {
		File pf = Project.getProjectFile();
		if (!pf.exists()) {
			/**
			 * Whoops - either the user has deleted their project during a session or save
			 * has been requested following a rollback file error.
			 */
			pf.mkdirs();
			Caretaker.initialise();
			addState("init");
		}
		new OmugiGraphExporter(Project.makeConfigurationFile()).exportGraph(ConfigGraph.getGraph());
		new OmugiGraphExporter(Project.makeLayoutFile()).exportGraph(layoutGraph);

		GraphStateFactory.clear();

		ConfigGraph.verifyGraph();
	}

	/**
	 * A look-up table of labels (classId) and property keys that are intended to be
	 * immutable.
	 */
	private Map<String, List<String>> nonEditableMap = new HashMap<>();

	/**
	 * List a property key associated with a node that is to be listed as immutable.
	 * 
	 * @param nl
	 * @param pn
	 */
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

	/**
	 * List all immutable properties so they can be disabled in a UI.
	 */
	private void buildNonEditablePropertyList() {
		for (ConfigurationNodeLabels key : ConfigurationNodeLabels.values()) {
			List<String> keys = new ArrayList<>();
			// default - subclass is never editable
			keys.add(TWA.SUBCLASS);
			keys.add(P_TWDATACLASS.key());
			keys.add(P_FUNCTIONCLASS.key());
			keys.add(P_DRIVERCLASS.key());
			keys.add(P_DECORATORCLASS.key());
			keys.add(P_DATAELEMENTTYPE.key());
			keys.add(P_CONSTANTCLASS.key());

			nonEditableMap.put(key.label(), keys);
		}
		addEntry(N_SPACE, P_SPACETYPE);
		addEntry(N_FIELD, P_FIELD_TYPE);
//		addEntry(N_TABLE, P_FIELD_TYPE);// done by P_DATAELEMENTTYPE above
		addEntry(N_FUNCTION, P_FUNCTIONCLASS);
		addEntry(N_FUNCTION, P_FUNCTIONTYPE);
		addEntry(N_INITFUNCTION, P_FUNCTIONTYPE);
		addEntry(N_COMPONENTTYPE, P_RELOCATEFUNCTION);
		addEntry(N_RECORD, P_DYNAMIC);
		addEntry(N_ROOT, P_MODEL_BUILTBY);

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
		if (pKeys == null)
			pKeys = new ArrayList<>();
		return Collections.unmodifiableCollection(pKeys);
	}

	/**
	 * The number of deployment instances for a session is recorded and passed to
	 * ModelRunner. This maybe used to prevent file name ambiguity.
	 * <p>
	 * TODO The need for this should be reassessed.
	 */
	@Deprecated
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

	/**
	 * A helper method to extract query table entries in a list.
	 * 
	 * @param constraint The query
	 * @return List of table entries for the given query (can be empty).
	 */
	static private List<String> getQueryStringTableEntries(SimpleDataTreeNode constraint) {
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
//	private void setupParentReferences(VisualNode vn) {
//		vn.setupParentReference(classParentTableMapping);
//	}

	private void fillClassParentMap(Map<String, List<StringTable>> classParentMap, TreeNode root,
			Set<String> discoveredFiles) {
		for (TreeNode childSpec : root.getChildren()) {
			String key = (String) ((SimpleDataTreeNode) childSpec).properties()
					.getPropertyValue(Archetypes.IS_OF_CLASS);
			List<StringTable> value = classParentMap.get(key);
			if (value == null)
				value = new ArrayList<>();
			StringTable item = (StringTable) ((SimpleDataTreeNode) childSpec).properties()
					.getPropertyValue(Archetypes.HAS_PARENT);
			value.add(item);
			classParentMap.put(key, value);
			// search subA

			@SuppressWarnings("unchecked")
			List<SimpleDataTreeNode> saConstraints = (List<SimpleDataTreeNode>) get(childSpec.getChildren(),
					selectZeroOrMany(hasProperty(Archetypes.CLASS_NAME, CheckSubArchetypeQuery.class.getName())));
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
		for (LayoutNode vn : layoutGraph.nodes())
			vn.shadowElements(ConfigGraph.getGraph());
	}

	private static Duple<LayoutNode, LayoutNode> getMatchingPair(Iterable<LayoutNode> visualNodes, Node node1,
			Node node2) {
		LayoutNode resultNode1 = null;
		LayoutNode resultNode2 = null;

		for (LayoutNode node : visualNodes) {
			if (node.id().equals(node1.id()))
				resultNode1 = node;
			else if (node.id().equals(node2.id()))
				resultNode2 = node;
			if (resultNode1 != null && resultNode2 != null)
				return new Duple<LayoutNode, LayoutNode>(resultNode1, resultNode2);
		}
		throw new IllegalStateException(
				"Matching node pair not found in visual graph [" + node1.id() + "," + node2.id() + "]");
	}

	private static TreeGraphDataNode findTwRoot(TreeGraph<TreeGraphDataNode, ALEdge> graph) {
		for (TreeGraphDataNode root : graph.roots())
			if (root.classId().equals(N_ROOT.label()))
				return root;
		return null;
	}

	private static TreeGraph<LayoutNode, LayoutEdge> buildLayoutGraph(
			TreeGraph<TreeGraphDataNode, ALEdge> importGraph) {
		TreeGraph<LayoutNode, LayoutEdge> newVisualGraph = new TreeGraph<LayoutNode, LayoutEdge>(
				new LayoutGraphFactory());
		for (TreeGraphDataNode importNode : importGraph.nodes()) {
			log.info("Creating visual node: " + importNode.id());
			Node node = newVisualGraph.nodeFactory().makeNode(importNode.id());
			LayoutNode visualNode = (LayoutNode) node;
			visualNode.setConfigNode(importNode);
		}

		for (TreeGraphDataNode importNode : importGraph.nodes()) {
			log.info("Creating visual node: " + importNode.id());
			TreeNode parent = importNode.getParent();
			if (parent != null) {
				Duple<LayoutNode, LayoutNode> vNodes = getMatchingPair(newVisualGraph.nodes(), parent, importNode);
				// child ---------------------- parent
				vNodes.getSecond().connectParent(vNodes.getFirst());
			}

		}

		LayoutGraphFactory vf = (LayoutGraphFactory) newVisualGraph.edgeFactory();
		for (TreeGraphDataNode importNode : importGraph.nodes()) {
			for (ALEdge edge : importNode.edges(Direction.OUT)) {
				log.info("Creating visual edge: " + edge.id());
				String visualId = edge.id();
				Duple<LayoutNode, LayoutNode> vNodes = getMatchingPair(newVisualGraph.nodes(), edge.startNode(),
						edge.endNode());
				LayoutEdge visualEdge = vf.makeEdge(vNodes.getFirst(), vNodes.getSecond(), visualId);
				visualEdge.setConfigEdge(edge);
				visualEdge.setVisible(true);
			}
		}

		Random rnd = new Random();
		for (LayoutNode visualNode : newVisualGraph.nodes()) {
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
		if (!GraphStateFactory.changed())
			return true;

		switch (DialogsFactory.yesNoCancel("Project has changed",
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
		if (UserProjectLink.haveUserProject()) {
			String title = "Save as";
			String header = "Cannot save projects linked to an IDE under a new name.";
			String content = "Disconnect '" + Project.getDisplayName() + "' from '"
					+ UserProjectLink.projectRoot().getName() + "' before saving under a new name.";
			DialogsFactory.errorAlert(title, header, content);
			return;
		}
		TreeGraphDataNode cRoot = findTwRoot(ConfigGraph.getGraph());
		LayoutNode vRoot = null;
		for (LayoutNode root : layoutGraph.roots())
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
		vRoot.configNode().rename(oldId, newId);

		Project.create(newId);

		doSave();

		PreferenceService.setImplementation(new PrefImpl(Project.makeProjectPreferencesFile()));

		// force a rebuild of the property editors
		controller.onRootNameChange();
	}

	private String getNewProjectName(IdentityScope scope, String proposedId, String title, String header,
			String content) {
		boolean modified = true;
		String result = scope.newId(false, proposedId).id();
		while (modified) {
			String userName = DialogsFactory.getText(title, header, content, result, DialogsFactory.REGX_ALPHA_CAP_NUMERIC);
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
