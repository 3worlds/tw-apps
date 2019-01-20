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

import au.edu.anu.rscs.aot.graph.Configurable;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.Edge;
import fr.cnrs.iees.graph.NodeFactory;
import fr.cnrs.iees.properties.ExtendablePropertyList;
import fr.cnrs.iees.tree.TreeNode;

public class AotNode2 extends TreeGraphNodeExtendableProperties 
implements Configurable{

	// TODO CAUTION: Probably won't work with hashset as equals must be defined.
	protected AotNode2(String label, String name, TreeNode treenode, ExtendablePropertyList properties, NodeFactory factory) {
		super(label, name, treenode,properties, factory);
	}

	@Override
	public Configurable initialise() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toDetailedString() {
		StringBuilder sb = new StringBuilder(toUniqueString());
		sb.append("=[");
		if (getParent()!=null)
			sb.append("↑").append(getParent().toUniqueString());
		else
			sb.append("ROOT");
		if (hasChildren()) {
			for (TreeNode n:getChildren()) {
				sb.append(" ↓").append(n.toUniqueString());
			}
		}
		if (getEdges(Direction.IN).iterator().hasNext()) {
			for (Edge e:getEdges(Direction.IN))
				sb.append(" ←").append(e.startNode().toUniqueString());
		}
		if (getEdges(Direction.OUT).iterator().hasNext()) {
			for (Edge e:getEdges(Direction.OUT))
				sb.append(" →").append(e.endNode().toUniqueString());
		}		
		if (size()>0)
			sb.append(' ').append(getProperties().toString());
		sb.append("]");
		return sb.toString();
	}
	@Override
	public boolean equals(Object obj) {
		if (obj==null)
			return false;
		if (!AotNode2.class.isAssignableFrom(obj.getClass()))
			return false;
		AotNode2 n = (AotNode2) obj;
		return (getLabel().equals(n.getLabel()) && getName().equals(n.getName()));
	}
	// Tricky: without this, the above method wont be called and two identically labelled+named
	// nodes are not recognized as such
	
	// TODO to be checked
	@Override
	public int hashCode() {
		return toUniqueString().hashCode();
	}

}
