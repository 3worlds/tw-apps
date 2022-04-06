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
 * @author Ian Davies 2 May 2020
 */
public class Vector {
	private double x;
	private double y;

	public Vector(double x, double y) {
		this.x = x;
		this.y = y;

	}

	public Vector add(Vector v) {
		return new Vector(x + v.x, y + v.y);
	}

	public Vector sub(Vector v) {
		return new Vector(x - v.x, y - v.y);
	}

	public Vector mul(double factor) {
		return new Vector(x * factor, y * factor);
	}

	// Difference??
	public Vector rmul(double factor) {
		return new Vector(x * factor, y * factor);
	}

	public Vector truediv(double factor) {
		return new Vector(x / factor, y / factor);
	}

	public Vector normalize() {
		return scale(1.0 / Distance.euclidianDistance(0, 0, x, y));
	}

	public Vector scale(double k) {
		return new Vector(x * k, y * k);
	}

	public double angle() {
		return (Math.atan2(y, x));
	}

}
