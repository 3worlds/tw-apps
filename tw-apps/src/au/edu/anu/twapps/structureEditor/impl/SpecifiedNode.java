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
import au.edu.anu.twapps.structureEditor.SpecifiableNode;
import fr.cnrs.iees.twcore.constants.Configuration;

public class SpecifiedNode implements SpecifiableNode, Configuration {
	private AotNode visualNode;

	public SpecifiedNode(AotNode visualNode) {
		this.visualNode = visualNode;
	}

	@Override
	public List<AotNode> getChildren() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getClassValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AotNode getConfigNode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean canDelete() {
		return getLabel().equals(N_ROOT);
	}

	@Override
	public boolean inRange(IntegerRange range, String childLabel) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getLabel() {
		return visualNode.getLabel();
	}

	@Override
	public List<AotNode> graphRoots() {
		List<AotNode> result = new ArrayList<>();
		for (AotNode root : visualNode.treeNodeFactory().roots())
			result.add(root);
		return result;
	}


}
