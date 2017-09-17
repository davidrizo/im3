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
import javafx.scene.Node;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.stage.Stage;

/**
 * // see http://code.makery.ch/blog/javafx-dialogs-official/

 * @author drizo
 */
public class CustomDialog  { 
    protected final Dialog dialog;

    public CustomDialog(Stage stage, String title, Node node) {
		dialog = new Dialog<>();
		dialog.initOwner(stage);
		dialog.setTitle(title);
		dialog.getDialogPane().setContent(node);
		ButtonType btn = new ButtonType("OK", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().add(btn);
	}

	public boolean show() {
		Optional<ButtonType> result = dialog.showAndWait();

		return (result.isPresent() && result.get().getButtonData() == ButtonData.OK_DONE);
     }	
}
