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

import au.edu.anu.rscs.aot.graph.property.Property;
import fr.cnrs.iees.graph.Edge;
import fr.cnrs.iees.graph.EdgeFactory;
import fr.cnrs.iees.graph.Graph;
import fr.cnrs.iees.graph.Node;
import fr.cnrs.iees.graph.NodeFactory;
import fr.cnrs.iees.properties.PropertyListFactory;
import fr.cnrs.iees.properties.ReadOnlyPropertyList;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.tree.Tree;

public class VisualGraph2 implements Tree<VisualNode2>,Graph<VisualNode2, VisualEdge2>,NodeFactory,EdgeFactory,PropertyListFactory{

	@Override
	public Iterable<VisualNode2> leaves() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<VisualNode2> nodes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean contains(VisualNode2 arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Iterable<VisualEdge2> edges() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<VisualNode2> roots() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<VisualNode2> findNodesByReference(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int maxDepth() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int minDepth() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public VisualNode2 root() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Tree<VisualNode2> subTree(VisualNode2 arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node makeNode(String arg0, String arg1, ReadOnlyPropertyList arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Edge makeEdge(Node arg0, Node arg1, String arg2, String arg3, ReadOnlyPropertyList arg4) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SimplePropertyList makePropertyList(Property... arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SimplePropertyList makePropertyList(String... arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ReadOnlyPropertyList makeReadOnlyPropertyList(Property... arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ReadOnlyPropertyList makeReadOnlyPropertyList(String... arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
