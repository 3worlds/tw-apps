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

import au.edu.anu.twapps.mm.visualGraph.VisualEdge;
import au.edu.anu.twapps.mm.visualGraph.VisualNode;
import fr.cnrs.iees.graph.impl.SimpleDataTreeNode;
import fr.ens.biologie.generic.utils.Duple;
import fr.ens.biologie.generic.utils.Tuple;

/**
 * Author Ian Davies
 *
 * Date 11 Jan. 2019
 */
public interface StructureEditable {
	/* These methods are all in the context of a single user selected VisualNode */

	/*
	 * Filters a list of possible children depending on current state of the
	 * configuration
	 */
	public List<SimpleDataTreeNode> filterChildSpecs(Iterable<SimpleDataTreeNode> childNodeSpecs);

	/*
	 * Filters a list of edgeSpecs to produce a list of duples of edge labels and
	 * end nodes
	 */
	public List<Tuple<String, VisualNode, SimpleDataTreeNode>> filterEdgeSpecs(Iterable<SimpleDataTreeNode> edgeSpecs);

	/* list of nodes that are eligible children of the edit node */
	public List<VisualNode> orphanedChildList(Iterable<SimpleDataTreeNode> childSpecs);

	/* create child on childSpec - optional childId */
	void onNewChild(String childLabel, String childId, SimpleDataTreeNode childBaseSpec);

	/* add edge called String,to an end node */
	public void onNewEdge(Tuple<String, VisualNode, SimpleDataTreeNode> details,double duration);

	public void onDeleteEdge(VisualEdge edge);

	/* delete this node */
	public void onDeleteNode(double duration);

	public boolean onRenameNode();

	public boolean onRenameEdge(VisualEdge edge);

	public void onDeleteTree(VisualNode root,double duration);

	/* collapse tree from this node */
	public void onCollapseTree(VisualNode childRoot,double duration);

	/* collapse all expanded trees from this node */
	public void onCollapseTrees(double duration);

	/* expand tree from this node */
	public void onExpandTree(VisualNode childRoot,double duration);

	/* expand all collapsed trees from this node */
	public void onExpandTrees(double duration);

	/* connect node as child of this node */
	public void onReconnectChild(VisualNode childNode);

	/* build implementation specific gui */
	public void buildgui();

	void onExportTree(VisualNode root);

	void onImportTree(SimpleDataTreeNode childSpec,double duration);

	void onDeleteParentLink(VisualNode child);
	
	boolean onOptionalProperties(List<SimpleDataTreeNode> propertySpecs,List<Duple<VisualEdge, SimpleDataTreeNode>> optionalEdgePropertySpecs);

}
