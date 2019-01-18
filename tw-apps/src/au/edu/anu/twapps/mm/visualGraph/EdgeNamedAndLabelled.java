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

import java.util.Objects;

import au.edu.anu.twapps.exceptions.TwAppsException;
import fr.cnrs.iees.graph.EdgeFactory;
import fr.cnrs.iees.graph.Node;
import fr.cnrs.iees.graph.impl.SimpleEdgeImpl;
import fr.ens.biologie.generic.Labelled;
import fr.ens.biologie.generic.Named;
import fr.ens.biologie.generic.NamedAndLabelled;

public class EdgeNamedAndLabelled extends SimpleEdgeImpl implements NamedAndLabelled {
	private LabelNameReference ln;


	// can't be renamed so must have this constructor
	protected EdgeNamedAndLabelled(Node start, Node end, String label, String name, EdgeFactory factory) {
		super(start, end, factory);
		ln = new LabelNameReference(label,name);
	}
	@Override
	public String getName() {
		return ln.getName();
	}

	@Override
	public boolean hasName(String name) {
		return ln.hasName(name);
	}

	@Override
	public boolean sameName(Named namedItem) {
		return ln.sameName(namedItem);
	}

	@Override
	public Named setName(String name) {
		// fluid??
		return ln.setName(name);
	}

	@Override
	public String getLabel() {
		return ln.getLabel();
	}

	@Override
	public boolean hasLabel(String label) {
		return ln.hasLabel(label);
	}

	@Override
	public boolean sameLabel(Labelled labelledItem) {
		return ln.hasLabel(labelledItem.getLabel());
	}

	@Override
	public Labelled setLabel(String label) {
		return ln.setLabel(label);
	}

}
