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

package au.edu.anu.twapps.mm.layout;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Random;

import au.edu.anu.twapps.mm.layoutGraph.LayoutNode;

/**
 * @author Ian Davies - 30 Apr 2020
 *         <p>
 *         Interface for wrappers of {@link LayoutNode} for the purpose of graph
 *         layout algorithms.
 */
public interface IVertex {

	/**
	 * Get the wrapped {@link LayoutNode}
	 * 
	 * @return the wrapped node.
	 */
	public LayoutNode getNode();

	/**
	 * Set the location of the underlying {@link LayoutNode}
	 * 
	 * @param x x dimension
	 * @param y y dimension.
	 */
	public void setLocation(double x, double y);

	/**
	 * Get the x value.
	 * 
	 * @return the x value.
	 */
	public double getX();

	/**
	 * Get the y value.
	 * 
	 * @return the y value.
	 */
	public double getY();

	/**
	 * Get the node name.
	 * 
	 * @return the node name.
	 */
	public String id();

	/**
	 * Randomly offset the location of the {@link LayoutNode}.
	 * 
	 * @param f   Jitter fraction.
	 * @param rnd Random number generator.
	 */
	public void jitter(double f, Random rnd);

	/**
	 * Fit the node locations within the rectangle.
	 * 
	 * @param from Current frame.
	 * @param to   Frame for re-scaling.
	 */
	public void normalise(Rectangle2D from, Rectangle2D to);

	/**
	 * 
	 * @param min Point to set for lower-left bound.
	 * @param max Point to set for top-right bound.
	 */
	public void getLayoutBounds(Point2D min, Point2D max);
}
