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

import fr.cnrs.iees.uit.space.Distance;

/**
 * @author Ian Davies - 2 May 2020
 *         <p>
 *         A simple vector class for use in the Lombardi layout algorithm.
 */
public class Vector {
	private double x;
	private double y;

	/**
	 * Construct a vettor of 2 values.
	 * 
	 * @param x x component.
	 * @param y y component.
	 */
	public Vector(double x, double y) {
		this.x = x;
		this.y = y;

	}

	/**
	 * Add 2 vectors.
	 * 
	 * @param v vector to add.
	 * @return vector sum.
	 */
	public Vector add(Vector v) {
		return new Vector(x + v.x, y + v.y);
	}

	/**
	 * Subtract 2 vectors.
	 * 
	 * @param v vector to subtract.
	 * @return vector subtraction.
	 */
	public Vector sub(Vector v) {
		return new Vector(x - v.x, y - v.y);
	}

	/**
	 * @param factor WIP
	 * @return WIP
	 */
	public Vector mul(double factor) {
		return new Vector(x * factor, y * factor);
	}

	// Difference??
	/**
	 * @param factor WIP
	 * @return WIP
	 */
	public Vector rmul(double factor) {
		return new Vector(x * factor, y * factor);
	}

	/**
	 * @param factor WIP
	 * @return WIP
	 */
	public Vector truediv(double factor) {
		return new Vector(x / factor, y / factor);
	}

	/**
	 * @return WIP
	 */
	public Vector normalize() {
		return scale(1.0 / Distance.euclidianDistance(0, 0, x, y));
	}

	/**
	 * @param k WIP
	 * @return WIP
	 */
	public Vector scale(double k) {
		return new Vector(x * k, y * k);
	}

	/**
	 * @return WIP
	 */
	public double angle() {
		return (Math.atan2(y, x));
	}

}
