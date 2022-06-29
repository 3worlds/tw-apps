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
package au.edu.anu.twapps.exceptions;

import fr.ens.biologie.generic.Textable;

/**
 * The {@link java.lang.Exception} class specific to this library.
 * 
 * @author shayne.flint@anu.edu.au
 * 
 */
public class TwAppsException extends RuntimeException {

	private static final long serialVersionUID = -4216594416420546384L;

	/**
	 * Instantiate an exception on an object with a message.
	 * 
	 * @param item    the item which caused the problem
	 * @param message the error message
	 */
	public TwAppsException(Textable item, String message) {
		super("[on " + item + "]\n[" + message + "]");
	}

	/**
	 * Instantiate an exception with a message
	 * 
	 * @param message the error message
	 */
	public TwAppsException(String message) {
		super("[" + message + "]");
	}

	/**
	 * Exception wrapper.
	 * 
	 * @param e the exception to wrap
	 */
	public TwAppsException(Exception e) {
		super(e);
	}

	/**
	 * Exception wrapper with additional information
	 * 
	 * @param message the error message
	 * @param e       the exception to wrap
	 */
	public TwAppsException(String message, Exception e) {
		super("[" + message + "]\n[original exception: " + e + "]");
		e.printStackTrace();
	}

}
