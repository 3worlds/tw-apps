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

package au.edu.anu.twapps.mm.configGraph;

import au.edu.anu.rscs.aot.archetype.CheckMessage;
import au.edu.anu.twcore.archetype.TWA;
import fr.cnrs.iees.graph.impl.ALEdge;
import fr.cnrs.iees.graph.impl.TreeGraph;
import fr.cnrs.iees.graph.impl.TreeGraphNode;

/**
 * @author Ian Davies
 *
 * @Date 13 Aug 2019
 */
public class ConfigGraph {
	private static TreeGraph<TreeGraphNode, ALEdge> graph;
	private static Iterable<CheckMessage> errors;

	private ConfigGraph() {
	}

	public static void setGraph(TreeGraph<TreeGraphNode, ALEdge> graph) {
		ConfigGraph.graph = graph;
		validateGraph();
	}

	public static TreeGraph<TreeGraphNode, ALEdge> getGraph() {
		return graph;
	}

	public static void validateGraph() {
		errors = TWA.checkSpecifications(graph);
		for (CheckMessage e:errors) {
			System.out.println(e.getCode()+"\tP1:"+e.getTarget()+"\t"+e.getArchetypeNode()+"\t"+e.getException());
		}
	}
	
	public Iterable<CheckMessage> getErrors(){
		return errors;
	}

}
