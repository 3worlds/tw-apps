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

import java.util.*;

import au.edu.anu.aot.archetype.Archetypes;
import fr.cnrs.iees.omugi.collections.tables.StringTable;
import au.edu.anu.qgraph.queries.*;
import au.edu.anu.omhtk.util.IntegerRange;
import au.edu.anu.twapps.dialogs.DialogsFactory;
import au.edu.anu.twapps.mm.layoutGraph.ElementDisplayText;
import au.edu.anu.twapps.mm.layoutGraph.LayoutNode;
import au.edu.anu.twcore.archetype.TWA;
import au.edu.anu.twcore.archetype.tw.*;
import fr.cnrs.iees.omugi.OmugiClassLoader;
import fr.cnrs.iees.omugi.graph.*;
import fr.cnrs.iees.omugi.graph.impl.SimpleDataTreeNode;
import fr.cnrs.iees.omugi.identity.impl.PairIdentity;
import fr.cnrs.iees.omugi.io.parsing.ValidPropertyTypes;
import fr.cnrs.iees.omugi.properties.ExtendablePropertyList;
import fr.cnrs.iees.omhtk.utils.Duple;

import static au.edu.anu.qgraph.queries.CoreQueries.*;
import static au.edu.anu.qgraph.queries.base.SequenceQuery.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;

/**
 * Wrapper class for managing the configuration archetype.
 * 
 * @author Ian Davies - 10 Jan. 2019
 */
public class TwSpecifications implements //
		Specifications {

	@Override
	public SimpleDataTreeNode getSpecsOf(NodeEditor editNode, TreeNode root, Set<String> discoveredFiles) {
		for (TreeNode n : root.getChildren()) {
			SimpleDataTreeNode spec = (SimpleDataTreeNode) n;
			if (isMatchingSpec(spec, editNode)) {
				return spec;
			} else {
				for (SimpleDataTreeNode constraint : getSubArchetypeQueries(spec)) {
					spec = findInSubArchetyes(constraint, editNode, discoveredFiles);
					if (spec != null)
						return spec;
				}
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public SimpleDataTreeNode getSubSpecsOf(SimpleDataTreeNode baseSpecs, Class<? extends TreeNode> subClass) {
		// multiple stopping conditions have many entries of IsOfClass
		if (subClass != null) {
			String parent = (String) baseSpecs.properties().getPropertyValue(Archetypes.IS_OF_CLASS);
			Tree<?> subClassTree = getSubArchetype(baseSpecs, subClass);
			if (subClassTree == null)
				return null;
			List<SimpleDataTreeNode> specs = (List<SimpleDataTreeNode>) get(subClassTree.root().getChildren(),
					selectOneOrMany(hasProperty(Archetypes.IS_OF_CLASS, parent)));
//			if (specs.size() == 1)
//				return specs.get(0);
//			else {
			for (SimpleDataTreeNode spec : specs) {
				StringTable t = (StringTable) spec.properties().getPropertyValue(Archetypes.HAS_PARENT);
				if (t.contains(parent + PairIdentity.LABEL_NAME_SEPARATOR)) {
					return spec;
				}
//				}
			}

		}
		return null;
	}

	@Override
	public List<SimpleDataTreeNode> getChildSpecsOf(NodeEditor editNode, SimpleDataTreeNode spec,
			SimpleDataTreeNode subSpec) {
		List<SimpleDataTreeNode> result = selectMatchingChildrenOf(editNode, spec.getParent());
		if (subSpec != null)
			result.addAll(selectMatchingChildrenOf(editNode, subSpec.getParent()));
		return result;
	}

	@Override
	public List<SimpleDataTreeNode> getPropertySpecsOf(SimpleDataTreeNode spec, SimpleDataTreeNode subSpec) {
		List<SimpleDataTreeNode> results = getPropertySpecs(spec);
		if (subSpec != null) {
			results.addAll(getPropertySpecs(subSpec));
		}
		return results;
	}

	@Override
	public List<SimpleDataTreeNode> getEdgeSpecsOf(SimpleDataTreeNode baseSpec, SimpleDataTreeNode subSpec) {
		List<SimpleDataTreeNode> result = getEdgeSpecs(baseSpec);
		if (subSpec != null)
			result.addAll(getEdgeSpecs(subSpec));
		return result;
	}

	@Override
	public boolean ifNameStartsWithUpperCase(SimpleDataTreeNode spec) {
		return getConstraint(spec, NameStartsWithUpperCaseQuery.class.getName()) != null;
	}

	@Override
	public List<Class<? extends TreeNode>> getSubClassesOf(SimpleDataTreeNode spec) {
		SimpleDataTreeNode propertySpec = getSubClassPropertySpec(spec);
		if (propertySpec != null) {
			StringTable classes = getClassTable(propertySpec);
			return listValidClasses(classes);
		} else
			return new ArrayList<Class<? extends TreeNode>>();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String[]> getQueryStringTables(SimpleDataTreeNode spec, Class<? extends Queryable> queryClass) {
		List<String[]> result = new ArrayList<>();
		if (spec == null)
			return result;
		List<SimpleDataTreeNode> querySpecs = (List<SimpleDataTreeNode>) get(spec.getChildren(),
				selectZeroOrMany(hasProperty(Archetypes.CLASS_NAME, queryClass.getName())));
		for (SimpleDataTreeNode querySpec : querySpecs) {
			List<String> entries = getQueryStringTableEntries(querySpec);
			if (!entries.isEmpty()) {
				String[] ss = new String[entries.size()];
				for (int i = 0; i < ss.length; i++)
					ss[i] = entries.get(i);
				result.add(ss);
			}
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean filterPropertyStringTableOptions(Iterable<SimpleDataTreeNode> propertySpecs,
			SimpleDataTreeNode baseSpec, SimpleDataTreeNode subSpec, String childId,
			Class<? extends Queryable>... queryClasses) {
		List<String[]> entries = new ArrayList<>();
		for (Class<? extends Queryable> qclass : queryClasses) {
			entries.addAll(getQueryStringTables(baseSpec, qclass));
			entries.addAll(getQueryStringTables(subSpec, qclass));
		}
		if (!entries.isEmpty()) {
			List<String> selectedKeys = DialogsFactory.getRadioButtonChoices(childId, "PropertyChoices", "", entries);
			if (selectedKeys == null)
				return false;
			Iterator<SimpleDataTreeNode> iter = propertySpecs.iterator();
			String keyHandled = null;
			while (iter.hasNext()) {
				SimpleDataTreeNode ps = iter.next();
				String key = (String) ps.properties().getPropertyValue(Archetypes.HAS_NAME);
				if (entriesContains(key, entries)) {
					String optionalKey = getSelectedEntry(key, selectedKeys, entries);
					if (!Objects.equals(key, keyHandled)) {
						if (optionalKey != null && !optionalKey.equals(key))
							iter.remove();
						else if (keyHandled == null)
							keyHandled = optionalKey;
					}
				}
			}
		}
		return true;
	}

	@Override
	public IntegerRange getMultiplicityOf(SimpleDataTreeNode spec) {
		return (IntegerRange) spec.properties().getPropertyValue(Archetypes.MULTIPLICITY);
	}

	// -----------------------end of implementation methods-----------------------

	private boolean entriesContains(String key, List<String[]> entries) {
		for (String[] ss : entries)
			for (String s : ss)
				if (s.equals(key))
					return true;
		return false;
	}

	private static String getSelectedEntry(String key, List<String> selectedKeys, List<String[]> entries) {
		if (selectedKeys == null)
			return null;
		for (int i = 0; i < selectedKeys.size(); i++) {
			String sel = selectedKeys.get(i);
			String[] entry = entries.get(i);
			for (int j = 0; j < entry.length; j++) {
				if (entry[j].equals(sel))
					return sel;
			}
		}
		return null;
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

	@SuppressWarnings("unchecked")
	private Tree<? extends TreeNode> getSubArchetype(SimpleDataTreeNode spec, Class<? extends TreeNode> subClass) {
		List<SimpleDataTreeNode> constraints = (List<SimpleDataTreeNode>) get(spec.getChildren(),
				selectZeroOrMany(hasProperty(Archetypes.CLASS_NAME, CheckSubArchetypeQuery.class.getName())));

		for (SimpleDataTreeNode constraint : constraints) {
			StringTable pars = (StringTable) constraint.properties().getPropertyValue(TWA.PARAMETERS);
			// We only want to add an SA if the pars.get(0)==subclass
			if (pars.get(0).equals(P_SA_SUBCLASS.key()))
				if (pars.getWithFlatIndex(1).equals(subClass.getName())) {
					return TWA.getSubArchetype(pars.get((pars.size() - 1)));
				}
		}
		return null;
	}

	private SimpleDataTreeNode getConstraint(SimpleDataTreeNode spec, String constraintClass) {
		return (SimpleDataTreeNode) get(spec.getChildren(),
				selectZeroOrOne(andQuery(hasTheLabel(Archetypes.MUST_SATISFY_QUERY),
						hasProperty(Archetypes.CLASS_NAME, constraintClass))));
	}

	private boolean isOfClass(SimpleDataTreeNode child, String label) {
		String ioc = (String) child.properties().getPropertyValue(Archetypes.IS_OF_CLASS);
		return ioc.equals(label);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SimpleDataTreeNode> getQueries(SimpleDataTreeNode spec, Class<? extends Queryable>... queries) {
		List<SimpleDataTreeNode> result = new ArrayList<>();
		if (spec == null)
			return result;
		for (Class<? extends Queryable> query : queries) {
			result.addAll((List<SimpleDataTreeNode>) get(spec.getChildren(),
					selectZeroOrMany(andQuery(hasTheLabel(Archetypes.MUST_SATISFY_QUERY),
							hasProperty(Archetypes.CLASS_NAME, query.getName())))));
		}
		return result;
	}

	/*-
	 * className = String("au.edu.anu.twcore.archetype.tw.OutNodeXorQuery")
	 * nodeLabel1 = String("componentType")
	 * nodeLabel2 = String("lifeCycle")
	 */
	@Override
	public List<Duple<String, String>> getNodeLabelDuples(List<SimpleDataTreeNode> queries) {
		List<Duple<String, String>> result = new ArrayList<>();
		for (SimpleDataTreeNode query : queries) {
			if (query.properties().hasProperty(TWA.NODE_LABEL_1) && query.properties().hasProperty(TWA.NODE_LABEL_2)) {
				result.add(new Duple<String, String>((String) query.properties().getPropertyValue(TWA.NODE_LABEL_1),
						(String) query.properties().getPropertyValue(TWA.NODE_LABEL_2)));
			}
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void filterRequiredPropertyQuery(LayoutNode vnode, SimpleDataTreeNode baseSpec, SimpleDataTreeNode subSpec) {
		List<SimpleDataTreeNode> queries = getQueries(baseSpec, RequirePropertyQuery.class);

		queries.addAll(getQueries(subSpec, RequirePropertyQuery.class));
		Set<String> pset = new HashSet<>();
		for (SimpleDataTreeNode query : queries) {
			StringTable conditions = (StringTable) query.properties().getPropertyValue(TWA.CONDITIONS);
			String key = conditions.getWithFlatIndex(1);
			if (!pset.contains(key)) {
				pset.add(key);
				Object obj = vnode.configGetPropertyValue(key);
				// works with obj ==null
				// gets the string list
				if (obj instanceof Enum<?>) {
					Class<? extends Enum<?>> e = (Class<? extends Enum<?>>) obj.getClass();
					String[] names = ValidPropertyTypes.namesOf(e);
					int choice = DialogsFactory.getListChoice(names, vnode.getDisplayText(ElementDisplayText.RoleName),
							key, obj.getClass().getSimpleName());
					if (choice >= 0) {
						Enum<?> value = ValidPropertyTypes.valueOf(names[choice], e);
						vnode.configNode().properties().setProperty(key, value);
					}
				}
			}
		}

		ExtendablePropertyList props = (ExtendablePropertyList) vnode.cProperties();
		for (SimpleDataTreeNode query : queries) {
			StringTable conditions = (StringTable) query.properties().getPropertyValue(TWA.CONDITIONS);
			String subjectKey = conditions.getWithFlatIndex(0);
			String conditionalKey = conditions.getWithFlatIndex(1);
			String[] conditionalValues = new String[conditions.size() - 2];
			for (int i = 2; i < conditions.size(); i++)
				conditionalValues[i - 2] = conditions.getWithFlatIndex(i);
			if (props.hasProperty(subjectKey))
				if (props.hasProperty(conditionalKey)) {
					String value = props.getPropertyValue(conditionalKey).toString();
					boolean satisfied = false;
					for (int i = 0; i < conditionalValues.length; i++)
						if (value.equals(conditionalValues[i]))
							satisfied = true;
					if (!satisfied)
						props.removeProperty(subjectKey);
				} else// conditional key not present so remove subject key
					props.removeProperty(subjectKey);
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SimpleDataTreeNode> getOptionalProperties(SimpleDataTreeNode baseSpec, SimpleDataTreeNode subSpec) {

		List<SimpleDataTreeNode> props = (List<SimpleDataTreeNode>) get(baseSpec.getChildren(),
				selectZeroOrMany(hasTheLabel(Archetypes.HAS_PROPERTY)));
		if (subSpec != null) {
			List<SimpleDataTreeNode> propsSub = (List<SimpleDataTreeNode>) get(subSpec.getChildren(),
					selectZeroOrMany(hasTheLabel(Archetypes.HAS_PROPERTY)));
			props.addAll(propsSub);
		}

		Set<String> depProps = new HashSet<>();
		List<SimpleDataTreeNode> reqs = getQueries(baseSpec, RequirePropertyQuery.class);
		reqs.addAll(getQueries(subSpec, RequirePropertyQuery.class));
		for (SimpleDataTreeNode req : reqs) {
			// conditions =
			// StringTable(([8]"units","dataElementType","Double","Float","Integer","Long","Short","Byte"))
			StringTable conditions = (StringTable) req.properties().getPropertyValue(TWA.CONDITIONS);
			depProps.add(conditions.getWithFlatIndex(0));
			depProps.add(conditions.getWithFlatIndex(1));
		}

		List<SimpleDataTreeNode> cXORpqs = getQueries(baseSpec, ChildXorPropertyQuery.class);
		cXORpqs.addAll(getQueries(subSpec, ChildXorPropertyQuery.class));
		for (SimpleDataTreeNode cXORpq : cXORpqs) {
			// edge_prop = StringTable(([2]"record","dataElementType"))
			StringTable conditions = (StringTable) cXORpq.properties().getPropertyValue(TWA.EDGE_PROP);
			depProps.add(conditions.getWithFlatIndex(1));
		}
		// CAUTION: this query has changed and now uses EDGE labels instead of NODE
		// labels
		// I dont know the consequences - JG 16/12/2021
		List<SimpleDataTreeNode> eXORpqs = getQueries(baseSpec, OutEdgeXorPropertyQuery.class);
		eXORpqs.addAll(getQueries(subSpec, OutEdgeXorPropertyQuery.class));
//		for (SimpleDataTreeNode eXORpq : eXORpqs) {
//			// no example not used yet
//		}

		List<SimpleDataTreeNode> pXORps = getQueries(baseSpec, PropertyXorQuery.class);
		pXORps.addAll(getQueries(subSpec, PropertyXorQuery.class));
		for (SimpleDataTreeNode pXORp : pXORps) {
			// proplist = StringTable(([2]file,type))
			StringTable conditions = (StringTable) pXORp.properties().getPropertyValue(TWA.PROP_LIST);
			depProps.add(conditions.getWithFlatIndex(0));
			depProps.add(conditions.getWithFlatIndex(1));
		}

		List<SimpleDataTreeNode> result = new ArrayList<>();
		for (SimpleDataTreeNode prop : props) {
			String name = (String) prop.properties().getPropertyValue(Archetypes.HAS_NAME);
			IntegerRange ir = (IntegerRange) prop.properties().getPropertyValue(Archetypes.MULTIPLICITY);
			if (ir.getFirst() == 0 && ir.getLast() == 1 && !depProps.contains(name))
				result.add(prop);
		}

		return result;
	}

	private boolean parentTablesMatch(SimpleDataTreeNode spec, NodeEditor editNode) {
		StringTable parentsSpecTable = (StringTable) spec.properties().getPropertyValue(Archetypes.HAS_PARENT);
		StringTable parentsTable = editNode.getParentTable();
		return (parentsTable.equals(parentsSpecTable));
	}

	private boolean idsMatch(SimpleDataTreeNode spec, NodeEditor editNode) {
		String hasId = (String) spec.properties().getPropertyValue(Archetypes.HAS_ID);
		if (editNode.getName().equals(hasId))
			return true;
		return false;
	}

	private boolean isMatchingSpec(SimpleDataTreeNode spec, NodeEditor editNode) {
		// 1. is of the specified class
		if (isOfClass(spec, editNode.getLabel())) {
			// 2. If root label it has no parent to check
			if (editNode.is3Wroot())
				return true;
			// 3. if parent tables match
			if (parentTablesMatch(spec, editNode)) {
				// 4. if it has an id then they must also match
				if (spec.properties().hasProperty(Archetypes.HAS_ID)) {
					if (idsMatch(spec, editNode))
						return true;
					else
						return false;
				} else
					return true;
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private List<SimpleDataTreeNode> getSubArchetypeQueries(SimpleDataTreeNode spec) {
		return (List<SimpleDataTreeNode>) get(spec.getChildren(),
				selectZeroOrMany(hasProperty(Archetypes.CLASS_NAME, CheckSubArchetypeQuery.class.getName())));
	}

	private SimpleDataTreeNode findInSubArchetyes(SimpleDataTreeNode constraint, NodeEditor editNode,
			Set<String> discoveredFiles) {
		List<String> pars = getQueryStringTableEntries(constraint);
		if (pars.get(0).equals(P_SA_SUBCLASS.key())) {
			String fname = pars.get(pars.size() - 1);
			// prevent infinite recursion
			if (!discoveredFiles.contains(fname)) {
				discoveredFiles.add(fname);
				Tree<?> tree = (Tree<?>) TWA.getSubArchetype(fname);
				return getSpecsOf(editNode, tree.root(), discoveredFiles);
			}
		}
		return null;
	}

	private List<SimpleDataTreeNode> selectMatchingChildrenOf(NodeEditor editNode, TreeNode root) {
		List<SimpleDataTreeNode> result = new ArrayList<>();
		@SuppressWarnings("unchecked")
		List<SimpleDataTreeNode> children = (List<SimpleDataTreeNode>) get(root.getChildren(),
				selectZeroOrMany(hasProperty(Archetypes.HAS_PARENT)));
		for (SimpleDataTreeNode n : children)
			if (editNode.references((StringTable) n.properties().getPropertyValue(Archetypes.HAS_PARENT)))
				result.add(n);
		return result;
	}

	@SuppressWarnings("unchecked")
	private List<SimpleDataTreeNode> getPropertySpecs(SimpleDataTreeNode spec) {
		return (List<SimpleDataTreeNode>) get(spec.getChildren(),
				selectZeroOrMany(hasTheLabel(Archetypes.HAS_PROPERTY)));
	}

	@SuppressWarnings("unchecked")
	private List<SimpleDataTreeNode> getEdgeSpecs(SimpleDataTreeNode spec) {
		return (List<SimpleDataTreeNode>) get(spec.getChildren(), selectZeroOrMany(hasTheLabel(Archetypes.HAS_EDGE)));
	}

	@SuppressWarnings("unused")
	private SimpleDataTreeNode getSubClassPropertySpec(SimpleDataTreeNode spec) {
		return (SimpleDataTreeNode) get(spec.getChildren(),
				selectZeroOrOne(hasProperty(Archetypes.HAS_NAME, TWA.SUBCLASS)));
	}

	private StringTable getClassTable(SimpleDataTreeNode propertySpec) {
		SimpleDataTreeNode constraint = (SimpleDataTreeNode) get(propertySpec.getChildren(),
				selectOne(hasProperty(Archetypes.CLASS_NAME, IsInValueSetQuery.class.getName())));
		return (StringTable) constraint.properties().getPropertyValue(TWA.VALUES);
	}

	@SuppressWarnings("unchecked")
	private List<Class<? extends TreeNode>> listValidClasses(StringTable classes) {
		List<Class<? extends TreeNode>> result = new ArrayList();
		for (int i = 0; i < classes.size(); i++) {
			try {
				result.add((Class<? extends TreeNode>) Class.forName(classes.getWithFlatIndex(i), true,
						OmugiClassLoader.getAppClassLoader()));
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return result;

	};

}
