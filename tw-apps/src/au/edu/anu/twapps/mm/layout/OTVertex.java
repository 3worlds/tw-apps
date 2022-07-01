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

import au.edu.anu.twapps.mm.visualGraph.VisualNode;

/**
 * @author Ian Davies - 24 Apr 2020
 */
public class OTVertex extends TreeVertexAdapter {

	private double _prelim;
	private int _number;
	private OTVertex _thread;
	private OTVertex _ancestor;
	private double _mod;
	private double _shift;
	private double _change;
	protected static int maxLevels = 0;
	protected static double[] levels = new double[10];
	private static final double distance = 1.0;

	/**
	 * Construct a vertex wrapper of a {@link VisualNode} for the {@link OTLayout}.
	 * 
	 * @param parent Parent vertex.
	 * @param node   {@link VisualNode} to be wrapped
	 */
	public OTVertex(TreeVertexAdapter parent, VisualNode node) {
		super(parent, node);
	}

	/**
	 * Applied recursively to all children as well as the function 'apportion'.
	 * After spacing out the children by calling ExecuteShifts, the node is placed
	 * at the midpoint of its outermost children.
	 * 
	 * @param num   Ordinal position in a list of siblings.
	 * @param depth tree depth
	 */
	public void firstWalk(int num, int depth) {
		setNumber(num);
		updateDepths(depth);
		if (isChildless()) {
			setPrelim(0.0);
			OTVertex leftSibling = prevSibling();
			if (leftSibling != null)
				setPrelim(leftSibling.getPrelim() + distance);
		} else {
			OTVertex leftMost = getFirstChild();
			OTVertex defaultAncestor = leftMost;
			OTVertex child = leftMost;
			for (int i = 0; child != null; ++i, child = child.nextSibling()) {
				child.firstWalk(i, depth + 1);
				defaultAncestor = child.apportion(defaultAncestor);
			}

			executeShifts();

			double midpoint = 0.5 * (leftMost.getPrelim() + getLastChild().getPrelim());

			OTVertex w = prevSibling();
			if (w != null) {
				setPrelim(w.getPrelim() + distance);
				setMod(getPrelim() - midpoint);
			} else {
				setPrelim(midpoint);
			}

		}
	}

	/**
	 * Update levels structure with the given level.
	 * 
	 * @param nLevels Number of levels required.
	 */
	private static void updateDepths(int nLevels) {
		if (levels.length <= nLevels)
			levels = resize(levels, 3 * nLevels / 2);
		levels[nLevels] = Math.max(levels[nLevels], OTVertex.distance);
		maxLevels = Math.max(maxLevels, nLevels);
	}

	private static final double[] resize(double[] a, int size) {
		if (a.length >= size)
			return a;
		double[] b = new double[size];
		System.arraycopy(a, 0, b, 0, a.length);
		return b;
	}

	private OTVertex getFirstChild() {
		return (OTVertex) getChildren().get(0);
	}

	private OTVertex getLastChild() {
		return (OTVertex) getChildren().get(getChildren().size() - 1);
	}

	private OTVertex prevSibling() {
		OTVertex parent = (OTVertex) getParent();
		if (parent != null) {
			int idx = parent.getChildren().indexOf(this);
			idx--;
			if (idx >= 0)
				return (OTVertex) parent.getChildren().get(idx);
		}
		return null;
	}

	private OTVertex nextSibling() {
		OTVertex parent = (OTVertex) getParent();
		if (parent != null) {
			int idx = parent.getChildren().indexOf(this);
			idx++;
			if (idx < parent.getChildren().size())
				return (OTVertex) parent.getChildren().get(idx);
		}
		return null;
	}

	/**
	 * This is the core of the algorithm. Here, a new subtree is combined with the
	 * previous subtrees. 'Threads' are used to traverse the inside and outside
	 * contours of the left and right subtree up to the highest common level. The
	 * vertices (v) used for the traversals are vip,vim, vop and vom, where 'o'
	 * means 'outside' and 'i' means 'inside', 'm' means left subtree and 'p' means
	 * right subtree. For summing the modifiers along th ecoutour, we use respective
	 * variables sip, sim, som and sop. Whenever two nodes of the inside contours
	 * conflict, we compute the left one of the greatest distinct ancestors using
	 * the function "Ancestor" and call "MoveSubtree to shift the subtree and
	 * prepare the shifts of smaller subtrees. Finally, we add a new 'thread' (if
	 * necessary).
	 */
	private OTVertex apportion(OTVertex defaultAncestor) {
		// i = inner, o = outer, r = right, l = left.
		OTVertex w = prevSibling();
		if (w != null) {
			OTVertex vip, vim, vop, vom;
			double sip, sim, sop, som;
			vip = vop = this;
			vim = w;
			vom = (OTVertex) vip.getParent().getChildren().get(0);

			sip = vip.getMod();
			sop = vop.getMod();
			sim = vim.getMod();
			som = vom.getMod();

			OTVertex nr = vim.nextRight();
			OTVertex nl = vip.nextLeft();
			while (nr != null && nl != null) {
				vim = nr;
				vip = nl;
				vom = vom.nextLeft();
				vop = vop.nextRight();

				vop.setAncestor(this);
				double vimpl = vim.getPrelim();
				double vippl = vip.getPrelim();
				double shift = (vimpl + sim) - (vippl + sip) + distance;

				shift = (vim.getPrelim() + sim) - (vip.getPrelim() + sip) + distance;
				if (shift > 0) {
					moveSubTree(ancestor(vim, defaultAncestor), shift);
					sip += shift;
					sop += shift;
				}
				sim += vim.getMod();
				sip += vip.getMod();
				som += vom.getMod();
				sop += vop.getMod();

				nr = vim.nextRight();
				nl = vip.nextLeft();
			}
			if (nr != null && vop.nextRight() == null) {
				vop.setThread(nr);
				double m = vop.getMod();
				m += sim - sop;
				vop.setMod(m);
			}
			if (nl != null && vom.nextLeft() == null) {
				vom.setThread(nl);
				double m = vom.getMod();
				m += sip - som;
				vom.setMod(m);
				defaultAncestor = this;
			}
		}
		return defaultAncestor;
	}

	/**
	 * This function is used to traverse the left contour of a subtree. It returns
	 * the successor of this vertex on this contour. This successor is either given
	 * by the leftmost child of this vertex or by its thread. The function returns
	 * null if and only if this vertex is on the highest evel of its subtree.
	 */
	private OTVertex nextLeft() {
		if (!getChildren().isEmpty())
			return (OTVertex) getChildren().get(0);
		return getThread();
	}

	/** Works analogously to nextLeft() */
	private OTVertex nextRight() {
		if (!getChildren().isEmpty())
			return (OTVertex) getChildren().get(getChildren().size() - 1);
		else
			return getThread();
	}

	/**
	 * Returns the left one of the greatest distinct ancestors of this vertex and
	 * its neighbour.
	 */
	private OTVertex ancestor(OTVertex insideLeftSubtree, OTVertex defaultAncestor) {
		// inside left subtree
		OTVertex parent = (OTVertex) getParent();
		OTVertex ancst = insideLeftSubtree.getAncestor();
		if (ancst != null)
			if (ancst.getParent() != null) {
				OTVertex ancstParent = (OTVertex) ancst.getParent();
				if (ancstParent.equals(parent))
					return ancst;
			}
		return defaultAncestor;
	}

	/**
	 * Shifts the current subtree rooted at "this". This is done by increasing
	 * prelim() and mod() by shift. All other shifts, applied to the smaller
	 * subtrees between wm and 'this' are performed later by ExecuteSHifts. To
	 * prepare for this, we adjust wm.change(), wm.shift() and this.change().
	 */
	private void moveSubTree(OTVertex wm, double shift) {
		double subTrees = getNumber() - wm.getNumber();

		double wpChange = getChange();
		wpChange -= shift / subTrees;
		setChange(wpChange);

		double wpShift = getShift();
		wpShift += shift;
		setShift(wpShift);

		double wmChange = wm.getChange();
		wmChange += shift / subTrees;
		wm.setChange(wmChange);

		double wpPrelim = getPrelim();
		wpPrelim += shift;
		setPrelim(wpPrelim);

		double wpMod = getMod();
		wpMod += shift;
		setMod(wpMod);
	}

	/**
	 * This function only needs one traversal of the children of this vertex to
	 * execute all shifts computed and memorized in MoveSubtree().
	 */
	private void executeShifts() {
		double shft = 0, chng = 0;
		for (OTVertex c = getLastChild(); c != null; c = c.prevSibling()) {
			double cprelim = c.getPrelim();
			cprelim += shft;
			c.setPrelim(cprelim);

			double dmod = c.getMod();
			dmod += shft;
			c.setMod(dmod);

			chng += c.getChange();
			shft += c.getShift() + chng;
		}
	}

	/**
	 * Computes all real x-coords by summing the modifiers recursively.
	 * 
	 * @param p     The vertex to set.
	 * @param m     The modifier
	 * @param depth current depth.
	 */
	public void secondWalk(OTVertex p, double m, int depth) {
		double y = getPrelim() + m;
		double x = OTVertex.levels[depth];
		setLocation(x, y);
		depth += 1;
		if (!isChildless()) {
			for (OTVertex child = getFirstChild(); child != null; child = child.nextSibling()) {
				child.secondWalk(this, m + getMod(), depth);
			}
		}
	}

	/**
	 * Getter for the preliminary position x.
	 * 
	 * @return current value.
	 */
	public double getPrelim() {
		return _prelim;
	}

	/**
	 * Setter for the preliminary position x.
	 * 
	 * @param prelim x position
	 */
	public void setPrelim(double prelim) {
		this._prelim = prelim;
	}

	/**
	 * Getter for the ordinal position amongst siblings.
	 * 
	 * @return vertex position.
	 */
	public int getNumber() {
		return _number;
	}

	/**
	 * Setter for the ordinal position amongst siblings.
	 * 
	 * @param number ordinal postion
	 */
	public void setNumber(int number) {
		this._number = number;
	}

	/**
	 * Getter for the thread current vertex. 'Threads' are used to traverse the
	 * inside and outside contours of the left and right subtree up to the highest
	 * common level.
	 * 
	 * @return current thread.
	 */
	public OTVertex getThread() {
		return _thread;
	}

	/**
	 * Setter for the thread current vertex. 'Threads' are used to traverse the
	 * inside and outside contours of the left and right subtree up to the highest
	 * common level.
	 * 
	 * @param thread The vertex in the thread chain.
	 */
	public void setThread(OTVertex thread) {
		this._thread = thread;
	}

	/**
	 * Ancestor either points to itself or to the root of the tree it belongs to.
	 * 
	 * @return current ancestor.
	 */
	public OTVertex getAncestor() {
		return _ancestor;
	}

	/**
	 * Ancestor either points to itself or to the root of the tree it belongs to.
	 * 
	 * @param ancestor The ancestor.
	 */
	public void setAncestor(OTVertex ancestor) {
		this._ancestor = ancestor;
	}

	/**
	 * Getter for the stored distance required to center a sub-tree.
	 * 
	 * @return current mod.
	 */
	public double getMod() {
		return _mod;
	}

	/**
	 * Setter for the stored distance required to center a sub-tree.
	 * 
	 * @param mod mod to store.
	 */
	public void setMod(double mod) {
		this._mod = mod;
	}

	/**
	 * Getter for the saved calculated x distance to shift so that this node does
	 * not overlap another. This is another term used to maintain linear time
	 * efficiency.
	 * 
	 * @return current saved shift.
	 */
	public double getShift() {
		return _shift;
	}

	/**
	 * Setter for the saved calculated x distance to shift so that this node does
	 * not overlap another. This is another term used to maintain linear time
	 * efficiency.
	 * 
	 * @param shift amount to shift x.
	 */
	public void setShift(double shift) {
		this._shift = shift;
	}

	/**
	 * Getter for the actual distance x shifted.
	 * 
	 * @return distance x
	 */
	public double getChange() {
		return _change;
	}

	/**
	 * Setter for the actual distance x shifted.
	 * 
	 * @param change distance x shifted
	 */
	public void setChange(double change) {
		this._change = change;
	}

}
