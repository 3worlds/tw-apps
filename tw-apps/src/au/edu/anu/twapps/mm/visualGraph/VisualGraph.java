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

import au.edu.anu.rscs.aot.AotException;
import au.edu.anu.rscs.aot.graph.property.PropertyKeys;
import au.edu.anu.twapps.exceptions.TwAppsException;
import fr.cnrs.iees.graph.Edge;
import fr.cnrs.iees.graph.EdgeFactory;
import fr.cnrs.iees.graph.Node;
import fr.cnrs.iees.graph.NodeFactory;
import fr.cnrs.iees.graph.TreeNode;
import fr.cnrs.iees.graph.TreeNodeFactory;
import fr.cnrs.iees.graph.impl.TreeGraph;
import fr.cnrs.iees.properties.ReadOnlyPropertyList;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.SharedPropertyListImpl;
import fr.ens.biologie.generic.Textable;

public class VisualGraph extends TreeGraph<VisualNode, VisualEdge> implements //
		NodeFactory, //
		EdgeFactory, //
		TreeNodeFactory, //
		Textable, //
		VisualKeys
/*
 * PropertyListFactory - should be there
 */ {
	private PropertyKeys nodeKeys;
	private PropertyKeys edgeKeys;

	// Constructors
	public VisualGraph() {
		super();
		/* PropertyListFactory */
		this.nodeKeys = getNodeKeys();
		this.edgeKeys = getEdgeKeys();
	}

	public VisualGraph(Iterable<VisualNode> list) {
		super(list);
	}

	public VisualGraph(VisualNode root) {
		super(root);
	}

	public EdgeFactory getEdgeFactory() {
		return this;
	}

//	public TreeNodeFactory getTreeFactory() {
		public VisualGraph getTreeFactory() {
		return this;
	}

	// ---------------------- NODE FACTORY -------------------------

	// This is disabled because any new node has to be inserted into the tree at the
	// proper spot. We dont want free-floating nodes in an AOT graph because it's a
	// tree.
	@Override
	public VisualNode makeNode(String arg0, String arg1, ReadOnlyPropertyList arg2) {
		throw new AotException("Attempt to instantiate an VisualNode outside of the tree context.");
	}

	// -------------------------------EdgeFactory

	/*
	 * Attempting to create a node or edge without labels or names should produce an
	 * exception NOT some self-generated thing.
	 */
	@Override
	public VisualEdge makeEdge(Node start, Node end, String label, String name, ReadOnlyPropertyList properties) {
		if (properties == null)
			properties = new SharedPropertyListImpl(edgeKeys);
		return new VisualEdge(start, end, label, name, (SimplePropertyList) properties, this);
	}

	@Override
	public VisualNode makeTreeNode(TreeNode parent) {
		return makeTreeNode(parent, null, null, null);
	}

	// ------------------------- TreeNodeFactory

	@Override
	public TreeNode makeTreeNode(TreeNode parent, String proposedId, SimplePropertyList properties) {
		if (properties == null)
			properties = new SharedPropertyListImpl(nodeKeys);
		VisualNode node = new VisualNode(label, name, properties, this);
		if (!nodes.add(node))
			throw new TwAppsException("Attempt to add duplicate node: " + node.toDetailedString());
		node.setParent(parent);
		if (parent != null)
			parent.addChild(node);
		return node;
	}

	@Override
	public Edge makeEdge(Node start, Node end, String proposedId, ReadOnlyPropertyList props) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node makeNode(String proposedId, ReadOnlyPropertyList props) {
		// TODO Auto-generated method stub
		return null;
	}
}