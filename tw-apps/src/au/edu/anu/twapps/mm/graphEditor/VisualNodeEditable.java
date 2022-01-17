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
	
	public VisualNode visualNode();
	
	public TreeGraph<VisualNode, VisualEdge> visualGraph();
	
	/* true if the this node can have more children of this label */
	public boolean moreChildrenAllowed(IntegerRange range, String childLabel);

	public boolean hasOutEdges();
	
	public Iterable<VisualEdge> getOutEdges();

	public String proposeAnId(String proposedName);
	
	public Class<? extends TreeGraphNode> getSubClass();

	public boolean hasOutEdgeTo(VisualNode endNode, String edgeLabel);

	public Iterable<VisualNode> getOutNodes();
	
	public boolean references (StringTable parents);

	public String extractParentReference(StringTable parents);

	
	//public void addProperty(String key, Object defaultValue);

}
