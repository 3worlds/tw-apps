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

import au.edu.anu.twapps.project.Project;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * Author Ian Davies
 *
 * Date 14 Dec. 2018
 */
public class GraphState {
	private static boolean hasChanged = false;
	private static StringProperty propertyTitle;
	private static StringProperty propertyJavaPath;

	public static boolean hasChanged() {
		return hasChanged;
	}

	public static void setTitleProperty(StringProperty tp, StringProperty pp) {
		propertyTitle = tp;
		propertyJavaPath = pp;
		if (propertyJavaPath != null)
			propertyJavaPath.addListener(new ChangeListener<String>() {

				@Override
				public void changed(@SuppressWarnings("rawtypes") ObservableValue observable, String oldValue, String newValue) {
					setTitle();
				}
			});
		setTitle();
	}

	private static void setTitle() {
		Platform.runLater(() -> {
			String title = null;
			if (Project.isOpen()) {
				title = Project.getDisplayName();
				if (hasChanged)
					title = "*" + title;
				if (propertyTitle != null) {
					if (propertyJavaPath != null) {
						if (!propertyJavaPath.get().equals("")) {
							title = title + "<o-o-o>" + propertyJavaPath.get();
						}
					}
					propertyTitle.setValue(title);
				}
			}
		});

	}

}
