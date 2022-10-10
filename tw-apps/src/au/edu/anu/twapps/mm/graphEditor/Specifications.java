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

import java.util.List;
import java.util.Set;

//import au.edu.anu.rscs.aot.queries.Query;
import au.edu.anu.rscs.aot.queries.Queryable;
import au.edu.anu.rscs.aot.util.IntegerRange;
import au.edu.anu.twapps.mm.layoutGraph.LayoutNode;
import fr.cnrs.iees.graph.TreeNode;
import fr.cnrs.iees.graph.impl.SimpleDataTreeNode;
import fr.ens.biologie.generic.utils.Duple;
import au.edu.anu.twcore.archetype.tw.OutNodeXorQuery;
import au.edu.anu.twcore.archetype.tw.RequirePropertyQuery;

/**
 * This interface is the contract between the 3Worlds archetype and what a
 * configuration graph builder (i.e. ModelMaker) requires.
 * 
 * @author Ian Davies -10 Jan. 2019
 */
public interface Specifications {

	/**
	 * Get specification of a given node in the configuration graph. This is a
	 * recursive function, called when searching all sub-archetype files.
	 * Ultimately, the caller should ensure that a specification has been found and
	 * throw an exception if this is not the case.
	 * <p>
	 * The method must add an entry for each archetype file it opens.
	 * 
	 * @param editNode        {@link VisualNodeEditable} interface of the
	 *                        {@link LayoutNode} being edited.
	 * @param root            The root node of the specification archetype tree.
	 * @param discoveredFiles Archetype files that have currently been searched.
	 * @return The specification {@link TreeNode} (often null during recursive
	 *         searches).
	 */
	public SimpleDataTreeNode getSpecsOf(VisualNodeEditable editNode, TreeNode root, Set<String> discoveredFiles);

	/**
	 * Get sub-class specification of a given node in the configuration graph. Null
	 * is returned if the node has no sub-class specification.
	 * 
	 * @param baseSpecs The base specification returned by
	 *                  {@link #getSpecsOf(VisualNodeEditable,TreeNode,Set)
	 *                  getSpecsOf}
	 * @param subClass  The java class of the node.
	 * @return The sub-class specification if one exists.
	 */
	public SimpleDataTreeNode getSubSpecsOf(SimpleDataTreeNode baseSpecs, Class<? extends TreeNode> subClass);

	/**
	 * Get a list of the specifications of all potential children of the parent
	 * node.
	 * 
	 * @param editParent {@link VisualNodeEditable} interface of the parent
	 *                   {@link LayoutNode}.
	 * @param baseSpec   The base specification returned by
	 *                   {@link #getSpecsOf(VisualNodeEditable,TreeNode,Set)
	 *                   getSpecsOf}
	 * @param subSpec    The sub-class specification (can be null) returned by
	 *                   {@link #getSubSpecsOf(SimpleDataTreeNode,Class)
	 *                   getSubSpecsOf}.
	 * @param root       Archetype tree root.
	 * @return List of possible child specifications.
	 */
	public Iterable<SimpleDataTreeNode> getChildSpecsOf(VisualNodeEditable editParent, SimpleDataTreeNode baseSpec,
			SimpleDataTreeNode subSpec, TreeNode root);

	/**
	 * Get a list of the specifications of all possible out-edges from the given
	 * base and sub-class specifications.
	 * 
	 * @param baseSpec The base specification returned by
	 *                 {@link #getSpecsOf(VisualNodeEditable,TreeNode,Set)
	 *                 getSpecsOf}
	 * @param subSpec  The sub-class specification returned by
	 *                 {@link #getSubSpecsOf(SimpleDataTreeNode,Class)
	 *                 getSubSpecsOf} (can be null).
	 * @return List of edge specifications (can be empty).
	 */
	public Iterable<SimpleDataTreeNode> getEdgeSpecsOf(SimpleDataTreeNode baseSpec, SimpleDataTreeNode subSpec);

	/**
	 * Get a list of all property specifications from the given base and sub-class
	 * specifications.
	 * 
	 * @param baseSpec The base specification returned by
	 *                 {@link #getSpecsOf(VisualNodeEditable,TreeNode,Set)
	 *                 getSpecsOf}
	 * @param subSpec  The sub-class specification returned by
	 *                 {@link #getSubSpecsOf(SimpleDataTreeNode,Class)
	 *                 getSubSpecsOf} (can be null).
	 * @return List of property specifications (can be empty).
	 */
	public Iterable<SimpleDataTreeNode> getPropertySpecsOf(SimpleDataTreeNode baseSpec, SimpleDataTreeNode subSpec);

	/**
	 * Returns the {@link IntegerRange} property value of the specification's
	 * multiplicity property.
	 * 
	 * @param spec The specification.
	 * @return {@link IntegerRange} which must exist.
	 */
	public IntegerRange getMultiplicityOf(SimpleDataTreeNode spec);

	/**
	 * Ask if the name of the element in the given specification must begin with an
	 * upper-case letter.
	 * 
	 * @param spec The specification containing the name property.
	 * @return true if constraint exists, false otherwise.
	 */
	public boolean nameStartsWithUpperCase(SimpleDataTreeNode spec);

	/**
	 * Get all java classes applicable to the given specification. These classes are
	 * presented to the user for selection when constructing a node for the
	 * configuration graph.
	 * 
	 * @param spec The specification.
	 * @return List of all java classes in the class hierarchy of the class
	 *         specified.
	 */
	public List<Class<? extends TreeNode>> getSubClassesOf(SimpleDataTreeNode spec);

	/**
	 * Get a list of all queries (constraints) of the given class referenced by the
	 * specification.
	 * 
	 * @param spec         The specification root to search for queries.
	 * @param queryClasses List of query classes being sort.
	 * @return List of query specifications matching any of these supplied
	 *         queryClasses.
	 */
	@SuppressWarnings("unchecked")
	public List<SimpleDataTreeNode> getQueries(SimpleDataTreeNode spec, Class<? extends Queryable>... queryClasses);

	/**
	 * Get a list of Query parameters for the given query class within the
	 * specification.
	 * 
	 * @param spec       The specification to search.
	 * @param queryClass Query class sort.
	 * @return List of query parameters.
	 */
	public List<String[]> getQueryStringTables(SimpleDataTreeNode spec, Class<? extends Queryable> queryClass);

	/**
	 * Present user optional property choices when constructing a new node for the
	 * configuration graph.
	 * 
	 * @param propertySpecs List of eligible property specifications.
	 * @param baseSpec      The base specification returned by
	 *                      {@link #getSpecsOf(VisualNodeEditable,TreeNode,Set)
	 *                      getSpecsOf}
	 * @param subSpec       The sub-class specification returned by
	 *                      {@link #getSubSpecsOf(SimpleDataTreeNode,Class)
	 *                      getSubSpecsOf} (can be null).
	 * @param childId       Label of the child being constructed.
	 * @param queryClasses  Applicable query (constraint).
	 * @return true if option was chosen by the user, false otherwise.
	 */
	@SuppressWarnings("unchecked")
	public boolean filterPropertyStringTableOptions(Iterable<SimpleDataTreeNode> propertySpecs,
			SimpleDataTreeNode baseSpec, SimpleDataTreeNode subSpec, String childId,
			Class<? extends Queryable>... queryClasses);

	/**
	 * Get a list of pairs of "NodeLabel1" and "NodeLabel2" pairs for application of
	 * other query choices (cf: {@link OutNodeXorQuery}).
	 * 
	 * @param queries List of query specifications.
	 * @return Pairs of NodeLabel1 and NodeLabel2 values.
	 */
	public List<Duple<String, String>> getNodeLabelDuples(List<SimpleDataTreeNode> queries);

	/**
	 * Filters properties in a newly constructed node with reference to contraints
	 * imposed by {@link RequirePropertyQuery}
	 * 
	 * @param node     The node containing the properties.
	 * @param baseSpec The base specification returned by
	 *                 {@link #getSpecsOf(VisualNodeEditable,TreeNode,Set)
	 *                 getSpecsOf}
	 * @param subSpec  The sub-class specification returned by
	 *                 {@link #getSubSpecsOf(SimpleDataTreeNode,Class)
	 *                 getSubSpecsOf} (can be null).
	 */
	public void filterRequiredPropertyQuery(LayoutNode node, SimpleDataTreeNode baseSpec, SimpleDataTreeNode subSpec);

	/**
	 * Get a list of the optional property specifications (0..1) whose optionality
	 * is not constrained by some other query,
	 * 
	 * @param baseSpec The base specification returned by
	 *                 {@link #getSpecsOf(VisualNodeEditable,TreeNode,Set)
	 *                 getSpecsOf}
	 * @param subSpec  The sub-class specification returned by
	 *                 {@link #getSubSpecsOf(SimpleDataTreeNode,Class)
	 *                 getSubSpecsOf} (can be null).
	 * @return List of property specifications marked optional (can be empty).
	 */
	public List<SimpleDataTreeNode> getOptionalProperties(SimpleDataTreeNode baseSpec, SimpleDataTreeNode subSpec);

}
