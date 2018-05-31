package es.ua.dlsi.im3.omr.muret.symbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IProgressObserver;
import es.ua.dlsi.im3.core.utils.ImageUtils;
import es.ua.dlsi.im3.gui.javafx.dialogs.*;
import es.ua.dlsi.im3.omr.classifiers.endtoend.AgnosticSequenceRecognizer;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbol;
import es.ua.dlsi.im3.omr.model.entities.Symbol;
import es.ua.dlsi.im3.omr.muret.ImageBasedAbstractController;
import es.ua.dlsi.im3.omr.muret.OMRApp;
import es.ua.dlsi.im3.omr.muret.model.OMRImage;
import es.ua.dlsi.im3.omr.muret.model.OMRPage;
import es.ua.dlsi.im3.omr.muret.model.OMRRegion;
import es.ua.dlsi.im3.omr.muret.model.OMRSymbol;
import es.ua.dlsi.im3.omr.muret.BoundingBoxBasedView;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @autor drizo
 */
public class SymbolCorrectionController extends ImageBasedAbstractController {
    @FXML
    BorderPane mainBorderPane;
    @FXML
    VBox regionsPane;
    @FXML
    Button btnChangeSymbol;
    @FXML
    ToggleButton toggleButtonEditMusic;

    AgnosticSymbolFont agnosticSymbolFont;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        regionsPane.prefHeightProperty().bind(mainPane.heightProperty());
        regionsPane.prefWidthProperty().bind(mainPane.widthProperty());
        btnChangeSymbol.disableProperty().bind(selectedSymbol.isNull());
        //initAddSymbolInteraction();
    }

    @Override
    public void setOMRImage(OMRImage omrImage) throws IM3Exception {
        agnosticSymbolFont = new AgnosticSymbolFonts().getAgnosticSymbolFont(omrImage.getOmrProject().getNotationType());
        super.setOMRImage(omrImage);
    }

    @Override
    protected BoundingBoxBasedView addSymbol(BoundingBoxBasedView regionView, OMRSymbol omrSymbol) throws IM3Exception {
        RegionView regionViewCast = (RegionView) regionView;
        SymbolView symbolView = new SymbolView(this, regionViewCast, omrSymbol, Color.DARKGREEN); //TODO
        regionViewCast.addSymbolView(symbolView);
        return symbolView;
    }
    @Override
    protected BoundingBoxBasedView addRegion(BoundingBoxBasedView pageView, OMRRegion omrRegion) throws IM3Exception {
        RegionView regionView = new RegionView(this, agnosticSymbolFont, (PageView) pageView, omrRegion, Color.RED); //TODO;
        regionsPane.getChildren().add(regionView);

        return regionView;
    }

    @Override
    protected BoundingBoxBasedView addPage(OMRPage omrPage) {
        return new PageView(this, omrPage, Color.BLUE); //TODO
    }

    @Override
    protected void doDeleteTreeItems() throws IM3Exception {
        if (selectedSymbol.get() instanceof SymbolView) {
            SymbolView symbolView = (SymbolView) selectedSymbol.get();
            symbolView.getOwner().getOMRRegion().removeSymbol(symbolView.getOwner());
        }
    }

    @Override
    protected double getZoomToFitRatio() {
        return 0;
    }

    @FXML
    private void handleGotoDocumentAnalysis() {
        this.dashboard.openImageDocumentAnalysis(omrImage);
    }

    @FXML
    private void handleRecognizeSymbols() {
        //TODO Barra progreso
        try {
            for (BoundingBoxBasedView selectedElement: selectedElements) {
                if ((selectedElement instanceof RegionView)) {
                    recognizeSymbolsInRegionDialog((RegionView) selectedElement);
                }
            }
        } catch (Exception e) {
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Cannot recognize symbols", e);
                ShowError.show(OMRApp.getMainStage(), "Cannot recognize regions", e);
        }
    }

    private void doRecognizeSymbolsInRegion(RegionView regionView) throws IM3Exception {
        try {
            //TODO A modelo
            BufferedImage bImage = SwingFXUtils.fromFXImage(regionView.getImageView().getImage(), null).getSubimage(
                    (int) regionView.getOwner().getFromX(), (int) regionView.getOwner().getFromY(),
                    (int) regionView.getOwner().getWidth(), (int) regionView.getOwner().getHeight());

            File file = File.createTempFile("region_" + regionView.getOwner().hashCode(), ".jpg");
            ImageIO.write(bImage, "jpg", file);
            AgnosticSequenceRecognizer agnosticSequenceRecognizer = new AgnosticSequenceRecognizer();
            List<AgnosticSymbol> symbolList = agnosticSequenceRecognizer.recognize(file);
            double currentX = 0; //TODO Ahora el reconocedor no me da x, los voy poniendo yo a ojo
            for (AgnosticSymbol agnosticSymbol : symbolList) {
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Adding symbol {0}", agnosticSymbol.getAgnosticString());
                //TODO Que no haya que poner bounding boxes
                OMRSymbol omrSymbol = new OMRSymbol(regionView.getOwner(), agnosticSymbol, currentX, 0, 25, 30);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        regionView.getOwner().addSymbol(omrSymbol);
                    }
                });
                currentX += 30;
            }
        } catch (IOException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Error recognizing symbols", e);
            throw new IM3Exception(e);
        }
    }
    private void recognizeSymbolsInRegionDialog(RegionView regionView) throws IOException, IM3Exception {
        //TODO Ver este dálogo
        WorkIndicatorDialog workIndicatorDialog = new WorkIndicatorDialog(OMRApp.getMainStage().getOwner(), "Recognizing symbol sequences in selected regions");

        workIndicatorDialog.addTaskEndNotification(result -> {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    String resultMessage = "Result: " + result;
                    ShowMessage.show(OMRApp.getMainStage(), resultMessage);
                }
            });
        });


        workIndicatorDialog.exec("", inputParam -> {
            try {
                doRecognizeSymbolsInRegion(regionView);
            } catch (IM3Exception e) {
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Error recognizing symbols", e);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        ShowError.show(OMRApp.getMainStage(), "Cannot recognize symbols", e);
                    }
                });
            }
            return new Integer(1);
        });

    }

    @FXML
    private void handleChangeSymbol() {
        doChangeSymbol();
    }

    public void doChangeSymbol() {
        ((SymbolView)selectedSymbol.get()).doEdit();
    }


    /*private void initAddSymbolInteraction() {
        addingSymbol = new SimpleBooleanProperty(false);
        toggleBtnAddSymbol.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                addingSymbol.setValue(newValue);
            }
        });
    }

    public boolean isAddingSymbol() {
        return addingSymbol.get();
    }

    public BooleanProperty addingSymbolProperty() {
        return addingSymbol;
    }*/

    public void setSymbolCorrectionToolbar(Node toolbar) {
        mainBorderPane.setBottom(toolbar);
    }

    public void removeSymbolCorrectionToolbar() {
        mainBorderPane.setBottom(null);
    }

    public BooleanProperty editMusicEnabledProperty() {
        return toggleButtonEditMusic.selectedProperty();
    }

}
