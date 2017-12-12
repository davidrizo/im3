package es.ua.dlsi.im3.omr.interactive.pageedit;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.gui.javafx.SelectionRectangle;
import es.ua.dlsi.im3.gui.javafx.dialogs.ShowChoicesDialog;
import es.ua.dlsi.im3.gui.javafx.dialogs.ShowError;
import es.ua.dlsi.im3.omr.interactive.OMRApp;
import es.ua.dlsi.im3.omr.interactive.pageedit.events.PageEditStepEvent;
import es.ua.dlsi.im3.omr.interactive.pageedit.events.RegionEditEvent;
import es.ua.dlsi.im3.omr.interactive.model.OMRPage;
import es.ua.dlsi.im3.omr.interactive.model.OMRRegion;
import es.ua.dlsi.im3.omr.interactive.pageedit.events.SymbolEditEvent;
import es.ua.dlsi.im3.omr.model.pojo.RegionType;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.collections.ListChangeListener;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PageView extends Group {
    OMRPage omrPage;
    PageEditController documentAnalysisController;
    ImageView imageView;
    private SelectionRectangle selectingRectangle;

    HashMap<OMRRegion, RegionView> regions;

    PageViewState state;
    /**
     * Region being edited
     */
    private RegionView editingRegion;
    private SymbolView editingSymbol;

    public PageView(OMRPage omrPage, PageEditController documentAnalysisController, ReadOnlyDoubleProperty widthProperty) {
        this.setFocusTraversable(true); // to receive key events
        state = PageViewState.idleRegion;
        regions = new HashMap<>();
        this.omrPage = omrPage;
        this.documentAnalysisController = documentAnalysisController;
        imageView = new ImageView();
        //imageView.fitWidthProperty().bind(widthProperty); // it provokes a zoom when adding a rectangle
        imageView.setPreserveRatio(true);
        imageView.setImage(SwingFXUtils.toFXImage(omrPage.getBufferedImage(), null));
        this.getChildren().add(imageView);
        initInteraction();
        loadRegions();
        initRegionBinding();
    }

    private void loadRegions() {
        for (OMRRegion omrRegion : omrPage.getRegionList()) {
            createRegionView(omrRegion);
        }
    }

    private void initRegionBinding() {
        omrPage.regionListProperty().addListener(new ListChangeListener<OMRRegion>() {
            @Override
            public void onChanged(Change<? extends OMRRegion> c) {
                while (c.next()) {
                    if (c.wasPermutated()) {
                        // no-op
                    } else if (c.wasUpdated()) {
                        //update item - no lo necesitamos de momento porque lo tenemos todo con binding, si no podríamos actualizar aquí
                    } else {
                        for (OMRRegion remitem : c.getRemoved()) {
                            removeRegionView(remitem);
                        }
                        for (OMRRegion additem : c.getAddedSubList()) {
                            createRegionView(additem);
                        }
                    }
                }
            }
        });
    }

    private void removeRegionView(OMRRegion remitem) {
        RegionView regionView = regions.remove(remitem);
        /*if (regionView == null) {
            throw new IM3RuntimeException("Item " + remitem + " not found");
        }*/
        this.getChildren().remove(regionView);
    }

    public void createRegionView(OMRRegion region) {
        RegionView regionView = new RegionView(this, region);
        regions.put(region, regionView);
        this.getChildren().add(regionView);
    }

    private void initInteraction() {
        imageView.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                handleEvent(new Event<MouseEvent>(t));
                //doMousePressed(t);
                //t.consume();
            }
        });

        imageView.addEventFilter(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                handleEvent(new Event<MouseEvent>(t));
                //doMouseDragged(t);
                //t.consume();
            }

        });

        imageView.addEventFilter(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                    handleEvent(new Event<MouseEvent>(t));
                    //doMouseReleased();
                    //t.consume();
                //} catch (IM3Exception e) {
                  //  ShowError.show(OMRApp.getMainStage(), "Cannot add staff", e);
                //}
            }
        });
        OMRApp.getKeyEventManager().setCurrentKeyEventHandler(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                handleEvent(new Event<KeyEvent>(event));
            }
        });
    }

    public void handleEvent(Event t) {
        //TODO Que no deje pulsar el botón de cambio de estado hasta que no esté en idle...
        if (t instanceof PageEditStepEvent) {
            PageEditStepEvent pageEditStepEvent = (PageEditStepEvent) t;
            switch (pageEditStepEvent.getContent()) {
                case regions:
                    changeState(PageViewState.idleRegion);
                    showRegions(true);
                    showSymbols(false);
                    showMusic(false);
                    break;
                case symbols:
                    changeState(PageViewState.idleSymbolRecognition);
                    showRegions(false);
                    showSymbols(true);
                    showMusic(false);
                    break;
                case music:
                    changeState(PageViewState.idleMusicEditing);
                    showRegions(false);
                    showSymbols(false);
                    showMusic(true);
                    break;
            }
            return;
        }

        try {
            KeyEvent keyEvent = null;
            MouseEvent mouseEvent = null;
            if (t.getContent() instanceof MouseEvent) {
                mouseEvent = (MouseEvent) t.getContent();
            } else if (t.getContent() instanceof KeyEvent) {
                keyEvent = (KeyEvent) t.getContent();
            }
            switch (state) {
                case idleRegion:
                    if (mouseEvent != null && mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED && mouseEvent.isPrimaryButtonDown() && mouseEvent.getClickCount() == 1) {
                        mouseEvent.consume();
                        createNewRegionRectangle(mouseEvent);
                        changeState(PageViewState.creatingRegion);
                    } else if (t instanceof RegionEditEvent) {
                        editingRegion = ((RegionEditEvent)t).getRegionView();
                        bringToTop(editingRegion); // if not, the handlers do not receive drag events when overlapped with other region
                        editingRegion.beginEdit();
                        ((RegionEditEvent)t).getContent().consume();
                        changeState(PageViewState.editingRegion);
                    }
                    break;
                case creatingRegion:
                    if (mouseEvent != null) {
                        if (mouseEvent.getEventType() == MouseEvent.MOUSE_DRAGGED) {
                            resizeNewRegionRectangle(mouseEvent);
                            mouseEvent.consume();
                        } else if (mouseEvent.getEventType() == MouseEvent.MOUSE_RELEASED) {
                            endNewRegionRectangle();
                            mouseEvent.consume();
                            changeState(PageViewState.idleRegion);
                        }
                    }
                    break;
                case editingRegion:
                    if (mouseEvent != null && mouseEvent.isPrimaryButtonDown()) {
                        editingRegion.acceptEdit();
                        changeState(PageViewState.idleRegion);
                        mouseEvent.consume();
                    } else if (keyEvent != null) {
                        if (keyEvent.getCode() == KeyCode.ENTER) {
                            editingRegion.acceptEdit(); //TODO Comando
                            changeState(PageViewState.idleRegion);
                            keyEvent.consume();
                        } else if (keyEvent.getCode() == KeyCode.ESCAPE) {
                            editingRegion.cancelEdit();
                            changeState(PageViewState.idleRegion);
                            keyEvent.consume();
                        } else if (keyEvent.getCode() == KeyCode.DELETE) {
                            omrPage.removeRegion(editingRegion.getOmrRegion());
                            changeState(PageViewState.idleRegion);
                            keyEvent.consume();
                        }
                    }
                    break;
                case idleSymbolRecognition:
                    if (t instanceof SymbolEditEvent) {
                        editingSymbol = ((SymbolEditEvent)t).getSymbolView();
                        RegionView regionView = (RegionView) editingSymbol.getParent();
                        regionView.bringToTop(editingSymbol); // if not, the handlers do not receive drag events when overlapped with other region
                        editingSymbol.beginEdit();
                        ((SymbolEditEvent)t).getContent().consume();
                        changeState(PageViewState.editingSymbol);
                    }
                    break;
                case editingSymbol:
                    if (mouseEvent != null && mouseEvent.isPrimaryButtonDown()) {
                        editingSymbol.acceptEdit();
                        changeState(PageViewState.idleSymbolRecognition);
                        mouseEvent.consume();
                    } else if (keyEvent != null) {
                        if (keyEvent.getCode() == KeyCode.ENTER) {
                            editingSymbol.acceptEdit(); //TODO Comando
                            changeState(PageViewState.idleSymbolRecognition);
                            keyEvent.consume();
                        } else if (keyEvent.getCode() == KeyCode.ESCAPE) {
                            editingSymbol.cancelEdit();
                            changeState(PageViewState.idleSymbolRecognition);
                            keyEvent.consume();
                        } else if (keyEvent.getCode() == KeyCode.DELETE) {
                            editingSymbol.getRegionView().getOmrRegion().removeSymbol(editingSymbol.getOmrSymbol());
                            changeState(PageViewState.idleSymbolRecognition);
                            keyEvent.consume();
                        }
                    }
                    break;
            }
        } catch (IM3Exception e) {
            ShowError.show(OMRApp.getMainStage(), "Cannot handle event " + e, e);
        }
    }

    private void bringToTop(RegionView editingRegion) {
        this.getChildren().remove(editingRegion);
        this.getChildren().add(editingRegion); // put on top
    }

    private void changeState(PageViewState newState) {
        state = newState;
        Logger.getLogger(PageView.class.getName()).log(Level.INFO, "Changing state to {0}", newState);
    }

    private void createNewRegionRectangle(MouseEvent t) {
        selectingRectangle = new SelectionRectangle(t.getX(), t.getY());
    }

    private void resizeNewRegionRectangle(MouseEvent t) {
        if (selectingRectangle.isInFirstClickState()) {
            selectingRectangle.changeState();
            // now we know the user wants a rectangle, it was not a single click
            getChildren().add(selectingRectangle.getRoot());
        }
        selectingRectangle.changeEndPoint(t.getX(), t.getY());
    }


    private void endNewRegionRectangle() throws IM3Exception {
        if (selectingRectangle != null) {
            selectingRectangle.changeState();
            onRegionIdentified(selectingRectangle.getSelectionRectangle().getX(), selectingRectangle.getSelectionRectangle().getY(),
                    selectingRectangle.getSelectionRectangle().getWidth(),
                    selectingRectangle.getSelectionRectangle().getHeight());
            getChildren().remove(selectingRectangle.getRoot());
            selectingRectangle = null;
        }
    }

    private void onRegionIdentified(double fromX, double fromY, double width, double height) {
        ShowChoicesDialog<RegionType> dlg = new ShowChoicesDialog<>();
        RegionType regionType = dlg.show(OMRApp.getMainStage(),"New region added", "Choose the region type", RegionType.values(), RegionType.staff); // default value, staff
        if (regionType != null) {
            omrPage.addRegion(new OMRRegion(fromX, fromY, width, height, regionType));
        }
    }

    private void showRegions(boolean show) {
        for (RegionView regionView: regions.values()) {
            regionView.showRegionBoundingBox(show);
        }
    }

    private void showSymbols(boolean show) {
        for (RegionView regionView: regions.values()) {
            regionView.showSymbols(show);
        }
    }

    private void showMusic(boolean show) {
    }


}
