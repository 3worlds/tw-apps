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

package au.edu.anu.twapps.structureEditor.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import au.edu.anu.rscs.aot.graph.AotNode;
import au.edu.anu.rscs.aot.util.IntegerRange;
import au.edu.anu.twapps.graphviz.GraphVisualisationConstants;
import au.edu.anu.twapps.structureEditor.ArchetypeConstants;
import au.edu.anu.twapps.structureEditor.QueryableNode;
import au.edu.anu.twapps.structureEditor.Specifications;
import au.edu.anu.twapps.structureEditor.StructureEditable;
import javafx.util.Pair;

public abstract class StructureEditorAdapter
		implements StructureEditable, GraphVisualisationConstants, ArchetypeConstants {
	protected Specifications specifications;
	protected QueryableNode queryNode;
	private AotNode newNode;
	protected AotNode nodeSpec;

	public StructureEditorAdapter(QueryableNode n) {
		super();
		this.newNode = null;
		this.queryNode = n;
		this.nodeSpec = specifications.getSpecificationOf(queryNode.getConfigNode());
	}

	@Override
	public AotNode locateNodeAt(double x, double y, double w, double h) {
		// rescale user's x,y into unit space
		newNode.setProperty(gvX, x / w);
		newNode.setProperty(gvY, y / h);
		return newNode;
	}

	@Override
	public boolean hasNewNode() {
		return newNode != null;
	}

	@Override
	public List<AotNode> allowedChildren(Iterable< AotNode> childSpecs) {
		List<AotNode> result = new ArrayList<AotNode>();
		for (AotNode childNodeSpec : childSpecs) {
			IntegerRange range = specifications.getMultiplicity(childNodeSpec, atName);
			String childLabel = specifications.getLabel(childNodeSpec);
			if (!queryNode.inRange(range, childLabel))
				result.add(childNodeSpec);
		}
		return result;
	}

	@Override
	public List<Pair<String, AotNode>> allowedOutEdges(Iterable<AotNode> edgeSpecs) {
		List<Pair<String, AotNode>> result = new ArrayList<>();
		List<String> edgePropXorOptions = specifications.getConstraintOptions(nodeSpec, atConstraintEdgePropXor);
		List<String> nodeNodeXorOptions = specifications.getConstraintOptions(nodeSpec, atConstraintNodeNodeXor);

		for (AotNode edgeSpec : edgeSpecs) {
			String nodeLabel = specifications.getEdgeToNodeLabel(edgeSpec);
			List<String> edgeLabelOptions = specifications.getConstraintOptions(edgeSpec, atConstraintElementLabel);
			// we now need the node list of the graph!
		}
		return result;
	}
	public List<AotNode> orphanedChildren(Iterable<AotNode> childSpecs) {
		List<AotNode> result = new ArrayList<>();
		
		return result;
	}
	protected boolean haveSpecification() {
		return nodeSpec != null;
	}
}
