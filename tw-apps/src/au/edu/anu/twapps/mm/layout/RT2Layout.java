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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import au.edu.anu.omhtk.rng.Pcg32;
import au.edu.anu.twapps.mm.layoutGraph.LayoutNode;
import au.edu.anu.twcore.root.World;


/**
 * @author Ian Davies - 1 May 2020
 *         <p>
 *         Creates radial tree layout similar to {@link RT1Layout} but with
 *         vertices of the same depth on the same circle. Circle radius increase
 *         with depth.
 *         </p>
 *         <p>
 *         Yee, K.P., Fisher, D., Dhamija, R. and Hearst, M., 2001, October.
 *         Animated exploration of graphs with radial layout. In Proc. IEEE
 *         InfoVis 2001 (pp. 43-50).
 * 
 */
public class RT2Layout implements ILayout {
	private class Factory implements ITreeVertexFactory {

		@Override
		public TreeVertexAdapter makeVertex(TreeVertexAdapter parent, LayoutNode node) {

			return new RT2Vertex(parent, node);
		}

	}

	private RT2Vertex root;
	private List<TreeVertexAdapter> isolated;

	/**
	 * @param rootNode                {@link LayoutNode} to use as the layout root
	 *                                (need not be {@link World} node).
	 * @param includeParentChildEdges Use parent-child relationships.
	 * @param includeCrossLinksEdges  Show cross-link edges
	 * @param sideline                Move isolated vertices to one side.
	 */
	public RT2Layout(LayoutNode rootNode, boolean includeParentChildEdges, boolean includeCrossLinksEdges, boolean sideline) {
		root = new RT2Vertex(null, rootNode);
		TreeVertexAdapter.buildSpanningTree(root, new Factory());
		isolated = new ArrayList<>();
		if (sideline)
			root.getIsolated(isolated, includeParentChildEdges, includeCrossLinksEdges);
	}

	@Override
	public ILayout compute(double jitter) {
		List<RT2Vertex> leaves = new ArrayList<>();
		root.collectLeaves(leaves);
		double angle = 0;
		double inc = 360.0 / leaves.size();
		for (RT2Vertex leaf : leaves) {
			leaf.setAngle(angle);
			angle += inc;
		}
		root.locate();

		if (jitter > 0) {
			Random rnd = new Pcg32();
			root.jitter(jitter, rnd);
		}

		Point2D min = new Point2D.Double(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
		Point2D max = new Point2D.Double(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);
		root.getLayoutBounds(min, max);
		root.normalise(ILayout.getBoundingFrame(min, max), ILayout.getFittingFrame());

		for (int i = 0; i < isolated.size(); i++) {
			IVertex v = isolated.get(i);
			v.setLocation(1.07, (double) i / (double) isolated.size());
		}

		return this;
	}

}
