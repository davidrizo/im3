package es.ua.dlsi.im3.omr.muret;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.gui.command.CommandManager;
import es.ua.dlsi.im3.gui.command.ICommand;
import es.ua.dlsi.im3.gui.command.IObservableTaskRunner;
import es.ua.dlsi.im3.gui.interaction.ISelectable;
import es.ua.dlsi.im3.gui.interaction.SelectionManager;
import es.ua.dlsi.im3.gui.javafx.BackgroundProcesses;
import es.ua.dlsi.im3.gui.javafx.collections.ObservableListViewListModelLink;
import es.ua.dlsi.im3.gui.javafx.collections.ObservableListViewSetModelLink;
import es.ua.dlsi.im3.gui.javafx.dialogs.ShowError;
import es.ua.dlsi.im3.omr.classifiers.endtoend.AgnosticSequenceRecognizer;
import es.ua.dlsi.im3.omr.classifiers.endtoend.HorizontallyPositionedSymbol;
import es.ua.dlsi.im3.omr.muret.model.*;
import javafx.application.Platform;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Orientation;
import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Scale;
import org.kordamp.ikonli.javafx.FontIcon;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.function.Function;
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
    @FXML
    FlowPane agnosticCorrectionPane;

    AgnosticStaffView agnosticStaffView;

    ObservableListViewListModelLink<OMRRegion, RegionView> regions;

    ObservableListViewSetModelLink<OMRSymbol, SymbolView> symbols;

    private RegionView selectedRegionView;

    SelectionManager selectionManager;
    ObjectProperty<SymbolView> selectedSymbolView;

    AgnosticSymbolFont agnosticSymbolFont;

    CommandManager commandManager;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        selectionManager = new SelectionManager();
        commandManager = new CommandManager();
        vboxSelectedStaff.minHeightProperty().bind(selectedStaffPane.minHeightProperty().add(agnosticStaffViewPane.minHeightProperty()));
        vboxSelectedStaff.prefHeightProperty().bind(vboxSelectedStaff.minHeightProperty());
        scrollPaneSelectedStaff.minHeightProperty().bind(vboxSelectedStaff.minHeightProperty());
        scrollPaneSelectedStaff.prefHeightProperty().bind(scrollPaneSelectedStaff.minHeightProperty());
        selectedStaffPane.minHeightProperty().bind(selectedStaffImageView.fitHeightProperty());
        selectedStaffPane.prefHeightProperty().bind(selectedStaffPane.minHeightProperty());

        vboxSelectedStaff.minWidthProperty().bind(selectedStaffPane.minWidthProperty());
        vboxSelectedStaff.prefWidthProperty().bind(vboxSelectedStaff.minWidthProperty());
        //scrollPaneSelectedStaff.minWidthProperty().bind(vboxSelectedStaff.minWidthProperty());
        //scrollPaneSelectedStaff.prefWidthProperty().bind(scrollPaneSelectedStaff.minWidthProperty());
        selectedStaffPane.minWidthProperty().bind(selectedStaffImageView.fitWidthProperty());
        selectedStaffPane.prefWidthProperty().bind(selectedStaffPane.minWidthProperty());

        selectedSymbolView = new SimpleObjectProperty<>();
    }

    public void loadOMRImage(OMRImage omrImage) throws IM3Exception {
        this.omrImage = omrImage;
        imageView.setImage(omrImage.getImage());
        imageView.setFitHeight(omrImage.getImage().getHeight());
        imageView.setFitWidth(omrImage.getImage().getWidth());
        imageView.setPreserveRatio(true);

        LinkedList<OMRRegion> regionsOfAllPages = new LinkedList<>();
        for (OMRPage page : omrImage.getPages()) {
            for (OMRRegion region : page.regionsProperty()) {
                regionsOfAllPages.add(region);
            }
        }

        ObservableList<OMRRegion> omrRegionObservableList = FXCollections.observableList(regionsOfAllPages);

        regions = new ObservableListViewListModelLink<>(omrRegionObservableList, new Function<OMRRegion, RegionView>() {
            @Override
            public RegionView apply(OMRRegion omrRegion) {
                return new RegionView("Region" + omrRegion.hashCode(), SymbolsController.this, null, omrRegion, REGION_COLOR);
            }
        });

        resizedImagePane.getChildren().addAll(regions.getViews()); //TODO Ver si es necesario sincronizar

        DoubleBinding scaleX = anchorPaneImageView.widthProperty().divide(omrImage.getImage().widthProperty());
        DoubleBinding scaleY = anchorPaneImageView.heightProperty().divide(omrImage.getImage().heightProperty());

        Scale scaleTransformation = new Scale();
        scaleTransformation.xProperty().bind(scaleX);
        scaleTransformation.yProperty().bind(scaleY);
        scaleTransformation.pivotXProperty().bind(resizedImagePane.layoutXProperty());
        scaleTransformation.pivotYProperty().bind(resizedImagePane.layoutYProperty());
        resizedImagePane.getTransforms().add(scaleTransformation);

        agnosticSymbolFont = new AgnosticSymbolFonts().getAgnosticSymbolFont(omrImage.getOmrProject().getNotationType());
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
        selectionManager.select(ownerTypeBoundingBoxBasedView);
        ownerTypeBoundingBoxBasedView.beginEdit();

        if (ownerTypeBoundingBoxBasedView instanceof RegionView) {
            agnosticStaffView = new AgnosticStaffView(this,
                    agnosticSymbolFont,
                    scrollPaneSelectedStaff.widthProperty(), 200, 10); //TODO height
            agnosticStaffViewPane.getChildren().setAll(agnosticStaffView);
            agnosticStaffViewPane.setPrefHeight(300);
            agnosticStaffViewPane.setMinHeight(200);


            createAgnosticCorrectionPane();
            RegionView regionView = (RegionView) ownerTypeBoundingBoxBasedView;
            OMRRegion omrRegion = regionView.getOwner();

            try {
                this.selectedRegionView = regionView;
                selectedStaffImageView.setImage(omrImage.getImage());
                selectedStaffImageView.setViewport(new Rectangle2D(omrRegion.getFromX(), omrRegion.getFromY(), omrRegion.getWidth(), omrRegion.getHeight()));

                selectedStaffImageView.setFitHeight(omrRegion.getHeight());
                selectedStaffImageView.setFitWidth(omrRegion.getWidth());

                symbolViewsGroup.getChildren().clear();
                symbolViewsGroup.setTranslateY(-omrRegion.getFromY());
                symbolViewsGroup.setTranslateX(-omrRegion.getFromX());

                loadSelectedRegionSymbols();

            } catch (IM3Exception e) {
                Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Cannot select region", e);
                ShowError.show(MuRET.getInstance().getMainStage(), "Cannot select region", e);
            }
        } else if (ownerTypeBoundingBoxBasedView instanceof SymbolView) {
            selectedSymbolView.set((SymbolView) ownerTypeBoundingBoxBasedView);
            agnosticStaffView.select((SymbolView) ownerTypeBoundingBoxBasedView);
        }
    }

    @Override
    public <OwnerType extends IOMRBoundingBox> void onUnselected(BoundingBoxBasedView<OwnerType> ownerTypeBoundingBoxBasedView) {
        ownerTypeBoundingBoxBasedView.endEdit(true);         //TODO Command para Undo - debería devolver el anterior valor
        if (ownerTypeBoundingBoxBasedView instanceof SymbolView) {
            agnosticStaffView.unSelect((SymbolView) ownerTypeBoundingBoxBasedView);
        }
        selectionManager.clearSelection();
        selectedSymbolView.set(null);

    }



    private void loadSelectedRegionSymbols() {
        symbolViewsGroup.getChildren().clear();//TODO Asociarlo con data
        symbols = new ObservableListViewSetModelLink<OMRSymbol, SymbolView>(selectedRegionView.getOwner().symbolsProperty(), new Function<OMRSymbol, SymbolView>() {
            @Override
            public SymbolView apply(OMRSymbol omrSymbol) {
                return new SymbolView("Symbol" + omrSymbol.hashCode(), SymbolsController.this, selectedRegionView, omrSymbol, SYMBOL_COLOR);
            }
        });

        for (SymbolView symbolView: symbols.getViews()) {
            try {
                agnosticStaffView.addSymbol(symbolView);
            } catch (IM3Exception e) {
                Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Error adding new symbol " + symbolView.owner + " to staff", e);
                ShowError.show(MuRET.getInstance().getMainStage(), "Error adding new symbol " + symbolView.owner + " to staff", e);
            }
        }

        symbolViewsGroup.getChildren().addAll(symbols.getViews());
        symbols.getViews().addListener(new ListChangeListener<SymbolView>() {
            @Override
            public void onChanged(Change<? extends SymbolView> change) {
                while (change.next()) {
                    if (change.wasRemoved()) {
                        for (SymbolView removed : change.getRemoved()) {
                            symbolViewsGroup.getChildren().remove(removed);
                            agnosticStaffView.remove(removed);
                        }
                    } else if (change.wasAdded()) {
                        for (SymbolView added : change.getAddedSubList()) {
                            symbolViewsGroup.getChildren().add(added);
                            try {
                                agnosticStaffView.addSymbol(added);
                            } catch (IM3Exception e) {
                                Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Error adding new symbol " + added.owner + " to staff", e);
                                ShowError.show(MuRET.getInstance().getMainStage(), "Error adding new symbol " + added.owner + " to staff", e);
                            }
                        }
                    }
                }
            }
        });
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
        //TODO Se invoca, pero no sé si vale para algo aún (estaba en la anterior versión)
    }

    /**
     * We do not create the same for all staves because the order of symbols may change
     */
    private void createAgnosticCorrectionPane()  {
        agnosticCorrectionPane.disableProperty().bind(selectedSymbolView.isNull());
        agnosticCorrectionPane.prefWrapLengthProperty().bind(scrollPaneSelectedStaff.widthProperty());
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
                doChangePosition(selectedSymbolView.get(), -1);
            }
        });
        Button buttonPositionUp = new Button("Position up\n(CMD+Up)", new FontIcon("oi-arrow-top"));
        buttonPositionUp.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                doChangePosition(selectedSymbolView.get(), 1);
            }
        });
        agnosticCorrectionPane.getChildren().add(buttonPositionDown);
        agnosticCorrectionPane.getChildren().add(buttonPositionUp);

        // see http://aalmiray.github.io/ikonli/cheat-sheet-openiconic.html
        Button buttonFlipStem = new Button("Flip stem\n(F)", new FontIcon("oi-elevator"));
        buttonFlipStem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                doFlipStem(selectedSymbolView.get());
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
            Button button = new Button();
            button.setGraphic(pane);
            button.setTooltip(new Tooltip(agnosticString));
            agnosticCorrectionPane.getChildren().add(button);
            button.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    doChangeSymbolType(selectedSymbolView.get(), agnosticString);
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

    public void doChangePosition(SymbolView symbolView, int linespaceDifference) {
        ICommand command = new ICommand() {
            @Override
            public void execute(IObservableTaskRunner observer) {
                agnosticStaffView.doChangePosition(linespaceDifference, symbolView);
            }

            @Override
            public boolean canBeUndone() {
                return true;
            }

            @Override
            public void undo() {
                agnosticStaffView.doChangePosition(-linespaceDifference, symbolView);
            }

            @Override
            public void redo() {
                agnosticStaffView.doChangePosition(linespaceDifference, symbolView);
            }

            @Override
            public String getEventName() {
                return "Change position";
            }
        };
        try {
            commandManager.executeCommand(command);
        } catch (IM3Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Cannot change position", e);
            ShowError.show(MuRET.getInstance().getMainStage(), "Cannot change position", e);
        }
    }

    public void doChangeSymbolType(SymbolView symbolView, String agnosticString) {
        ICommand command = new ICommand() {
            String previousType;
            @Override
            public void execute(IObservableTaskRunner observer) throws IM3Exception {
                previousType = agnosticStaffView.doChangeSymbolType(agnosticString, symbolView);
            }

            @Override
            public boolean canBeUndone() {
                return true;
            }

            @Override
            public void undo() throws IM3Exception {
                agnosticStaffView.doChangeSymbolType(previousType, symbolView);
            }

            @Override
            public void redo() throws IM3Exception {
                agnosticStaffView.doChangeSymbolType(agnosticString, symbolView);
            }

            @Override
            public String getEventName() {
                return "Change type";
            }
        };
        try {
            commandManager.executeCommand(command);
        } catch (IM3Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Cannot change type", e);
            ShowError.show(MuRET.getInstance().getMainStage(), "Cannot change type", e);
        }
    }

    public void doFlipStem(SymbolView symbolView) {
        String agnosticString = symbolView.getOwner().getGraphicalSymbol().getSymbol().toAgnosticString();
        String newAgnosticString = null;
        if (agnosticString.endsWith("_down")) {
            newAgnosticString = agnosticString.substring(0, agnosticString.length()-5) + "_up";
        } else if (agnosticString.endsWith("_up")) {
            newAgnosticString = agnosticString.substring(0, agnosticString.length()-3) + "_down";
        }
        if (newAgnosticString != null) {
            doChangeSymbolType(symbolView, newAgnosticString);
        }
    }

}
