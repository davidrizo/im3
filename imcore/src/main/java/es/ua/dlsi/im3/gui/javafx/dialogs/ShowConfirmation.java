/*
 * Copyright (C) 2014 David Rizo Valero
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package es.ua.dlsi.im3.gui.javafx.dialogs;

import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

/**
 *
 * @author drizo
 */
public class ShowConfirmation {
	/**
	 * @return true when user accepts
	 */
	public static boolean show(Stage stage, String message) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.initOwner(stage);

		alert.setTitle(stage.getTitle());
		alert.setHeaderText(null);
		alert.setContentText(message);

		Optional<ButtonType> result = alert.showAndWait();
		return result.get().getButtonData() == ButtonBar.ButtonData.OK_DONE;
	}
}
