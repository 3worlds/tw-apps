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

import au.edu.anu.rscs.aot.collections.tables.StringTable;
import au.edu.anu.rscs.aot.util.IntegerRange;
import au.edu.anu.twapps.mm.visualGraph.VisualEdge;
import au.edu.anu.twapps.mm.visualGraph.VisualNode;
import fr.cnrs.iees.graph.impl.TreeGraph;
import fr.cnrs.iees.graph.impl.TreeGraphNode;

// just experimenting with what services mm requires of an aotnode.

// impl will have a VisualNode which hosts the configuration node
public interface VisualNodeEditable {
	public boolean hasChildren();

	/* return the class value or null from the hosted config node */
	public String getClassValue();

	/* get the configuration node under-pinning this */
	public TreeGraphNode getConfigNode();

	/*
	 * normally true unless this is the configuration root (3worlds:<projectName>)
	 */
	public boolean canDelete();
	
	public boolean canRename();

	/* true if the this node can have more children of this label */
	public boolean moreChildrenAllowed(IntegerRange range, String childLabel);

	public Iterable<VisualNode> graphRoots();

	public boolean hasOutEdges();
	
	public Iterable<VisualEdge> getOutEdges();

	public boolean isLeaf();

	public boolean isCollapsed();

	public VisualNode newChild(String label, String name);

	public String proposeAnId(String proposedName);
	
	public VisualNode getSelectedVisualNode();

	public Class<? extends TreeGraphNode> getSubClass();

	public boolean hasOutEdgeTo(VisualNode endNode, String edgeLabel);

	public Iterable<VisualNode> getOutNodes();

	public String cClassId();
	
	public VisualEdge newEdge(String id, String label,VisualNode vEnd);

	public void reconnectChild(VisualNode vnChild);
	
	public boolean references (StringTable parents);
	
	public TreeGraph<VisualNode, VisualEdge> getGraph();

	public String extractParentReference(StringTable parents);

	public StringTable getParentTable();

	
	//public void addProperty(String key, Object defaultValue);

}
