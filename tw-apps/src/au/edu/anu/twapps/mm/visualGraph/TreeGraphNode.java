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

import java.util.Collection;
import java.util.Objects;

import fr.cnrs.iees.graph.NodeFactory;
import fr.cnrs.iees.graph.impl.SimpleNodeImpl;
import fr.cnrs.iees.tree.TreeNode;
import fr.cnrs.iees.tree.TreeNodeFactory;
import fr.ens.biologie.generic.Labelled;
import fr.ens.biologie.generic.Named;
import fr.ens.biologie.generic.NamedAndLabelled;

public class TreeGraphNode extends SimpleNodeImpl implements TreeNode, NamedAndLabelled {
	
	private TreeNode treenode;
	private LabelNameReference ln;
	
	// -- SimpleNodeImpl
	protected TreeGraphNode(String label, String name,NodeFactory factory) {
		super(factory);
		ln = new LabelNameReference(label,name);
	}
	

//-------------------TreeNode
	protected final TreeNode getTreeNode() {
		return treenode;
	}

	protected final void setTreeNode(TreeNode treenode) {
		this.treenode = treenode;
	}

	@Override
	public final void addChild(TreeNode child) {
		treenode.addChild(child);
	}

	@Override
	public final Iterable<TreeNode> getChildren() {
		return treenode.getChildren();
	}

	@Override
	public final TreeNode getParent() {
		return treenode.getParent();
	}

	@Override
	public final boolean hasChildren() {
		return treenode.hasChildren();
	}

	@Override
	public final int nChildren() {
		return treenode.nChildren();
	}

	@Override
	public final void setChildren(TreeNode... children) {
		treenode.setChildren(children);
	}

	@Override
	public final void setChildren(Iterable<TreeNode> children) {
		treenode.setChildren(children);

	}

	@Override
	public final void setChildren(Collection<TreeNode> children) {
		treenode.setChildren(children);

	}

	@Override
	public final void setParent(TreeNode parent) {
		if (treenode.getParent() == null)
			treenode.setParent(parent);
	}

	@Override
	public final TreeNodeFactory treeNodeFactory() {
		return treeNodeFactory();
	}

	@Override
	public final String getName() {
		return ln.getName();
	}

	@Override
	public final boolean hasName(String name) {
		return ln.hasName(name);
	}

	@Override
	public final boolean sameName(Named namedItem) {
		return ln.sameName(namedItem);
	}

	@Override
	public final Named setName(String name) {
		return ln.setName(name);
	}

	@Override
	public final String getLabel() {
		return ln.getLabel();
	}

	@Override
	public final boolean hasLabel(String label) {
		return ln.hasLabel(label);
	}

	@Override
	public final boolean sameLabel(Labelled labelledItem) {
		return ln.sameLabel(labelledItem);
	}

	@Override
	public final Labelled setLabel(String label) {
		return ln.setLabel(label);
	}

}
