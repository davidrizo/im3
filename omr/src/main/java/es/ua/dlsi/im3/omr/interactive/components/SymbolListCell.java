/*
 * Copyright (C) 2016 David Rizo Valero
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
package es.ua.dlsi.im3.omr.interactive.components;

import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

/**
 *
 * @author drizo
 */
public class SymbolListCell extends ListCell<SymbolView> {
	@Override
	public void updateItem(SymbolView item, boolean empty) {
		super.updateItem(item, empty);
		if (empty) {
			setGraphic(null);
			setText(null);
		} else {
			HBox hbox = new HBox(5);
			hbox.getChildren().add(item.getMiniatureGroup());
			if (item.getImageView() != null) {
				hbox.getChildren().add(item.getImageView());
			}
			if (item.getNotationSymbolViewInStaff() != null) {
				hbox.getChildren().add(item.getNotationSymbolViewInStaff());
				// setText(item.getNotationSymbolView().getText());
				if (item.getSymbol().getPositionInStaff() != null) {
					String pos = item.getSymbol().getPositionInStaff().toString();
					Text text = new Text(pos);
					hbox.getChildren().add(text);
				}
			}
			setGraphic(hbox);
			if (this.isSelected()) {
				if (item.isChecked()) {
					hbox.setStyle("-fx-background-color: #8CDDff;");
				} else if (item.isEditing()){
					hbox.setStyle("-fx-background-color: #5EFF00;");
				} else {
					hbox.setStyle("-fx-background-color: #0000ff;");
				}
			} else {
				if (item.isChecked()) {
					hbox.setStyle("-fx-background-color: #8CDD81;");
				}
			}
			// setText("Tagged files: " + item.getNumTagsFiles());
		}
	}
}
