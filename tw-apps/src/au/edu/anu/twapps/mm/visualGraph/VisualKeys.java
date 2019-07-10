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

import au.edu.anu.rscs.aot.graph.property.PropertyKeys;

public class VisualKeys {
	public final static String vnx = "x";
	public final static String vny = "y";
	public final static String vnText = "text";
	public final static String vnSymbol = "symbol";
	public final static String vnCategory = "category";
	public final static String vnParentLine = "parentLine";
	public final static String vnCollapsed = "collapsed";
	private static PropertyKeys nodeKeys;

	public final static String veText = "text";
	public final static String veSymbol = "symbol";

	private static PropertyKeys edgeKeys;

	static {
		nodeKeys = new PropertyKeys(vnx, vny, vnText, vnSymbol, vnCategory, vnParentLine, vnCollapsed);
		edgeKeys = new PropertyKeys(veText, veSymbol);
	}

	public static PropertyKeys getNodeKeys() {
		return nodeKeys;
	}

	public static PropertyKeys getEdgeKeys() {
		return edgeKeys;
	}

	private VisualKeys() {
	};
}
