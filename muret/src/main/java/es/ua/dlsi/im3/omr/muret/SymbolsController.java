package es.ua.dlsi.im3.omr.muret;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.gui.interaction.ISelectable;
import es.ua.dlsi.im3.gui.javafx.BackgroundProcesses;
import es.ua.dlsi.im3.gui.javafx.dialogs.ShowError;
import es.ua.dlsi.im3.omr.classifiers.endtoend.AgnosticSequenceRecognizer;
import es.ua.dlsi.im3.omr.classifiers.endtoend.HorizontallyPositionedSymbol;
import es.ua.dlsi.im3.omr.muret.model.*;
import javafx.application.Platform;
import javafx.beans.binding.DoubleBinding;
import javafx.collections.FXCollections;
import javafx.collections.SetChangeListener;
import javafx.collections.transformation.SortedList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToolBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.transform.Scale;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @autor drizo
 */
public class SymbolsController extends MuRETBaseController {
    private static final Color REGION_COLOR = Color.RED; //TODO
    private static final Color SYMBOL_COLOR = Color.GREEN; //TODO

    @FXML
    ScrollPane scrollPaneSelectedStaff;
    @FXML
    VBox vboxSelectedStaff;
    @FXML
    Pane agnosticStaffViewPane;
    @FXML
    Pane selectedStaffPane;
    @FXML
    ImageView selectedStaffImageView;
    @FXML
    Group symbolViewsGroup;

    /**
     * Full omrImage
     */
    @FXML
    ImageView imageView;
    @FXML
    AnchorPane anchorPaneImageView;
    @FXML
    Pane resizedImagePane;
    @FXML
    ToolBar toolbarToolSpecific;

    AgnosticStaffView agnosticStaffView;
    /**
     * Use this one to browse
     */
    SortedList<RegionView> regionViews;
    /**
     * But add to the underlying list
     */
    LinkedList<RegionView> regionViews_data;

    private RegionView selectedRegionView;
    /**
     * Use this one to browse
     */
    SortedList<SymbolView> symbolViews;
    /**
     * But add to the underlying list
     */
    LinkedList<SymbolView> symbolViews_data;

    HashMap<OMRSymbol, SymbolView> omrSymbolSymbolViewHashMap;



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        vboxSelectedStaff.minHeightProperty().bind(selectedStaffPane.minHeightProperty().add(agnosticStaffViewPane.minHeightProperty()));
        vboxSelectedStaff.prefHeightProperty().bind(vboxSelectedStaff.minHeightProperty());
        scrollPaneSelectedStaff.minHeightProperty().bind(vboxSelectedStaff.minHeightProperty());
        scrollPaneSelectedStaff.prefHeightProperty().bind(scrollPaneSelectedStaff.minHeightProperty());
        selectedStaffPane.minHeightProperty().bind(selectedStaffImageView.fitHeightProperty());
        selectedStaffPane.prefHeightProperty().bind(selectedStaffPane.minHeightProperty());

        vboxSelectedStaff.minWidthProperty().bind(selectedStaffPane.minWidthProperty().add(agnosticStaffViewPane.minWidthProperty()));
        vboxSelectedStaff.prefWidthProperty().bind(vboxSelectedStaff.minWidthProperty());
        //scrollPaneSelectedStaff.minWidthProperty().bind(vboxSelectedStaff.minWidthProperty());
        //scrollPaneSelectedStaff.prefWidthProperty().bind(scrollPaneSelectedStaff.minWidthProperty());
        selectedStaffPane.minWidthProperty().bind(selectedStaffImageView.fitWidthProperty());
        selectedStaffPane.prefWidthProperty().bind(selectedStaffPane.minWidthProperty());

    }

    public void loadOMRImage(OMRImage omrImage) throws IM3Exception {
        this.omrImage = omrImage;
        imageView.setImage(omrImage.getImage());
        imageView.setFitHeight(omrImage.getImage().getHeight());
        imageView.setFitWidth(omrImage.getImage().getWidth());
        imageView.setPreserveRatio(true);
        regionViews_data = new LinkedList<>();
        regionViews = new SortedList<>(FXCollections.observableList(regionViews_data), new Comparator<RegionView>() {
            @Override
            public int compare(RegionView o1, RegionView o2) {
                return o1.owner.compareTo(o2.owner);
            }
        });

        DoubleBinding scaleX = anchorPaneImageView.widthProperty().divide(omrImage.getImage().widthProperty());
        DoubleBinding scaleY = anchorPaneImageView.heightProperty().divide(omrImage.getImage().heightProperty());

        Scale scaleTransformation = new Scale();
        scaleTransformation.xProperty().bind(scaleX);
        scaleTransformation.yProperty().bind(scaleY);
        scaleTransformation.pivotXProperty().bind(resizedImagePane.layoutXProperty());
        scaleTransformation.pivotYProperty().bind(resizedImagePane.layoutYProperty());
        resizedImagePane.getTransforms().add(scaleTransformation);


        for (OMRPage page : omrImage.getPages()) {
            for (OMRRegion region : page.regionsProperty()) {
                RegionView regionView = new RegionView("Region" + region.hashCode(), this, null, region, REGION_COLOR);
                regionViews_data.add(regionView);
                resizedImagePane.getChildren().add(regionView);
                //TODO Que cuando se seleccione que se cambie lo de arriba y se muestre seleccionado
            }
        }
    }

    @Override
    protected void bindZoom(Scale scaleTransformation) {

    }

    @Override
    protected double computeZoomToFitRatio() {
        return 0;
    }

    @Override
    public <OwnerType extends IOMRBoundingBox> void doSelect(BoundingBoxBasedView<OwnerType> ownerTypeBoundingBoxBasedView) {
        if (ownerTypeBoundingBoxBasedView instanceof RegionView) {
            agnosticStaffView = new AgnosticStaffView(this,
                    MuRET.getInstance().getAgnosticSymbolFonts().getAgnosticSymbolFont(MuRET.getInstance().getModel().getCurrentProject().getNotationType()),
                    scrollPaneSelectedStaff.widthProperty(), 300, 10); //TODO height
            agnosticStaffViewPane.getChildren().setAll(agnosticStaffView);
            agnosticStaffViewPane.setPrefHeight(300);
            agnosticStaffViewPane.setMinHeight(300);

            RegionView regionView = (RegionView) ownerTypeBoundingBoxBasedView;
            OMRRegion omrRegion = regionView.getOwner();

            try {
                this.selectedRegionView = regionView;
                selectedStaffImageView.setImage(omrImage.getImage());
                selectedStaffImageView.setViewport(new Rectangle2D(omrRegion.getFromX(), omrRegion.getFromY(), omrRegion.getWidth(), omrRegion.getHeight()));

                selectedStaffImageView.setFitHeight(omrRegion.getHeight());
                selectedStaffImageView.setFitWidth(omrRegion.getWidth());

                symbolViewsGroup.getChildren().clear();

                loadSelectedRegionSymbols();
            } catch (IM3Exception e) {
                Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Cannot select region", e);
                ShowError.show(MuRET.getInstance().getMainStage(), "Cannot select region", e);
            }
        } else {
            //TODO Symbol selected
        }
    }

    private void loadSelectedRegionSymbols() {
        symbolViewsGroup.getChildren().clear();//TODO Asociarlo con data
        omrSymbolSymbolViewHashMap = new HashMap<>();
        symbolViews_data = new LinkedList<>();
        symbolViews = new SortedList<>(FXCollections.observableList(symbolViews_data), new Comparator<SymbolView>() {
            @Override
            public int compare(SymbolView o1, SymbolView o2) {
                return o1.owner.compareTo(o2.owner);
            }
        });

        // first load already existing symbols
        for (OMRSymbol omrSymbol: selectedRegionView.getOwner().symbolsProperty()) {
            addSymbol(omrSymbol);
        }

        // then add a listener to update the view when the model changes
        selectedRegionView.getOwner().symbolsProperty().addListener(new SetChangeListener<OMRSymbol>() {
            @Override
            public void onChanged(Change<? extends OMRSymbol> change) {
                Logger.getLogger(SymbolsController.class.getName()).log(Level.INFO, "Selected region symbols changed {0}", change);
                //TODO Inserción
                if (change.wasRemoved()) {
                    OMRSymbol removedElement = change.getElementRemoved();
                } else if (change.wasAdded()) {
                    addSymbol(change.getElementAdded());
                }
            }
        });
    }

    private void addSymbol(OMRSymbol omrSymbol) {
        SymbolView symbolView = new SymbolView("Symbol" + omrSymbol.hashCode(), this, selectedRegionView, omrSymbol, SYMBOL_COLOR);
        omrSymbolSymbolViewHashMap.put(omrSymbol, symbolView);
        symbolViews_data.add(symbolView);
        symbolViewsGroup.getChildren().add(symbolView); //TODO enlazar group con data

        try {
            agnosticStaffView.addSymbol(symbolView);
        } catch (IM3Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Cannot a symbol to the agnostic staff", e);
            ShowError.show(MuRET.getInstance().getMainStage(), "Cannot a symbol to the agnostic staff", e);
        }
    }

    @FXML
    public void handleRecognitionTool() {
        toolbarToolSpecific.getItems().clear();

        Button staffEndToEndRecognition = new Button("Staff end to end");
        staffEndToEndRecognition.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (selectedRegionView != null) {
                    recognizeStaffEndToEnd(selectedRegionView.getOwner());
                }
            }
        });
        toolbarToolSpecific.getItems().add(staffEndToEndRecognition);
    }

    private void recognizeStaffEndToEnd(OMRRegion omrRegion) {
        Callable<Void> process = new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                doRecognizeStaffEndToEnd(omrRegion);
                return null;
            }
        };

        new BackgroundProcesses().launch(this.resizedImagePane.getScene().getWindow(), "Recognizing symbol sequences in selected staff", "Recognition finished", "Cannot recognize symbols", process);
    }

    private void doRecognizeStaffEndToEnd(OMRRegion omrRegion) throws IM3Exception, IOException {
        AgnosticSequenceRecognizer agnosticSequenceRecognizer = MuRET.getInstance().getModel().getClassifiers().getEndToEndAgnosticSequenceRecognizerInstance();

        File tmpFile = File.createTempFile("staff" + omrRegion.hashCode(), "jpg");
        //TODO A modelo
        BufferedImage bImage = SwingFXUtils.fromFXImage(omrImage.getImage(), null).getSubimage(
                (int) omrRegion.getFromX(), (int) omrRegion.getFromY(),
                (int) omrRegion.getWidth(), (int) omrRegion.getHeight());

        ImageIO.write(bImage, "jpg", tmpFile);

        List<HorizontallyPositionedSymbol> symbolList = agnosticSequenceRecognizer.recognize(tmpFile);
        Platform.runLater(
                new Runnable() {
                    @Override
                    public void run() {
                        for (HorizontallyPositionedSymbol horizontallyPositionedSymbol : symbolList) {
                            try {
                                OMRSymbol omrSymbol = new OMRSymbol(omrRegion, horizontallyPositionedSymbol.getAgnosticSymbol(), horizontallyPositionedSymbol.getFromX(), 0, horizontallyPositionedSymbol.getToX() - horizontallyPositionedSymbol.getFromX(), omrRegion.getHeight());
                                omrRegion.addSymbol(omrSymbol);
                            } catch (IM3Exception e) {
                                Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Cannot add a new symbol", e);
                                ShowError.show(MuRET.getInstance().getMainStage(), "Cannot add a new symbol", e);
                            }

                        }
                    }
                });
    }


    @Override
    public void unselect() {

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

    public void onSymbolChanged(OMRSymbol owner) throws IM3Exception {
        throw new IM3Exception("TO-DO");
    }
}
