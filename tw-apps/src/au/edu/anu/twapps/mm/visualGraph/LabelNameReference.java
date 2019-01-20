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

import au.edu.anu.twapps.exceptions.TwAppsException;
import fr.ens.biologie.generic.Labelled;
import fr.ens.biologie.generic.Named;
import fr.ens.biologie.generic.NamedAndLabelled;

public class LabelNameReference implements NamedAndLabelled {

	private String label;
	private String name;

	public LabelNameReference(String label, String name) {
		this.label = label;
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

		// We should return false if either or both is null - otherwise we are stuck
	@Override
	public boolean hasName(String name) {
		if (name == null || this.name == null)
			return false;
		return this.name.equals(name);
	}

	@Override
	public boolean sameName(Named namedItem) {
		return this.hasName(namedItem.getName());
	}

	@Override
	public Named setName(String name) {
		if (this.name == null)
			this.name = name;
		else
			throw new TwAppsException("Attempt to rename " + this.name + " to " + name);
		return this;
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public boolean hasLabel(String label) {
		if (label == null || this.label == null)
			return false;
		return this.label.equals(label);
	}

	@Override
	public boolean sameLabel(Labelled labelledItem) {
		return hasLabel(labelledItem.getLabel());
	}

	@Override
	public Labelled setLabel(String label) {
		if (this.label == null)
			this.label = label;
		else
			throw new TwAppsException("Attempt to relabel " + this.label + " to " + label);
		return this;
	}

}
