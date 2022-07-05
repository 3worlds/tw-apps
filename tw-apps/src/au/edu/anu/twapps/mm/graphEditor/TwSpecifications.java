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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import au.edu.anu.rscs.aot.archetype.ArchetypeArchetypeConstants;
import au.edu.anu.rscs.aot.collections.tables.StringTable;
import au.edu.anu.rscs.aot.queries.Queryable;
import au.edu.anu.rscs.aot.util.IntegerRange;
import au.edu.anu.twapps.dialogs.Dialogs;
import au.edu.anu.twapps.exceptions.TwAppsException;
import au.edu.anu.twapps.mm.visualGraph.ElementDisplayText;
import au.edu.anu.twapps.mm.visualGraph.VisualNode;
import au.edu.anu.twcore.archetype.TWA;
import au.edu.anu.twcore.archetype.TwArchetypeConstants;
import au.edu.anu.twcore.archetype.tw.CheckSubArchetypeQuery;
import au.edu.anu.twcore.archetype.tw.ChildXorPropertyQuery;
import au.edu.anu.twcore.archetype.tw.IsInValueSetQuery;
import au.edu.anu.twcore.archetype.tw.NameStartsWithUpperCaseQuery;
import au.edu.anu.twcore.archetype.tw.OutEdgeXorPropertyQuery;
import au.edu.anu.twcore.archetype.tw.PropertyXorQuery;
import au.edu.anu.twcore.archetype.tw.RequirePropertyQuery;
import fr.cnrs.iees.OmugiClassLoader;
import fr.cnrs.iees.graph.DataHolder;
import fr.cnrs.iees.graph.Tree;
import fr.cnrs.iees.graph.TreeNode;
import fr.cnrs.iees.graph.impl.SimpleDataTreeNode;
import fr.cnrs.iees.identity.impl.PairIdentity;
import fr.cnrs.iees.io.parsing.ValidPropertyTypes;
import fr.cnrs.iees.properties.ExtendablePropertyList;
import fr.ens.biologie.generic.utils.Duple;

import static au.edu.anu.rscs.aot.queries.CoreQueries.*;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;

/**
 * Author Ian Davies - 10 Jan. 2019
 */
public class TwSpecifications implements //
		Specifications, //
		ArchetypeArchetypeConstants, //
		TwArchetypeConstants {
// replaced by TableAdapter.equals() method
//	private static boolean equals(StringTable t1, StringTable t2) {
//		// NB: should assume neither table is null.
//		// yes, but table elements may be null...
//		if (t1.ndim() != t2.ndim())
//			return false;
//		if (t1.size() != t2.size())
//			return false;
//		for (int i = 0; i < t1.size(); i++) {
//			if (t1.getWithFlatIndex(i)==null) {
//				if (t2.getWithFlatIndex(i)!=null)
//					return false;
//			}
//			else {
//				if (t2.getWithFlatIndex(i)==null)
//					return false;
//				else
//					if (!t1.getWithFlatIndex(i).equals(t2.getWithFlatIndex(i)))
//						return false;
//			}
//		}
//		return true;
//	}

	@SuppressWarnings("unchecked")
	@Override
	public SimpleDataTreeNode getSpecsOf(VisualNodeEditable editNode, TreeNode root, Set<String> discoveredFiles){
		for (TreeNode childSpec : root.getChildren()) {
			if (isOfClass((SimpleDataTreeNode) childSpec, editNode.visualNode().configNode().classId())) {
				StringTable parentsSpecTable = (StringTable) ((SimpleDataTreeNode) childSpec).properties()
						.getPropertyValue(aaHasParent);
				StringTable parentsTable = editNode.visualNode().parentTable();
				// Dodgy: the parentTable is unknown during an import;
				if (parentsTable == null)
					return (SimpleDataTreeNode) childSpec;
//				else if (equals(parentsTable, parentsSpecTable)) {
				else if (parentsTable.equals(parentsSpecTable)) {
					DataHolder dh = (DataHolder) childSpec;
					if (dh.properties().hasProperty(aaHasId)) {
						String hasId = (String) dh.properties().getPropertyValue(aaHasId);
						if (editNode.visualNode().configNode().id().equals(hasId))
							return (SimpleDataTreeNode) childSpec;
					} else
						return (SimpleDataTreeNode) childSpec;
				}
			}
			// search subArchetypes
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
						SimpleDataTreeNode result = getSpecsOf(editNode, tree.root(), discoveredFiles);
						if (result != null)
							return result;
					}
				}
			}
		}
		return null; // DO NOT throw exp on null as this is recursive and expected!
	}

	@SuppressWarnings("unchecked")
	@Override
	public SimpleDataTreeNode getSubSpecsOf(SimpleDataTreeNode baseSpecs, Class<? extends TreeNode> subClass) {
		// multiple stopping condtions have many entries of IsOfClass
		if (subClass != null) {
			String parent = (String) baseSpecs.properties().getPropertyValue(aaIsOfClass);
			Tree<?> subClassTree = getSubArchetype(baseSpecs, subClass);
			if (subClassTree == null)
				return null;
			List<SimpleDataTreeNode> specs = (List<SimpleDataTreeNode>) get(subClassTree.root().getChildren(),
					selectOneOrMany(hasProperty(aaIsOfClass, parent)));
			if (specs.size() == 1)
				return specs.get(0);
			else {
				for (SimpleDataTreeNode spec : specs) {
					StringTable t = (StringTable) spec.properties().getPropertyValue(aaHasParent);
					if (t.contains(parent + PairIdentity.LABEL_NAME_SEPARATOR)) {
						return spec;
					}
				}
			}

		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Iterable<SimpleDataTreeNode> getChildSpecsOf(VisualNodeEditable editNode, SimpleDataTreeNode parentSpec,
			SimpleDataTreeNode parentSubSpec, TreeNode root) {
		// String parentLabel = (String)
		// parentSpec.properties().getPropertyValue(aaIsOfClass);
		List<SimpleDataTreeNode> children = (List<SimpleDataTreeNode>) get(root.getChildren(),
				selectZeroOrMany(hasProperty(aaHasParent)));
		// could have a query here for finding a parent in a parent Stringtable
		List<SimpleDataTreeNode> result = new ArrayList<>();
		for (SimpleDataTreeNode n : children)
			if (editNode.references((StringTable) n.properties().getPropertyValue(aaHasParent)))
				result.add(n);

		// addChildrenTo(result, parentLabel, children);
		if (parentSubSpec != null) {
			// look for children in the subclass tree root
			children = (List<SimpleDataTreeNode>) get(parentSubSpec.getParent().getChildren(),
					selectZeroOrMany(hasProperty(aaHasParent)));
			// addChildrenTo(result, parentLabel, children);
			for (SimpleDataTreeNode an : children)
				if (editNode.references((StringTable) an.properties().getPropertyValue(aaHasParent)))
					result.add(an);

		}

		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Iterable<SimpleDataTreeNode> getPropertySpecsOf(SimpleDataTreeNode spec, SimpleDataTreeNode subSpec) {
		List<SimpleDataTreeNode> results = (List<SimpleDataTreeNode>) get(spec.getChildren(),
				selectZeroOrMany(hasTheLabel(aaHasProperty)));
		if (subSpec != null) {
			List<SimpleDataTreeNode> subList = (List<SimpleDataTreeNode>) get(subSpec.getChildren(),
					selectZeroOrMany(hasTheLabel(aaHasProperty)));
			results.addAll(subList);
		}
		return results;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Iterable<SimpleDataTreeNode> getEdgeSpecsOf(SimpleDataTreeNode baseSpec, SimpleDataTreeNode subSpec) {
		List<SimpleDataTreeNode> result = (List<SimpleDataTreeNode>) get(baseSpec.getChildren(),
				selectZeroOrMany(hasTheLabel(aaHasEdge)));
		if (subSpec != null)
			result.addAll(
					(List<SimpleDataTreeNode>) get(subSpec.getChildren(), selectZeroOrMany(hasTheLabel(aaHasEdge))));
		return result;
	}

	@Override
	public boolean nameStartsWithUpperCase(SimpleDataTreeNode spec) {
		return getConstraint(spec, NameStartsWithUpperCaseQuery.class.getName()) != null;
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public List<Class<? extends TreeNode>> getSubClassesOf(SimpleDataTreeNode spec) {
		List<Class<? extends TreeNode>> result = new ArrayList<>();
		SimpleDataTreeNode propertySpec = (SimpleDataTreeNode) get(spec.getChildren(),
				selectZeroOrOne(hasProperty(aaHasName, twaSubclass)));
		if (propertySpec != null) {
			SimpleDataTreeNode constraint = (SimpleDataTreeNode) get(propertySpec.getChildren(),
					selectOne(hasProperty(aaClassName, IsInValueSetQuery.class.getName())));
			StringTable classes = (StringTable) constraint.properties().getPropertyValue(twaValues);
			for (int i = 0; i < classes.size(); i++) {
				try {
					result.add((Class<? extends TreeNode>) Class.forName(classes.getWithFlatIndex(i), true,
							OmugiClassLoader.getAppClassLoader()));
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String[]> getQueryStringTables(SimpleDataTreeNode spec, Class<? extends Queryable> queryClass) {
		List<String[]> result = new ArrayList<>();
		if (spec == null)
			return result;
		List<SimpleDataTreeNode> querySpecs = (List<SimpleDataTreeNode>) get(spec.getChildren(),
				selectZeroOrMany(hasProperty(aaClassName, queryClass.getName())));
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

	private boolean entriesContains(String key, List<String[]> entries) {
		for (String[] ss : entries)
			for (String s : ss)
				if (s.equals(key))
					return true;
		return false;
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
		// TODO: this is a mess! to be cleaned up
		if (!entries.isEmpty()) {
			List<String> selectedKeys = Dialogs.getRadioButtonChoices(childId, "PropertyChoices", "", entries);
			if (selectedKeys == null)
				return false;
			Iterator<SimpleDataTreeNode> iter = propertySpecs.iterator();
			String keyHandled = null;
			while (iter.hasNext()) {
				SimpleDataTreeNode ps = iter.next();
				String key = (String) ps.properties().getPropertyValue(aaHasName);
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
		return (IntegerRange) spec.properties().getPropertyValue(aaMultiplicity);
	}

	// -----------------------end of implementation methods-----------------------

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
				selectZeroOrMany(hasProperty(aaClassName, CheckSubArchetypeQuery.class.getName())));

		for (SimpleDataTreeNode constraint : constraints) {
			StringTable pars = (StringTable) constraint.properties().getPropertyValue(twaParameters);
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
				selectZeroOrOne(andQuery(hasTheLabel(aaMustSatisfyQuery), hasProperty(aaClassName, constraintClass))));
	}

	private boolean isOfClass(SimpleDataTreeNode child, String label) {
		String ioc = (String) child.properties().getPropertyValue(aaIsOfClass);
		return ioc.equals(label);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SimpleDataTreeNode> getQueries(SimpleDataTreeNode spec, Class<? extends Queryable>... queries) {
		List<SimpleDataTreeNode> result = new ArrayList<>();
		if (spec == null)
			return result;
		for (Class<? extends Queryable> query : queries) {
			result.addAll((List<SimpleDataTreeNode>) get(spec.getChildren(), selectZeroOrMany(
					andQuery(hasTheLabel(aaMustSatisfyQuery), hasProperty(aaClassName, query.getName())))));
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
			if (query.properties().hasProperty(twaNodeLabel1) && query.properties().hasProperty(twaNodeLabel2)) {
				result.add(new Duple<String, String>((String) query.properties().getPropertyValue(twaNodeLabel1),
						(String) query.properties().getPropertyValue(twaNodeLabel2)));
			}
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void filterRequiredPropertyQuery(VisualNode vnode, SimpleDataTreeNode baseSpec, SimpleDataTreeNode subSpec) {
		List<SimpleDataTreeNode> queries = getQueries(baseSpec, RequirePropertyQuery.class);

		queries.addAll(getQueries(subSpec, RequirePropertyQuery.class));
		Set<String> pset = new HashSet<>();
		for (SimpleDataTreeNode query : queries) {
			StringTable conditions = (StringTable) query.properties().getPropertyValue(twaConditions);
			String key = conditions.getWithFlatIndex(1);
			if (!pset.contains(key)) {
				pset.add(key);
				Object obj = vnode.configGetPropertyValue(key);
				// works with obj ==null
				// gets the string list
				if (obj instanceof Enum<?>) {
					Class<? extends Enum<?>> e = (Class<? extends Enum<?>>) obj.getClass();
					String[] names = ValidPropertyTypes.namesOf(e);
					int choice = Dialogs.getListChoice(names, vnode.getDisplayText(ElementDisplayText.RoleName), key,
							obj.getClass().getSimpleName());
					if (choice >= 0) {
						Enum<?> value = ValidPropertyTypes.valueOf(names[choice], e);
						vnode.configNode().properties().setProperty(key, value);
					}
				}
			}
		}

		ExtendablePropertyList props = (ExtendablePropertyList) vnode.cProperties();
		for (SimpleDataTreeNode query : queries) {
			StringTable conditions = (StringTable) query.properties().getPropertyValue(twaConditions);
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
				selectZeroOrMany(hasTheLabel(aaHasProperty)));
		if (subSpec != null) {
			List<SimpleDataTreeNode> propsSub = (List<SimpleDataTreeNode>) get(subSpec.getChildren(),
					selectZeroOrMany(hasTheLabel(aaHasProperty)));
			props.addAll(propsSub);
		}

		Set<String> depProps = new HashSet<>();
		List<SimpleDataTreeNode> reqs = getQueries(baseSpec, RequirePropertyQuery.class);
		reqs.addAll(getQueries(subSpec, RequirePropertyQuery.class));
		for (SimpleDataTreeNode req : reqs) {
			// conditions =
			// StringTable(([8]"units","dataElementType","Double","Float","Integer","Long","Short","Byte"))
			StringTable conditions = (StringTable) req.properties().getPropertyValue(twaConditions);
			depProps.add(conditions.getWithFlatIndex(0));
			depProps.add(conditions.getWithFlatIndex(1));
		}

		List<SimpleDataTreeNode> cXORpqs = getQueries(baseSpec, ChildXorPropertyQuery.class);
		cXORpqs.addAll(getQueries(subSpec, ChildXorPropertyQuery.class));
		for (SimpleDataTreeNode cXORpq : cXORpqs) {
			// edge_prop = StringTable(([2]"record","dataElementType"))
			StringTable conditions = (StringTable) cXORpq.properties().getPropertyValue(twaEdgeProp);
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
			StringTable conditions = (StringTable) pXORp.properties().getPropertyValue(twaPropList);
			depProps.add(conditions.getWithFlatIndex(0));
			depProps.add(conditions.getWithFlatIndex(1));
		}

		List<SimpleDataTreeNode> result = new ArrayList<>();
		for (SimpleDataTreeNode prop : props) {
			String name = (String) prop.properties().getPropertyValue(aaHasName);
			IntegerRange ir = (IntegerRange) prop.properties().getPropertyValue(aaMultiplicity);
			if (ir.getFirst() == 0 && ir.getLast() == 1 && !depProps.contains(name))
				result.add(prop);
		}

		return result;
	}
}
