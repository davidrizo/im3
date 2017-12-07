package es.ua.dlsi.im3.omr.interactive.documentanalysis;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.gui.javafx.SelectionRectangle;
import es.ua.dlsi.im3.gui.javafx.dialogs.ShowChoicesDialog;
import es.ua.dlsi.im3.gui.javafx.dialogs.ShowError;
import es.ua.dlsi.im3.omr.interactive.OMRApp;
import es.ua.dlsi.im3.omr.interactive.model.OMRPage;
import es.ua.dlsi.im3.omr.interactive.model.OMRRegion;
import es.ua.dlsi.im3.omr.segmentation.RegionType;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class PageView extends Group {
    OMRPage omrPage;
    DocumentAnalysisController documentAnalysisController;
    ImageView imageView;
    private SelectionRectangle selectingRectangle;

    public PageView(OMRPage omrPage, DocumentAnalysisController documentAnalysisController, ReadOnlyDoubleProperty widthProperty) {
        this.omrPage = omrPage;
        this.documentAnalysisController = documentAnalysisController;
        imageView = new ImageView();
        //imageView.fitWidthProperty().bind(widthProperty); // it provokes a zoom when adding a rectangle
        imageView.setPreserveRatio(true);
        imageView.setImage(SwingFXUtils.toFXImage(omrPage.getBufferedImage(), null));
        this.getChildren().add(imageView);
        initInteraction();
    }

    public void addRegion(OMRRegion region) {
        RegionView regionView = new RegionView(region);
        this.getChildren().add(regionView);
    }

    private void initInteraction() {
        imageView.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                doMousePressed(t);
                t.consume();
            }
        });

        imageView.addEventFilter(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                doMouseDragged(t);
                t.consume();
            }

        });

        imageView.addEventFilter(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                try {
                    doMouseReleased();
                    t.consume();
                } catch (IM3Exception e) {
                    ShowError.show(OMRApp.getMainStage(), "Cannot add staff", e);
                }
            }
        });

    }

    private void doMousePressed(MouseEvent t) {
        selectingRectangle = new SelectionRectangle(t.getX(), t.getY());
    }

    private void doMouseDragged(MouseEvent t) {
        if (selectingRectangle.isInFirstClickState()) {
            selectingRectangle.changeState();
            // now we know the user wants a rectangle, it was not a single click
            getChildren().add(selectingRectangle.getRoot());
        }
        selectingRectangle.changeEndPoint(t.getX(), t.getY());
    }


    private void doMouseReleased() throws IM3Exception {
        if (selectingRectangle != null) {
            selectingRectangle.changeState();
            onRegionIdentified(selectingRectangle.getSelectionRectangle().getX(), selectingRectangle.getSelectionRectangle().getY(),
                    selectingRectangle.getSelectionRectangle().getX() + selectingRectangle.getSelectionRectangle().getWidth(),
                    selectingRectangle.getSelectionRectangle().getY() + selectingRectangle.getSelectionRectangle().getHeight());
            getChildren().remove(selectingRectangle.getRoot());
            selectingRectangle = null;
        }
    }

    private void onRegionIdentified(double fromX, double fromY, double toX, double toY) {
        ShowChoicesDialog<RegionType> dlg = new ShowChoicesDialog<>();
        RegionType regionType = dlg.show(OMRApp.getMainStage(),"New region added", "Choose the region type", RegionType.values(), RegionType.staff); // default value, staff
        if (regionType != null) {
            addRegion(new OMRRegion(fromX, fromY, toX, toY, regionType));
        }
    }
}
