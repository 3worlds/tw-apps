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

package au.edu.anu.twapps.mm;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Ian Davies
 *
 * @date 1 Jun 2020
 */
// Undo/Redo pattern
public class Caretaker {
	private static List<IMemento> mementos;
	private static int index;

	public static void initialise() {
		clear();
	}

	public static void finalise() {
		clear();
	}

	public static void addState(IMemento m) {
		index++;
		mementos.add(index, m);
	}

	public static IMemento prev() {
		index--;
		return mementos.get(index);
	}

	public static IMemento succ() {
		index++;
		return mementos.get(index);
	}

	public static boolean hasPrev() {
		return index > 0;
	}

	public static boolean hasSucc() {
		return index < (mementos.size() - 1);
	}

	public static String getPrevDescription() {
		return mementos.get(index).getDescription();
	}

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
