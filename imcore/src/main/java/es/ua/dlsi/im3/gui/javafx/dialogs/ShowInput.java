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
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 *
 * @author drizo
 */
public class ShowInput {

    /**
     * @return null when cancelled
     */
    public static String show(Stage stage, String title, String message) {
		TextInputDialog dialog = new TextInputDialog();
		dialog.initOwner(stage);
		dialog.setTitle(stage.getTitle());
		dialog.setHeaderText(title);
		dialog.setContentText(message);

		// Traditional way to get the response value.
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()) {
			return result.get();
		} else {
			return null;
		}
    }


    /**
     * @return null when cancelled
     */
    public static String show(Window ownerWindow, String title, String message) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.initOwner(ownerWindow);
        dialog.setHeaderText(title);
        dialog.setContentText(message);

        // Traditional way to get the response value.
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            return result.get();
        } else {
            return null;
        }
    }
}
