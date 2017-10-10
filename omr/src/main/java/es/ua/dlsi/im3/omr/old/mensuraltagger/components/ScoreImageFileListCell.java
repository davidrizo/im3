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
package es.ua.dlsi.im3.omr.old.mensuraltagger.components;

import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

/**
 *
 * @author drizo
 */
public class ScoreImageFileListCell extends ListCell<ScoreImageFile> {

    public ScoreImageFileListCell() {
    }

    @Override
    public void updateItem(ScoreImageFile item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setGraphic(null);
            setText(null);
        } else {
            ImageView imageView = new ImageView();
            imageView.imageProperty().bind(item.imageProperty());
            imageView.fitWidthProperty().bind(this.widthProperty());
            imageView.setPreserveRatio(true);

            final Text text = new Text();
            text.textProperty().bind(item.tagsFileProperty().asString());
            text.setScaleX(1.5);
            text.setScaleY(1.5);
            StackPane panel = new StackPane(imageView, text);
            setGraphic(panel);
        }


        /*if (item.tagsFileProperty().isNull().get()) {
            setText("Not tagged");
            //text.setFill(Color.RED);
            //text.setStroke(Color.RED);
        } else {
            setText("Tagged");
            //text.setFill(Color.BLUE);
            //text.setStroke(Color.BLUE);
        }*/

	    /*oct 2017 item.tagsFilesProperty().addListener(new ListChangeListener() {
		@Override
		public void onChanged(Change c) {
		    if (c.getList().isEmpty()) {
			text.setFill(Color.RED);
			text.setStroke(Color.RED);					
		    } else {
			text.setFill(Color.BLUE);
			text.setStroke(Color.BLUE);		
		    }
		}
	    });
	    
	    if (item.tagsFilesProperty().isEmpty()) {
		text.setFill(Color.RED);
		text.setStroke(Color.RED);		
	    } else {
		text.setFill(Color.BLUE);
		text.setStroke(Color.BLUE);		
	    }
	    StackPane panel = new StackPane(imageView, text);	    
	    setGraphic(panel);
	   // setText("Tagged files: " + item.getNumTagsFiles());
	}*/
    }
}
