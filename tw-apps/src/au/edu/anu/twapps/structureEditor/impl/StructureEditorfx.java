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

import java.util.List;
import java.util.Map;

import au.edu.anu.rscs.aot.graph.AotNode;
import au.edu.anu.twapps.structureEditor.SpecifiableNode;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.MouseEvent;
import javafx.util.Pair;

/**
 * Author Ian Davies
 *
 * Date 13 Jan. 2019
 */
// TODO move to tw-uifx
public class StructureEditorfx extends StructureEditorAdapter {

	private ContextMenu cm;

	public StructureEditorfx(SpecifiableNode n, MouseEvent event) {
		super(n);
		cm = new ContextMenu();
		buildgui();
		cm.show((Node) event.getSource(), event.getScreenX(), event.getScreenY());
	}

	@Override
	public void buildgui() {
		if (haveSpecification()) {
			Iterable<AotNode> childSpecs = specifications.getChildSpecificationsOf(targetNode.getLabel(), nodeSpec,
					targetNode.getClassValue());
			List<AotNode> allowedChildSpecs = newChildList(childSpecs);
			List<AotNode> orphanedChildren = orphanedChildList(childSpecs);
			Iterable<AotNode> edgeSpecs = specifications.getEdgeSpecificationsOf(targetNode.getLabel(), nodeSpec,
					targetNode.getClassValue());
			List<Pair<String, AotNode>> allowedEdges = newEdgeList(edgeSpecs);

			if (!allowedChildSpecs.isEmpty()) {
				// add new children options
			}

			if (!orphanedChildren.isEmpty()) {
				// list new toNode edge options
			}

			if (!allowedEdges.isEmpty()) {
				// addEdgeOptions
			}

			cm.getItems().add(new SeparatorMenuItem());

			if (targetNode.getChildren().isEmpty()) {
				// add exportTreeOptions
			}
			if (!allowedChildSpecs.isEmpty()) {
				// add import tree options
			}

			if (targetNode.canDelete() || !targetNode.getChildren().isEmpty())
				if (!(allowedChildSpecs.isEmpty() && orphanedChildren.isEmpty()))
					cm.getItems().add(new SeparatorMenuItem());

		}

	}

}
