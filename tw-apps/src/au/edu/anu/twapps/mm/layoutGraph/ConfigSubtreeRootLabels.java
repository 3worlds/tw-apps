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
package au.edu.anu.twapps.mm.layoutGraph;

import java.util.HashSet;
import java.util.Set;

import fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels;

/**
 * This static class is used to identify the sub-tree membership for nodes in
 * the configuration graph. This information can be used to determine a colour
 * scheme for display.
 * 
 * @author Ian Davies - 20 Sep. 2017
 */
public class ConfigSubtreeRootLabels {
	private static Set<String> labelSet = new HashSet<>();
	static {
		labelSet.add(ConfigurationNodeLabels.N_SYSTEM.label());
		labelSet.add(ConfigurationNodeLabels.N_DYNAMICS.label());
		labelSet.add(ConfigurationNodeLabels.N_STRUCTURE.label());
		labelSet.add(ConfigurationNodeLabels.N_DATADEFINITION.label());
		labelSet.add(ConfigurationNodeLabels.N_EXPERIMENT.label());
		labelSet.add(ConfigurationNodeLabels.N_UI.label());
		labelSet.add(ConfigurationNodeLabels.N_PREDEFINED.label());
	}

	/**
	 * @param nodeLabel The label to search for.
	 * @return True if the given node label is present: false otherwise
	 */
	public static boolean contains(String nodeLabel) {
		return labelSet.contains(nodeLabel);
	}

}
