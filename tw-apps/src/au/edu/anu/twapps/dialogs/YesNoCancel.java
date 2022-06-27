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

package au.edu.anu.twapps.dialogs;

/**
 * @author Ian Davies - 25 June 2022
 *         <p>
 *         A return type for some methods in {@link IDialogs}.
 *         </p>
 *         This is used to avoid dependence on any types in Javafx as we wish to
 *         maintain independence from whatever implementations user may choose
 *         (i.e something other that javafx).
 */
public enum YesNoCancel {
	/**
	 * User want action performed e.g save changes before closing.
	 */
	yes,
	/**
	 * User does not want action performed e.g. do not save changes before closing.
	 */
	no,
	/**
	 * User wants to cancel the operation altogether e.g don't save or not save just
	 * stay where we are.
	 */
	cancel;
}
