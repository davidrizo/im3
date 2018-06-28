package es.ua.dlsi.im3.omr.muret.symbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.gui.interaction.ISelectable;
import es.ua.dlsi.im3.gui.interaction.ISelectionChangeListener;
import es.ua.dlsi.im3.gui.javafx.dialogs.*;
import es.ua.dlsi.im3.omr.classifiers.endtoend.AgnosticSequenceRecognizer;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbol;
import es.ua.dlsi.im3.omr.muret.ImageBasedAbstractController;
import es.ua.dlsi.im3.omr.muret.OMRApp;
import es.ua.dlsi.im3.omr.muret.model.*;
import es.ua.dlsi.im3.omr.muret.BoundingBoxBasedView;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Orientation;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import org.kordamp.ikonli.javafx.FontIcon;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.LinkedList;
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
    /*@FXML
    Button btnChangeSymbol;*/
    @FXML
    ToggleButton toggleButtonEditMusic;

    BooleanProperty agnosticMode;

    ObjectProperty<SymbolView> selectedSymbolView;

    AgnosticSymbolFont agnosticSymbolFont;

    private FlowPane agnosticCorrectionPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        regionsPane.prefHeightProperty().bind(mainPane.heightProperty());
        regionsPane.prefWidthProperty().bind(mainPane.widthProperty());

        agnosticMode = new SimpleBooleanProperty(true);
        selectedSymbolView = new SimpleObjectProperty<>();
        initSymbolSelection();
    }

    private void initCorrectionPanes() {
        try {
            createAgnosticCorrectionPane();
            // set is by default
            setAgnosticMode(true);
        } catch (IM3Exception e) {
            e.printStackTrace();
            ShowError.show(OMRApp.getMainStage(), "Cannot create agnostic correction pane", e);
        }
    }

    private void initSymbolSelection() {
        /*btnChangeSymbol.setDisable(true);
        selectionManager.subscribe(new ISelectionChangeListener() {
            @Override
            public void onSelectionChange(Collection<ISelectable> selection) {
                try {
                    if (selectionManager.getSelection().size() == 1 && selectionManager.getSingleElementSelected() instanceof SymbolView) {
                        selectedSymbolView = (SymbolView) selectionManager.getSingleElementSelected();
                        btnChangeSymbol.setDisable(false);
                    } else {
                        btnChangeSymbol.setDisable(true);
                    }
                } catch (IM3Exception e) {
                    ShowError.show(OMRApp.getMainStage(), "Cannot handle selection", e);
                    btnChangeSymbol.setDisable(true);
                }
            }
        });*/
    }

    @Override
    public void setOMRImage(OMRImage omrImage) throws IM3Exception {
        agnosticSymbolFont = new AgnosticSymbolFonts().getAgnosticSymbolFont(omrImage.getOmrProject().getNotationType());
        super.setOMRImage(omrImage);

        initCorrectionPanes(); // set it here because it needs the agnosticSymbolFont to be set
    }

    @Override
    protected BoundingBoxBasedView addSymbol(BoundingBoxBasedView regionView, OMRSymbol omrSymbol) throws IM3Exception {
        RegionView regionViewCast = (RegionView) regionView;
        SymbolView symbolView = new SymbolView(getNextID("Symbol"), this, regionViewCast, omrSymbol, Color.DARKGREEN); //TODO
        regionViewCast.addSymbolView(symbolView);
        return symbolView;
    }
    @Override
    protected BoundingBoxBasedView addRegion(BoundingBoxBasedView pageView, OMRRegion omrRegion) throws IM3Exception {
        RegionView regionView = new RegionView(getNextID("Region"), this, agnosticSymbolFont, (PageView) pageView, omrRegion, Color.RED); //TODO;
        regionsPane.getChildren().add(regionView);

        return regionView;
    }

    @Override
    protected BoundingBoxBasedView addPage(OMRPage omrPage) {
        return new PageView(getNextID("Page"), this, omrPage, Color.BLUE); //TODO
    }

    @Override
    protected void doDeleteTreeItems() throws IM3Exception {
        if (selectionManager.isCommonBaseClass(SymbolView.class)) {
            for (ISelectable selectable: selectionManager.getSelection()) {
                SymbolView symbolView = (SymbolView) selectable;
                symbolView.getOMRSymbol().getOMRRegion().removeSymbol(symbolView.getOMRSymbol());
            }
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
            if (selectionManager.isCommonBaseClass(RegionView.class)) {
                for (ISelectable selectedElement: selectionManager.getSelection()) {
                    RegionView regionView = (RegionView) selectedElement;
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

    /*@FXML
    private void handleChangeSymbol() {
        doChangeSymbol();
    }*/

    /*public void doChangeSymbol() {
        selectedSymbolView.get().doEdit();
    }*/


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

    /*public void setSymbolCorrectionToolbar(Node toolbar) {
        mainBorderPane.setBottom(toolbar);
    }

    public void removeSymbolCorrectionToolbar() {
        mainBorderPane.setBottom(null);
    }*/

    public BooleanProperty agnosticModeProperty() {
        return agnosticMode;
    }

    @Override
    public <OwnerType extends IOMRBoundingBox> void doSelect(BoundingBoxBasedView<OwnerType> boundingBoxBasedView) {
        /*if (selectedSymbolView != null) {
            selectedSymbolView.get().endEdit(); // if not editing it does not happen anything
        }*/
        super.doSelect(boundingBoxBasedView);
        selectedSymbolView.setValue((SymbolView) boundingBoxBasedView);
    }

    @Override
    public BoundingBoxBasedView doSelect(OMRSymbol omrSymbol) throws IM3Exception {
        /*if (selectedSymbolView != null) {
            selectedSymbolView.get().endEdit(); // if not editing it does not happen anything
        }*/

        return super.doSelect(omrSymbol);
    }

    @Override
    public ISelectable first() {
        return null; //TODO
    }

    @Override
    public ISelectable last() {
        return null;//TODO
    }

    @Override
    public ISelectable previous(ISelectable s) {
        return null;//TODO
    }

    @Override
    public ISelectable next(ISelectable s) {
        return null;//TODO
    }

    /**
     * @param enableAgnosticMode If false it is the semantic (music) mode
     */
    private void setAgnosticMode(boolean enableAgnosticMode) {
        agnosticMode.setValue(enableAgnosticMode);
        if (enableAgnosticMode) {
            mainBorderPane.setBottom(agnosticCorrectionPane);
        } else {
            mainBorderPane.setBottom(null);
        }
    }

    @FXML
    private void handleEditMusic() {
        setAgnosticMode(!toggleButtonEditMusic.isSelected());
    }

    public BooleanProperty agnosticCorrectionPaneDisableProperty() {
        return agnosticCorrectionPane.disableProperty();
    }

    /**
     * We do not create the same for all staves because the order of symbols may change
     */
    private void createAgnosticCorrectionPane() throws IM3Exception {
        agnosticCorrectionPane = new FlowPane();
        agnosticCorrectionPane.disableProperty().bind(selectedSymbolView.isNull());
        agnosticCorrectionPane.prefWrapLengthProperty().bind(mainBorderPane.widthProperty());
        agnosticCorrectionPane.setOrientation(Orientation.HORIZONTAL);
        agnosticCorrectionPane.setRowValignment(VPos.CENTER);
        agnosticCorrectionPane.setColumnHalignment(HPos.CENTER);

        List<String> agnosticStrings =  new LinkedList<>(agnosticSymbolFont.getGlyphs().keySet());

        //TODO Diseñar la usabilidad de todo esto
        /*Button buttonAccept = new Button("Accepto correction\n(ENTER)", new FontIcon("oi-check"));
        agnosticCorrectionPane.getChildren().add(buttonAccept);
        buttonAccept.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                selectedSymbolView.get().acceptCorrection();
                //doEndEdit();
            }
        });

        //Button buttonClose = new Button("Cancel correction\n(ESC)", new FontIcon("oi-x"));
        Button buttonClose = new Button("Cancel correction\n(ESC)", new FontIcon("oi-reload"));
        agnosticCorrectionPane.getChildren().add(buttonClose);
        buttonClose.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                selectedSymbolView.get().cancelCorrection();
                //doEndEdit();
            }
        });*/




        // see http://aalmiray.github.io/ikonli/cheat-sheet-openiconic.html
        Button buttonPositionDown = new Button("Position down\n(CMD+Down)", new FontIcon("oi-arrow-bottom"));
        buttonPositionDown.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                selectedSymbolView.get().doChangePosition(-1);
                //doChangePosition(-1);
            }
        });
        Button buttonPositionUp = new Button("Position up\n(CMD+Up)", new FontIcon("oi-arrow-top"));
        buttonPositionUp.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                selectedSymbolView.get().doChangePosition(1);
                //doChangePosition(1);
            }
        });
        agnosticCorrectionPane.getChildren().add(buttonPositionDown);
        agnosticCorrectionPane.getChildren().add(buttonPositionUp);

        // see http://aalmiray.github.io/ikonli/cheat-sheet-openiconic.html
        Button buttonFlipStem = new Button("Flip stem\n(F)", new FontIcon("oi-elevator"));
        buttonFlipStem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                selectedSymbolView.get().doFlipStem();
                //doChangePosition(-1);
            }
        });
        agnosticCorrectionPane.getChildren().add(buttonFlipStem);


        for (String agnosticString: agnosticStrings) {
            Shape shape = agnosticSymbolFont.createFontBasedText(agnosticString);
            //shape.setLayoutX(15);
            shape.setLayoutY(25);
            Pane pane = new Pane(shape); // required
            pane.setPrefHeight(50);
            pane.setPrefWidth(30);
            Button button = new Button("", pane);
            button.setTooltip(new Tooltip(agnosticString));
            agnosticCorrectionPane.getChildren().add(button);
            button.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    //doChangeSymbolType(agnosticString);
                    selectedSymbolView.get().doChangeSymbolType(agnosticString);
                }
            });
        }

        /*correctingSymbol.addListener(new ChangeListener<SymbolView>() {
            @Override
            public void changed(ObservableValue<? extends SymbolView> observable, SymbolView oldValue, SymbolView newValue) {
                SymbolCorrectionController symbolCorrectionController = (SymbolCorrectionController) controller;
                if (newValue == null) {
                    symbolCorrectionController.removeSymbolCorrectionToolbar();
                } else {
                    symbolCorrectionController.setSymbolCorrectionToolbar(agnosticCorrectionPane);
                }
            }
        });*/
    }
}
