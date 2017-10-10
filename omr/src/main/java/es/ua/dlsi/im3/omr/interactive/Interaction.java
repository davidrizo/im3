package es.ua.dlsi.im3.omr.interactive;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.gui.javafx.dialogs.ShowError;
import es.ua.dlsi.im3.gui.useractionlogger.ActionLogger;
import es.ua.dlsi.im3.gui.useractionlogger.actions.Coordinate;
import es.ua.dlsi.im3.gui.useractionlogger.actions.MouseClickAction;
import es.ua.dlsi.im3.gui.useractionlogger.actions.MouseMoveAction;
import es.ua.dlsi.im3.omr.old.mensuraltagger.OMRMainController;
import javafx.beans.property.BooleanProperty;
import javafx.event.EventHandler;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Used to encapsulate the interaction with the image view to simplify the OMRController class
 */
public class Interaction {

    private final ImageView imageView;
    private final Pane marksPane;
    private final BooleanProperty identiyingStaves;

    MouseMoveAction mouseMoveAction;
    private SelectionRectangle selectingRectangle;

    public Interaction(ImageView imageView, Pane marksPane, BooleanProperty identiyingStaves) {
        this.imageView = imageView;
        this.marksPane = marksPane;
        this.identiyingStaves = identiyingStaves;
        init();
    }

    private void init() {
        marksPane.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                doMousePressed(t);
            }
        });

        marksPane.addEventFilter(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                doMouseDragged(t);
            }

        });

        marksPane.addEventFilter(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                doMouseReleased();
            }
        });

    }

    private void doMousePressed(MouseEvent t) {
        if (identiyingStaves.get()) {
            selectingRectangle = new SelectionRectangle(t.getX(), t.getY());
        }
    }

    private void doMouseDragged(MouseEvent t) {
        if (identiyingStaves.get()) {
            if (selectingRectangle.getState() == SelectionRectangle.State.firstClick) {
                selectingRectangle.changeState();
                // now we know the user wants a rectangle, it was not a single click
                marksPane.getChildren().add(selectingRectangle.getRoot());
            }
            selectingRectangle.changeEndPoint(t.getX(), t.getY());
        }
    }


    private void doMouseReleased() {
        if (identiyingStaves.get()) {
            if (selectingRectangle != null) {
                selectingRectangle.changeState();
            }
        }
    }
}
