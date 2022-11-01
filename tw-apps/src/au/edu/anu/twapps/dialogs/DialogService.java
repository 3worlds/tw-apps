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

import java.io.File;
import java.util.List;

/**
 * Singleton container for dialog box implementation.
 *
 * @author Ian Davies - 12 Dec. 2018
 * 
 */
public final class DialogService {
	private static Dialogs impl;

	/**
	 * Private constructor to prevent instantiation.
	 */
	private DialogService() {
	};

	/**
	 * Sets the current implementation of the dialogs to be used. This must be done
	 * before any other use of the DialogService.
	 * 
	 * @param impl The implementation of {@link Dialogs}.
	 */
	public static void setImplementation(Dialogs impl) {
		DialogService.impl = impl;
	}

	/**
	 * @return The current Dialogs implementation.
	 */
	public static Dialogs getImplementation() {
		return impl;
	}

	/**
	 * regex for all chars of a floating point number
	 */
	public static final String REGX_REAL = "[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?";
	/**
	 * regex string for any alphabetic character.
	 */
	public static final String REGX_ALPHA = "([a-zA-Z]*)?";
	/**
	 * regex string for alpha-numeric characters.
	 */
	public static final String REGX_ALPHA_NUMERIC = "([a-zA-Z0-9]*)?";
	/**
	 * regex string for alpha-numeric chars or blank space. This may need to change!
	 * It is used to allow the user to return an empty string but this is bad
	 * practice. The user should choose cancel!
	 */
	public static final String REGX_ALPHA_NUMERIC_SPACE = "([a-zA-Z][a-zA-Z0-9 ]*)?";
	/**
	 * regex for Alpha-numeric string starting with uppercase.
	 */
	public static final String REGX_ALPHA_CAP_NUMERIC = "([A-Z][a-zA-Z0-9]*)?";

}
