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
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import au.edu.anu.omhtk.rng.Pcg32;
import au.edu.anu.twapps.mm.layoutGraph.LayoutNode;
import au.edu.anu.twcore.root.World;

/**
 * @author Ian Davies - 24 Apr 2020
 *         <p>
 *         Creates an ordered tree layout. Lays out a rooted tree horizontally
 *         such that each depth level of the tree is on a shared vertical line.
 *         Each vertex can iterate over its children, so most of the code can be
 *         found in recursive methods in {@link OTVertex}.
 *         </p>
 *         <p>
 *         "Buchheim, C., Jünger, M. and Leipert, S., 2002, August. Improving
 *         Walker’s algorithm to run in linear time. In International Symposium
 *         on Graph Drawing (pp. 344-353). Springer, Berlin, Heidelberg.
 *         </p>
 *         <p>
 *         pseudo code (broken link?):
 *         http://dirk.jivas.de/papers/buchheim02improving.pdf
 *         </p>
 *         Full discussion in: https://llimllib.github.io/pymag-trees/
 */
public class OTLayout implements ILayout {
	private class Factory implements ITreeVertexFactory {

		@Override
		public TreeVertexAdapter makeVertex(TreeVertexAdapter parent, LayoutNode node) {
			return new OTVertex(parent, node);
		}
	}

	private OTVertex root;
	private List<TreeVertexAdapter> isolated;

	/**
	 * Create an {@link LayoutType#OrderedTree OrderedTree} layout.
	 * 
	 * @param rootNode                {@link LayoutNode} to use as the layout root
	 *                                (need not be {@link World} node).
	 * @param includeParentChildEdges Use parent-child relationships.
	 * @param includeCrossLinksEdges  Show cross-link edges
	 * @param sideline                Move isolated vertices to one side.
	 */
	public OTLayout(LayoutNode rootNode, boolean includeParentChildEdges, boolean includeCrossLinksEdges,
			boolean sideline) {
		this.root = new OTVertex(null, rootNode);
		TreeVertexAdapter.buildSpanningTree(root, new Factory());

		isolated = new ArrayList<>();
		if (sideline)
			root.getIsolated(isolated, includeParentChildEdges, includeCrossLinksEdges);

	}

	@Override
	public ILayout compute(double jitter) {
		OTVertex.maxLevels = 0;
		Arrays.fill(OTVertex.levels, 0);

		root.firstWalk(0, 1);

		determineDepths();

		root.secondWalk(null, -root.getPrelim(), 0);

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

	/**
	 * Set the tree depth level for each vertex.
	 */
	private static void determineDepths() {
		for (int i = 1; i < OTVertex.maxLevels; ++i)
			OTVertex.levels[i] += OTVertex.levels[i - 1];
	}

}
