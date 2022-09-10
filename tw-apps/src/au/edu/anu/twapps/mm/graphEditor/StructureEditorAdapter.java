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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.text.WordUtils;

import au.edu.anu.rscs.aot.archetype.ArchetypeArchetypeConstants;
import au.edu.anu.rscs.aot.collections.tables.Dimensioner;
import au.edu.anu.rscs.aot.collections.tables.StringTable;
import au.edu.anu.rscs.aot.util.FileUtilities;
import au.edu.anu.rscs.aot.util.IntegerRange;
import au.edu.anu.twapps.dialogs.Dialogs;
import au.edu.anu.twapps.mm.IGraphVisualiser;
import au.edu.anu.twapps.mm.IMMController;
import au.edu.anu.twapps.mm.configGraph.ConfigGraph;
import au.edu.anu.twapps.mm.visualGraph.ElementDisplayText;
import au.edu.anu.twapps.mm.visualGraph.VisualEdge;
import au.edu.anu.twapps.mm.visualGraph.VisualGraphFactory;
import au.edu.anu.twapps.mm.visualGraph.VisualNode;
import au.edu.anu.twcore.archetype.TWA;
import au.edu.anu.twcore.archetype.TwArchetypeConstants;
import au.edu.anu.twcore.archetype.tw.CheckConstantTrackingQuery;
import au.edu.anu.twcore.archetype.tw.ChildXorPropertyQuery;
import au.edu.anu.twcore.archetype.tw.EndNodeHasPropertyQuery;
import au.edu.anu.twcore.archetype.tw.ExclusiveCategoryQuery;
import au.edu.anu.twcore.archetype.tw.IsInValueSetQuery;
import au.edu.anu.twcore.archetype.tw.OutEdgeXorQuery;
import au.edu.anu.twcore.archetype.tw.OutNodeXorQuery;
//import au.edu.anu.twcore.archetype.tw.PropertiesMatchDefinitionQuery;
import au.edu.anu.twcore.archetype.tw.PropertyXorQuery;
import au.edu.anu.twcore.graphState.GraphState;
import au.edu.anu.twcore.project.Project;
import au.edu.anu.twcore.root.EditableFactory;
import au.edu.anu.twcore.root.TwConfigFactory;
import au.edu.anu.twcore.userProject.UserProjectLink;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.Edge;
import fr.cnrs.iees.graph.Node;
import fr.cnrs.iees.graph.TreeNode;
import fr.cnrs.iees.graph.impl.ALDataEdge;
import fr.cnrs.iees.graph.impl.ALEdge;
import fr.cnrs.iees.graph.impl.ALNode;
import fr.cnrs.iees.graph.impl.SimpleDataTreeNode;
import fr.cnrs.iees.graph.impl.TreeGraph;
import fr.cnrs.iees.graph.impl.TreeGraphDataNode;
import fr.cnrs.iees.graph.impl.TreeGraphNode;
import fr.cnrs.iees.graph.io.impl.OmugiGraphExporter;
import fr.cnrs.iees.identity.impl.PairIdentity;
import fr.cnrs.iees.io.FileImporter;
import fr.cnrs.iees.io.parsing.ValidPropertyTypes;
import fr.cnrs.iees.properties.ExtendablePropertyList;
//import fr.cnrs.iees.properties.ExtendablePropertyList;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.SimplePropertyListImpl;
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

import fr.ens.biologie.codeGeneration.JavaUtilities;
import fr.ens.biologie.generic.utils.Duple;
import fr.ens.biologie.generic.utils.Logging;
import fr.ens.biologie.generic.utils.Tuple;
import static au.edu.anu.rscs.aot.queries.CoreQueries.*;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.*;

/**
 * Author Ian Davies - 10 Jan. 2019
 * <p>
 * Adapter class to perform the work of the {@link StructureEditable} interface.
 */
public abstract class StructureEditorAdapter
		implements StructureEditable, TwArchetypeConstants, ArchetypeArchetypeConstants {
	private static Logger log = Logging.getLogger(StructureEditorAdapter.class);

	/**
	 * Reference to the 3Worlds {@link Specifications} interface.
	 */
	protected Specifications specifications;

	/**
	 * Reference to the {@link VisualNodeEditable} interface with its underlying
	 * {@VisualNode}. This is the currently selected node for editing.
	 */
	protected VisualNodeEditable editableNode;
	/**
	 * Reference to any newly constructed node. If this is not null when the editor
	 * closes (not all operations involve node creation), the user is prompted to
	 * place the node somewhere in the view.
	 */
	protected VisualNode newChild;

	/**
	 * The specifications of the node, currently selected for editing.
	 */
	protected SimpleDataTreeNode baseSpec;

	/* specifications of subclass of this editingNode if it has one */
	/**
	 * The specifications of the sub-class of the node, currently selected for
	 * editing (can be null).
	 */
	protected SimpleDataTreeNode subClassSpec;

	/**
	 * Reference to the graph {@link IGraphVisualiser} interface.
	 */
	protected IGraphVisualiser gvisualiser;

	protected IMMController controller;

	/**
	 * Constructor
	 * @param selectedNode The selected node for editing {@link VisualNodeEditable}.
	 * @param gv           Interface to the {@link IGraphVisualiser}.
	 * @param controller   The model controller {@link IMMController}.
	 */
	public StructureEditorAdapter(VisualNodeEditable selectedNode, IGraphVisualiser gv, IMMController controller) {
		super();
		this.specifications = new TwSpecifications();
		this.controller = controller;
		this.newChild = null;
		this.editableNode = selectedNode;
		Set<String> discoveredFile = new HashSet<>();
		this.baseSpec = specifications.getSpecsOf(editableNode, TWA.getRoot(), discoveredFile);
		if (baseSpec ==null)
			throw new NullPointerException(
					"Specification for '" + editableNode.visualNode().configNode().toShortString() + "' was not found.");

		this.subClassSpec = specifications.getSubSpecsOf(baseSpec, editableNode.getSubClass());
		this.gvisualiser = gv;
		log.info("BaseSpec: " + baseSpec);
		log.info("SubSpec: " + subClassSpec);
	}

	@Override
	public List<SimpleDataTreeNode> filterChildSpecs(Iterable<SimpleDataTreeNode> childSpecs) {
		// childXorPropertyQuerySpec
		List<SimpleDataTreeNode> result = new ArrayList<SimpleDataTreeNode>();
		List<String[]> tables = specifications.getQueryStringTables(baseSpec, ChildXorPropertyQuery.class);
		tables.addAll(specifications.getQueryStringTables(subClassSpec, ChildXorPropertyQuery.class));
		for (SimpleDataTreeNode childSpec : childSpecs) {
			boolean reserved = false;
			if (childSpec.properties().hasProperty(aaHasId)) {
				reserved = ConfigurationReservedNodeId
						.isPredefined((String) childSpec.properties().getPropertyValue(aaHasId));
			}
			if (!reserved) {
				String childLabel = (String) childSpec.properties().getPropertyValue(aaIsOfClass);
				IntegerRange range = specifications.getMultiplicityOf(childSpec);
				if (editableNode.moreChildrenAllowed(range, childLabel)) {
					if (!tables.isEmpty()) {
						if (allowedChild(childLabel, tables))
							result.add(childSpec);
					} else
						result.add(childSpec);
				}
			}
		}
		result.sort((n1,n2)-> n1.id().compareToIgnoreCase(n2.id()));
//		Collections.sort(result, new Comparator<Node>() {
//			@Override
//			public int compare(Node o1, Node o2) {
//				return o1.id().compareToIgnoreCase(o2.id());
//			}
//		});
		return result;
	}

	private boolean allowedChild(String childLabel, List<String[]> tables) {
		VisualNode vn = editableNode.visualNode();
		for (String[] ss : tables) {
			if (ss[0].equals(childLabel)) {
				if (vn.configHasProperty(ss[1]))
					return false;
			}
		}
		return true;
	};

	private List<VisualNode> findNodesReferenced(String ref) {
		if (ref.endsWith(":"))
			ref = ref.substring(0, ref.length() - 1);
		String[] labels = ref.split(":/");
		int end = labels.length - 1;
		List<VisualNode> result = new ArrayList<>();
		TreeGraph<VisualNode, VisualEdge> vg = gvisualiser.getVisualGraph();
		for (VisualNode vn : vg.nodes()) {
			if (vn.configNode().classId().equals(labels[end])) {
				if (end == 0)
					result.add(vn);
				else {
					VisualNode parent = vn.getParent();
					if (hasParent(parent, labels, end - 1))
						result.add(vn);
				}
			}
		}
		return result;
	}

	private static boolean hasParent(VisualNode parent, String[] labels, int index) {
		if (parent == null)
			return false;
		if (index < 0)
			return false;
		if (index == 0 && parent.configNode().classId().equals(labels[index]))
			return true;
		return hasParent(parent.getParent(), labels, index - 1);
	}

	@Override
	public List<Tuple<String, VisualNode, SimpleDataTreeNode>> filterEdgeSpecs(Iterable<SimpleDataTreeNode> edgeSpecs) {
		List<Tuple<String, VisualNode, SimpleDataTreeNode>> result = new ArrayList<>();
		for (SimpleDataTreeNode edgeSpec : edgeSpecs) {
//			log.info(edgeSpec.toShortString());
			String toNodeRef = (String) edgeSpec.properties().getPropertyValue(aaToNode);
			String edgeLabel = (String) edgeSpec.properties().getPropertyValue(aaIsOfClass);
			if (toNodeRef.endsWith(":"))
				toNodeRef = toNodeRef.substring(0, toNodeRef.length() - 1);
			String[] labels = toNodeRef.split(":/");
			String toNodeLabel = labels[labels.length - 1];

			log.info(edgeLabel);
			List<VisualNode> endNodes = findNodesReferenced(toNodeRef);
			for (VisualNode endNode : endNodes) {
				if (!editableNode.visualNode().id().equals(endNode.id())) // no edges to self
					if (!editableNode.hasOutEdgeTo(endNode, edgeLabel))
						if (satisfiesEdgeMultiplicity(edgeSpec, toNodeLabel, edgeLabel))
							if (satisfyExclusiveCategoryQuery(edgeSpec, endNode, edgeLabel))
								if (satisfyOutNodeXorQuery(edgeSpec, endNode, edgeLabel))
									if (satisfyOutEdgeXorQuery(edgeSpec, endNode, edgeLabel))
//										if (satisfyOutEdgeNXorQuery(edgeSpec, endNode, edgeLabel))
										if (satisfyEndNodeHasPropertyQuery(edgeSpec, endNode, edgeLabel))
											if (satisfyCheckConstantTrackingQuery(edgeSpec, endNode, edgeLabel))
												result.add(new Tuple<String, VisualNode, SimpleDataTreeNode>(edgeLabel,
														endNode, edgeSpec));
			}
		}
		Collections.sort(result, new Comparator<Tuple<String, VisualNode, SimpleDataTreeNode>>() {
			@Override
			public int compare(Tuple<String, VisualNode, SimpleDataTreeNode> o1,
					Tuple<String, VisualNode, SimpleDataTreeNode> o2) {
				String s1 = o1.getFirst() + o1.getSecond();
				String s2 = o2.getFirst() + o2.getSecond();
				return s1.compareToIgnoreCase(s2);
			}
		});
		return result;
	}

	private boolean satisfiesEdgeMultiplicity(SimpleDataTreeNode edgeSpec, String toNodeLabel, String edgeLabel) {
		IntegerRange range = specifications.getMultiplicityOf(edgeSpec);
		@SuppressWarnings("unchecked")
		List<Node> nodes = (List<Node>) get(editableNode.visualNode().configNode().edges(Direction.OUT),
				selectZeroOrMany(hasTheLabel(edgeLabel)), edgeListStartNodes(),
				selectZeroOrMany(hasTheLabel(toNodeLabel)));
		if (nodes.size() >= range.getLast())
			return false;
		else
			return true;
	}

	/*-	mustSatisfyQuery leafTableSpec
			className = String("au.edu.anu.twcore.archetype.tw.EndNodeHasPropertyQuery")
			propname = String("dataElementType")
	 */
	@SuppressWarnings("unchecked")
	private boolean satisfyEndNodeHasPropertyQuery(SimpleDataTreeNode edgeSpec, VisualNode endNode, String edgeLabel) {
		List<SimpleDataTreeNode> queries = specifications.getQueries((SimpleDataTreeNode) edgeSpec,
				EndNodeHasPropertyQuery.class);
		for (SimpleDataTreeNode query : queries) {
			String key = (String) query.properties().getPropertyValue(twaPropName);
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
	private boolean satisfyCheckConstantTrackingQuery(SimpleDataTreeNode edgeSpec, VisualNode vnEndNode,
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
	private boolean satisfyOutEdgeXorQuery(SimpleDataTreeNode edgeSpec, VisualNode endNode, String proposedEdgeLabel) {
		List<SimpleDataTreeNode> queries = specifications.getQueries((SimpleDataTreeNode) edgeSpec.getParent(),
				OutEdgeXorQuery.class);
		for (SimpleDataTreeNode query : queries) {
			if (queryReferencesLabel(proposedEdgeLabel, query, twaEdgeLabel1, twaEdgeLabel2)) {
				Set<String> qp1 = new HashSet<>();
				Set<String> qp2 = new HashSet<>();
				qp1.addAll(getEdgeLabelRefs(query.properties(), twaEdgeLabel1));
				qp2.addAll(getEdgeLabelRefs(query.properties(), twaEdgeLabel2));
				Set<String> es1 = new HashSet<>();
				Set<String> es2 = new HashSet<>();
				for (Edge e : editableNode.visualNode().configNode().edges(Direction.OUT))
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
	private boolean satisfyOutNodeXorQuery(SimpleDataTreeNode edgeSpec, VisualNode proposedEndNode,
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
		if (!editableNode.hasOutEdges())
			return true;

		boolean result = false;
		for (Duple<String, String> entry : entries) {
			result = result || OutNodeXorQuery.propose(editableNode.visualNode().configNode(),
					proposedEndNode.configNode(), entry.getFirst(), entry.getSecond());
		}
		return result;

	}

	@SuppressWarnings("unchecked")
	private boolean satisfyExclusiveCategoryQuery(SimpleDataTreeNode edgeSpec, VisualNode proposedCat,
			String edgeLabel) {
		if (specifications.getQueries((SimpleDataTreeNode) edgeSpec.getParent(), ExclusiveCategoryQuery.class)
				.isEmpty())
			return true;
		return ExclusiveCategoryQuery.propose(editableNode.visualNode().configNode(), proposedCat.configNode());
	}

	public List<VisualNode> orphanedChildList(Iterable<SimpleDataTreeNode> childSpecs) {
		List<VisualNode> result = new ArrayList<>();
		for (VisualNode root : editableNode.visualGraph().roots()) {
			String rootLabel = root.configNode().classId();
			for (SimpleDataTreeNode childSpec : childSpecs) {
				String specLabel = (String) childSpec.properties().getPropertyValue(aaIsOfClass);
				if (rootLabel.equals(specLabel))
					result.add(root);
			}
		}
		result.sort((n1,n2)-> n1.id().compareToIgnoreCase(n2.id()));
//		Collections.sort(result, new Comparator<Node>() {
//			@Override
//			public int compare(Node o1, Node o2) {
//				return o1.id().compareToIgnoreCase(o2.id());
//			}
//		});
		return result;
	}

	protected boolean haveSpecification() {
		return baseSpec != null;
	}

	private String promptForNewNode(String label, String promptName, boolean capitalize) {
		String strPattern = Dialogs.vsAlphaAlphaNumericSpace;
		if (capitalize)
			strPattern = Dialogs.vsAlphaCapAlphaNumeric;
		return Dialogs.getText("'" + label + "' element name.", "", "Name:", promptName, strPattern);
	}

	private Class<? extends TreeNode> promptForClass(List<Class<? extends TreeNode>> subClasses,
			String rootClassSimpleName) {
		String[] list = new String[subClasses.size()];
		for (int i = 0; i < subClasses.size(); i++)
			list[i] = subClasses.get(i).getSimpleName();
		int result = Dialogs.getListChoice(list, "Sub-classes", rootClassSimpleName, "select:");
		if (result != -1)
			return subClasses.get(result);
		else
			return null;
	}

	private String getNewName(String title, String label, String defName, SimpleDataTreeNode childBaseSpec) {
		// default name is label with 1 appended
//		String post = label.substring(1, label.length());
//		String pre = label.substring(0, 1);
//		String promptId = pre.toLowerCase() + post.replaceAll("[aeiou]", "") + "1";
		String promptId = defName;
		boolean capitalize = false;
		if (childBaseSpec != null)
			capitalize = specifications.nameStartsWithUpperCase(childBaseSpec);
		if (capitalize)
			promptId = WordUtils.capitalize(promptId);
		boolean modified = true;
		promptId = editableNode.proposeAnId(promptId);
		while (modified) {
			String userName = promptForNewNode(title, promptId, capitalize);
			if (userName == null)
				return null;// cancel
			userName = userName.trim();
			if (userName.equals(""))
				return null; // implicit cancel
			// userName = promptId;
//			if (capitalize)
//				userName = WordUtils.capitalize(userName);
			String newName = editableNode.proposeAnId(userName);
			modified = !newName.equals(userName);
			promptId = newName;
		}
		return promptId;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onNewChild(String childLabel, String childId, SimpleDataTreeNode childBaseSpec) {
		String promptId = childId;
		if (promptId == null)
			promptId = getNewName(childLabel, childLabel, ConfigurationNodeLabels.labelValueOf(childLabel).defName(),
					childBaseSpec);
		if (promptId == null)
			return;
		String childClassName = (String) childBaseSpec.properties().getPropertyValue(aaIsOfClass);
		Class<? extends TreeNode> subClass = null;
		List<Class<? extends TreeNode>> subClasses = specifications.getSubClassesOf(childBaseSpec);
		if (subClasses.size() > 1) {
			subClass = promptForClass(subClasses, childClassName);
			if (subClass == null)
				return;// cancel
		} else if (subClasses.size() == 1) {
			subClass = subClasses.get(0);
		}
		SimpleDataTreeNode childSubSpec = specifications.getSubSpecsOf(childBaseSpec, subClass);
		// unfiltered propertySpecs

		List<SimpleDataTreeNode> propertySpecs = (List<SimpleDataTreeNode>) specifications
				.getPropertySpecsOf(childBaseSpec, childSubSpec);
		if (!specifications.filterPropertyStringTableOptions(propertySpecs, childBaseSpec, childSubSpec,
				childClassName + PairIdentity.LABEL_NAME_SEPARATOR + promptId, ChildXorPropertyQuery.class,
				PropertyXorQuery.class))
			return;// cancel

		// filter out optional properties
//		List<String> opNames = new ArrayList<>();
		List<SimpleDataTreeNode> ops = specifications.getOptionalProperties(childBaseSpec, childSubSpec);
		for (SimpleDataTreeNode op : ops)
			if (propertySpecs.contains(op))
				propertySpecs.remove(op);

//			opNames.add((String) op.properties().getPropertyValue(aaHasName));

		// make the node
		newChild = editableNode.visualNode().newChild(childLabel, promptId);
		newChild.setCollapse(false);
		newChild.setVisible(true);
		newChild.setCategory();
//		VisualNodeEditable vne = new VisualNodeEditor(newChild, editableNode.getGraph());
		StringTable parents = (StringTable) childBaseSpec.properties().getPropertyValue(aaHasParent);
		newChild.setParentRef(parents);
		for (SimpleDataTreeNode propertySpec : propertySpecs) {
			String key = (String) propertySpec.properties().getPropertyValue(aaHasName);
//			System.out.println(key);
			if (key.equals(twaSubclass)) {
				log.info("Add property: " + subClass.getName());
				newChild.addProperty(twaSubclass, subClass.getName());
			} else {
				String type = (String) propertySpec.properties().getPropertyValue(aaType);
				Object defValue = ValidPropertyTypes.getDefaultValue(type);

				if (defValue instanceof Enum<?>) {
					Class<? extends Enum<?>> e = (Class<? extends Enum<?>>) defValue.getClass();

					SimpleDataTreeNode constraint = (SimpleDataTreeNode) get(propertySpec.getChildren(),
							selectZeroOrOne(hasProperty(aaClassName, IsInValueSetQuery.class.getName())));
					if (constraint != null) {
						StringTable classes = (StringTable) constraint.properties().getPropertyValue(twaValues);
						if (classes.size() > 1) {
							String[] names = ValidPropertyTypes.namesOf(e);
							int choice = Dialogs.getListChoice(names,
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

		specifications.filterRequiredPropertyQuery(newChild, childBaseSpec, childSubSpec);

		processPropertiesMatchDefinition(newChild, childBaseSpec, childSubSpec);

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
		controller.onNewNode(newChild);
	}

	protected void setDefaultSpaceDims(TreeGraphDataNode spaceNode) {
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

	protected void processPropertiesMatchDefinition(VisualNode newChild, SimpleDataTreeNode childBaseSpec,
			SimpleDataTreeNode childSubSpec) {
//		@SuppressWarnings("unchecked")
//		List<SimpleDataTreeNode> queries = specifications.getQueries(childBaseSpec,
//				PropertiesMatchDefinitionQuery.class);
//		if (queries.isEmpty())
//			return;
//		SimpleDataTreeNode query = queries.get(0);
//
//		StringTable values = (StringTable) query.properties().getPropertyValue("values");
//		String dataCategory = values.getWithFlatIndex(0);
//
//		Duple<Boolean, Collection<TreeGraphDataNode>> defData = PropertiesMatchDefinitionQuery
//				.getDataDefs(newChild.getConfigNode(), dataCategory);
//		if (defData == null)
//			return;
//
//		Collection<TreeGraphDataNode> defs = defData.getSecond();
//		Boolean useAutoVar = defData.getFirst();
//		if (defs == null) {
//			return;
//		}
//
//		ExtendablePropertyList newProps = (ExtendablePropertyList) newChild.getConfigNode().properties();
//		if (useAutoVar) {
//			newProps.addProperty("age", 0);
//			newProps.addProperty("birthDate", 0);
//			// newProps.addProperty("name","Skippy");
//		}
//		for (TreeGraphDataNode def : defs) {
//			if (def.classId().equals(N_FIELD.label()))
//				newProps.addProperty(def.id(), ((FieldNode) def).newInstance());
//			else {
//				@SuppressWarnings("unchecked")
//				List<Node> dims = (List<Node>) get(def.edges(Direction.OUT), edgeListEndNodes(),
//						selectZeroOrMany(hasTheLabel(N_DIMENSIONER.label())));
//				if (!dims.isEmpty())
//					newProps.addProperty(def.id(), ((TableNode) def).templateInstance());
//				else
//					Dialogs.errorAlert("Node construction error", newChild.getDisplayText(ElementDisplayText.RoleName),
//							"Cannot add '" + def.classId() + ":" + def.id() + "' because it has no dimensions");
//			}
//		}
	}

	@Override
	public void onNewEdge(Tuple<String, VisualNode, SimpleDataTreeNode> details, double duration) {
		if (editableNode.visualNode().isCollapsed())
			gvisualiser.expandTreeFrom(editableNode.visualNode(), duration);
		String id = getNewName(details.getFirst(), details.getFirst(),
				ConfigurationEdgeLabels.labelValueOf(details.getFirst()).defName(), null);
		if (id == null)
			return;
		connectTo(id, details, duration);

		ConfigGraph.verifyGraph();
		GraphState.setChanged();
	}

	@SuppressWarnings("unchecked")
	private void connectTo(String edgeId, Tuple<String, VisualNode, SimpleDataTreeNode> p, double duration) {
		String edgeLabel = p.getFirst();
		VisualNode target = p.getSecond();
		SimpleDataTreeNode edgeSpec = p.getThird();
		VisualEdge vEdge = editableNode.visualNode().newEdge(edgeId, edgeLabel, target);
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
				String key = (String) propertySpec.properties().getPropertyValue(aaHasName);
				String type = (String) propertySpec.properties().getPropertyValue(aaType);
				Object defValue = ValidPropertyTypes.getDefaultValue(type);
				log.info("Add property: " + key);
				props.addProperty(key, defValue);
			}
			controller.onNewEdge(vEdge);
		}
		gvisualiser.onNewEdge(vEdge, duration);

	}

	private void deleteNode(VisualNode vNode, double duration) {
		// don't leave nodes hidden
		if (vNode.hasCollaspedChild())
			gvisualiser.expandTreeFrom(vNode, duration);
		// remove from view while still intact
		gvisualiser.removeView(vNode);
		// this and its config from graphs and disconnect
		vNode.remove();
	}

	@Override
	public void onDeleteNode(double duration) {
		deleteNode(editableNode.visualNode(), duration);
		controller.onNodeDeleted();
		GraphState.setChanged();
		ConfigGraph.verifyGraph();
	}

	@Override
	public boolean onRenameNode() {
		String userName = getNewName(
				editableNode.visualNode().configNode() + ":" + editableNode.visualNode().configNode().id(),
				editableNode.visualNode().configNode().classId(), editableNode.visualNode().configNode().id(),
				baseSpec);
		if (userName != null) {
			renameNode(userName, editableNode.visualNode());
			gvisualiser.onNodeRenamed(editableNode.visualNode());
			controller.onElementRenamed();
			return true;
		}
		return false;
	}

	@Override
	public boolean onRenameEdge(VisualEdge edge) {
		String lbl = edge.getConfigEdge().classId();
		String userName = getNewName(lbl + ":" + edge.id(), lbl, ConfigurationEdgeLabels.labelValueOf(lbl).defName(),
				null);
		if (userName != null) {
			renameEdge(userName, edge);
			gvisualiser.onEdgeRenamed(edge);
			controller.onElementRenamed();
			return true;
		}
		return false;
	}

	private void renameEdge(String uniqueId, VisualEdge vEdge) {
		// NB: graphs must be saved and reloaded after this op because Map<> of node
		// edges will have old KEYS
		ALEdge cEdge = vEdge.getConfigEdge();
		cEdge.rename(cEdge.id(), uniqueId);
		vEdge.rename(vEdge.id(), uniqueId);
	}

	private void renameNode(String uniqueId, VisualNode vNode) {
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
				text = findReplace(Dialogs.vsAlphaNumeric, from, to, text);
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

	@Override
	public void onCollapseTree(VisualNode childRoot, double duration) {
		gvisualiser.collapseTreeFrom(childRoot, duration);
		controller.onTreeCollapse();
		GraphState.setChanged();

	}

	@Override
	public void onCollapseTrees(double duration) {
		for (VisualNode child : editableNode.visualNode().getChildren()) {
			if (!child.isCollapsed())
				gvisualiser.collapseTreeFrom(child, duration);
		}
		controller.onTreeCollapse();
		GraphState.setChanged();
	}

	@Override
	public void onExpandTree(VisualNode childRoot, double duration) {
		gvisualiser.expandTreeFrom(childRoot, duration);
		controller.onTreeExpand();
		GraphState.setChanged();
	}

	@Override
	public void onExpandTrees(double duration) {
		for (VisualNode child : editableNode.visualNode().getChildren()) {
			if (child.isCollapsed())
				gvisualiser.expandTreeFrom(child, duration);
		}
		controller.onTreeExpand();
		GraphState.setChanged();
	}

	@Override
	public void onReconnectChild(VisualNode vnChild) {
		editableNode.visualNode().reconnectChild(vnChild);
		gvisualiser.onNewParent(vnChild);
		ConfigGraph.verifyGraph();
		GraphState.setChanged();
	}

	private void deleteTree(VisualNode root, double duration) {
		// avoid concurrent modification
		List<VisualNode> list = new LinkedList<>();
		for (VisualNode child : root.getChildren())
			list.add(child);

		for (VisualNode child : list)
			deleteTree(child, duration);
		deleteNode(root, duration);
	}

	@Override
	public void onDeleteTree(VisualNode root, double duration) {
		deleteTree(root, duration);
		controller.onNodeDeleted();
		GraphState.setChanged();
		ConfigGraph.verifyGraph();
	}

	private void deleteEdge(VisualEdge vEdge) {
		ALEdge cEdge = vEdge.getConfigEdge();
		// Remove visual elements before disconnecting
		gvisualiser.removeView(vEdge);
		// Remove ids before disconnecting;
		EditableFactory vf = (EditableFactory) vEdge.factory();
		EditableFactory cf = (EditableFactory) cEdge.factory();
		vf.expungeEdge(vEdge);
		cf.expungeEdge(cEdge);
		vEdge.disconnect();
		cEdge.disconnect();
	}

	@Override
	public void onDeleteEdge(VisualEdge edge) {
		boolean mayHaveProperties = false;
		if (edge.getConfigEdge() instanceof ALDataEdge)
			mayHaveProperties = true;
		deleteEdge(edge);
		if (mayHaveProperties)
			controller.onEdgeDeleted();
		GraphState.setChanged();
		ConfigGraph.verifyGraph();
	}

	@Override
	public void onExportTree(VisualNode root) {
		String filePrompt = root.getDisplayText(ElementDisplayText.RoleName).replace(":", "_") + ".utg";
		File file = Dialogs.exportFile("", Project.USER_ROOT, filePrompt);
		if (file == null)
			return;
		if (file.getAbsolutePath().contains(".3w/"))
			Dialogs.infoAlert("Export", "Cannot export to a project directory", file.getAbsolutePath());
		else {
			exportTree(file, root.configNode());
		}
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

	@Override
	public void onImportTree(SimpleDataTreeNode childSpec, double duration) {
		TreeGraph<TreeGraphDataNode, ALEdge> importGraph = getImportGraph(childSpec);
		if (importGraph != null) {
			importGraph(importGraph, editableNode.visualNode(), duration);
			controller.doLayout(duration);
			GraphState.setChanged();
			ConfigGraph.verifyGraph();
		}
	}

	private void importGraph(TreeGraph<TreeGraphDataNode, ALEdge> configSubGraph, VisualNode vParent, double duration) {
		// Only trees are exported not graph lists. Therefore, it is proper to do this
		// by recursion

		// Store edge info for later processing
		Map<ALEdge, VisualNode> outEdgeMap = new HashMap<>();
		Map<ALEdge, VisualNode> inEdgeMap = new HashMap<>();

		TreeGraphDataNode cParent = vParent.configNode();
		TwConfigFactory cFactory = (TwConfigFactory) cParent.factory();
		VisualGraphFactory vFactory = (VisualGraphFactory) vParent.factory();
		TreeGraphDataNode importNode = configSubGraph.root();
		// follow tree
		importTree(importNode, cParent, vParent, cFactory, vFactory, outEdgeMap, inEdgeMap);
		// clone any edges found
		outEdgeMap.entrySet().forEach(entry -> {
			// get start/end nodes
			ALEdge importEdge = entry.getKey();
			VisualNode vStart = entry.getValue();
			VisualNode vEnd = inEdgeMap.get(importEdge);
			TreeGraphDataNode cStart = vStart.configNode();
			TreeGraphDataNode cEnd = vEnd.configNode();
			// make edges
			ALEdge newCEdge = (ALEdge) cFactory.makeEdge(cFactory.edgeClass(importEdge.classId()), cStart, cEnd,
					importEdge.id());
			VisualEdge newVEdge = vFactory.makeEdge(vStart, vEnd, newCEdge.id());
			newVEdge.setConfigEdge(newCEdge);
			newVEdge.setVisible(true);
			cloneEdgeProperties(importEdge, newCEdge);
			gvisualiser.onNewEdge(newVEdge, duration);
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

	private void importTree(TreeGraphDataNode importNode, TreeGraphDataNode cParent, VisualNode vParent,
			TwConfigFactory cFactory, VisualGraphFactory vFactory, Map<ALEdge, VisualNode> outEdgeMap,
			Map<ALEdge, VisualNode> inEdgeMap) {
		// NB: ids will change depending on scope.
		TreeGraphDataNode newCNode = (TreeGraphDataNode) cFactory.makeNode(cFactory.nodeClass(importNode.classId()),
				importNode.id());
		VisualNode newVNode = vFactory.makeNode(newCNode.id());
		newCNode.connectParent(cParent);
		newVNode.connectParent(vParent);
		newVNode.setConfigNode(newCNode);
		Set<String> discoveredFile = new HashSet<>();
		VisualNodeEditable vne = new VisualNodeEditor(newVNode, editableNode.visualGraph());
		// this depends on the parent table been present so its circular
		// This will break eventually when finding the spec without knowing the precise
		// parent when there can be more than one.
		// StringTable parentTable = controller.findParentTable(newVNode);

		SimpleDataTreeNode specs = specifications.getSpecsOf(vne, TWA.getRoot(), discoveredFile);
		StringTable parents = (StringTable) specs.properties().getPropertyValue(aaHasParent);
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
		gvisualiser.onNewNode(newVNode);

		for (TreeNode importChild : importNode.getChildren())
			importTree((TreeGraphDataNode) importChild, newCNode, newVNode, cFactory, vFactory, outEdgeMap, inEdgeMap);
	}

	@SuppressWarnings("unchecked")
	private TreeGraph<TreeGraphDataNode, ALEdge> getImportGraph(SimpleDataTreeNode childSpec) {
		File importFile = Dialogs.getExternalProjectFile();
		if (importFile == null)
			return null;
		TreeGraph<TreeGraphDataNode, ALEdge> importGraph = (TreeGraph<TreeGraphDataNode, ALEdge>) FileImporter
				.loadGraphFromFile(importFile);
		// check the root node class is the same as that in the spec
		String label = (String) childSpec.properties().getPropertyValue(aaIsOfClass);
		if (!label.equals(importGraph.root().classId())) {
			Dialogs.errorAlert("Import error", "Incompatible file", "Tree with root '" + label
					+ "' requested but root of this file is '" + importGraph.root().classId() + "'.");
			return null;
		}
		return importGraph;
	}

	@Override
	public void onDeleteParentLink(VisualNode vChild) {
		// messy: onParentChanged never expect edge deletion
		VisualNode vParent = editableNode.visualNode();
		TreeGraphNode cChild = vChild.configNode();
		TreeGraphNode cParent = editableNode.visualNode().configNode();
		gvisualiser.onRemoveParentLink(vChild);
		vParent.disconnectFrom(vChild);
		cParent.disconnectFrom(cChild);
		gvisualiser.getVisualGraph().onParentChanged();
		ConfigGraph.onParentChanged();
		GraphState.setChanged();
		ConfigGraph.verifyGraph();
	}

	@Override
	public boolean onOptionalProperties(List<SimpleDataTreeNode> optionalNodePropertySpecs,
			List<Duple<VisualEdge, SimpleDataTreeNode>> optionalEdgePropertySpecs) {
		List<String> displayNames = new ArrayList<>();
		List<Boolean> selected = new ArrayList<>();
		// DisplayName, Spec, propertyList
		Map<String, Tuple<String, SimpleDataTreeNode, ExtendablePropertyList>> propertyDetailsMap = new LinkedHashMap<>();
		TreeGraphDataNode cn = (TreeGraphDataNode) editableNode.visualNode().configNode();
		for (SimpleDataTreeNode p : optionalNodePropertySpecs) {
			String name = (String) p.properties().getPropertyValue(aaHasName);
			String displayName = cn.toShortString() + "#" + name;
			displayNames.add(displayName);
			propertyDetailsMap.put(displayName, new Tuple<String, SimpleDataTreeNode, ExtendablePropertyList>(name, p,
					(ExtendablePropertyList) cn.properties()));
			if (cn.properties().hasProperty(name))
				selected.add(true);
			else
				selected.add(false);
		}
		for (Duple<VisualEdge, SimpleDataTreeNode> ep : optionalEdgePropertySpecs) {
			ALDataEdge edge = (ALDataEdge) ep.getFirst().getConfigEdge();
			SimpleDataTreeNode ps = ep.getSecond();
			String name = (String) ps.properties().getPropertyValue(aaHasName);
			String displayName = edge.toShortString() + "#" + name;
			displayNames.add(displayName);
			propertyDetailsMap.put(displayName, new Tuple<String, SimpleDataTreeNode, ExtendablePropertyList>(name, ps,
					(ExtendablePropertyList) edge.properties()));
			if (edge.properties().hasProperty(name))
				selected.add(true);
			else
				selected.add(false);

		}
		List<Tuple<String, SimpleDataTreeNode, ExtendablePropertyList>> additions = new ArrayList<>();
		List<Tuple<String, SimpleDataTreeNode, ExtendablePropertyList>> deletions = new ArrayList<>();
		// If cancel is pressed the original list of selected items is returned and
		// therefore no change should result.
		List<String> selectedItems = Dialogs.getCBSelections(editableNode.visualNode().configNode().toShortString(),
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
				SpaceType st = (SpaceType) cn.properties().getPropertyValue(P_SPACETYPE.key());
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
				String type = (String) spec.properties().getPropertyValue(aaType);
				defValue = ValidPropertyTypes.getDefaultValue(type);
			}
			p.addProperty(key, defValue);
		}

		if (!deletions.isEmpty() || !additions.isEmpty())
			return true;
		else
			return false;
	}
}
