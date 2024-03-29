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

/**
 * See if we need an adjacency matrix 'adjMat' since every node has a neighbour
 * list. Not yet implemented.
 */
public class LmbEdge {
	// private double dist;// set on init - ideal spring length = k
	// private double factor;// set on init
	private double cx;// ctrl pt
	private double cy;// ctrl pt
//	private double pcx;// ctrl pt
//	private double pcy;// ctrl pt
	private double pa;// p angle, set on init
	private double qa;// q angle, set on init
//	private double pf;// p rot force
//	private double qf;// q rot force
	private LmbVertex p;
	private LmbVertex q;

	// if p not in nodeDict: Node(p)
//	if q not in nodeDict: Node(q)
//	if p not in adjMat: adjMat[p] = {}
//	if q not in adjMat: adjMat[q] = {}
//	adjMat[p][q] = self
//	adjMat[q][p] = self
//	self.p = nodeDict[p]
//	self.q = nodeDict[q]

	/**
	 * Not yet implemented.
	 * 
	 * @param p Not yet implemented.
	 * @param q Not yet implemented.
	 */
	public LmbEdge(LmbVertex p, LmbVertex q) {
		this.p = p;
		this.q = q;
	}

	/**
	 *  Not yet implemented.
	 */
	public void init() {
		// dist = K??
		pa = p.nextTanAngle();
		qa = q.nextTanAngle();
	}

	/**
	 * @return Not yet implemented.
	 */
	public LmbVertex getP() {
		return p;
	}

	/**
	 * @return Not yet implemented.
	 */
	public LmbVertex getQ() {
		return q;
	}

	/**
	 *  Not yet implemented.
	 * @param k Not yet implemented.
	 * @return Not yet implemented.
	 */
	public double setAttractionDisplacement(double k) {
		return FRVertex.attrApply(p, q, k);
	}

	// n is from
	/**
	 *  Not yet implemented.
	 * @param n Not yet implemented.
	 * @return Not yet implemented.
	 */
	public double tanAngle(LmbVertex n) {
		return pie2(n.getAngle() + tanAngleRel(n));
	}

	// n is from
	/** Not yet implemented.
	 * @param n Not yet implemented.
	 * @return Not yet implemented.
	 */
	public double tanAngleRel(LmbVertex n) {
		if (n == p)
			return pa;
		else
			return qa;
	}

	/**
	 *  Not yet implemented.
	 * @param n Not yet implemented.
	 * @return Not yet implemented.
	 */
	public double edgeAngle(LmbVertex n) {
		LmbVertex n1;
		LmbVertex n2;
		if (p == n) {
			n1 = p;
			n2 = q;
		} else {
			n1 = q;
			n2 = p;
		}
		double y = -(n2.getY() - n1.getY());
		double x = (n2.getX() - n1.getX());
		return pie2(Math.atan2(y, x));
	}

	// #n is from
	/**
	 *  Not yet implemented.
	 * @param n Not yet implemented.
	 * @return Not yet implemented.
	 */
	public double diffAngle(LmbVertex n) {
		return pie(pie(tanAngle(n)) - pie(edgeAngle(n)));
	}

	/**
	 *  Not yet implemented.
	 * @param n Not yet implemented.
	 * @param angle Not yet implemented.
	 */
	public void setAngle(LmbVertex n, double angle) {
		if (n == p) {
			pa = angle;
		} else {
			qa = angle;
		}
	}

	// #n is from
	/**
	 * Not yet implemented.
	 * 
	 * @param n Not yet implemented.
	 * @param f Not yet implemented.
	 */
	public void addForce(LmbVertex n, double f) {
//		if (n == p)
//			pf += f;
//		else
//			qf += f;
	}

	/**
	 * @return Not yet implemented.
	 */
	public Vector ctrlPt() {
		return new Vector(cx, cy);
	}
	// ------------------------------------

	/**
	 *  Not yet implemented.
	 * @param rad Not yet implemented.
	 * @return Not yet implemented.
	 */
	public static double pie(double rad) {
		rad = pie2(rad);
		if (rad <= Math.PI)
			return rad;
		else
			return rad - 2 * Math.PI;
	}

	/**
	 *  Not yet implemented.
	 * @param rad Not yet implemented.
	 * @return Not yet implemented.
	 */
	public static double pie2(double rad) {
		return rad % (2 * Math.PI);
	}

	/**
	 *  Not yet implemented.
	 * @param x Not yet implemented.
	 * @param y Not yet implemented.
	 * @return Not yet implemented.
	 */
	public static double mag(double x, double y) {
		return Math.sqrt(x * x + y * y);
	}

	/**
	 * @return Not yet implemented.
	 */
	public boolean finalStep() {
		boolean result = true;
		double pd = diffAngle(p);
		double qd = diffAngle(q);
		double optidiff = (pd - qd) / 2.0;
		double adjust = Math.abs(pd + qd) / 2.0;
		if (adjust > 0.01)
			result = false;
		if (optidiff > pd) {// increase
			if (p.degree() == 1)
				pa = LmbEdge.pie2(pa + adjust * 2.0);
			else if (q.degree() == 1)
				qa = LmbEdge.pie2(qa + adjust * 2.0);
			else {
				pa = LmbEdge.pie2(pa + adjust);
				qa = LmbEdge.pie2(qa + adjust);
			}
		} else {// decrease
			if (p.degree() == 1)
				pa = LmbEdge.pie2(pa - adjust * 2.0);
			else if (q.degree() == 1)
				qa = LmbEdge.pie2(qa - adjust * 2.0);
			else {
				pa = LmbEdge.pie2(pa - adjust * 2.0);
				qa = LmbEdge.pie2(qa = adjust * 2.0);
			}
		}
		return result;
	}

}
