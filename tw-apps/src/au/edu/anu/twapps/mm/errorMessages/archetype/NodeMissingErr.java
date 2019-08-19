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

package au.edu.anu.twapps.mm.errorMessages.archetype;

import au.edu.anu.rscs.aot.util.IntegerRange;
import au.edu.anu.twcore.errorMessaging.ErrorMessageAdaptor;
import fr.cnrs.iees.graph.impl.SimpleDataTreeNode;

/**
 * Author Ian Davies
 *
 * Date Dec 12, 2018
 */
public class NodeMissingErr extends ErrorMessageAdaptor{
//	private IntegerRange expectedRange;
//	private int foundCount;
//	private AotNode spec;
//	private String reference;

public NodeMissingErr(String reference, IntegerRange expectedRange, int foundCount, SimpleDataTreeNode spec) {
//	this.reference = reference;
//	this.expectedRange = expectedRange;
//	this.foundCount = foundCount;
//	this.spec = spec;
//	String parent = ArchetypeHelper.getParentLabelFromReference(reference);
//	String[] parents;
//	if (parent.equals(""))
//		parents = ArchetypeHelper.getParentLabels(spec);
//	else {
//		parents = new String[1];
//		parents[0] = parent;
//	}
//	msg1 = "Missing Node: Add node " + formatParentChildString(reference, spec) + ".";
//	msg2 = msg1 + " Expected " + expectedRange.toString() + " but found " + foundCount + ".";
//	msg3 = msg2 + "\nSpecification:\n" + spec.toDetailedString() + "\n";

}
}
