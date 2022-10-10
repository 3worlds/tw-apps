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

package au.edu.anu.twapps.mm.layout;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import au.edu.anu.twapps.mm.layoutGraph.ElementDisplayText;
import au.edu.anu.twapps.mm.layoutGraph.LayoutNode;
import fr.cnrs.iees.graph.Edge;

/**
 * @author Ian Davies - 30 Apr 2020
 *         <p>
 *         Abstract adapter class for building tree vertices.
 */
public abstract class TreeVertexAdapter extends VertexAdapter implements ITreeVertex<TreeVertexAdapter> {
	private List<TreeVertexAdapter> _children;
	private TreeVertexAdapter _parent;

	/**
	 * Tree vertex constructor.
	 * 
	 * @param parent Parent vertex.
	 * @param node   Underlying {@link LayoutNode}.
	 */
	public TreeVertexAdapter(TreeVertexAdapter parent, LayoutNode node) {
		super(node);
		this._parent = parent;
		this._children = new ArrayList<>();
	}

	/**
	 * @return true if this node has edges to other visible nodes.
	 */
	public boolean nodeHasEdgesToVisibleNodes() {
		for (Edge e : getNode().edges()) {
			LayoutNode startNode = (LayoutNode) e.startNode();
			LayoutNode endNode = (LayoutNode) e.endNode();
			if (startNode.isVisible() && endNode.isVisible())
				return true;
		}
		return false;
	}

	@Override
	public boolean isChildless() {
		return _children.isEmpty();
	}

	@Override
	public boolean hasParent() {
		return _parent != null;
	}

	@Override
	public List<TreeVertexAdapter> getChildren() {
		return _children;
	}

	@Override
	public TreeVertexAdapter getParent() {
		return _parent;
	}

	@Override
	public void jitter(double f, Random rnd) {
		super.jitter(f, rnd);
		for (TreeVertexAdapter c : getChildren())
			c.jitter(f, rnd);
	}

	@Override
	public void getLayoutBounds(Point2D min, Point2D max) {
		super.getLayoutBounds(min, max);
		for (TreeVertexAdapter c : getChildren())
			c.getLayoutBounds(min, max);
	}

	@Override
	public void normalise(Rectangle2D from, Rectangle2D to) {
		super.normalise(from, to);
		for (IVertex c : getChildren())
			c.normalise(from, to);
	}

	/**
	 * Recursively builds the tree from just those nodes that are currently visible.
	 * 
	 * @param vertex  The current vertex.
	 * @param factory The vertex factory.
	 */
	public static void buildSpanningTree(TreeVertexAdapter vertex, ITreeVertexFactory factory) {
		List<LayoutNode> sortList = new ArrayList<>();
		String parentId = "";
		if (vertex.hasParent())
			parentId = vertex.getParent().getNode().id();
		for (LayoutNode nChild : vertex.getNode().getChildren()) {
			String childId = nChild.id();
			if (!nChild.isCollapsed() && !childId.equals(parentId) && nChild.isVisible())
				sortList.add(nChild);
		}
		LayoutNode nParent = vertex.getNode().getParent();
		if (nParent != null)
			if (!nParent.isCollapsed() && !nParent.id().equals(parentId) && nParent.isVisible())
				sortList.add(nParent);

		sortList.sort((n1, n2) -> n1.getDisplayText(ElementDisplayText.RoleName)
				.compareTo(n2.getDisplayText(ElementDisplayText.RoleName)));
//		sortList.sort(new Comparator<VisualNode>() {
//			@Override
//			public int compare(VisualNode o1, VisualNode o2) {
//				return o1.getDisplayText(ElementDisplayText.RoleName)
//						.compareTo(o2.getDisplayText(ElementDisplayText.RoleName));
//			}
//		});
		for (LayoutNode nChild : sortList) {
			TreeVertexAdapter vChild = factory.makeVertex(vertex, nChild);
			vertex.getChildren().add(vChild);
			buildSpanningTree(vChild, factory);
		}
	}

	/**
	 * Recursively builds a list of vertices that have no visible edges. These can
	 * be placed to one side of the display to reduce clutter.
	 * 
	 * @param lstIsolated          The accumulating list of isolated vertices.
	 * @param showParentChildEdges true if these edges are currently displayed.
	 * @param showCrossLinkEdges   true if these edges are currently displayed.
	 */
	public void getIsolated(List<TreeVertexAdapter> lstIsolated, boolean showParentChildEdges,
			boolean showCrossLinkEdges) {
		if (!showParentChildEdges)
			if (!showCrossLinkEdges)
				lstIsolated.add(this);
			else if (!nodeHasEdgesToVisibleNodes())
				lstIsolated.add(this);
		for (TreeVertexAdapter c : getChildren()) {
			c.getIsolated(lstIsolated, showParentChildEdges, showCrossLinkEdges);
		}
	}

}
