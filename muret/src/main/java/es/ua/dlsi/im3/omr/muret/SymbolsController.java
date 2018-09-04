package es.ua.dlsi.im3.omr.muret;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.gui.interaction.ISelectable;
import es.ua.dlsi.im3.gui.javafx.BackgroundProcesses;
import es.ua.dlsi.im3.gui.javafx.dialogs.ShowError;
import es.ua.dlsi.im3.omr.classifiers.endtoend.AgnosticSequenceRecognizer;
import es.ua.dlsi.im3.omr.classifiers.endtoend.HorizontallyPositionedSymbol;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbol;
import es.ua.dlsi.im3.omr.muret.model.*;
import javafx.application.Platform;
import javafx.beans.binding.DoubleBinding;
import javafx.collections.FXCollections;
import javafx.collections.transformation.SortedList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToolBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
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

    @FXML
    ScrollPane scrollPaneSelectedStaff;
    @FXML
    BorderPane borderPaneSelectedStaff;
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        agnosticStaffView = new AgnosticStaffView(this,
                MuRET.getInstance().getAgnosticSymbolFonts().getAgnosticSymbolFont(MuRET.getInstance().getModel().getCurrentProject().getNotationType()),
                borderPaneSelectedStaff.widthProperty(), 300, 10); //TODO height
        borderPaneSelectedStaff.setCenter(agnosticStaffView);
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
        RegionView regionView = (RegionView) ownerTypeBoundingBoxBasedView;
        try {
            this.selectedRegionView = regionView;
            ImageView selectedStaffView = new ImageView(omrImage.getImage());
            OMRRegion omrRegion = regionView.getOwner();
            selectedStaffView.setViewport(new Rectangle2D(omrRegion.getFromX(), omrRegion.getFromY(), omrRegion.getWidth(), omrRegion.getHeight()));
            borderPaneSelectedStaff.setTop(selectedStaffView);
            scrollPaneSelectedStaff.setPrefHeight(omrRegion.getHeight() + agnosticStaffView.getHeight());
        } catch (IM3Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Cannot select region", e);
            ShowError.show(MuRET.getInstance().getMainStage(), "Cannot select region", e);
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
                                e.printStackTrace();
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
    }
}
