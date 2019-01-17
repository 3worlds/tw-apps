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

import au.edu.anu.twapps.mm.visualGraph.VisualGraph;
import au.edu.anu.twapps.mm.visualGraph.VisualNode;

public class MockController implements Controllable{
	private Modelable model;
	public MockController() {
		model = new ModelMaker(this);
	}

	@Override
	public void onProjectClosing(VisualGraph layoutGraph) {
		System.out.println("onProjectClosing"+layoutGraph.toString());		
	}

	@Override
	public void onProjectOpened(VisualGraph layoutGraph) {
		System.out.println("onProjectOpened: "+layoutGraph.size());	
		for (VisualNode n : layoutGraph.nodes()) {
			System.out.println(n.toString());
			System.out.println(n.getConfigNode().toString());
		}
	}

	@Override
	public void onStartWaiting() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onEndWaiting() {
		// TODO Auto-generated method stub
		
	}
	
	public void handleNewProject() {
		model.doNewProject();
	}

}
