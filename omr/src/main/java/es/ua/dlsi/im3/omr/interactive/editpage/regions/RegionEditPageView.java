package es.ua.dlsi.im3.omr.interactive.editpage.regions;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.gui.javafx.SelectionRectangle;
import es.ua.dlsi.im3.gui.javafx.dialogs.ShowChoicesDialog;
import es.ua.dlsi.im3.gui.javafx.dialogs.ShowError;
import es.ua.dlsi.im3.omr.interactive.Event;
import es.ua.dlsi.im3.omr.interactive.OMRApp;
import es.ua.dlsi.im3.omr.interactive.editpage.RegionBasedPageView;
import es.ua.dlsi.im3.omr.interactive.model.OMRPage;
import es.ua.dlsi.im3.omr.interactive.model.OMRRegion;
import es.ua.dlsi.im3.omr.model.pojo.RegionType;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;


public class RegionEditPageView extends RegionBasedPageView<PageRegionsEditController, RegionEditView, RegionViewState> {
    private SelectionRectangle selectingRectangle;

    /**
     * Region being edited
     */
    private RegionEditView editingRegion;

    public RegionEditPageView(OMRPage omrPage, PageRegionsEditController pageController, ReadOnlyDoubleProperty widthProperty) {
        super(omrPage, pageController, widthProperty);
    }


    @Override
    protected void initStateMachine() {
        state = RegionViewState.idle;
    }

    @Override
    public RegionEditView createRegionView(OMRRegion region) {
        return new RegionEditView(this, region);
    }

    public void handleEvent(Event t) {
        try {
            KeyEvent keyEvent = null;
            MouseEvent mouseEvent = null;
            boolean mouseClicked = false;
            if (t.getContent() instanceof MouseEvent) {
                mouseEvent = (MouseEvent) t.getContent();
                mouseClicked = mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED || mouseEvent.getEventType() == MouseEvent.MOUSE_CLICKED;
            } else if (t.getContent() instanceof KeyEvent) {
                keyEvent = (KeyEvent) t.getContent();
            }
            switch (state) {
                case idle:
                    if (!(t instanceof RegionEditEvent) && mouseEvent != null && mouseClicked && mouseEvent.getButton() == MouseButton.PRIMARY && mouseEvent.getClickCount() == 1) {
                        mouseEvent.consume();
                        createNewRectangle(mouseEvent);
                        changeState(RegionViewState.creating);
                    } else if (t instanceof RegionEditEvent) {

                        editingRegion = ((RegionEditEvent)t).getRegionView();
                        if (mouseEvent != null && mouseClicked && mouseEvent.getButton() == MouseButton.SECONDARY) {
                            editingRegion.showRegionTypeContextMenu(mouseEvent.getScreenX(), mouseEvent.getScreenY());
                        } else {
                            bringToTop(editingRegion); // if not, the handlers do not receive drag events when overlapped with other region
                            editingRegion.beginEdit();
                            ((RegionEditEvent)t).getContent().consume();
                            changeState(RegionViewState.editing);
                        }
                    }
                    break;
                case creating:
                    if (mouseEvent != null) {
                        if (mouseEvent.getEventType() == MouseEvent.MOUSE_DRAGGED) {
                            resizeNewRectangle(mouseEvent);
                            mouseEvent.consume();
                        } else if (mouseEvent.getEventType() == MouseEvent.MOUSE_RELEASED) {
                            endNewRectangle();
                            mouseEvent.consume();
                            changeState(RegionViewState.idle);
                        }
                    }
                    break;
                case editing:
                    if (mouseEvent != null && mouseEvent.isPrimaryButtonDown()) {
                        editingRegion.acceptEdit();
                        changeState(RegionViewState.idle);
                        mouseEvent.consume();
                    } else if (keyEvent != null) {
                        if (keyEvent.getCode() == KeyCode.ENTER) {
                            editingRegion.acceptEdit(); //TODO Comando
                            changeState(RegionViewState.idle);
                            keyEvent.consume();
                        } else if (keyEvent.getCode() == KeyCode.ESCAPE) {
                            editingRegion.cancelEdit();
                            changeState(RegionViewState.idle);
                            keyEvent.consume();
                        } else if (keyEvent.getCode() == KeyCode.DELETE) {
                            omrPage.removeRegion(editingRegion.getOmrRegion());
                            changeState(RegionViewState.idle);
                            keyEvent.consume();
                        }
                    }
                    break;
            }
        } catch (IM3Exception e) {
            ShowError.show(OMRApp.getMainStage(), "Cannot handle event " + e, e);
        }
    }

    protected void onRectangleDrawn(double fromX, double fromY, double width, double height) {
        ShowChoicesDialog<RegionType> dlg = new ShowChoicesDialog<>();
        RegionType regionType = dlg.show(OMRApp.getMainStage(),"New region added", "Choose the region type", RegionType.values(), RegionType.staff); // default value, staff
        if (regionType != null) {
            omrPage.addRegion(new OMRRegion(fromX, fromY, width, height, regionType));
        }
    }

    protected void createNewRectangle(MouseEvent t) {
        selectingRectangle = new SelectionRectangle(t.getX(), t.getY());
    }

    protected void resizeNewRectangle(MouseEvent t) {
        if (selectingRectangle.isInFirstClickState()) {
            selectingRectangle.changeState();
            // now we know the user wants a rectangle, it was not a single click
            getChildren().add(selectingRectangle.getRoot());
        }
        selectingRectangle.changeEndPoint(t.getX(), t.getY());
    }


    protected void endNewRectangle() throws IM3Exception {
        if (selectingRectangle != null) {
            selectingRectangle.changeState();
            onRectangleDrawn(selectingRectangle.getSelectionRectangle().getX(), selectingRectangle.getSelectionRectangle().getY(),
                    selectingRectangle.getSelectionRectangle().getWidth(),
                    selectingRectangle.getSelectionRectangle().getHeight());
            getChildren().remove(selectingRectangle.getRoot());
            selectingRectangle = null;
        }
    }


    /*private void showRegions(boolean show) {
        for (RegionView regionView: regions.values()) {
            regionView.showRegionBoundingBox(show);
        }
    }*/
}
