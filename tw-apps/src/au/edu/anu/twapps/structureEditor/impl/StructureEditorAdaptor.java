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

import au.edu.anu.rscs.aot.graph.AotNode;
import au.edu.anu.rscs.aot.util.IntegerRange;
import au.edu.anu.twapps.graphviz.GraphVisualisationConstants;
import au.edu.anu.twapps.structureEditor.ArchetypeConstants;
import au.edu.anu.twapps.structureEditor.QueryableNode;
import au.edu.anu.twapps.structureEditor.Specifier;
import au.edu.anu.twapps.structureEditor.StructureEditable;

public class StructureEditorAdaptor implements StructureEditable,GraphVisualisationConstants,ArchetypeConstants{
	private Specifier archSpecs;
	private QueryableNode qnode;
	private AotNode newNode;
	private AotNode nodeSpec;
	public StructureEditorAdaptor(QueryableNode n) {
		super();
		this.newNode = null;
		this.qnode = n;
		this.nodeSpec = archSpecs.getSpecificationOf(qnode.getConfigNode());
	}

	@Override
	public AotNode placeNodeAt(double x, double y, double w, double h) {
		// scale into unit space
		newNode.setProperty(gvX,x/w);
		newNode.setProperty(gvY,y/h);
		return newNode;
	}

	@Override
	public boolean hasNewNode() {
		return newNode!=null;
	}
	

	@Override
	public Iterable<AotNode> allowedChildren(Iterable<AotNode> childSpecs) {
		List<AotNode> result = new ArrayList<AotNode>();
		for (AotNode childSpec:childSpecs) {		
			IntegerRange range = archSpecs.getMultiplicity(childSpec,atName);
			String childLabel = archSpecs.getLabel(childSpec);
			if (!qnode.inRange(range,childLabel))
				result.add(childSpec);			
		}
		return result;
	}

	@Override
	public Iterable<AotNode> allowedNeighbours(Iterable<AotNode> neighbourSpecs) {
		// TODO Auto-generated method stub
		return null;
	}



}
