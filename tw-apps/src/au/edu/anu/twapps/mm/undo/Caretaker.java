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

package au.edu.anu.twapps.mm.undo;

import java.util.LinkedList;
import java.util.List;

/**
 * A static class to manage the list of {@link IMemento} and the index of the
 * current state.
 * 
 * @author Ian Davies - 1 Jun 2020
 */

public class Caretaker {
	private static List<IMemento> mementos;
	private static int index;

	private Caretaker() {

	}

	/**
	 * Called when a project opens to clear any remaining {@link IMemento}s. Only
	 * required is project has terminated unexpectedly.
	 */
	public static void initialise() {
		clear();
	}

	/**
	 * Called when a project closes to clear any remaining {@link IMemento}s.
	 */
	public static void finalise() {
		clear();
	}

	/**
	 * Add an {@link IMemento} state to the list and increment the index.
	 * 
	 * @param m The new state.
	 */
	public static void addState(IMemento m) {
		index++;
		mementos.add(index, m);
	}

	/**
	 * Step back to the previous state in an undo operation.
	 * 
	 * @return The previous state.
	 */
	public static IMemento prev() {
		index--;
		return mementos.get(index);
	}

	/**
	 * Step forward to the next state in a redo operation.
	 * 
	 * @return The next state.
	 */
	public static IMemento succ() {
		index++;
		return mementos.get(index);
	}

	/**
	 * Does a previous state exist? Used to update a control with 'undo' option.
	 * 
	 * @return true if a previous state exists, false otherwise.
	 */
	public static boolean hasPrev() {
		return index > 0;
	}

	/**
	 * Does a successor state exist? Used to update a control with 'redo' option.
	 * 
	 * @return true if a successor state exists, false otherwise.
	 */
	public static boolean hasSucc() {
		return index < (mementos.size() - 1);
	}

	/**
	 * Getter for the Description of the previous state.
	 * 
	 * @return The description.
	 */
	public static String getPrevDescription() {
		return mementos.get(index).getDescription();
	}

	/**
	 * Getter for the Description of the successor state.
	 * 
	 * @return The description.
	 */
	public static String getSuccDescription() {
		return mementos.get(index + 1).getDescription();
	}

	private static void clear() {
		if (mementos != null)
			for (IMemento m : mementos)
				m.finalise();
		mementos = new LinkedList<>();
		index = -1;
	}

}
