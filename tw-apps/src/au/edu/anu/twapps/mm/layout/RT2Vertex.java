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

import java.util.List;

import au.edu.anu.twapps.mm.visualGraph.VisualNode;

/**
 * @author Ian Davies 26 Apr 2020
 */
public class RT2Vertex extends TreeVertexAdapter {

	private int _depth;
	private double _angle;

	/**
	 * Construct a vertex wrapper of a {@link VisualNode} for the {@link RT2Layout}.
	 * 
	 * @param parent Parent vertex.
	 * @param node   {@link VisualNode} to be wrapped
	 */
	public RT2Vertex(TreeVertexAdapter parent, VisualNode node) {
		super(parent, node);
		this._depth = 0;
		if (hasParent())
			_depth = ((RT2Vertex) getParent()).getDepth() + 1;
	}

	/**
	 * Get the depth of this vertex in the tree.
	 * 
	 * @return the depth.
	 */
	public int getDepth() {
		return _depth;
	}

	/**
	 * Set the angle of this vertex relative to its parent.
	 * 
	 * @param angle angle in radians.
	 */
	public void setAngle(double angle) {
		_angle = angle;
	}

	/**
	 * Get the angle of this vertex relative to its parent.
	 * 
	 * @return angle in radians.
	 */
	public double getAngle() {
		if (!hasParent())
			return 0.0;
		if (isChildless())
			return _angle;
		else {
			RT2Vertex left = getLefthand();
			RT2Vertex right = getRightHand();
			double la = left.getAngle();
			double ra = right.getAngle();
			return la + (ra - la) / 2.0;
		}
	}

	/**
	 * Recursively collect all leaf vertices.
	 * 
	 * @param leaves current leaf collection.
	 */
	public void collectLeaves(List<RT2Vertex> leaves) {
		if (isChildless())
			leaves.add(this);
		for (TreeVertexAdapter c : getChildren())
			((RT2Vertex) c).collectLeaves(leaves);
	}

	private void updatePosition(double angle) {
		double x = _depth * Math.cos(Math.toRadians(angle));
		double y = _depth * Math.sin(Math.toRadians(angle));
		setLocation(x, y);
	}

	/**
	 * Recursively update position to Cartesian coordinates.
	 */
	public void locate() {
		updatePosition(getAngle());
		for (TreeVertexAdapter c : getChildren())
			((RT2Vertex) c).locate();
	}

	private RT2Vertex getLefthand() {
		if (isChildless())
			return this;
		return ((RT2Vertex) getChildren().get(0)).getLefthand();
	}

	private RT2Vertex getRightHand() {
		if (isChildless())
			return this;
		return ((RT2Vertex) getChildren().get(getChildren().size() - 1)).getRightHand();
	}

}
