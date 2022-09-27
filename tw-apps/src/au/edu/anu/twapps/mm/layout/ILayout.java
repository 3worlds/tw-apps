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

/**
 * @author Ian Davies - 10 Jan. 2019
 */
public interface ILayout {
	/**
	 * Re-scaling function.(possible duplicate?)
	 * 
	 * @param value   Value within the range.
	 * @param fromMin Minimum of value.
	 * @param fromMax Maximum of value
	 * @param toMin   Minimum of new range.
	 * @param toMax   Maximum of new range.
	 * @return scaled value.
	 */
	public static double rescale(double value, double fromMin, double fromMax, double toMin, double toMax) {
		double fromRange = fromMax - fromMin;
		double toRange = toMax - toMin;
		if (fromRange == 0.0)
			return toRange / 2.0 + toMin;
		double p = (value - fromMin) / fromRange;
		return p * toRange + toMin;
	}

	/**
	 * Move the value (x or y) by some random amount.
	 * 
	 * @param value          Value to be modified.
	 * @param jitterFraction fraction of space.
	 * @param rnd            Random number generator.
	 * @return updated value.
	 */
	public static double jitter(double value, double jitterFraction, Random rnd) {
		double delta = rnd.nextDouble() * jitterFraction;
		if (rnd.nextBoolean())
			return value + delta;
		else
			return value - delta;
	}

	/**
	 * get the unit dimensions of the drawing frame.
	 * 
	 * @return The drawing frame.
	 */
	public static Rectangle2D getFittingFrame() {
		return new Rectangle2D.Double(0.05, 0.05, 0.9, 0.9);
	}

	/**
	 * Get the bounding dimensions of the layout drawing frame. The
	 * {@link #getFittingFrame() fittingFrame} is scaled into this frame.
	 * 
	 * @param min Lower-left corner
	 * @param max Top-right corner
	 * @return Bounding rectangle
	 */
	public static Rectangle2D getBoundingFrame(Point2D min, Point2D max) {
		return new Rectangle2D.Double(min.getX(), min.getY(), max.getX() - min.getX(), max.getY() - min.getY());
	}

	/**
	 * Compute the layout.
	 * 
	 * @param jitterFrac Random offset of final node positions (relative to drawing
	 *                   area dimensions)
	 * @return this layout class.
	 */
	public ILayout compute(double jitterFrac);

}
