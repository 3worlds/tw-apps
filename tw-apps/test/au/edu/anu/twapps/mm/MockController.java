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

package au.edu.anu.twapps.mm;

import java.io.File;
import java.util.Collection;

import au.edu.anu.twapps.mm.layout.LayoutType;
import au.edu.anu.twapps.mm.visualGraph.VisualEdge;
import au.edu.anu.twapps.mm.visualGraph.VisualNode;
import fr.cnrs.iees.graph.impl.TreeGraph;

public class MockController implements IMMController{
	private IMMModel model;
	private TreeGraph<VisualNode,VisualEdge> graph;
	public MockController() {
		model = new MMModel(this);
	}

	@Override
	public void onProjectClosing() {
		System.out.println("onProjectClosing"+graph.toString());
		graph=null;
	}

	@Override
	public void onProjectOpened(TreeGraph<VisualNode, VisualEdge> layoutGraph) {
		this.graph=layoutGraph;
		// Here we would build the graph display and populate the property editors
		System.out.println("onProjectOpened: "+graph.nNodes());	
		for (VisualNode n : graph.nodes()) {
			System.out.println(n.toString());
			//System.out.println(n.getConfigNode().toString());
		}
	}

	
//	public void handleNewProject() {
//		model.doNewProject();
//	}
	
	public void handleOpenProject(File file) {
		model.doOpenProject(file);
	}

	@Override
	public void onNodeSelected(VisualNode n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNewNode(VisualNode n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNodeDeleted() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTreeCollapse() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTreeExpand() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onItemEdit(Object item) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNewEdge(VisualEdge e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onEdgeDeleted() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onElementRenamed() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void setDefaultTitle() {
		System.out.println("TITLE SET TO DEFAULT");		
	}

	@Override
	public void collapsePredef() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public LayoutType getCurrentLayout() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onRollback(TreeGraph<VisualNode, VisualEdge> layoutGraph) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getPreferences() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void putPreferences() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doLayout(double duration) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doFocusedLayout(VisualNode root, LayoutType layout, double duration) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Collection<String> getUnEditablePropertyKeys(String label) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onAddRemoveProperty(VisualNode vn) {
		// TODO Auto-generated method stub
		
	}




}
