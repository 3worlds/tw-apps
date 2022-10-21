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

import java.util.*;

import au.edu.anu.twapps.mm.layoutGraph.LayoutNode;
import fr.cnrs.iees.uit.space.Distance;
import fr.cnrs.iees.omhtk.utils.Permute;
import fr.cnrs.iees.omhtk.utils.Shuffle;

/**
 *  Not yet implemented.
 */
public class LmbVertex extends FRVertex {
	// private List<LmbVertex> _neighbours;
	private double rf;// rotation force?
	private double tx; // tangential
	private double ty;// tangential
	// private int degree;// assigned on initlen([x for x in n.nodes()])
	private int curTan = -1;
	private double angle;
	private double aIncrement;// assigned on init n.aIncrement = (2*m.pi)/n.degree
	private Map<LmbVertex, LmbEdge> _adjMap;
	// For safety, we need neighbours in a predicatable order
	private List<LmbVertex> neighbours;

	/**
	 * @param node WIP
	 */
	public LmbVertex(LayoutNode node) {
		super(node);
	}

	/**
	 *  WIP
	 */
	public void init() {
		neighbours = new ArrayList<>();
		if (_adjMap != null) {
			for (LmbVertex nn : _adjMap.keySet())
				neighbours.add(nn);
			neighbours.sort((v1,v2)->v1.id().compareTo(v2.id()));
//			neighbours.sort(new Comparator<LmbVertex>() {
//
//				@Override
//				public int compare(LmbVertex v1, LmbVertex v2) {
//					return v1.id().compareTo(v2.id());
//				}
//
//			});
			aIncrement = (2 * Math.PI) / (double) degree();
		}

	}

	/**
	 * @param adjMap WIP
	 */
	public void addNeighbour(Map<LmbVertex, LmbEdge> adjMap) {
		_adjMap = adjMap;
		setHasEdge(true);
	}

	// ----------------------------

	/**
	 * @return WIP
	 */
	public double nextTanAngle() {
		curTan++;
		return aIncrement * curTan;
	}

	// n is to
	/**
	 * @param n WIP
	 * @return WIP
	 */
	public double tanAngle(LmbVertex n) {
		return getEdge(n).tanAngle(this);
	}

	// n is to
	/**
	 * @param n WIP
	 * @return WIP
	 */
	public double tanAngleRel(LmbVertex n) {
		return getEdge(n).tanAngleRel(this);
	}

	// #n is to
	/**
	 * @param n WIP
	 * @return WIP
	 */
	public double edgeAngle(LmbVertex n) {
		return getEdge(n).edgeAngle(this);
	}

	/**
	 * @param n WIP
	 * @return WIP
	 */
	public double diffAngle(LmbVertex n) {
		return getEdge(n).diffAngle(this);
	}

	/**
	 * @param nn WIP
	 * @param angle WIP
	 */
	public void setAngle(LmbVertex nn, double angle) {

		getEdge(nn).setAngle(this, angle);
	}

	/**
	 * @param n WIP
	 * @param f WIP
	 */
	public void addForce(LmbVertex n, double f) {
		getEdge(n).addForce(this, f);
	}

	/**
	 * @param n WIP
	 * @return WIP
	 */
	private LmbEdge getEdge(LmbVertex n) {
		return _adjMap.get(n);
	}

	/**
	 * @return WIP
	 */
	public double getAngle() {
		return angle;
	}

	/**
	 * @return WIP
	 */
	public int degree() {
		return neighbours.size();
	}

	/**
	 * @param rfKopp WIP
	 * @param maxDeterministicShuffle WIP
	 * @param shuffleSamples WIP
	 */
	public void shuffleTans(double rfKopp, int maxDeterministicShuffle, int shuffleSamples) {
		double bestVal = rfComputeTot(rfKopp);
		double[] bestCombo = new double[degree()];
		for (int i = 0; i < bestCombo.length; i++) {
			LmbVertex nn = neighbours.get(i);
			bestCombo[i] = tanAngleRel(nn);
		}
		// double[] orig = bestCombo.clone();

		if (degree() <= maxDeterministicShuffle) {
			// full factorial shuffle
			double[] angles = bestCombo.clone();
			int[][] perms = Permute.getPemutationIndices(degree());
			for (int p = 0; p < perms.length; p++) {
				int[] perm = perms[p];
				for (int i = 0; i < degree(); i++) {
					setAngle(neighbours.get(i), angles[perm[i]]);
				}
				double val = rfComputeTot(rfKopp);
				if (val < bestVal) {
					bestVal = val;
					for (int c = 0; c < degree(); c++)
						bestCombo[c] = angles[perm[c]];
				}
			}
		} else {// #random shuffle
			for (int k = 0; k < degree(); k++)
				for (int j = 0; j < degree(); j++)
					if (k != j) {
						double[] angles = bestCombo.clone();
						angles[k] = bestCombo[j];
						angles[j] = bestCombo[k];
						for (int i = 0; i < degree(); i++)
							setAngle(neighbours.get(i), angles[i]);
						double val = rfComputeTot(rfKopp);
						if (val < bestVal) {
							bestVal = val;
							bestCombo = angles.clone();
						}
					}
			double[] angles = bestCombo.clone();
			for (int z = 0; z < shuffleSamples; z++) {
				Integer[] indices = Shuffle.shuffleIndices(angles.length);
				for (int i = 0; i < degree(); i++)
					setAngle(neighbours.get(i), angles[indices[i]]);
				double val = rfComputeTot(rfKopp);
				if (val < bestVal) {
					bestVal = val;
					for (int c = 0; c < degree(); c++)
						bestCombo[c] = angles[indices[c]];
				}
			}
		}
		for (int i = 0; i < degree(); i++) {
			setAngle(neighbours.get(i), bestCombo[i]);
		}
//		dump(orig, bestCombo);

	}

//	private void dump(double[] orig, double[] best) {
//		System.out.println(id());
//		for (int i = 0; i < degree(); i++) {
//			LmbVertex nn = neighbours.get(i);
//			System.out.println("\t->\t" + nn.id() + "\t" + Math.toDegrees(orig[i]) + "\t->\t" + Math.toDegrees(best[i]));
//		}
//		System.out.println("--------------------------");
//
//	}

	/**
	 * @param rfKopp WIP
	 * @return WIP
	 */
	private double rfComputeTot(double rfKopp) {
		double temprf = 0.0;
		for (LmbVertex nn : neighbours) {
			double opti = LmbEdge.pie(edgeAngle(nn) - nn.diffAngle(this));
			double rot = LmbEdge.pie(opti - tanAngle(nn));
			temprf += Math.abs(rot) * rfKopp;
			rot = diffAngle(nn);
			temprf += Math.abs(rot) * rfKopp;
		}
		return temprf;
		/*-		#squared????
		#rf+=rot*rot#squared????
		#sqroot??? cuz sq doesnt work for <1 as expected
		return rf#squared????
		*/
	}

	/**
	 * @param rfKopp WIP
	 * @param rfKadj WIP
	 * @return WIP
	 */
	public double rotationalDisplacement(double rfKopp, double rfKadj) {
		rf = rfComputeNet(rfKopp, rfKadj);
		return rf;
	}

	/*-	rf = 0.0
	for nn in n.nodes():
		#opp tan
		opti = pie(n.edgeAngle(nn)-nn.diffAngle(n))
		rot = pie(opti-n.tanAngle(nn))
		rf+=rot*rfKopp
		#my tan
		rf-=n.diffAngle(nn)*rfKadj
	return rf
	*/
	/**
	 * @param rfKopp WIP
	 * @param rfKadj WIP
	 * @return WIP
	 */
	private double rfComputeNet(double rfKopp, double rfKadj) {
		double temprf = 0;
		for (LmbVertex nn : neighbours) {
			double opti = LmbEdge.pie(edgeAngle(nn) - nn.diffAngle(this));
			double rot = LmbEdge.pie(opti - tanAngle(nn));
			temprf += rot * rfKopp;
			temprf -= diffAngle(nn) * rfKadj;
		}
		return temprf;
	}

	/*-	for nn in n.nodes():#move n according to nn
			avg = (n.diffAngle(nn)-nn.diffAngle(n))/2.0
			aopti = pie2(nn.tanAngle(n)+avg)#absolute optimal edge angle
			rot = pie(aopti-nn.edgeAngle(n))
			len = dist(n, nn);
			optip = (nn.x+m.cos(aopti)*len, nn.y+m.sin(aopti)*len)
			#make force proportional to rotation proposed
			n.tx += (optip[0] - n.x)*tangentialK#*(abs(rot)/m.pi)
			n.ty += (optip[1] - n.y)*tangentialK#*(abs(rot)/m.pi)
	*/
	/**
	 * @param tangentialK WIP
	 * @return WIP
	 */
	public double trangentialDisplacement(double tangentialK) {
		for (LmbVertex nn : neighbours) {
			double avg = diffAngle(nn) - nn.diffAngle(this) / 2.0;
			// absolute optimal edge angle
			double aopti = LmbEdge.pie2(nn.tanAngle(this) + avg);
//			double rot = LmbEdge.pie(aopti - nn.edgeAngle(this));
			double len = dist(this, nn);
			double optipx = nn.getX() + Math.cos(aopti) * len;
			double optipy = nn.getY() + Math.sin(aopti) * len;
			// make force proportional to rotation proposed
			tx += (optipx - getX()) * tangentialK;
			ty += (optipy - getY()) * tangentialK;
		}
		return Math.sqrt((tx * tx) + (ty * ty));
	}

	/**
	 * @param n1 WIP
	 * @param n2 WIP
	 * @return WIP
	 */
	private static double dist(LmbVertex n1, LmbVertex n2) {
		return Distance.euclidianDistance(n1.getX(), n1.getY(), n2.getX(), n2.getY());
	}

	/**
	 * @param temp WIP
	 */
	public void updateAngle(double temp) {
		angle += rf * temp;
		rf = 0;
	}

	/**
	 * Update the node position with the displacement limited by temperature
	 * 
	 * @param temperature temperature
	 * @return energy
	 */
	/*-def updatePos(temp):
		temp2 = temp*temp
		for i in range(2): tr[i] = 0
		for i in range(2): bl[i] = 0
		for n in nodes():
			n.fx+=n.tx#some other factor mb?
			n.fy+=n.ty#some other factor mb?
			len2 = n.fx*n.fx + n.fy*n.fy
			
			if len2 < temp2:
				n.x += n.fx
				n.y += n.fy
			else: #limit by temp
				fact = temp/m.sqrt(len2)
				n.x += n.fx * fact
				n.y += n.fy * fact
			
			#bounding box
			if n.x < bl[0]: bl[0] = n.x
			elif n.x > tr[0]: tr[0] = n.x
			if n.y < bl[1]: bl[1] = n.y
			elif n.y > tr[1]: tr[1] = n.y
	*/
	/**
	 * WIP
	 */
	public double displace(double temperature) {
		fx += tx;
		fy += ty;
		double force = Math.sqrt(fx * fx + fy * fy);
		if (force < temperature) {
			setLocation(getX() + fx, getY() + fy);
		} else {
			double fact = temperature / force;
			double dx = fx * fact;
			double dy = fy * fact;
			setLocation(getX() + dx, getY() + dy);
		}
		fx = 0;
		fy = 0;
		tx = 0;
		ty = 0;
		return force;
	}

}
