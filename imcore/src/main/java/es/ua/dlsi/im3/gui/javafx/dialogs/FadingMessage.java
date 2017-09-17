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

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
@see http://robmayhew.com/javafx-2-0-fading-status-message/
@date 14/02/2012
 **/
public class FadingMessage {
	public static void flash(Node node, String message) {
        Font font = Font.font("Verdana", FontWeight.NORMAL, 20);
        Color boxColor = Color.GREY;
        Color textColor = Color.WHITE;
        double duration = 3500;
        double arcH = 5;
        double arcW = 5;
 
        final Rectangle rectangle = new Rectangle();
        final Text text = new Text(message);
 
 
        double x = 0;
        double y = 0;
        text.setLayoutX(x);
        text.setLayoutY(y);
        text.setFont(font);
        text.setFill(textColor);
 
        Scene scene = node.getScene();
        final Parent p = scene.getRoot();
 
        if (p instanceof Group) 
        {
            Group group = (Group) p;
            group.getChildren().add(rectangle);
            group.getChildren().add(text);
        }
        if (p instanceof Pane) 
        {
            Pane group = (Pane) p;
            group.getChildren().add(rectangle);
            group.getChildren().add(text);
        }
 
        Bounds bounds = text.getBoundsInParent();
 
        double sWidth = scene.getWidth();
        double sHeight = scene.getHeight();
 
        x = sWidth / 2 - (bounds.getWidth() / 2);
        y = sHeight / 2 - (bounds.getHeight() / 2);
        text.setLayoutX(x);
        text.setLayoutY(y);
        bounds = text.getBoundsInParent();
        double baseLineOffset = text.getBaselineOffset();
 
 
        rectangle.setFill(boxColor);
        rectangle.setLayoutX(x - arcW);
        rectangle.setLayoutY(y - baseLineOffset - arcH);
        rectangle.setArcHeight(arcH);
        rectangle.setArcWidth(arcW);
        rectangle.setWidth(bounds.getWidth() + arcW * 2);
        rectangle.setHeight(bounds.getHeight() + arcH * 2);
 
        FadeTransition ft = new FadeTransition(
                Duration.millis(duration), rectangle);
        ft.setFromValue(1.0);
        ft.setToValue(0.0);
        ft.play();
        ft.setOnFinished(new EventHandler<ActionEvent>() {
 
            public void handle(ActionEvent event) {
                if (p instanceof Group) {
                    Group group = (Group) p;
                    group.getChildren().remove(rectangle);
                    group.getChildren().remove(text);
                }
                 if (p instanceof Pane) {
                    Pane group = (Pane) p;
                    group.getChildren().remove(rectangle);
                    group.getChildren().remove(text);
                }
            }
        });
        FadeTransition ft2 = new FadeTransition(
                Duration.millis(duration + (duration * .1)), text);
        ft2.setFromValue(1.0);
        ft2.setToValue(0.0);
        ft2.play();
    }
}
