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

package au.edu.anu.twapps.mm.visualGraph;

import java.util.Map;

//import au.edu.anu.rscs.aot.graph.AotEdge;
import au.edu.anu.twapps.exceptions.TwAppsException;
import fr.cnrs.iees.graph.Edge;
import fr.cnrs.iees.graph.EdgeFactory;
import fr.cnrs.iees.graph.Node;
import fr.cnrs.iees.graph.TreeNode;
import fr.cnrs.iees.graph.impl.DefaultEdgeFactory;
import fr.cnrs.iees.graph.impl.DefaultNodeFactory;
import fr.cnrs.iees.graph.impl.DefaultTreeNodeFactory;
import fr.cnrs.iees.graph.impl.TreeGraph;
import fr.cnrs.iees.graph.impl.TreeGraphFactory;
import fr.cnrs.iees.identity.IdentityScope;
import fr.cnrs.iees.properties.ReadOnlyPropertyList;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.ens.biologie.generic.Textable;

/**
 * Author Ian Davies
 *
 * Date 2 Feb. 2019
 */
public class VisualGraph extends TreeGraph<VisualNode, VisualEdge>//
		implements//
		DefaultNodeFactory, //
		DefaultEdgeFactory, //
		DefaultTreeNodeFactory, //
		Textable, //
		VisualKeys {

	private TreeGraphFactory factory;

	// Constructors
	public VisualGraph() {
		super();
		init();
	}

	public VisualGraph(Iterable<VisualNode> list) {
		super(list);
		init();
	}
	
	public VisualGraph(Map<String,String> labels) {
		super();
		init();
	}
	
	public VisualGraph(VisualNode root) {
		//???
		super(root);
		init();
	}
	private void init() {
		this.factory = new TreeGraphFactory();
	}
	

	public EdgeFactory getEdgeFactory() {
		return this;
	}

//	public TreeNodeFactory getTreeFactory() {
	public TreeGraphFactory getTreeFactory() {
		return factory;
	}

	// ---------------------- NODE FACTORY -------------------------

	// This is disabled because any new node has to be inserted into the tree at the
	// proper spot. We dont want free-floating nodes in an AOT graph because it's a
	// tree.

	// Factory hierarchy needs rewriting. Perhaps Factories2.dia Elements2.dia?
	@Override
	public Node makeNode() {
		throw new TwAppsException("Attempt to instantiate an VisualNode outside of the tree context.");
	}

	@Override
	public Node makeNode(ReadOnlyPropertyList arg0) {
		return makeNode();
	}

	@Override
	public Node makeNode(String arg0) {
		return makeNode();
	}

	@Override
	public Node makeNode(Class<? extends Node> arg0) {
		return makeNode();
	}

	@Override
	public Node makeNode(Class<? extends Node> arg0, String arg1) {
		return makeNode();
	}

	@Override
	public Node makeNode(Class<? extends Node> arg0, ReadOnlyPropertyList arg1) {
		return makeNode();
	}

	@Override
	public Node makeNode(Class<? extends Node> arg0, String arg1, ReadOnlyPropertyList arg2) {
		return makeNode();
	}

	@Override
	public Node makeNode(String proposedId, ReadOnlyPropertyList props) {
		return makeNode();
	}

	// -------------------------------EdgeFactory

	@Override
	public VisualEdge makeEdge(Node start, Node end) {
		return (VisualEdge) factory.makeEdge(VisualEdge.class, start, end);
	}

	@Override
	public VisualEdge makeEdge(Node start, Node end, ReadOnlyPropertyList props) {
		return (VisualEdge) factory.makeEdge(VisualEdge.class, start, end);
	}

	@Override
	public VisualEdge makeEdge(Node start, Node end, String proposedId, ReadOnlyPropertyList props) {
		return (VisualEdge) factory.makeEdge(VisualEdge.class, start, end, proposedId, props);
	}

	@Override
	public VisualEdge makeEdge(Node start, Node end, String proposedId) {
		return (VisualEdge) factory.makeEdge(VisualEdge.class, start, end, proposedId);
	}

	@Override
	public Edge makeEdge(Class<? extends Edge> edgeClass, Node start, Node end) {
		return (VisualEdge) factory.makeEdge(edgeClass, start, end);
	}

	@Override
	public Edge makeEdge(Class<? extends Edge> edgeClass, Node start, Node end, String proposedId) {
		return (VisualEdge) factory.makeEdge(edgeClass, start, end, proposedId);
	}

	@Override
	public Edge makeEdge(Class<? extends Edge> edgeClass, Node start, Node end, ReadOnlyPropertyList props) {
		return (VisualEdge) factory.makeEdge(edgeClass, start, end, props);
	}

	@Override
	public Edge makeEdge(Class<? extends Edge> edgeClass, Node start, Node end, String proposedId,
			ReadOnlyPropertyList props) {
		return (VisualEdge) factory.makeEdge(edgeClass, start, end, proposedId, props);
	}

	// ------------------------- TreeNodeFactory

	private VisualNode addANode(VisualNode node) {
		if (nodes.add(node))
			return node;
				
		else
			throw new TwAppsException("Attempt to add duplicate node");
	}

	@Override
	public VisualNode makeTreeNode(TreeNode parent, SimplePropertyList props) {
		return addANode((VisualNode) factory.makeTreeNode(VisualNode.class, parent, props));
	}

	@Override
	public VisualNode makeTreeNode(TreeNode parent) {
		return addANode((VisualNode) factory.makeTreeNode(VisualNode.class, parent));
	}

	@Override
	public VisualNode makeTreeNode(TreeNode parent, String proposedId) {
		return addANode((VisualNode) factory.makeTreeNode(VisualNode.class, parent, proposedId));
	}

	@Override
	public VisualNode makeTreeNode(TreeNode parent, String proposedId, SimplePropertyList props) {
		return addANode((VisualNode) factory.makeTreeNode(VisualNode.class, parent, proposedId, props));
	}

	@Override
	public TreeNode makeTreeNode(Class<? extends TreeNode> nodeClass, TreeNode parent) {
		return addANode((VisualNode) factory.makeTreeNode(nodeClass, parent));
	}

	@Override
	public TreeNode makeTreeNode(Class<? extends TreeNode> nodeClass, TreeNode parent, SimplePropertyList props) {
		return addANode((VisualNode) factory.makeTreeNode(nodeClass, parent, props));
	}

	@Override
	public TreeNode makeTreeNode(Class<? extends TreeNode> nodeClass, TreeNode parent, String proposedId) {
		return addANode((VisualNode) factory.makeTreeNode(nodeClass, parent, proposedId));
	}

	@Override
	public TreeNode makeTreeNode(Class<? extends TreeNode> nodeClass, TreeNode parent, String proposedId,
			SimplePropertyList props) {
		return addANode((VisualNode) factory.makeTreeNode(nodeClass, parent, proposedId, props));
	}

	@Override
	public IdentityScope scope() {
		// TODO Auto-generated method stub
		return null;
	}

}