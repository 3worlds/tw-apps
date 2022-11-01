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

package au.edu.anu.twapps.mm.graphEditor;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.*;

import org.apache.commons.text.WordUtils;

import au.edu.anu.aot.archetype.Archetypes;
import fr.cnrs.iees.omugi.collections.tables.*;
import au.edu.anu.omhtk.util.*;
import au.edu.anu.twapps.dialogs.DialogService;
import au.edu.anu.twapps.mm.GraphVisualiser;
import au.edu.anu.twapps.mm.MMController;
import au.edu.anu.twapps.mm.configGraph.ConfigGraph;
import au.edu.anu.twapps.mm.layoutGraph.ElementDisplayText;
import au.edu.anu.twapps.mm.layoutGraph.LayoutEdge;
import au.edu.anu.twapps.mm.layoutGraph.LayoutGraphFactory;
import au.edu.anu.twapps.mm.layoutGraph.LayoutNode;
import au.edu.anu.twcore.archetype.TWA;
import au.edu.anu.twcore.archetype.tw.*;
import au.edu.anu.twcore.graphState.*;
import au.edu.anu.twcore.project.Project;
import au.edu.anu.twcore.root.EditableFactory;
import au.edu.anu.twcore.root.TwConfigFactory;
import au.edu.anu.twcore.userProject.UserProjectLink;
import fr.cnrs.iees.omugi.graph.*;
import fr.cnrs.iees.omugi.graph.impl.*;
import fr.cnrs.iees.omugi.graph.io.impl.OmugiGraphExporter;
import fr.cnrs.iees.omugi.identity.impl.PairIdentity;
import fr.cnrs.iees.omugi.io.FileImporter;
import fr.cnrs.iees.omugi.io.parsing.ValidPropertyTypes;
import fr.cnrs.iees.omugi.properties.ExtendablePropertyList;
import fr.cnrs.iees.omugi.properties.SimplePropertyList;
import fr.cnrs.iees.omugi.properties.impl.SimplePropertyListImpl;
import fr.cnrs.iees.twcore.constants.BorderListType;
import fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels;
import fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels;
import fr.cnrs.iees.twcore.constants.ConfigurationReservedNodeId;
import fr.cnrs.iees.twcore.constants.SpaceType;
import fr.cnrs.iees.twcore.constants.TwFunctionTypes;
import fr.cnrs.iees.uit.space.Box;
import fr.cnrs.iees.uit.space.Point;

import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.*;

import fr.cnrs.iees.omhtk.codeGeneration.JavaUtilities;
import fr.cnrs.iees.omhtk.utils.*;
import static au.edu.anu.qgraph.queries.CoreQueries.*;
import static au.edu.anu.qgraph.queries.base.SequenceQuery.*;

/**
 * Adapter class to perform the work of the {@link StructureEditor} interface.
 * 
 * @author Ian Davies - 10 Jan. 2019
 */
public abstract class StructureEditorAdapter implements StructureEditor {
	private static Logger log = Logging.getLogger(StructureEditorAdapter.class);

	/**
	 * Reference to the 3Worlds {@link Specifications} interface.
	 */
	protected final Specifications specifications;

	/**
	 * Reference to the {@link NodeEditor} interface with its underlying
	 * {@VisualNode}. This is the currently selected node for editing.
	 */
	protected final NodeEditor nodeEditor;
	/**
	 * Reference to any newly constructed node. If this is not null when the editor
	 * closes (not all operations involve node creation) and the user is prompted to
	 * place the node somewhere in the view.
	 */
	protected LayoutNode newChild;

	/**
	 * The specifications of the node, currently selected for editing.
	 */
	protected final SimpleDataTreeNode baseSpec;

	/* specifications of subclass of this editingNode if it has one */
	/**
	 * The specifications of the sub-class of the node, currently selected for
	 * editing (can be null).
	 */
	protected final SimpleDataTreeNode subClassSpec;

	/**
	 * Reference to the graph {@link GraphVisualiser} interface.
	 */
	protected final GraphVisualiser visualiser;

	protected final MMController controller;

	/**
	 * 
	 * @param nodeEditor The selected node for editing {@link NodeEditor}.
	 * @param visualiser Interface to the {@link GraphVisualiser}.
	 * @param controller The model controller {@link MMController}.
	 */
	public StructureEditorAdapter(NodeEditor nodeEditor, GraphVisualiser visualiser, MMController controller) {
		super();
		this.specifications = new TwSpecifications();
		this.controller = controller;
		this.newChild = null;
		this.nodeEditor = nodeEditor;
		Set<String> discoveredFile = new HashSet<>();
		this.baseSpec = specifications.getSpecsOf(nodeEditor, TWA.getRoot(), discoveredFile);
		if (baseSpec == null)
			throw new NullPointerException("Specification for '" + nodeEditor + "' was not found.");

		this.subClassSpec = specifications.getSubSpecsOf(baseSpec, nodeEditor.getSubClass());
		this.visualiser = visualiser;
		log.info("BaseSpec: " + baseSpec);
		log.info("SubSpec: " + subClassSpec);
	}

	@Override
	public List<SimpleDataTreeNode> filterChildSpecs(Iterable<SimpleDataTreeNode> childSpecs) {
		List<SimpleDataTreeNode> result = new ArrayList<SimpleDataTreeNode>();
		Collection<String[]> tables = specifications.getQueryStringTables(baseSpec, ChildXorPropertyQuery.class);
		tables.addAll(specifications.getQueryStringTables(subClassSpec, ChildXorPropertyQuery.class));
		for (SimpleDataTreeNode childSpec : childSpecs) {
			boolean isPredef = false;
			if (childSpec.properties().hasProperty(Archetypes.HAS_ID))
				isPredef = isPredefined(childSpec);
			if (!isPredef) {
				String childLabel = (String) childSpec.properties().getPropertyValue(Archetypes.IS_OF_CLASS);
				IntegerRange range = specifications.getMultiplicityOf(childSpec);
				if (nodeEditor.moreChildrenAllowed(range, childLabel)) {
					if (!tables.isEmpty()) {
						if (allowedChild(childLabel, tables))
							result.add(childSpec);
					} else
						result.add(childSpec);
				}
			}
		}
		result.sort((n1, n2) -> n1.id().compareToIgnoreCase(n2.id()));
		return result;
	}

	@Override
	public List<Tuple<String, LayoutNode, SimpleDataTreeNode>> filterEdgeSpecs(Iterable<SimpleDataTreeNode> edgeSpecs) {
		List<Tuple<String, LayoutNode, SimpleDataTreeNode>> result = new ArrayList<>();
		for (SimpleDataTreeNode edgeSpec : edgeSpecs) {
			String toNodeRef = (String) edgeSpec.properties().getPropertyValue(Archetypes.TO_NODE);
			String edgeLabel = (String) edgeSpec.properties().getPropertyValue(Archetypes.IS_OF_CLASS);
			String toNodeLabel = getEndNodeLabel(toNodeRef);
			log.info(edgeLabel);
			List<LayoutNode> endNodes = findNodesReferencedBy(toNodeRef);
			for (LayoutNode endNode : endNodes) {
				if (!nodeEditor.getName().equals(endNode.id())) // no edges to self
					if (!nodeEditor.hasOutEdgeTo(endNode, edgeLabel))
						if (satisfiesEdgeMultiplicity(edgeSpec, toNodeLabel, edgeLabel))
							if (satisfyExclusiveCategoryQuery(edgeSpec, endNode, edgeLabel))
								if (satisfyOutNodeXorQuery(edgeSpec, endNode, edgeLabel))
									if (satisfyOutEdgeXorQuery(edgeSpec, endNode, edgeLabel))
										if (satisfyEndNodeHasPropertyQuery(edgeSpec, endNode, edgeLabel))
											if (satisfyCheckConstantTrackingQuery(edgeSpec, endNode, edgeLabel))
												result.add(new Tuple<String, LayoutNode, SimpleDataTreeNode>(edgeLabel,
														endNode, edgeSpec));
			}
		}
		result.sort((n1, n2) -> {
			String s1 = n1.getFirst() + n1.getSecond();
			String s2 = n2.getFirst() + n2.getSecond();
			return s1.compareToIgnoreCase(s2);
		});
		return result;
	}

	public List<LayoutNode> getOrphanedChildren(Iterable<SimpleDataTreeNode> childSpecs) {
		List<LayoutNode> result = new ArrayList<>();
		for (LayoutNode root : visualiser.getLayoutGraph().roots()) {
			String rootLabel = root.configNode().classId();
			for (SimpleDataTreeNode childSpec : childSpecs) {
				String specLabel = (String) childSpec.properties().getPropertyValue(Archetypes.IS_OF_CLASS);
				if (rootLabel.equals(specLabel))
					result.add(root);
			}
		}
		result.sort((n1, n2) -> n1.id().compareToIgnoreCase(n2.id()));
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onNewChild(String childLabel, String childId, SimpleDataTreeNode childBaseSpec) {
		String newId = getNewId(childLabel, childId, childBaseSpec);
		if (newId == null)
			// operation cancelled by user
			return;
		Duple<String, Class<? extends TreeNode>> subClassInfo = getSubClassInfo(childBaseSpec);
		if (subClassInfo == null)
			// operation cancelled by user
			return;
		SimpleDataTreeNode childSubSpec = specifications.getSubSpecsOf(childBaseSpec, subClassInfo.getSecond());

		// get the unfiltered propertySpecs
		List<SimpleDataTreeNode> propertySpecs = specifications.getPropertySpecsOf(childBaseSpec, childSubSpec);
		if (!specifications.filterPropertyStringTableOptions(propertySpecs, childBaseSpec, childSubSpec,
				subClassInfo.getFirst() + PairIdentity.LABEL_NAME_SEPARATOR + newId, ChildXorPropertyQuery.class,
				PropertyXorQuery.class))
			// operation cancelled by user
			return;

		propertySpecs = removeOptionalProperties(propertySpecs, childBaseSpec, childSubSpec);

		newChild = createChild(childLabel, newId, childBaseSpec);

		setDefaultPropertyValues(propertySpecs, subClassInfo);

		specifications.filterRequiredPropertyQuery(newChild, childBaseSpec, childSubSpec);

		controller.onNewNode(newChild);
	}

	@Override
	public void onNewEdge(Tuple<String, LayoutNode, SimpleDataTreeNode> details, double duration) {
		if (nodeEditor.isCollapsed())
			visualiser.expandTreeFrom(nodeEditor.layoutNode(), duration);
		String id = getNewName(details.getFirst(), details.getFirst(),
				ConfigurationEdgeLabels.labelValueOf(details.getFirst()).defName(), null);
		if (id == null)
			// operation cancelled by user.
			return;
		connectTo(id, details, duration);
		ConfigGraph.verifyGraph();
		GraphStateService.getImplementation().setChanged();
	}

	@Override
	public void onDeleteNode(double duration) {
		deleteNode(nodeEditor.layoutNode(), duration);
		controller.onNodeDeleted();
		GraphStateService.getImplementation().setChanged();
		ConfigGraph.verifyGraph();
	}

	@Override
	public boolean onRenameNode() {
		String userName = getNewName(nodeEditor.toString(), nodeEditor.getLabel(), nodeEditor.getName(), baseSpec);
		if (userName != null) {
			renameNode(userName, nodeEditor.layoutNode());
			visualiser.onNodeRenamed(nodeEditor.layoutNode());
			controller.onElementRenamed();
			return true;
		}
		// operation cancelled by user.
		return false;
	}

	@Override
	public boolean onRenameEdge(LayoutEdge edge) {
		String lbl = edge.getConfigEdge().classId();
		String userName = getNewName(lbl + ":" + edge.id(), lbl, ConfigurationEdgeLabels.labelValueOf(lbl).defName(),
				null);
		if (userName != null) {
			renameEdge(userName, edge);
			visualiser.onEdgeRenamed(edge);
			controller.onElementRenamed();
			return true;
		}
		return false;
		// cancelled by user
	}

	@Override
	public void onCollapseTree(LayoutNode childRoot, double duration) {
		visualiser.collapseTreeFrom(childRoot, duration);
		controller.onTreeCollapse();
		GraphStateService.getImplementation().setChanged();
	}

	@Override
	public void onCollapseTrees(double duration) {
		for (LayoutNode child : nodeEditor.getChildren()) {
			if (!child.isCollapsed())
				visualiser.collapseTreeFrom(child, duration);
		}
		controller.onTreeCollapse();
		GraphStateService.getImplementation().setChanged();
	}

	@Override
	public void onExpandTree(LayoutNode childRoot, double duration) {
		visualiser.expandTreeFrom(childRoot, duration);
		controller.onTreeExpand();
		GraphStateService.getImplementation().setChanged();
	}

	@Override
	public void onExpandTrees(double duration) {
		for (LayoutNode child : nodeEditor.getChildren()) {
			if (child.isCollapsed())
				visualiser.expandTreeFrom(child, duration);
		}
		controller.onTreeExpand();
		GraphStateService.getImplementation().setChanged();
	}

	@Override
	public void onReconnectChild(LayoutNode vnChild) {
		nodeEditor.connectChild(vnChild);
		visualiser.onNewParent(vnChild);
		ConfigGraph.verifyGraph();
		GraphStateService.getImplementation().setChanged();
	}

	@Override
	public void onDeleteTree(LayoutNode root, double duration) {
		deleteTree(root, duration);
		controller.onNodeDeleted();
		GraphStateService.getImplementation().setChanged();
		ConfigGraph.verifyGraph();
	}

	@Override
	public void onDeleteEdge(LayoutEdge edge) {
		boolean mayHaveProperties = false;
		if (edge.getConfigEdge() instanceof ALDataEdge)
			mayHaveProperties = true;
		deleteEdge(edge);
		if (mayHaveProperties)
			controller.onEdgeDeleted();
		GraphStateService.getImplementation().setChanged();
		ConfigGraph.verifyGraph();
	}

	@Override
	public void onExportTree(LayoutNode root) {
		String filePrompt = root.getDisplayText(ElementDisplayText.RoleName).replace(":", "_") + ".utg";
		File file = DialogService.getImplementation().exportFile("", Project.USER_ROOT, filePrompt);
		if (file == null)
			return;
		if (file.getAbsolutePath().contains(".3w/"))
			DialogService.getImplementation().infoAlert("Export", "Cannot export to a project directory",
					file.getAbsolutePath());
		else {
			exportTree(file, root.configNode());
		}
	}

	@Override
	public void onImportTree(SimpleDataTreeNode childSpec, double duration) {
		TreeGraph<TreeGraphDataNode, ALEdge> importGraph = getImportGraph(childSpec);
		if (importGraph != null) {
			importGraph(importGraph, nodeEditor.layoutNode(), duration);
			controller.doLayout(duration);
			GraphStateService.getImplementation().setChanged();
			ConfigGraph.verifyGraph();
		}
	}

	@Override
	public void onDeleteParentLink(LayoutNode vChild) {
		visualiser.onRemoveParentLink(vChild);
		nodeEditor.deleteParentLink(vChild);
		visualiser.getLayoutGraph().onParentChanged();
		ConfigGraph.onParentChanged();
		GraphStateService.getImplementation().setChanged();
		ConfigGraph.verifyGraph();
	}

	@Override
	public boolean onOptionalProperties(List<SimpleDataTreeNode> optionalNodePropertySpecs,
			List<Duple<LayoutEdge, SimpleDataTreeNode>> optionalEdgePropertySpecs) {
		List<String> displayNames = new ArrayList<>();
		List<Boolean> selected = new ArrayList<>();
		Map<String, Tuple<String, SimpleDataTreeNode, ExtendablePropertyList>> propertyDetailsMap = new LinkedHashMap<>();

		getCurrentChoices(optionalNodePropertySpecs, optionalEdgePropertySpecs, displayNames, selected,
				propertyDetailsMap);

		List<Tuple<String, SimpleDataTreeNode, ExtendablePropertyList>> additions = new ArrayList<>();
		List<Tuple<String, SimpleDataTreeNode, ExtendablePropertyList>> deletions = new ArrayList<>();

		promptForChanges(displayNames, selected, propertyDetailsMap, additions, deletions);

		updateChanges(additions, deletions);

		if (!deletions.isEmpty() || !additions.isEmpty())
			return true;
		else
			return false;
	}

	// ------------------ Helpers -----------------------------

	private boolean isPredefined(SimpleDataTreeNode specs) {
		return ConfigurationReservedNodeId
				.isPredefined((String) specs.properties().getPropertyValue(Archetypes.HAS_ID));
	}

	private boolean allowedChild(String childLabel, Collection<String[]> tables) {
		for (String[] ss : tables) {
			if (ss[0].equals(childLabel)) {
				if (nodeEditor.hasProperty(ss[1]))
					return false;
			}
		}
		return true;
	};

	private List<LayoutNode> findNodesReferencedBy(String ref) {
		if (ref.endsWith(":"))
			ref = ref.substring(0, ref.length() - 1);
		String[] labels = ref.split(":/");
		int end = labels.length - 1;
		List<LayoutNode> result = new ArrayList<>();
		TreeGraph<LayoutNode, LayoutEdge> vg = visualiser.getLayoutGraph();
		for (LayoutNode vn : vg.nodes()) {
			if (vn.configNode().classId().equals(labels[end])) {
				if (end == 0)
					result.add(vn);
				else {
					LayoutNode parent = vn.getParent();
					if (hasParent(parent, labels, end - 1))
						result.add(vn);
				}
			}
		}
		return result;
	}

	private static boolean hasParent(LayoutNode parent, String[] labels, int index) {
		if (parent == null)
			return false;
		if (index < 0)
			return false;
		if (index == 0 && parent.configNode().classId().equals(labels[index]))
			return true;
		return hasParent(parent.getParent(), labels, index - 1);
	}

	private boolean satisfiesEdgeMultiplicity(SimpleDataTreeNode edgeSpec, String toNodeLabel, String edgeLabel) {
		IntegerRange range = specifications.getMultiplicityOf(edgeSpec);
		@SuppressWarnings("unchecked")
		List<Node> nodes = (List<Node>) get(nodeEditor.getConfigOutEdges(), selectZeroOrMany(hasTheLabel(edgeLabel)),
				edgeListStartNodes(), selectZeroOrMany(hasTheLabel(toNodeLabel)));
		if (nodes.size() >= range.getLast())
			return false;
		else
			return true;
	}

	private String getEndNodeLabel(String toNodeRef) {
		if (toNodeRef.endsWith(":"))
			toNodeRef = toNodeRef.substring(0, toNodeRef.length() - 1);
		String[] labels = toNodeRef.split(":/");
		return labels[labels.length - 1];
	}

	/*-	mustSatisfyQuery leafTableSpec
	className = String("au.edu.anu.twcore.archetype.tw.EndNodeHasPropertyQuery")
	propname = String("dataElementType")
	*/
	@SuppressWarnings("unchecked")
	private boolean satisfyEndNodeHasPropertyQuery(SimpleDataTreeNode edgeSpec, LayoutNode endNode, String edgeLabel) {
		Collection<SimpleDataTreeNode> queries = specifications.getQueries((SimpleDataTreeNode) edgeSpec,
				EndNodeHasPropertyQuery.class);
		for (SimpleDataTreeNode query : queries) {
			String key = (String) query.properties().getPropertyValue(TWA.PROP_NAME);
			if (!endNode.configHasProperty(key))
				return false;
		}
		return true;
	}

	/**
	 * @param edgeSpec  specifications of the proposed edge
	 * @param endNode   proposed end node
	 * @param edgeLabel the edge classId
	 * @return true if condition is satisfied including if condition is unable to be
	 *         determined because config is incomplete.
	 */
	private boolean satisfyCheckConstantTrackingQuery(SimpleDataTreeNode edgeSpec, LayoutNode vnEndNode,
			String edgeLabel) {
// if not a track field or table then don't care
		if (!(edgeLabel.equals(E_TRACKFIELD.label()) || edgeLabel.equals(E_TRACKTABLE.label())))
			return true;
		TreeNode endNode = vnEndNode.configNode();
		String endNodeLabel = endNode.classId();
// not my problem - probably can't happen
		if (!(endNodeLabel.equals(N_FIELD.label()) || endNodeLabel.equals(N_TABLE.label())))
			return true;
		return CheckConstantTrackingQuery.propose(endNode);
	}

	@SuppressWarnings("unchecked")
	private boolean satisfyOutEdgeXorQuery(SimpleDataTreeNode edgeSpec, LayoutNode endNode, String proposedEdgeLabel) {
		Collection<SimpleDataTreeNode> queries = specifications.getQueries((SimpleDataTreeNode) edgeSpec.getParent(),
				OutEdgeXorQuery.class);
		for (SimpleDataTreeNode query : queries) {
			if (queryReferencesLabel(proposedEdgeLabel, query, TWA.EDGE_LABEL_1, TWA.EDGE_LABEL_2)) {
				Set<String> qp1 = new HashSet<>();
				Set<String> qp2 = new HashSet<>();
				qp1.addAll(getEdgeLabelRefs(query.properties(), TWA.EDGE_LABEL_1));
				qp2.addAll(getEdgeLabelRefs(query.properties(), TWA.EDGE_LABEL_2));
				Set<String> es1 = new HashSet<>();
				Set<String> es2 = new HashSet<>();
				for (Edge e : nodeEditor.getConfigOutEdges())
					if (qp1.contains(e.classId()))
						es1.add(e.classId());
					else if (qp2.contains(e.classId()))
						es2.add(e.classId());
				/*- 4 cases:
				 * 1) es2>0 and es2>0 (error cond. nothing allowed)
				 * 2) es1==0& es2==0 (both empty - any allowed)
				 * 3) es1>0 & es2==0 ( only edge of es1 type allowed)
				 * 4) es1==0 & es2>0 (only edge of es2 type allowed);
				 *
				 */
				if (!es1.isEmpty() && !es2.isEmpty()) // 1 - error only possible from handmade config
					return false;
				else if (es1.isEmpty() && es2.isEmpty()) // 2
					return true;
				else if (!es1.isEmpty() && qp1.contains(proposedEdgeLabel)) // 3
					return true;
				else if (!es2.isEmpty() && qp2.contains(proposedEdgeLabel)) // 4
					return true;
				else
					return false;
			}
		}
		return true;
	}

	private boolean queryReferencesLabel(String entry, SimpleDataTreeNode query, String key1, String key2) {
		Set<String> refs = new HashSet<>();
		refs.addAll(getEdgeLabelRefs(query.properties(), key1));
		refs.addAll(getEdgeLabelRefs(query.properties(), key2));
		return refs.contains(entry);
	}

	private Collection<? extends String> getEdgeLabelRefs(SimplePropertyList properties, String key) {
		List<String> result = new ArrayList<>();
		Class<?> c = properties.getPropertyClass(key);
		if (c.equals(String.class)) {
			result.add((String) properties.getPropertyValue(key));
		} else {
			StringTable st = (StringTable) properties.getPropertyValue(key);
			for (int i = 0; i < st.size(); i++)
				result.add(st.getWithFlatIndex(i));
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	private boolean satisfyOutNodeXorQuery(SimpleDataTreeNode edgeSpec, LayoutNode proposedEndNode,
			String proposedEdgeLabel) {
		log.info(edgeSpec.toString());
		List<SimpleDataTreeNode> queries = specifications.getQueries((SimpleDataTreeNode) edgeSpec.getParent(),
				OutNodeXorQuery.class);

// query not required
		if (queries.isEmpty())
			return true;

// May find more than one of these queries
		List<Duple<String, String>> entries = specifications.getNodeLabelDuples(queries);

// Uncommitted: therefore we can have either of the entries
		if (!nodeEditor.hasOutEdges())
			return true;

		boolean result = false;
		for (Duple<String, String> entry : entries) {
			result = result || OutNodeXorQuery.propose(nodeEditor.getConfigNode(), proposedEndNode.configNode(),
					entry.getFirst(), entry.getSecond());
		}
		return result;

	}

	@SuppressWarnings("unchecked")
	private boolean satisfyExclusiveCategoryQuery(SimpleDataTreeNode edgeSpec, LayoutNode proposedCat,
			String edgeLabel) {
		if (specifications.getQueries((SimpleDataTreeNode) edgeSpec.getParent(), ExclusiveCategoryQuery.class)
				.isEmpty())
			return true;
		return ExclusiveCategoryQuery.propose(nodeEditor.getConfigNode(), proposedCat.configNode());
	}

	private String promptForNewNode(String label, String promptName, boolean capitalize) {
		String strPattern = DialogService.REGX_ALPHA_NUMERIC_SPACE;
		if (capitalize)
			strPattern = DialogService.REGX_ALPHA_CAP_NUMERIC;
		return DialogService.getImplementation().getText("'" + label + "' element name.", "", "Name:", promptName,
				strPattern);
	}

	private Class<? extends TreeNode> promptForClass(List<Class<? extends TreeNode>> subClasses,
			String rootClassSimpleName) {
		String[] list = new String[subClasses.size()];
		for (int i = 0; i < subClasses.size(); i++)
			list[i] = subClasses.get(i).getSimpleName();
		int result = DialogService.getImplementation().getListChoice(list, "Sub-classes", rootClassSimpleName,
				"select:");
		if (result != -1)
			return subClasses.get(result);
		else
			return null;
	}

	private String getNewName(String title, String label, String defName, SimpleDataTreeNode childBaseSpec) {
		// default name is label with 1 appended
		String promptId = defName;
		boolean capitalize = false;
		if (childBaseSpec != null)
			capitalize = specifications.ifNameStartsWithUpperCase(childBaseSpec);
		if (capitalize)
			promptId = WordUtils.capitalize(promptId);
		boolean modified = true;
		promptId = nodeEditor.proposeAnId(promptId);
		while (modified) {
			String userName = promptForNewNode(title, promptId, capitalize);
			if (userName == null)
				return null;// cancel
			userName = userName.trim();
			if (userName.equals(""))
				return null; // implicit cancel
			String newName = nodeEditor.proposeAnId(userName);
			modified = !newName.equals(userName);
			promptId = newName;
		}
		return promptId;
	}

	private String getNewId(String childLabel, String childId, SimpleDataTreeNode baseSpec) {
		String newId = childId;
		if (newId == null)
			newId = getNewName(childLabel, childLabel, ConfigurationNodeLabels.labelValueOf(childLabel).defName(),
					baseSpec);
		return newId;
	}

	private Duple<String, Class<? extends TreeNode>> getSubClassInfo(SimpleDataTreeNode spec) {
		String className = (String) spec.properties().getPropertyValue(Archetypes.IS_OF_CLASS);
		Class<? extends TreeNode> klass = null;
		List<Class<? extends TreeNode>> klasses = specifications.getSubClassesOf(spec);
		if (klasses.size() > 1) {
			klass = promptForClass(klasses, className);
			if (klass == null)
				return null;
		} else if (klasses.size() == 1) {
			klass = klasses.get(0);
		}
		return new Duple<String, Class<? extends TreeNode>>(className, klass);
	}

	private List<SimpleDataTreeNode> removeOptionalProperties(List<SimpleDataTreeNode> propertySpecs,
			SimpleDataTreeNode baseSpec, SimpleDataTreeNode subSpec) {
		List<SimpleDataTreeNode> ops = specifications.getOptionalProperties(baseSpec, subSpec);
		for (SimpleDataTreeNode op : ops)
			if (propertySpecs.contains(op))
				propertySpecs.remove(op);
		return propertySpecs;

	}

	private void setDefaultPropertyValues(List<SimpleDataTreeNode> propertySpecs,
			Duple<String, Class<? extends TreeNode>> subClassInfo) {
		for (SimpleDataTreeNode propertySpec : propertySpecs) {
			String key = (String) propertySpec.properties().getPropertyValue(Archetypes.HAS_NAME);
			if (key.equals(TWA.SUBCLASS)) {
				log.info("Add property: " + subClassInfo.getSecond().getName());
				newChild.addProperty(TWA.SUBCLASS, subClassInfo.getSecond().getName());
			} else {
				String type = (String) propertySpec.properties().getPropertyValue(Archetypes.TYPE);
				Object defValue = ValidPropertyTypes.getDefaultValue(type);

				if (defValue instanceof Enum<?>) {
					@SuppressWarnings("unchecked")
					Class<? extends Enum<?>> e = (Class<? extends Enum<?>>) defValue.getClass();

					SimpleDataTreeNode constraint = (SimpleDataTreeNode) get(propertySpec.getChildren(),
							selectZeroOrOne(hasProperty(Archetypes.CLASS_NAME, IsInValueSetQuery.class.getName())));
					if (constraint != null) {
						StringTable classes = (StringTable) constraint.properties().getPropertyValue(TWA.VALUES);
						if (classes.size() > 1) {
							String[] names = ValidPropertyTypes.namesOf(e);
							int choice = DialogService.getImplementation().getListChoice(names,
									newChild.getDisplayText(ElementDisplayText.RoleName), key,
									e.getClass().getSimpleName());
							defValue = ValidPropertyTypes.valueOf(names[choice], e);
						} else if (classes.size() == 1) {
							defValue = ValidPropertyTypes.valueOf(classes.getWithFlatIndex(0), e);
						}
					}
				}
				log.info("Add property: " + key);
				newChild.addProperty(key, defValue);
			}
		}
		if (newChild.configNode().classId().equals(N_SPACE.label()))
			setDefaultSpaceDims(newChild.configNode());
		if (newChild.configNode().classId().equals(N_FUNCTION.label())) {
			TwFunctionTypes ft = (TwFunctionTypes) newChild.configGetPropertyValue(P_FUNCTIONTYPE.key());
			if (!ft.returnStatement().isBlank()) {
				StringTable defValue = new StringTable(new Dimensioner(1));
				defValue.setByInt("\t" + ft.returnStatement() + ";", 0);
				newChild.addProperty(P_FUNCTIONSNIPPET.key(), defValue);
			}
		}

	}

	private LayoutNode createChild(String childLabel, String newId, SimpleDataTreeNode childBaseSpec) {
		LayoutNode nc = nodeEditor.newChild(childLabel, newId);
		nc.setCollapse(false);
		nc.setVisible(true);
		nc.setCategory();
		StringTable parents = (StringTable) childBaseSpec.properties().getPropertyValue(Archetypes.HAS_PARENT);
		nc.setParentRef(parents);
		return nc;
	}

	private void setDefaultSpaceDims(TreeGraphDataNode spaceNode) {
		SpaceType st = (SpaceType) spaceNode.properties().getPropertyValue(P_SPACETYPE.key());
		int nDims = st.dimensions();
		// borderType BorderTypeList
		Dimensioner bd = new Dimensioner(nDims * 2);
		Dimensioner[] d1 = new Dimensioner[1];
		d1[0] = bd;
		BorderListType defBlt = BorderListType.defaultValue();
		BorderListType blt = new BorderListType(d1);
		for (int i = 0; i < blt.size(); i++)
			if (i % 2 == 0)
				blt.setWithFlatIndex(defBlt.getWithFlatIndex(0), i);
			else
				blt.setWithFlatIndex(defBlt.getWithFlatIndex(1), i);
		spaceNode.properties().setProperty(P_SPACE_BORDERTYPE.key(), blt);

		// observationWindow Box. ok so not really a box since what is a 1d box?
		Point[] points = new Point[2]; // upper/lower or lower/upper bounds
		double[] lowerBounds = new double[nDims];
		double[] upperBounds = new double[nDims];
		Arrays.fill(lowerBounds, 0.0);
		Arrays.fill(upperBounds, 0.0);// Leave it to queries to indicate a +ve range is needed.
		// Point p1 = Point.newPoint(lowerBounds);
		for (int i = 0; i < 2; i++) {
			points[0] = Point.newPoint(lowerBounds);
			points[1] = Point.newPoint(upperBounds);
		}
		if (spaceNode.properties().hasProperty(P_SPACE_OBSWINDOW.key()))
			spaceNode.properties().setProperty(P_SPACE_OBSWINDOW.key(), Box.boundingBox(points[0], points[1]));
	}

	@SuppressWarnings("unchecked")
	private void connectTo(String edgeId, Tuple<String, LayoutNode, SimpleDataTreeNode> p, double duration) {
		String edgeLabel = p.getFirst();
		LayoutNode target = p.getSecond();
		SimpleDataTreeNode edgeSpec = p.getThird();
		LayoutEdge vEdge = nodeEditor.newEdge(edgeId, edgeLabel, target);
		if (vEdge.getConfigEdge() instanceof ALDataEdge) {
			ALDataEdge edge = (ALDataEdge) vEdge.getConfigEdge();
			ExtendablePropertyList props = (ExtendablePropertyList) edge.properties();
			List<SimpleDataTreeNode> propertySpecs = (List<SimpleDataTreeNode>) specifications
					.getPropertySpecsOf(edgeSpec, null);
			if (!specifications.filterPropertyStringTableOptions(propertySpecs, edgeSpec, null,
					edgeLabel + PairIdentity.LABEL_NAME_SEPARATOR + edgeId, PropertyXorQuery.class))
				return;// cancel

			// filter out optional properties
//			List<String> opNames = new ArrayList<>();
			List<SimpleDataTreeNode> ops = specifications.getOptionalProperties(edgeSpec, null);
			for (SimpleDataTreeNode op : ops)
				if (propertySpecs.contains(op))
					propertySpecs.remove(op);

			for (SimpleDataTreeNode propertySpec : propertySpecs) {
				String key = (String) propertySpec.properties().getPropertyValue(Archetypes.HAS_NAME);
				String type = (String) propertySpec.properties().getPropertyValue(Archetypes.TYPE);
				Object defValue = ValidPropertyTypes.getDefaultValue(type);
				log.info("Add property: " + key);
				props.addProperty(key, defValue);
			}
			controller.onNewEdge(vEdge);
		}
		visualiser.onNewEdge(vEdge, duration);

	}

	private void deleteNode(LayoutNode vNode, double duration) {
		// don't leave nodes hidden
		if (vNode.hasCollapsedChild())
			visualiser.expandTreeFrom(vNode, duration);
		// remove from view while still intact
		visualiser.removeView(vNode);
		// this and its config from graphs and disconnect
		vNode.remove();
	}

	private void renameEdge(String uniqueId, LayoutEdge vEdge) {
		// NB: graphs must be saved and reloaded after this op because Map<> of node
		// edges will have old KEYS
		ALEdge cEdge = vEdge.getConfigEdge();
		cEdge.rename(cEdge.id(), uniqueId);
		vEdge.rename(vEdge.id(), uniqueId);
	}

	private void renameNode(String uniqueId, LayoutNode vNode) {
		TreeGraphDataNode cNode = vNode.configNode();
		if (cNode.classId().equals(N_SYSTEM.label())) {
			File javaDir = Project.makeFile(Project.LOCAL_JAVA_CODE, vNode.id());
			if (javaDir.exists()) {
				File[] deps = javaDir.listFiles(new FilenameFilter() {
					@Override
					public boolean accept(File dir, String name) {
						return ((name.contains(".java") && !name.contains(vNode.getParent().id())));
					}
				});

				for (File f : deps) {
					File newFilef = new File(new File(f.getParent()).getParent() + File.separator + uniqueId
							+ File.separator + f.getName());
					// update package name! What about deps in other packages
					FileUtilities.copyFileReplace(f, newFilef);
					try {
						JavaUtilities.updatePackgeEntry(newFilef, vNode.id(), uniqueId);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				try {
					// delete the old tree
					FileUtilities.deleteFileTree(javaDir);
					if (UserProjectLink.haveUserProject()) {
						// delete old tree in user project
						File remoteDir = new File(UserProjectLink.srcRoot().getAbsolutePath() + File.separator
								+ Project.CODE + File.separator + vNode.id());
						if (remoteDir.exists())
							FileUtilities.deleteFileTree(remoteDir);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		if (cNode.classId().equals(N_RECORD.label()) || cNode.classId().equals(N_TABLE.label())
				|| cNode.classId().equals(N_FIELD.label())) {
			snippetCodeRefactor(cNode.classId(), cNode.id(), uniqueId);
		}

		// NB: graphs must be saved and reloaded after this op because Map<> of nodes
		// will have old KEYS
		cNode.rename(cNode.id(), uniqueId);
		vNode.rename(vNode.id(), uniqueId);
	}

	private static String findReplace(String regex, String from, String to, String text) {
		// pattern matching words as defined by the regex
		Pattern pattern = Pattern.compile(regex);
		Map<String, String> replacements = new HashMap<>();
		replacements.put(from, to);
		StringBuilder sb = new StringBuilder();
		Matcher matcher = pattern.matcher(text);
		int lastEnd = 0;
		while (matcher.find()) {
			int startIndex = matcher.start();
			if (startIndex > lastEnd) {
				// add missing chars
				sb.append(text.substring(lastEnd, startIndex));
			}
			// replace text, if necessary
			String group = matcher.group();
			String result = replacements.get(group);
			sb.append(result == null ? group : result);
			lastEnd = matcher.end();
		}
		sb.append(text.substring(lastEnd));
		return sb.toString();
	}

	private static void snippetCodeRefactor(String label, String from, String to) {
		List<StringTable> snippets = new ArrayList<>();
		List<String> functionNames = new ArrayList<>();
		for (Node n : ConfigGraph.getGraph().nodes()) {
			if (n.classId().equals(N_FUNCTION.label()) || n.classId().equals(N_INITFUNCTION.label())) {
				TreeGraphDataNode f = (TreeGraphDataNode) n;
				snippets.add((StringTable) f.properties().getPropertyValue(P_FUNCTIONSNIPPET.key()));
				functionNames.add(n.toShortString());
			}
		}

		for (StringTable t : snippets) {
			String fname = functionNames.get(snippets.indexOf(t));
			String text = "";
			boolean candidate = false;
			for (int i = 0; i < t.size(); i++) {
				String l = t.getByInt(i);
				text += l + "\n";
				if (l.contains(from))
					candidate = true;
			}
			if (candidate) {
				text = findReplace(DialogService.REGX_ALPHA_NUMERIC, from, to, text);
				String[] lines = text.split("\\n");
				if (lines.length < t.size()) {
					for (int j = lines.length; j < t.size(); j++)
						if (!t.getByInt(j).trim().isBlank())
//							log.info("'"+fname+"': Table line not updated [" + (j + 1) + "] '" + t.getByInt(j) + "'");
							System.out.println("'" + fname + "': Table line not updated [" + (j + 1) + "] '"
									+ t.getByInt(j) + "'");
				} else if (lines.length > t.size()) {
					for (int j = t.size(); j < lines.length; j++) {
						if (!lines[j].trim().isBlank())
//							log.info("'" + fname + "': Replacement line missed [" + (j + 1) + "] '" + lines[j] + "'");
							System.out.println(
									"'" + fname + "': Replacement line missed [" + (j + 1) + "] '" + lines[j] + "'");
					}
				}
				for (int i = 0; i < t.size(); i++) {
					if (i < lines.length) {
						t.setByInt(lines[i], i);
					}
				}
			}
		}

	}

	private void deleteTree(LayoutNode root, double duration) {
		// avoid concurrent modification
		List<LayoutNode> list = new LinkedList<>();
		for (LayoutNode child : root.getChildren())
			list.add(child);

		for (LayoutNode child : list)
			deleteTree(child, duration);
		deleteNode(root, duration);
	}

	private void deleteEdge(LayoutEdge vEdge) {
		ALEdge cEdge = vEdge.getConfigEdge();
		// Remove visual elements before disconnecting
		visualiser.removeView(vEdge);
		// Remove ids before disconnecting;
		EditableFactory vf = (EditableFactory) vEdge.factory();
		EditableFactory cf = (EditableFactory) cEdge.factory();
		vf.expungeEdge(vEdge);
		cf.expungeEdge(cEdge);
		vEdge.disconnect();
		cEdge.disconnect();
	}

	private void exportTree(File file, TreeGraphDataNode root) {
		TwConfigFactory factory = new TwConfigFactory();
		TreeGraph<TreeGraphDataNode, ALEdge> exportGraph = new TreeGraph<TreeGraphDataNode, ALEdge>(factory);
		List<ALEdge> configOutEdges = new ArrayList<>();

		cloneTree(factory, null, root, exportGraph, configOutEdges);

		// Look for outedges within the sub-tree
		for (ALEdge configEdge : configOutEdges) {
			TreeGraphDataNode cloneStartNode = getNode(exportGraph, configEdge.startNode());
			TreeGraphDataNode cloneEndNode = getNode(exportGraph, configEdge.endNode());
			if (cloneEndNode != null && cloneStartNode != null) {
				ALEdge cloneEdge = (ALEdge) factory.makeEdge(factory.edgeClass(configEdge.classId()), cloneStartNode,
						cloneEndNode, configEdge.id());
				cloneEdgeProperties(configEdge, cloneEdge);
			}
		}

		new OmugiGraphExporter(file).exportGraph(exportGraph);
	}

	private static void cloneTree(TwConfigFactory factory, TreeGraphDataNode cloneParent, TreeGraphDataNode configNode,
			TreeGraph<TreeGraphDataNode, ALEdge> graph, List<ALEdge> outEdges) {

		// make the node
		TreeGraphDataNode cloneNode = (TreeGraphDataNode) factory.makeNode(factory.nodeClass(configNode.classId()),
				configNode.id());

		// store any out edges to check later if they are within the export sub-tree
		for (ALEdge edge : configNode.edges(Direction.OUT))
			outEdges.add(edge);

		// clone node properties
		cloneNodeProperties(configNode, cloneNode);

		// clone tree structure
		cloneNode.connectParent(cloneParent);

		// follow tree
		for (TreeNode configChild : configNode.getChildren())
			cloneTree(factory, cloneNode, (TreeGraphDataNode) configChild, graph, outEdges);

	}

	private TreeGraphDataNode getNode(TreeGraph<TreeGraphDataNode, ALEdge> exportGraph, ALNode configNode) {
		for (TreeGraphDataNode cloneNode : exportGraph.nodes()) {
			if (cloneNode.id().equals(configNode.id()))
				return (TreeGraphDataNode) cloneNode;
		}
		return null;
	}

	private void importGraph(TreeGraph<TreeGraphDataNode, ALEdge> configSubGraph, LayoutNode vParent, double duration) {
		// Only trees are exported not graph lists. Therefore, it is proper to do this
		// by recursion

		// Store edge info for later processing
		Map<ALEdge, LayoutNode> outEdgeMap = new HashMap<>();
		Map<ALEdge, LayoutNode> inEdgeMap = new HashMap<>();

		TreeGraphDataNode cParent = vParent.configNode();
		TwConfigFactory cFactory = (TwConfigFactory) cParent.factory();
		LayoutGraphFactory vFactory = (LayoutGraphFactory) vParent.factory();
		TreeGraphDataNode importNode = configSubGraph.root();
		// follow tree
		importTree(importNode, cParent, vParent, cFactory, vFactory, outEdgeMap, inEdgeMap);
		// clone any edges found
		outEdgeMap.entrySet().forEach(entry -> {
			// get start/end nodes
			ALEdge importEdge = entry.getKey();
			LayoutNode vStart = entry.getValue();
			LayoutNode vEnd = inEdgeMap.get(importEdge);
			TreeGraphDataNode cStart = vStart.configNode();
			TreeGraphDataNode cEnd = vEnd.configNode();
			// make edges
			ALEdge newCEdge = (ALEdge) cFactory.makeEdge(cFactory.edgeClass(importEdge.classId()), cStart, cEnd,
					importEdge.id());
			LayoutEdge newVEdge = vFactory.makeEdge(vStart, vEnd, newCEdge.id());
			newVEdge.setConfigEdge(newCEdge);
			newVEdge.setVisible(true);
			cloneEdgeProperties(importEdge, newCEdge);
			visualiser.onNewEdge(newVEdge, duration);
		});

	}

	private static void cloneEdgeProperties(ALEdge from, ALEdge to) {
		if (from instanceof ALDataEdge) {
			// Add edge properties
			ALDataEdge fromDataEdge = (ALDataEdge) from;
			ALDataEdge toDataEdge = (ALDataEdge) to;
			SimplePropertyListImpl fromProps = (SimplePropertyListImpl) fromDataEdge.properties();
			ExtendablePropertyList toProps = (ExtendablePropertyList) toDataEdge.properties();
			for (String key : fromProps.getKeysAsArray())
				toProps.addProperty(key, fromProps.getPropertyValue(key));
		}

	}

	private static void cloneNodeProperties(TreeGraphDataNode from, TreeGraphDataNode to) {
		// clone node properties
		ExtendablePropertyList fromProps = (ExtendablePropertyList) from.properties();
		ExtendablePropertyList toProps = (ExtendablePropertyList) to.properties();
		for (String key : fromProps.getKeysAsArray())
			toProps.addProperty(key, fromProps.getPropertyValue(key));

	}

	private void importTree(TreeGraphDataNode importNode, TreeGraphDataNode cParent, LayoutNode vParent,
			TwConfigFactory cFactory, LayoutGraphFactory vFactory, Map<ALEdge, LayoutNode> outEdgeMap,
			Map<ALEdge, LayoutNode> inEdgeMap) {
		// NB: ids will change depending on scope.
		TreeGraphDataNode newCNode = (TreeGraphDataNode) cFactory.makeNode(cFactory.nodeClass(importNode.classId()),
				importNode.id());
		LayoutNode newVNode = vFactory.makeNode(newCNode.id());
		newCNode.connectParent(cParent);
		newVNode.connectParent(vParent);
		newVNode.setConfigNode(newCNode);
		Set<String> discoveredFile = new HashSet<>();

		NodeEditor vne = new NodeEditorAdapter(newVNode);
		// this depends on the parent table been present so its circular
		// This will break eventually when finding the spec without knowing the precise
		// parent when there can be more than one.
		// StringTable parentTable = controller.findParentTable(newVNode);

		SimpleDataTreeNode specs = specifications.getSpecsOf(vne, TWA.getRoot(), discoveredFile);
		StringTable parents = (StringTable) specs.properties().getPropertyValue(Archetypes.HAS_PARENT);
		newVNode.setParentRef(parents);
		newVNode.setCategory();
		newVNode.setVisible(true);
		newVNode.setCollapse(false);
		newVNode.setPosition(Math.random() * 0.5, Math.random() * 0.5);
		// Collect outEdges and pair with visual node.
		// Can't depend on ids! Match the imported edge with the newly created visual
		// node at time of creation.
		for (ALEdge outEdge : importNode.edges(Direction.OUT))
			outEdgeMap.put(outEdge, newVNode);
		for (ALEdge inEdge : importNode.edges(Direction.IN))
			inEdgeMap.put(inEdge, newVNode);

		// clone node properties
		cloneNodeProperties(importNode, newCNode);

		// update visual display
		visualiser.onNewNode(newVNode);

		for (TreeNode importChild : importNode.getChildren())
			importTree((TreeGraphDataNode) importChild, newCNode, newVNode, cFactory, vFactory, outEdgeMap, inEdgeMap);
	}

	@SuppressWarnings("unchecked")
	private TreeGraph<TreeGraphDataNode, ALEdge> getImportGraph(SimpleDataTreeNode childSpec) {
		File importFile = DialogService.getImplementation().getExternalProjectFile();
		if (importFile == null)
			return null;
		TreeGraph<TreeGraphDataNode, ALEdge> importGraph = (TreeGraph<TreeGraphDataNode, ALEdge>) FileImporter
				.loadGraphFromFile(importFile);
		// check the root node class is the same as that in the spec
		String label = (String) childSpec.properties().getPropertyValue(Archetypes.IS_OF_CLASS);
		if (!label.equals(importGraph.root().classId())) {
			DialogService.getImplementation().errorAlert("Import error", "Incompatible file", "Tree with root '" + label
					+ "' requested but root of this file is '" + importGraph.root().classId() + "'.");
			return null;
		}
		return importGraph;
	}

	private void getCurrentChoices(List<SimpleDataTreeNode> nodePropertySpecs,
			List<Duple<LayoutEdge, SimpleDataTreeNode>> edgePropertySpecs, List<String> displayNames,
			List<Boolean> selected,
			Map<String, Tuple<String, SimpleDataTreeNode, ExtendablePropertyList>> propertyDetailsMap) {
		// DisplayName, Spec, propertyList
		// collect optional node properties
		TreeGraphDataNode cn = (TreeGraphDataNode) nodeEditor.getConfigNode();
		for (SimpleDataTreeNode p : nodePropertySpecs) {
			String name = (String) p.properties().getPropertyValue(Archetypes.HAS_NAME);
			String displayName = cn.toShortString() + "#" + name;
			displayNames.add(displayName);
			propertyDetailsMap.put(displayName, new Tuple<String, SimpleDataTreeNode, ExtendablePropertyList>(name, p,
					(ExtendablePropertyList) cn.properties()));
			if (cn.properties().hasProperty(name))
				selected.add(true);
			else
				selected.add(false);
		}
		// collect option outedge properties
		for (Duple<LayoutEdge, SimpleDataTreeNode> ep : edgePropertySpecs) {
			ALDataEdge edge = (ALDataEdge) ep.getFirst().getConfigEdge();
			SimpleDataTreeNode ps = ep.getSecond();
			String name = (String) ps.properties().getPropertyValue(Archetypes.HAS_NAME);
			String displayName = edge.toShortString() + "#" + name;
			displayNames.add(displayName);
			propertyDetailsMap.put(displayName, new Tuple<String, SimpleDataTreeNode, ExtendablePropertyList>(name, ps,
					(ExtendablePropertyList) edge.properties()));
			if (edge.properties().hasProperty(name))
				selected.add(true);
			else
				selected.add(false);

		}
	}

	private void promptForChanges(List<String> displayNames, List<Boolean> selected,
			Map<String, Tuple<String, SimpleDataTreeNode, ExtendablePropertyList>> propertyDetailsMap,
			List<Tuple<String, SimpleDataTreeNode, ExtendablePropertyList>> additions,
			List<Tuple<String, SimpleDataTreeNode, ExtendablePropertyList>> deletions) {
		// If cancel is pressed the original list of selected items is returned and
		// therefore no change should result.
		List<String> selectedItems = DialogService.getImplementation().getCBSelections(nodeEditor.toString(),
				"Optional properties", displayNames, selected);
		for (String displayName : displayNames) {
			boolean isSelected = selected.get(displayNames.indexOf(displayName));
			// addition iff selected and not currently present
			if (!isSelected && selectedItems.contains(displayName))
				additions.add(propertyDetailsMap.get(displayName));
			// deletion iff not selected and present
			else if (isSelected && !selectedItems.contains(displayName))
				deletions.add(propertyDetailsMap.get(displayName));
		}
	}

	private void updateChanges(List<Tuple<String, SimpleDataTreeNode, ExtendablePropertyList>> additions,
			List<Tuple<String, SimpleDataTreeNode, ExtendablePropertyList>> deletions) {
		// Deletions
		for (Tuple<String, SimpleDataTreeNode, ExtendablePropertyList> details : deletions) {
			String key = details.getFirst();
			ExtendablePropertyList p = details.getThird();
			p.removeProperty(key);
		}
		// Additions
		for (Tuple<String, SimpleDataTreeNode, ExtendablePropertyList> details : additions) {
			String key = details.getFirst();
			SimpleDataTreeNode spec = details.getSecond();
			ExtendablePropertyList p = details.getThird();
			Object defValue;

			if (key.equals(P_SPACE_OBSWINDOW.key())) {
				SpaceType st = (SpaceType) nodeEditor.getConfigNode().properties().getPropertyValue(P_SPACETYPE.key());
				int nDims = st.dimensions();
				Point[] points = new Point[2]; // upper/lower or lower/upper bounds
				double[] lowerBounds = new double[nDims];
				double[] upperBounds = new double[nDims];
				Arrays.fill(lowerBounds, 0.0);
				Arrays.fill(upperBounds, 0.0);// Leave it to queries to indicate a +ve range is needed.
				for (int i = 0; i < 2; i++) {
					points[0] = Point.newPoint(lowerBounds);
					points[1] = Point.newPoint(upperBounds);
				}
				defValue = Box.boundingBox(points[0], points[1]);
			} else {
				String type = (String) spec.properties().getPropertyValue(Archetypes.TYPE);
				defValue = ValidPropertyTypes.getDefaultValue(type);
			}
			p.addProperty(key, defValue);
		}
	}

//	protected void processPropertiesMatchDefinition(LayoutNode newChild, SimpleDataTreeNode childBaseSpec,
//	SimpleDataTreeNode childSubSpec) {
//@SuppressWarnings("unchecked")
//List<SimpleDataTreeNode> queries = specifications.getQueries(childBaseSpec,
//		PropertiesMatchDefinitionQuery.class);
//if (queries.isEmpty())
//	return;
//SimpleDataTreeNode query = queries.get(0);
//
//StringTable values = (StringTable) query.properties().getPropertyValue("values");
//String dataCategory = values.getWithFlatIndex(0);
//
//Duple<Boolean, Collection<TreeGraphDataNode>> defData = PropertiesMatchDefinitionQuery
//		.getDataDefs(newChild.getConfigNode(), dataCategory);
//if (defData == null)
//	return;
//
//Collection<TreeGraphDataNode> defs = defData.getSecond();
//Boolean useAutoVar = defData.getFirst();
//if (defs == null) {
//	return;
//}
//
//ExtendablePropertyList newProps = (ExtendablePropertyList) newChild.getConfigNode().properties();
//if (useAutoVar) {
//	newProps.addProperty("age", 0);
//	newProps.addProperty("birthDate", 0);
//	// newProps.addProperty("name","Skippy");
//}
//for (TreeGraphDataNode def : defs) {
//	if (def.classId().equals(N_FIELD.label()))
//		newProps.addProperty(def.id(), ((FieldNode) def).newInstance());
//	else {
//		@SuppressWarnings("unchecked")
//		List<Node> dims = (List<Node>) get(def.edges(Direction.OUT), edgeListEndNodes(),
//				selectZeroOrMany(hasTheLabel(N_DIMENSIONER.label())));
//		if (!dims.isEmpty())
//			newProps.addProperty(def.id(), ((TableNode) def).templateInstance());
//		else
//			Dialogs.errorAlert("Node construction error", newChild.getDisplayText(ElementDisplayText.RoleName),
//					"Cannot add '" + def.classId() + ":" + def.id() + "' because it has no dimensions");
//	}
//}
//}

}
