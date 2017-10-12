package es.ua.dlsi.im3.omr.interactive;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.gui.javafx.SelectionRectangle;
import es.ua.dlsi.im3.gui.javafx.dialogs.ShowError;
import es.ua.dlsi.im3.gui.useractionlogger.actions.MouseMoveAction;
import javafx.beans.property.BooleanProperty;
import javafx.event.EventHandler;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

/**
 * Used to encapsulate the interaction with the image view to simplify the OMRController class
 */
public class PageInteraction {

    private final ImageView imageView;
    private final Pane marksPane;
    private final BooleanProperty identiyingStaves;
    private final OMRController controller;

    MouseMoveAction mouseMoveAction;
    private SelectionRectangle selectingRectangle;

    public PageInteraction(OMRController controller, ImageView imageView, Pane marksPane, BooleanProperty identiyingStaves) {
        this.imageView = imageView;
        this.marksPane = marksPane;
        this.identiyingStaves = identiyingStaves;
        this.controller = controller;
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
                try {
                    doMouseReleased();
                } catch (IM3Exception e) {
                    ShowError.show(OMRApp.getMainStage(), "Cannot add staff", e);
                }
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
            if (selectingRectangle.isInFirstClickState()) {
                selectingRectangle.changeState();
                // now we know the user wants a rectangle, it was not a single click
                marksPane.getChildren().add(selectingRectangle.getRoot());
            }
            selectingRectangle.changeEndPoint(t.getX(), t.getY());
        }
    }


    private void doMouseReleased() throws IM3Exception {
        if (identiyingStaves.get()) {
            if (selectingRectangle != null) {
                selectingRectangle.changeState();
                controller.onStaffIdentified(selectingRectangle.getSelectionRectangle().getX(), selectingRectangle.getSelectionRectangle().getY(),
                        selectingRectangle.getSelectionRectangle().getX()+selectingRectangle.getSelectionRectangle().getWidth(),
                        selectingRectangle.getSelectionRectangle().getY()+selectingRectangle.getSelectionRectangle().getHeight());
                marksPane.getChildren().remove(selectingRectangle.getRoot());
                selectingRectangle = null;
            }
        }
    }
}
