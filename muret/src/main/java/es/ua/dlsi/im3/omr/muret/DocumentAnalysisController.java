package es.ua.dlsi.im3.omr.muret;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.gui.interaction.ISelectable;
import es.ua.dlsi.im3.gui.interaction.SelectionManager;
import es.ua.dlsi.im3.gui.javafx.collections.ObservableListViewListModelLink;
import es.ua.dlsi.im3.gui.javafx.collections.ObservableListViewSetModelLink;
import es.ua.dlsi.im3.gui.javafx.dialogs.FXMLDialog;
import es.ua.dlsi.im3.gui.javafx.dialogs.ShowError;
import es.ua.dlsi.im3.omr.muret.model.*;
import es.ua.dlsi.im3.omr.muret.old.OMRApp;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.SetChangeListener;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;

import java.net.URL;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @autor drizo
 */
public class DocumentAnalysisController extends MuRETBaseController {
    private static final Color PAGE_COLOR = Color.BLUE; //TODO
    private static final Color REGION_COLOR = Color.RED;

    @FXML
    BorderPane rootBorderPane;
    @FXML
    ScrollPane scrollPane;

    @FXML
    AnchorPane imagePane;

    @FXML
    Text textFileName;

    @FXML
    ToggleGroup toolToggle;

    @FXML
    ToggleButton toggleManual;

    @FXML
    ToggleButton toggleAutomatic;

    @FXML
    ToolBar toolbarToolSpecific;

    ImageView imageView;

    ObservableListViewSetModelLink<OMRPage, PageContents> pageViews;

    Group pageViewsGroup;

    SelectionManager selectionManager;

    enum InteractionMode {eIdle, eSplittingPages, eSplittingRegions, eDrawingPages, eDrawingRegions};

    InteractionMode interactionMode;

    class PageContents implements Comparable<PageContents> {
        OMRPage omrPage;
        PageView pageView;
        Group regionViewsGroup;
        ObservableListViewSetModelLink<OMRRegion, RegionView> regions;

        public PageContents(OMRPage page) {
            this.omrPage = page;
            this.pageView = new PageView("Page" + page.hashCode(), DocumentAnalysisController.this, page, PAGE_COLOR);
            regionViewsGroup = new Group();

            regions = new ObservableListViewSetModelLink<OMRRegion, RegionView>(page.regionsProperty(), new Function<OMRRegion, RegionView>() {
                @Override
                public RegionView apply(OMRRegion omrRegion) {
                    return new RegionView("Region" + omrRegion.hashCode(), DocumentAnalysisController.this, pageView, omrRegion, REGION_COLOR);
                }
            });

            regionViewsGroup.getChildren().setAll(regions.getViews());
            regions.getViews().addListener(new ListChangeListener<RegionView>() {
                @Override
                public void onChanged(Change<? extends RegionView> c) {
                    while (c.next()) {
                        if (c.wasRemoved()) {
                            for (RegionView regionView: c.getRemoved()) {
                                regionViewsGroup.getChildren().remove(regionView);
                            }
                        } else if (c.wasAdded()) {
                            for (RegionView regionView: c.getAddedSubList()) {
                                regionViewsGroup.getChildren().add(regionView);
                            }
                        }
                    }
                }
            });
        }

        @Override
        public int compareTo(PageContents o) {
            return omrPage.compareTo(o.omrPage);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        imagePane.prefWidthProperty().bind(scrollPane.widthProperty());
        selectionManager = new SelectionManager();
        interactionMode = InteractionMode.eIdle;
        initTools();
    }

    private void initTools() {
        toolToggle.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                toolbarToolSpecific.getItems().clear();
                changeCursor(Cursor.DEFAULT);
                if (newValue == toggleAutomatic) {
                    createAutomaticRecognitionTools();
                } else if (newValue == toggleManual) {
                    createManualEditingTools();
                }
            }
        });

        toggleManual.setSelected(true);
    }

    private void changeCursor(Cursor cursor) {
        Scene scene  = this.imagePane.getScene();
        if (scene != null) {
            scene.setCursor(cursor);
        }
    }

    private void createAutomaticRecognitionTools() {
        Button recognizePages = new Button("Pages");
        toolbarToolSpecific.getItems().add(recognizePages);

        Button recognizeRegions = new Button("Regions in pages");
        toolbarToolSpecific.getItems().add(recognizeRegions);
    }

    private void createManualEditingTools() {
        ToggleGroup toolSpecificToggle = new ToggleGroup();

        Button clear = new Button("Clear");
        toolbarToolSpecific.getItems().add(clear);
        clear.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                toolSpecificToggle.selectToggle(null);
                doClear();
            }
        });

        ToggleButton splitPages = new ToggleButton("Split pages");
        splitPages.setToggleGroup(toolSpecificToggle);
        toolbarToolSpecific.getItems().add(splitPages);

        ToggleButton splitRegions = new ToggleButton("Split regions");
        toolbarToolSpecific.getItems().add(splitRegions);
        splitRegions.setToggleGroup(toolSpecificToggle);

        ToggleButton drawPages = new ToggleButton("Draw pages");
        toolbarToolSpecific.getItems().add(drawPages);
        drawPages.setToggleGroup(toolSpecificToggle);

        ToggleButton drawRegions = new ToggleButton("Draw regions");
        toolbarToolSpecific.getItems().add(drawRegions);
        drawRegions.setToggleGroup(toolSpecificToggle);

        toolSpecificToggle.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                if (newValue == null) {
                    changeCursor(Cursor.DEFAULT);
                    interactionMode = InteractionMode.eIdle;
                } else if (newValue == splitPages) {
                    interactionMode = InteractionMode.eSplittingPages;
                    changeCursor(Cursor.E_RESIZE);
                } else if (newValue == splitRegions) {
                    interactionMode = InteractionMode.eSplittingRegions;
                    changeCursor(Cursor.S_RESIZE);
                } else if (newValue == drawPages) {
                    interactionMode = InteractionMode.eDrawingPages;
                    changeCursor(Cursor.CROSSHAIR);
                } else if (newValue == drawRegions) {
                    interactionMode = InteractionMode.eDrawingRegions;
                    changeCursor(Cursor.CROSSHAIR);
                }
            }
        });
    }

    private void doClear() {
        omrImage.getPages().clear();
    }

    @Override
    protected void bindZoom(Scale scaleTransformation) {
        scaleTransformation.pivotXProperty().bind(imagePane.layoutXProperty());
        scaleTransformation.pivotYProperty().bind(imagePane.layoutYProperty());
        imagePane.getTransforms().add(scaleTransformation);
    }

    public void loadOMRImage(OMRImage omrImage) throws IM3Exception {
        this.omrImage = omrImage;
        textFileName.setText(omrImage.getImageFile().getAbsolutePath());
        imageView = new ImageView(omrImage.getImage());
        imageView.setPreserveRatio(true);
        imagePane.setMinWidth(imageView.getImage().getWidth());
        imagePane.setMinHeight(imageView.getImage().getHeight());
        imagePane.getChildren().add(imageView);

        handleZoomToFit();
        loadData();
    }

    private void loadData() {
        pageViews = new ObservableListViewSetModelLink<OMRPage, PageContents>(omrImage.getPages(), new Function<OMRPage, PageContents>() {
            @Override
            public PageContents apply(OMRPage omrPage) {
                return new PageContents(omrPage);
            }
        });

        pageViewsGroup = new Group();
        imagePane.getChildren().add(pageViewsGroup);
        for (PageContents pageContents: pageViews.getViews()) {
            pageViewsGroup.getChildren().add(pageContents.pageView);
            pageViewsGroup.getChildren().add(pageContents.regionViewsGroup);

        }

        pageViews.getViews().addListener(new ListChangeListener<PageContents>() {
            @Override
            public void onChanged(Change<? extends PageContents> c) {
                while (c.next()) {
                    if (c.wasRemoved()) {
                        for (PageContents pageContents: c.getRemoved()) {
                            pageViewsGroup.getChildren().remove(pageContents.pageView);
                            pageViewsGroup.getChildren().remove(pageContents.regionViewsGroup);
                        }
                    } else if (c.wasAdded()) {
                        for (PageContents pageContents: c.getAddedSubList()) {
                            pageViewsGroup.getChildren().add(pageContents.pageView);
                            pageViewsGroup.getChildren().add(pageContents.regionViewsGroup);
                        }
                    }
                }
            }
        });

    }

    //TODO - PONER ESTE CÓDIGO EN OTRO SITIO
    /*private void fillInstrumentLabels() {
        hboxInstruments.getChildren().clear();

        if (omrImage.getInstrumentList() == null ||omrImage.getInstrumentList().isEmpty()) {
            addInstrumentHyperlink("Set instrument...");
        } else {
            for (OMRInstrument omrInstrument: omrImage.getInstrumentList()) {
                addInstrumentHyperlink(omrInstrument.getName());
            }
        }

    }

    private void addInstrumentHyperlink(String label) {
        Hyperlink addInstrumentHyperlink = new Hyperlink(label);
        addInstrumentHyperlink.getStyleClass().add("orderImagesInstruments");
        hboxInstruments.getChildren().add(addInstrumentHyperlink);

        addInstrumentHyperlink.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                openInstrumentsDialog();
            }
        });
    }

    //TODO Lista instrumentos enlazada MVC
    private void openInstrumentsDialog() {
        InstrumentsController instrumentsController = null;
        FXMLDialog fxmlDialog = null;
        try {
            fxmlDialog = new FXMLDialog(this.hboxInstruments.getScene().getWindow(), "Instruments", "/fxml/muret/instruments.fxml");
            instrumentsController = (InstrumentsController) fxmlDialog.getController();
            instrumentsController.loadInstruments(MuRET.getInstance().getModel().getCurrentProject().getInstruments().getInstrumentSet(), omrImage.getInstrumentList());
        } catch (IM3Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Cannot show the instruments dialog", e);
            ShowError.show(OMRApp.getMainStage(), "Cannot show the instruments dialog", e);
        }

        if (fxmlDialog != null) {
            if (fxmlDialog.show()) {
                omrImage.getInstrumentList().clear();

                for (OMRInstrument omrInstrument: instrumentsController.getInstruments()) {
                    omrImage.getInstrumentList().add(omrInstrument);
                    MuRET.getInstance().getModel().getCurrentProject().getInstruments().addInstrument(omrInstrument);
                }
                fillInstrumentLabels();
            }
        }
    }*/

    @Override
    protected double computeZoomToFitRatio() {
        double xRatio = this.scrollPane.getViewportBounds().getWidth() / this.imageView.getLayoutBounds().getWidth();
        double yRatio = this.scrollPane.getViewportBounds().getHeight() / this.imageView.getLayoutBounds().getHeight();
        if (xRatio > yRatio) {
            return xRatio;
        } else {
            return yRatio;
        }
    }

    @Override
    public <OwnerType extends IOMRBoundingBox> void doSelect(BoundingBoxBasedView<OwnerType> ownerTypeBoundingBoxBasedView) {
        selectionManager.select(ownerTypeBoundingBoxBasedView);
    }

    @Override
    public void unselect() {
        selectionManager.clearSelection();
    }

    @Override
    public ISelectable first() {
        return null;
    }

    @Override
    public ISelectable last() {
        return null;
    }

    @Override
    public ISelectable previous(ISelectable s) {
        return null;
    }

    @Override
    public ISelectable next(ISelectable s) {
        return null;
    }
}
