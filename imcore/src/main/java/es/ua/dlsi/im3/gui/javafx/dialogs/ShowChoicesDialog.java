/*
 * Copyright (C) 2015 David Rizo Valero
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

import java.util.Collection;
import java.util.Optional;
import javafx.scene.control.ChoiceDialog;
import javafx.stage.Stage;

/**
 * see http://code.makery.ch/blog/javafx-dialogs-official/
 *
 * @author drizo
 */
public class ShowChoicesDialog<T> {

    /**
     * @param message
     * @return true when user accepts
     */
    public T show(Stage stage, String title, String message, Collection<T> choices) {
        ChoiceDialog<T> dialog = new ChoiceDialog<>();
        dialog.getItems().addAll(choices);
        dialog.setTitle(stage.getTitle());
        dialog.setHeaderText(title);
        dialog.setContentText(message);
        dialog.initOwner(stage);
        Optional<T> result = dialog.showAndWait();
        if (result.isPresent()) {
            return result.get();
        } else {
            return null;
        }
    }
}
