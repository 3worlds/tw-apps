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

import au.edu.anu.twapps.mm.visualGraph.VisualGraphFactory;

/**
 * Author Ian Davies - - 11 Jul 2019
 * <p>
 * Keys shared by all nodes created by the {@link VisualGraphFactory}.
 */
public interface VisualKeys {
	/**
	 * y position.
	 */
	public final static String vnx = "x";
	/**
	 * y position.
	 */
	public final static String vny = "y";
	/**
	 * the cateogyr of sub-tree the node belongs to (for purposes of colour
	 * schemes).
	 */
	public final static String vnCategory = "category";
	/**
	 * Tree if node is collapsed (hidden).
	 */
	public final static String vnCollapsed = "collapsed";
	/**
	 * The parent reference for this node (maintained for purpose of graph editing).
	 */
	public final static String vnParentRef = "parentRef";
	/**
	 * Node is visible.
	 */
	public final static String vnVisible = "visible";
	/**
	 * Edge is visible.
	 */
	public final static String veVisible = "visible";
}
