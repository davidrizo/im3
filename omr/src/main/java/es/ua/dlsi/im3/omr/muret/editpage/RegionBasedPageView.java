package es.ua.dlsi.im3.omr.muret.editpage;

import es.ua.dlsi.im3.omr.muret.Event;
import es.ua.dlsi.im3.omr.muret.OMRApp;
import es.ua.dlsi.im3.omr.muret.model.OMRPage;
import es.ua.dlsi.im3.omr.muret.model.OMRRegion;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.collections.ListChangeListener;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Contains a page view with a list of identified regions on top of an image. When the user interacts with the region
 * the interaction is passed as an abstract method to the children of this class
 * @param <PageBasedControllerType>
 * @param <RegionViewType>
 * @param <StateType>
 */
public abstract class RegionBasedPageView<PageBasedControllerType extends PageBasedController, RegionViewType extends RegionBaseView, StateType> extends Group {
    protected OMRPage omrPage;
    PageBasedControllerType pageController;
    ImageView imageView;

    HashMap<OMRRegion, RegionViewType> regions;

    protected StateType state;
    /**
     * Region being edited
     */
    private RegionViewType editingRegion;

    public RegionBasedPageView(OMRPage omrPage, PageBasedControllerType pageController, ReadOnlyDoubleProperty widthProperty) {
        this.setFocusTraversable(true); // to receive key events
        initStateMachine();
        regions = new HashMap<>();
        this.omrPage = omrPage;
        this.pageController = pageController;
        imageView = new ImageView();
        //imageView.fitWidthProperty().bind(widthProperty); // it provokes a zoom when adding a rectangle
        imageView.setPreserveRatio(true);
        imageView.setImage(SwingFXUtils.toFXImage(omrPage.getBufferedImage(), null));
        this.getChildren().add(imageView);
        initInteraction();
        loadRegions();
        initRegionBinding();
    }

    protected abstract void initStateMachine();

    public OMRPage getOmrPage() {
        return omrPage;
    }

    private void loadRegions() {
        for (OMRRegion omrRegion : omrPage.getRegionList()) {
            createAndAddRegionView(omrRegion);
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
                            createAndAddRegionView(additem);
                        }
                    }
                }
            }
        });
    }

    private void removeRegionView(OMRRegion remitem) {
        RegionViewType regionView = regions.remove(remitem);
        /*if (regionView == null) {
            throw new IM3RuntimeException("Item " + remitem + " not found");
        }*/
        this.getChildren().remove(regionView);
    }

    public abstract RegionViewType createRegionView(OMRRegion region);

    public void createAndAddRegionView(OMRRegion region) {
        //SymbolsRegionView regionView = new SymbolsRegionView(this, region);
        RegionViewType regionView = createRegionView(region);
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

    public abstract void handleEvent(Event t);

    protected void bringToTop(RegionViewType editingRegion) {
        this.getChildren().remove(editingRegion);
        this.getChildren().add(editingRegion); // put on top
    }

    protected void changeState(StateType newState) {
        state = newState;
        Logger.getLogger(RegionBasedPageView.class.getName()).log(Level.INFO, "Changing state to {0}", newState);
    }

}
