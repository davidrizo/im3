package es.ua.dlsi.im3.omr.muret;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.adt.graphics.BoundingBox;
import es.ua.dlsi.im3.core.adt.graphics.BoundingBoxXY;
import es.ua.dlsi.im3.core.conversions.MensuralToModern;
import es.ua.dlsi.im3.core.patternmatching.NearestNeighbourClassesRanking;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.clefs.ClefF4;
import es.ua.dlsi.im3.core.score.clefs.ClefG2;
import es.ua.dlsi.im3.core.score.io.ScoreSongImporter;
import es.ua.dlsi.im3.core.score.io.mei.MEISongImporter;
import es.ua.dlsi.im3.core.score.layout.CoordinateComponent;
import es.ua.dlsi.im3.core.score.layout.DiplomaticLayout;
import es.ua.dlsi.im3.core.score.layout.HorizontalLayout;
import es.ua.dlsi.im3.core.score.layout.ScoreLayout;
import es.ua.dlsi.im3.core.score.layout.coresymbols.LayoutStaffSystem;
import es.ua.dlsi.im3.core.utils.FileUtils;
import es.ua.dlsi.im3.gui.command.CommandManager;
import es.ua.dlsi.im3.gui.command.ICommand;
import es.ua.dlsi.im3.gui.command.IObservableTaskRunner;
import es.ua.dlsi.im3.gui.interaction.ISelectable;
import es.ua.dlsi.im3.gui.interaction.SelectionManager;
import es.ua.dlsi.im3.gui.javafx.BackgroundProcesses;
import es.ua.dlsi.im3.gui.javafx.DraggableRectangle;
import es.ua.dlsi.im3.gui.javafx.collections.ObservableListViewListModelLink;
import es.ua.dlsi.im3.gui.javafx.collections.ObservableListViewSetModelLink;
import es.ua.dlsi.im3.gui.javafx.dialogs.OpenFolderDialog;
import es.ua.dlsi.im3.gui.javafx.dialogs.ShowChoicesDialog;
import es.ua.dlsi.im3.gui.javafx.dialogs.ShowError;
import es.ua.dlsi.im3.gui.score.javafx.ScoreSongView;
import es.ua.dlsi.im3.omr.classifiers.endtoend.AgnosticSequenceRecognizer;
import es.ua.dlsi.im3.omr.classifiers.endtoend.HorizontallyPositionedSymbol;
import es.ua.dlsi.im3.omr.classifiers.segmentation.ISymbolClusterer;
import es.ua.dlsi.im3.omr.classifiers.segmentation.SymbolClusterer;
import es.ua.dlsi.im3.omr.classifiers.symbolrecognition.*;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbol;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticToken;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticVersion;
import es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.Directions;
import es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.Note;
import es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.Smudge;
import es.ua.dlsi.im3.omr.language.OMRTransduction;
import es.ua.dlsi.im3.omr.language.mensural.GraphicalMensuralSymbolsAutomaton;
import es.ua.dlsi.im3.omr.model.entities.Region;
import es.ua.dlsi.im3.omr.model.entities.Strokes;
import es.ua.dlsi.im3.omr.model.entities.Symbol;
import es.ua.dlsi.im3.omr.muret.model.*;
import es.ua.dlsi.im3.omr.muret.old.OMRApp;
import es.ua.dlsi.im3.omr.muret.old.symbols.StrokesView;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Orientation;
import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;
import javafx.stage.Window;
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
 * Document analysis and symbols editor controller
 * @autor drizo
 */
public class DocumentAnalysisSymbolsDiplomaticMusicController extends MuRETBaseController {
    static final Color PAGE_COLOR = Color.BLUE; //TODO
    static final Color REGION_COLOR = Color.RED;
    static final Color SYMBOL_COLOR = Color.GREEN; //TODO
    static final Color STROKES_COLOR = Color.LIGHTGREEN;

    //// --- Common -----
    @FXML
    BorderPane rootBorderPane;

    @FXML
    Text textFileName;

    @FXML
    ToggleGroup toolToggle;

    @FXML
    ToolBar toolbarToolSpecific;
    /**
     * Used for binding comments
     */
    private BoundingBoxBasedView<? extends IOMRBoundingBox> lastSelectedView;

    enum InteractionMode {eIdle, eDocAnalysisSplittingPages, eDocAnalysisSplittingRegions, eDocAnalysisDrawingPages, eDocAnalysisDrawingRegions, eSymbolsBoundingBox, eSymbolsStrokes};

    enum Phase {eDocAnalysis, eSymbols, eMusic};

    InteractionMode interactionMode;

    Phase phase;

    CommandManager commandManager;

    @FXML
    ToolBar toolBarToolSpecificOptions;

    //// --- Document analysis related ------
    @FXML
    AnchorPane imagePane;

    @FXML
    ToggleButton toggleDocumentAnalysisManual;

    @FXML
    ToggleButton toggleDocumentAnalysisAutomatic;

    @FXML
    ImageView imageView;

    @FXML
    ScrollPane scrollPaneImage;

    ObservableListViewSetModelLink<OMRPage, PageViewContents> pageViews;

    Group pageViewsGroup;

    SelectionManager selectionManager;

    //// --- Symbols related ------
    @FXML
    VBox vboxSymbols;
    @FXML
    TitledPane tittledPaneSymbols;
    @FXML
    ToggleButton toggleSymbolRecognition;
    @FXML
    ToggleButton toggleSymbolManual;
    @FXML
    ScrollPane scrollPaneSelectedStaff;
    @FXML
    VBox vboxSelectedStaff;
    @FXML
    Pane staffEditViewPane;
    @FXML
    Pane selectedStaffPane;
    @FXML
    ImageView selectedStaffImageView;
    @FXML
    Group symbolViewsGroup;
    @FXML
    FlowPane symbolCorrectionPane;
    @FXML
    TextArea symbolCommentsTextArea;

    AgnosticStaffView agnosticStaffView;

    ObservableListViewListModelLink<OMRRegion, RegionView> regions;

    ObservableListViewSetModelLink<OMRSymbol, SymbolView> symbols;

    private RegionView selectedRegionView;

    ObjectProperty<SymbolView> selectedSymbolView;

    AgnosticSymbolFont agnosticSymbolFont;

    private Timer strokesTimer;



    /**
     * Used to draw new symbol bounding box
     */
    private DraggableRectangle newSymbolBoundingBox;

    /**
     * Symbol strokes mode
     */
    private OMRStroke newOMRStroke;
    private OMRStrokes newOMRStrokes;
    private StrokesView newStrokesView;

    //-------------- Diplomatic music edition related
    @FXML
    ToggleButton toggleDiplomatic;


    ////// -----------------------------------------------
    public DocumentAnalysisSymbolsDiplomaticMusicController() {
        commandManager = new CommandManager();
        selectionManager = new SelectionManager();
    }
    
    private Window getWindow() {
        return this.rootBorderPane.getScene().getWindow();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        interactionMode = InteractionMode.eIdle;
        phase = Phase.eDocAnalysis;

        imagePane.prefWidthProperty().bind(scrollPaneImage.widthProperty());

        vboxSymbols.minHeightProperty().bind(scrollPaneSelectedStaff.minHeightProperty().add(symbolCorrectionPane.minHeightProperty()));
        vboxSymbols.prefHeightProperty().bind(scrollPaneSelectedStaff.prefHeightProperty().add(symbolCorrectionPane.prefHeightProperty()));

        vboxSelectedStaff.minHeightProperty().bind(selectedStaffPane.minHeightProperty().add(staffEditViewPane.minHeightProperty()));
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

        symbolCorrectionPane.disableProperty().bind(selectedSymbolView.isNull());
        symbolCorrectionPane.prefWrapLengthProperty().bind(scrollPaneSelectedStaff.widthProperty());
        symbolCorrectionPane.setOrientation(Orientation.HORIZONTAL);
        symbolCorrectionPane.setRowValignment(VPos.CENTER);
        symbolCorrectionPane.setColumnHalignment(HPos.CENTER);

        initTools();
        initInteraction();

    }

    private void changeCursor(Cursor cursor) {
        Scene scene  = this.imagePane.getScene();
        if (scene != null) {
            scene.setCursor(cursor);
        }
    }

    private void initTools() {
        toolToggle.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                toolbarToolSpecific.getItems().clear();
                interactionMode = InteractionMode.eIdle;
                changeCursor(Cursor.DEFAULT);
                if (newValue == toggleDocumentAnalysisAutomatic) {
                    phase = Phase.eDocAnalysis;
                    createDocumentAnalysisAutomaticRecognitionTools();
                } else if (newValue == toggleDocumentAnalysisManual) {
                    phase = Phase.eDocAnalysis;
                    createDocumentAnalysisManualEditingTools();
                } else if (newValue == toggleSymbolManual) {
                    phase = Phase.eSymbols;
                    createAgnosticCorrectionPane();
                    createManualSymbolEditingTools();
                } else if (newValue == toggleSymbolRecognition) {
                    phase = Phase.eSymbols;
                    createAgnosticCorrectionPane();
                    createAutomaticSymbolRecognitionTools();
                } else if (newValue == toggleDiplomatic) {
                    phase = Phase.eMusic;
                    createDiplomaticEditionTools();
                }
            }
        });

        toggleDocumentAnalysisManual.setSelected(true);
    }

    private void initInteraction() {
        //TODO Cambio cursor según estemos en un panel u otro y dependiendo del modo

        imagePane.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (interactionMode == InteractionMode.eDocAnalysisSplittingPages) {
                    if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
                        doSplitPage(event.getX(), event.getY());
                    }
                } else if (interactionMode == InteractionMode.eDocAnalysisSplittingRegions) {
                    if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
                        doSplitRegion(event.getX(), event.getY());
                    }
                }
            }
        });

        selectedStaffPane.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (interactionMode == InteractionMode.eSymbolsBoundingBox) {
                    newSymbolBoundingBox = new DraggableRectangle(event.getX(), event.getY(), 1, 1, Color.GOLD);
                    newSymbolBoundingBox.setFill(Color.TRANSPARENT);
                    newSymbolBoundingBox.setStroke(Color.GOLD);
                    newSymbolBoundingBox.setStrokeWidth(2);
                    selectedStaffPane.getChildren().add(newSymbolBoundingBox);

                } else if (interactionMode == InteractionMode.eSymbolsStrokes) {
                    // try to create a stroke list
                    newOMRStroke = new OMRStroke();
                    newOMRStroke.addPoint(event.getX()+selectedRegionView.getOwner().getFromX(), event.getY()+selectedRegionView.getOwner().getFromY());

                    if (newStrokesView == null) {
                        newOMRStrokes = new OMRStrokes();
                        newStrokesView = new StrokesView(newOMRStrokes, -selectedRegionView.getOwner().getFromX(), -selectedRegionView.getOwner().getFromY(), STROKES_COLOR);
                        selectedStaffPane.getChildren().add(newStrokesView);
                    }

                    newOMRStrokes.addStroke(newOMRStroke);

                }
            }
        });

        selectedStaffPane.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (newSymbolBoundingBox != null) {
                    newSymbolBoundingBox.widthProperty().setValue(event.getX() - newSymbolBoundingBox.xProperty().getValue() );
                    newSymbolBoundingBox.heightProperty().setValue(event.getY() - newSymbolBoundingBox.yProperty().getValue());
                } else if (newOMRStroke != null) {
                    newOMRStroke.addPoint(event.getX()+selectedRegionView.getOwner().getFromX(), event.getY()+selectedRegionView.getOwner().getFromY());
                }
            }
        });
        selectedStaffPane.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (newSymbolBoundingBox != null) { // if adding a symbol
                    addNewSymbolWithBoundingBox(
                            newSymbolBoundingBox.xProperty().get()+selectedRegionView.getOwner().getFromX(),
                            newSymbolBoundingBox.yProperty().get()+selectedRegionView.getOwner().getFromY(),
                            newSymbolBoundingBox.widthProperty().get(),
                            newSymbolBoundingBox.heightProperty().get(), null);

                    selectedStaffPane.getChildren().remove(newSymbolBoundingBox); // it will be added on the insertion of the symbol
                    newSymbolBoundingBox = null;
                } else if (newOMRStroke != null) {
                    newOMRStroke = null;
                    startStrokesTimer(); // when it finishes, the strokes object is closed
                    //TODO Añadir símbolo - timer creación nuevo símbolo sólo si tiene más de 1 punto
                }
            }
        });
    }

    private GrayscaleImageData getGrayScaleImage(double x, double y, double width, double height) throws IM3Exception {
        es.ua.dlsi.im3.omr.model.entities.Image image = omrImage.createPOJO();
        BoundingBox boundingBox = new BoundingBoxXY(x, y, x+width, y+height);
        int[] pixels = image.getGrayscaleImagePixelsNormalized(omrImage.getOmrProject().getImagesFolder(), boundingBox);
        return new GrayscaleImageData(pixels);
    }

    private void addNewSymbolWithBoundingBox(double x, double y, double width, double height, OMRStrokes strokes) {
        if (width > 1 && height > 1) {
            Callable<Void> task = new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    GrayscaleImageData grayscaleImageData = getGrayScaleImage(x, y, width, height);
                    Strokes strokesPOJO = null;

                    AgnosticSymbol bestMatch = null;
                    if (strokes != null) {
                        strokesPOJO = strokes.createPOJO();
                        NearestNeighbourClassesRanking<AgnosticSymbol, SymbolImageAndPointsPrototype> orderedRecognizedSymbols= MuRET.getInstance().getModel().getClassifiers().getBimodalSymbolFromImageDataAndStrokesRecognizer().recognize(grayscaleImageData, strokesPOJO);

                        if (orderedRecognizedSymbols.size() > 0) {
                            bestMatch = orderedRecognizedSymbols.first();
                        }
                    } else {
                        NearestNeighbourClassesRanking<AgnosticSymbol, SymbolImagePrototype> orderedRecognizedSymbols = MuRET.getInstance().getModel().getClassifiers().getSymbolFromImageDataRecognizer().recognize(grayscaleImageData);
                        bestMatch = null;
                        if (orderedRecognizedSymbols.size() > 0) {
                            bestMatch = orderedRecognizedSymbols.first();
                        }
                    }

                    //symbolView.doEdit();
                    AgnosticSymbol _bestMatch = bestMatch;
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            addNewSymbol(_bestMatch, x, y, width, height, strokes);
                        }
                    });

                    return null;
                }
            };
            
            BackgroundProcesses backgroundProcesses = new BackgroundProcesses();
            backgroundProcesses.launch(getWindow(), "Classifying symbol", null, "Cannot recognize or add symbol", true, task);
        } else {
            ShowError.show(OMRApp.getMainStage(), "Cannot add a symbols with bounding box of width or height with less than 2 pixels");
        }
    }

    private void addNewSymbol(AgnosticSymbol bestMatch, double x, double y, double width, double height, OMRStrokes strokes) {
        AgnosticSymbol agnosticSymbol = null;
        if (bestMatch != null) {
            try {
                //TODO Version
                agnosticSymbol = AgnosticSymbol.parseAgnosticString(AgnosticVersion.v2, bestMatch.getAgnosticString());

                if (agnosticSymbol.getSymbol() instanceof Note) { //TODO resto de tipos
                    Note note = (Note) agnosticSymbol.getSymbol();
                    if (note.getStemDirection() == null && note.getDurationSpecification().isUsesStem()) {
                        if (agnosticSymbol.getPositionInStaff().getLineSpace() < PositionsInStaff.LINE_3.getLineSpace()) {
                            note.setStemDirection(Directions.up);
                        } else {
                            note.setStemDirection(Directions.down);
                        }
                    }
                }
            } catch (IM3Exception e) {
                ShowError.show(OMRApp.getMainStage(), "Cannot get a symbol from recognized data", e);
            }
        }

        if (agnosticSymbol == null) {
            agnosticSymbol = new AgnosticSymbol(AgnosticVersion.v2, new Smudge(), PositionInStaff.fromLine(3)); //TODO Version
        }
        try {
            OMRSymbol omrSymbol = new OMRSymbol(selectedRegionView.getOwner(), agnosticSymbol, x, y, width, height);
            if (strokes != null) {
                omrSymbol.setStrokes(strokes);
            }
            ICommand command = new ICommand() {
                OMRSymbol newSymbol;
                @Override
                public void execute(IObservableTaskRunner observer) {
                    newSymbol = omrSymbol;
                    selectedRegionView.getOwner().addSymbol(omrSymbol); // ImageBasedAbstractController is listening the model for changes and it propagates any change
                }

                @Override
                public boolean canBeUndone() {
                    return true;
                }

                @Override
                public void undo() {
                    selectedRegionView.getOwner().removeSymbol(newSymbol);

                }

                @Override
                public void redo() {
                    selectedRegionView.getOwner().addSymbol(newSymbol);
                }

                @Override
                public String getEventName() {
                    return "Add symbol " + newSymbol.toString();
                }
            };

            commandManager.executeCommand(command);
            SymbolView symbolView = symbols.getView(omrSymbol);
            symbolView.sendSelectRequest();
        } catch (IM3Exception e) {
            ShowError.show(OMRApp.getMainStage(), "Cannot add symbol", e);
        }
    }

    private void startStrokesTimer() {
        strokesTimer = new Timer();
        TimerTask completeSymbolTask = new TimerTask() {
            @Override
            public void run() {
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Time expired, symbol complete");
                // the timer runs in other thread
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        //TODO ActionLogger.log(UserActionsPool.symbolCompleteTimer, currentScoreImageTags.getName(), currentScoreImageTags.getCurrentSymbolView().hashCode());
                        doStrokesComplete();
                    }
                });

            }
        };
        //strokesTimer.schedule(completeSymbolTask, (long) (sliderTimer.getValue() * 1000.0));
        strokesTimer.schedule(completeSymbolTask, (long) (2 * 1000.0)); //TODO Parametrizar - preferences - ahora son 300 ms
    }

    private void doStrokesComplete() {
        if (newStrokesView.hasMoreThan1Point()) {
            addNewSymbol(newStrokesView);
        }
        newStrokesView = null;
        cancelStrolesTimer();
    }

    private void addNewSymbol(StrokesView newStrokesView) {
        double x = newStrokesView.getBoundsInLocal().getMinX();
        double y = newStrokesView.getBoundsInLocal().getMinY();
        double w = newStrokesView.getBoundsInLocal().getWidth();
        double h = newStrokesView.getBoundsInLocal().getHeight();

        //TODO usar los trazos para clasificar en lugar de sólo el bounding box
        addNewSymbolWithBoundingBox(x, y, w, h, newStrokesView.getOmrStrokes());
        this.selectedStaffPane.getChildren().remove(newStrokesView); // add it
        newStrokesView = null;
    }


    private void cancelStrolesTimer() {
        if (strokesTimer != null) {
            strokesTimer.cancel();
            strokesTimer = null;
        }
    }


    private void createDocumentAnalysisAutomaticRecognitionTools() {
        hideSymbolsPane();
        Button recognizePages = new Button("Pages");
        toolbarToolSpecific.getItems().add(recognizePages);

        Button recognizeRegions = new Button("Regions in pages");
        toolbarToolSpecific.getItems().add(recognizeRegions);
    }


    private void createDocumentAnalysisManualEditingTools() {
        hideSymbolsPane();
        ToggleGroup toolSpecificToggle = new ToggleGroup();

        Button clear = new Button("Clear");
        toolbarToolSpecific.getItems().add(clear);
        clear.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                toolSpecificToggle.selectToggle(null);
                doRegionsClear();
            }
        });

        ToggleButton select = new ToggleButton("Select");
        select.setToggleGroup(toolSpecificToggle);
        toolbarToolSpecific.getItems().add(select);

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
                toolBarToolSpecificOptions.getItems().clear();
                interactionMode = InteractionMode.eIdle;
                changeCursor(Cursor.DEFAULT);

                if (newValue == null || newValue == select) {
                    changeCursor(Cursor.DEFAULT);
                    interactionMode = InteractionMode.eIdle;
                    //createDocumentAnalysisSelectOptions();
                } else if (newValue == splitPages) {
                    interactionMode = InteractionMode.eDocAnalysisSplittingPages;
                    changeCursor(Cursor.E_RESIZE);
                } else if (newValue == splitRegions) {
                    interactionMode = InteractionMode.eDocAnalysisSplittingRegions;
                    changeCursor(Cursor.S_RESIZE);
                } else if (newValue == drawPages) {
                    interactionMode = InteractionMode.eDocAnalysisDrawingPages;
                    changeCursor(Cursor.CROSSHAIR);
                } else if (newValue == drawRegions) {
                    interactionMode = InteractionMode.eDocAnalysisDrawingRegions;
                    changeCursor(Cursor.CROSSHAIR);
                }
            }
        });
    }

    /*private void createDocumentAnalysisSelectOptions() {
        ToggleGroup toggleGroup = new ToggleGroup();

        ToggleButton toggleButtonSelectAny = new ToggleButton("Any");
        toggleGroup.getToggles().add(toggleButtonSelectAny);
        toolBarToolSpecificOptions.getItems().add(toggleButtonSelectAny);
        toggleGroup.selectToggle(toggleButtonSelectAny);

        ToggleButton toggleButtonSelectPages = new ToggleButton("Pages");
        toggleGroup.getToggles().add(toggleButtonSelectPages);
        toolBarToolSpecificOptions.getItems().add(toggleButtonSelectPages);

        ToggleButton toggleButtonSelectRegions = new ToggleButton("Regions");
        toggleGroup.getToggles().add(toggleButtonSelectRegions);
        toolBarToolSpecificOptions.getItems().add(toggleButtonSelectRegions);

        toggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                interactionMode = InteractionMode.eIdle;
                if (newValue == toggleButtonSelectAny) {
                    interactionMode = InteractionMode.eDocAnalysisSelectingAny;
                } else if (newValue == toggleButtonSelectPages) {
                    interactionMode = InteractionMode.eDocAnalysisDrawingPages;
                } else if (newValue == toggleButtonSelectRegions) {
                    interactionMode = InteractionMode.eDocAnalysisSelectingRegions;
                }
            }
        });
    }*/

    public void loadOMRImage(OMRImage omrImage) throws IM3Exception {
        this.omrImage = omrImage;
        textFileName.setText(omrImage.getImageFile().getAbsolutePath());
        imageView = new ImageView(omrImage.getImage());
        imageView.setPreserveRatio(true);
        imagePane.setMinWidth(imageView.getImage().getWidth());
        imagePane.setMinHeight(imageView.getImage().getHeight());
        imagePane.getChildren().add(imageView);

        agnosticSymbolFont = new AgnosticSymbolFonts().getAgnosticSymbolFont(omrImage.getOmrProject().getNotationType());

        handleZoomToFit();
        loadData();
    }

    private void loadData() {
        pageViews = new ObservableListViewSetModelLink<OMRPage, PageViewContents>(omrImage.getPages(), new Function<OMRPage, PageViewContents>() {
            @Override
            public PageViewContents apply(OMRPage omrPage) {
                return new PageViewContents(DocumentAnalysisSymbolsDiplomaticMusicController.this, omrPage);
            }
        });

        pageViewsGroup = new Group();
        imagePane.getChildren().add(pageViewsGroup);
        for (PageViewContents pageContents: pageViews.getViews()) {
            pageViewsGroup.getChildren().add(pageContents.pageView);
            pageViewsGroup.getChildren().add(pageContents.regionViewsGroup);

        }

        pageViews.getViews().addListener(new ListChangeListener<PageViewContents>() {
            @Override
            public void onChanged(Change<? extends PageViewContents> c) {
                while (c.next()) {
                    if (c.wasRemoved()) {
                        for (PageViewContents pageContents: c.getRemoved()) {
                            pageViewsGroup.getChildren().remove(pageContents.pageView);
                            pageViewsGroup.getChildren().remove(pageContents.regionViewsGroup);
                        }
                    } else if (c.wasAdded()) {
                        for (PageViewContents pageContents: c.getAddedSubList()) {
                            pageViewsGroup.getChildren().add(pageContents.pageView);
                            pageViewsGroup.getChildren().add(pageContents.regionViewsGroup);
                        }
                    }
                }
            }
        });

    }    

    //// ------- Document analysis ------
    private void doRegionsClear() {
        omrImage.getPages().clear();
    }

    private void doSplitPage(double x, double y) {
        ICommand command = new ICommand() {
            @Override
            public void execute(IObservableTaskRunner observer) throws Exception {
                omrImage.splitPageAt(x);
                //loadPages(); //TODO observables
            }

            @Override
            public boolean canBeUndone() {
                return false; //TODO
            }

            @Override
            public void undo() {

            }

            @Override
            public void redo() {

            }

            @Override
            public String getEventName() {
                return "Create regions";
            }
        };

        try {
            commandManager.executeCommand(command);
        } catch (IM3Exception e) {
            e.printStackTrace();
            showError( "Cannot split page", e);
        }
    }

    private void doSplitRegion(double x, double y) {
        ICommand command = new ICommand() {
            @Override
            public void execute(IObservableTaskRunner observer) throws Exception {
                omrImage.splitRegionAt(x, y);
                //loadPages(); //TODO observables
            }

            @Override
            public boolean canBeUndone() {
                return false; //TODO
            }

            @Override
            public void undo() {

            }

            @Override
            public void redo() {

            }

            @Override
            public String getEventName() {
                return "Create regions";
            }
        };
        try {
            commandManager.executeCommand(command);
        } catch (IM3Exception e) {
            e.printStackTrace();
            showError( "Cannot split region", e);
        }
    }

    ///// --- Symbols related --
    private void showSymbolsPane() {
        tittledPaneSymbols.setExpanded(true);
    }

    private void hideSymbolsPane() {
        tittledPaneSymbols.setExpanded(false);
    }

    private void createManualSymbolEditingTools() {
        showSymbolsPane();

        ToggleGroup toggleGroup = new ToggleGroup();

        ToggleButton selectButton = new ToggleButton("Select");
        selectButton.setToggleGroup(toggleGroup);
        toolbarToolSpecific.getItems().add(selectButton);

        ToggleButton boundingBoxButton = new ToggleButton("Bounding boxes");
        boundingBoxButton.setToggleGroup(toggleGroup);
        toolbarToolSpecific.getItems().add(boundingBoxButton);

        ToggleButton strokesButton = new ToggleButton("Strokes");
        strokesButton.setToggleGroup(toggleGroup);
        toolbarToolSpecific.getItems().add(strokesButton);

        toggleGroup.selectToggle(selectButton);

        toggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                interactionMode = InteractionMode.eIdle;
                changeCursor(Cursor.DEFAULT);

                if (newValue == selectButton) {
                    // already changed above
                } else if (newValue == boundingBoxButton) {
                    if (loadSymbolFromImageClassifier()) {
                        changeCursor(Cursor.SE_RESIZE);
                        interactionMode = InteractionMode.eSymbolsBoundingBox;
                    } else {
                        toggleGroup.selectToggle(selectButton);
                    }
                } else if (newValue == strokesButton) {
                    if (loadSymbolFromImageAndStrokesClassifier()) {
                        changeCursor(Cursor.CROSSHAIR);
                        interactionMode = InteractionMode.eSymbolsStrokes;
                    } else {
                        toggleGroup.selectToggle(selectButton);
                    }
                }
            }
        });
    }

    private boolean loadSymbolFromImageClassifier() {
        ISymbolFromImageDataRecognizer symbolFromImageDataRecognizer = MuRET.getInstance().getModel().getClassifiers().getSymbolFromImageDataRecognizer();
        if (!symbolFromImageDataRecognizer.isTrained()) {
            OpenFolderDialog openFolderDialog = new OpenFolderDialog();
            File folder = openFolderDialog.openFolder(getWindow(), "Choose a training folder");

            if (folder == null) {
                return false;
            }

            Callable<Void> task = new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    symbolFromImageDataRecognizer.trainFromFolder(folder);
                    return null;
                }
            };

            BackgroundProcesses backgroundProcesses = new BackgroundProcesses();
            backgroundProcesses.launch(getWindow(), "Loading classifier", "Classifier loaded", "Cannot load classifier", true, task);
        }
        return true;
    }

    private boolean loadSymbolFromImageAndStrokesClassifier() {
        IBimodalSymbolFromImageDataAndStrokesRecognizer bimodalSymbolFromImageDataAndStrokesRecognizer = MuRET.getInstance().getModel().getClassifiers().getBimodalSymbolFromImageDataAndStrokesRecognizer();
        if (!bimodalSymbolFromImageDataAndStrokesRecognizer.isTrained()) {
            OpenFolderDialog openFolderDialog = new OpenFolderDialog();
            File folder = openFolderDialog.openFolder(getWindow(), "Choose a training folder");
            if (folder == null) {
                return false;
            }
            Callable<Void> task = new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    bimodalSymbolFromImageDataAndStrokesRecognizer.trainFromFolder(folder);
                    return null;
                }
            };

            BackgroundProcesses backgroundProcesses = new BackgroundProcesses();
            backgroundProcesses.launch(getWindow(), "Loading classifier", "Classifier loaded", "Cannot load classifier", true, task);
        }
        return true;
    }


    private void createDiplomaticEditionTools() {
        showSymbolsPane();
        createDiplomaticEditionCorrectionPane();

        Button selectButton = new Button("Select");
        selectButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //TODO
            }
        });
        toolbarToolSpecific.getItems().add(selectButton);
    }

    private void createDiplomaticEditionCorrectionPane() {
        this.symbolCorrectionPane.getChildren().clear();

        ToggleGroup toggleGroup = new ToggleGroup();

        ToggleButton clefTB = new ToggleButton("Clef");
        toggleGroup.getToggles().add(clefTB);
        symbolCorrectionPane.getChildren().add(clefTB);

        ToggleButton keyTB = new ToggleButton("Key");
        toggleGroup.getToggles().add(keyTB);
        symbolCorrectionPane.getChildren().add(keyTB);

        ToggleButton meterTB = new ToggleButton("Meter");
        toggleGroup.getToggles().add(meterTB);
        symbolCorrectionPane.getChildren().add(meterTB);

        ToggleButton notesRestsTB = new ToggleButton("Notes/rests");
        toggleGroup.getToggles().add(notesRestsTB);
        symbolCorrectionPane.getChildren().add(notesRestsTB);
    }


    private void createAutomaticSymbolRecognitionTools() {
        showSymbolsPane();

        Button imageEndToEndRecognition = new Button("Image end to end");
        imageEndToEndRecognition.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                recognizeImageEndToEnd();
            }
        });
        toolbarToolSpecific.getItems().add(imageEndToEndRecognition);


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

    private void loadSelectedRegionSymbols() {
        symbolViewsGroup.getChildren().clear();
        symbols = new ObservableListViewSetModelLink<OMRSymbol, SymbolView>(selectedRegionView.getOwner().symbolsProperty(), new Function<OMRSymbol, SymbolView>() {
            @Override
            public SymbolView apply(OMRSymbol omrSymbol) {
                return new SymbolView("Symbol" + omrSymbol.hashCode(), DocumentAnalysisSymbolsDiplomaticMusicController.this, selectedRegionView, omrSymbol, SYMBOL_COLOR);
            }
        });

        for (SymbolView symbolView: symbols.getViews()) {
            try {
                agnosticStaffView.addSymbol(symbolView);
            } catch (IM3Exception e) {
                Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Error adding new symbol " + symbolView.owner + " to staff", e);
                showError( "Error adding new symbol " + symbolView.owner + " to staff", e);
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
                                showError( "Error adding new symbol " + added.owner + " to staff", e);
                            }
                        }
                    }
                }
            }
        });
    }

    private void recognizeStaffEndToEnd(OMRRegion omrRegion) {
        Callable<Void> process = new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                doRecognizeStaffEndToEnd(omrRegion);
                return null;
            }
        };

        new BackgroundProcesses().launch(this.imagePane.getScene().getWindow(), "Recognizing symbol sequences in selected staff", "Recognition finished", "Cannot recognize symbols", true, process);
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
                                showError( "Cannot add a new symbol", e);
                            }

                        }
                    }
                });
    }

    /**
     * We do not create the same for all staves because the order of symbols may change
     */
    private void createAgnosticCorrectionPane()  {
        this.symbolCorrectionPane.getChildren().clear();
        List<String> agnosticStrings =  new LinkedList<>(agnosticSymbolFont.getGlyphs().keySet());

        //TODO Diseñar la usabilidad de todo esto
        /*Button buttonAccept = new Button("Accepto correction\n(ENTER)", new FontIcon("oi-check"));
        symbolCorrectionPane.getChildren().add(buttonAccept);
        buttonAccept.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                selectedSymbolView.get().acceptCorrection();
                //doEndEdit();
            }
        });

        //Button buttonClose = new Button("Cancel correction\n(ESC)", new FontIcon("oi-x"));
        Button buttonClose = new Button("Cancel correction\n(ESC)", new FontIcon("oi-reload"));
        symbolCorrectionPane.getChildren().add(buttonClose);
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
        symbolCorrectionPane.getChildren().add(buttonPositionDown);
        symbolCorrectionPane.getChildren().add(buttonPositionUp);

        // see http://aalmiray.github.io/ikonli/cheat-sheet-openiconic.html
        Button buttonFlipStem = new Button("Flip stem\n(F)", new FontIcon("oi-elevator"));
        buttonFlipStem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                doFlipStem(selectedSymbolView.get());
            }
        });
        symbolCorrectionPane.getChildren().add(buttonFlipStem);


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
            symbolCorrectionPane.getChildren().add(button);
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
                    symbolCorrectionController.setSymbolCorrectionToolbar(symbolCorrectionPane);
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
            showError( "Cannot change position", e);
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
            showError( "Cannot change type", e);
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

    public void onSymbolChanged(OMRSymbol owner) {
        //TODO Se llama desde AgnosticStaffView pero no se usa
    }


    private void recognizeImageEndToEnd() {
        doRegionsClear();

        //TODO Pasar a un modelo + comando
        IImageSymbolRecognizer symbolRecognizer = SymbolRecognizerFactory.getInstance().create();
        //TODO Proceso background - diálogo
        try {
            List<Symbol> recognizedSymbols = symbolRecognizer.recognize(omrImage.getImageFile());
            omrImage.replaceSymbols(recognizedSymbols);
            handleDivideSymbolsIntoRegions();
        } catch (IM3Exception e) {
            e.printStackTrace();
            ShowError.show(this.imagePane.getScene().getWindow(), "Cannot recognize symbols", e);

        }
    }

    //TODO Esto no localiza páginas?
    private void handleDivideSymbolsIntoRegions() {
        try {
            List<OMRSymbol> allOMRSymbols = omrImage.getAllSymbols();
            List<Symbol> allSymbols = new LinkedList<>();
            for (OMRSymbol omrSymbol : allOMRSymbols) {
                allSymbols.add(omrSymbol.createPOJO());
            }

            //TODO A modelo
            ISymbolClusterer symbolClusterer = new SymbolClusterer();
            ShowChoicesDialog<Integer> choicesDialog = new ShowChoicesDialog<>();
            Integer[] staves = {2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14};
            Integer choice = choicesDialog.show(getWindow(), "Division of symbols in regions", "Select the expected number of staves", staves, 6);
            if (choice != null) {
                SortedSet<Region> recognizedRegions = symbolClusterer.cluster(allSymbols, choice);
                omrImage.clear();
                OMRPage omrPage = new OMRPage(omrImage, omrImage.getBoundingBox().getFromX(), omrImage.getBoundingBox().getFromY(),
                        omrImage.getBoundingBox().getToX(), omrImage.getBoundingBox().getToY());
                omrImage.addPage(omrPage);
                int id = 1;
                for (Region region: recognizedRegions) {
                    OMRRegion omrRegion = new OMRRegion(omrPage, id++, region);
                    omrPage.addRegion(omrRegion);
                }
            }
        } catch (IM3Exception e) {
            e.printStackTrace();
            showError( "Cannot divide symbols", e);
        }
    }

    ///// --- From MuRETController -- //TODO ¿Quitar?

    @Override
    protected void bindZoom(Scale scaleTransformation) {
        scaleTransformation.pivotXProperty().bind(imagePane.layoutXProperty());
        scaleTransformation.pivotYProperty().bind(imagePane.layoutYProperty());
        imagePane.getTransforms().add(scaleTransformation);
    }

    @Override
    protected double computeZoomToFitRatio() {
        double xRatio = this.scrollPaneImage.getViewportBounds().getWidth() / this.imageView.getLayoutBounds().getWidth();
        double yRatio = this.scrollPaneImage.getViewportBounds().getHeight() / this.imageView.getLayoutBounds().getHeight();
        if (xRatio > yRatio) {
            return xRatio;
        } else {
            return yRatio;
        }
    }

    @Override
    public <OwnerType extends IOMRBoundingBox> void doSelect(BoundingBoxBasedView<OwnerType> ownerTypeBoundingBoxBasedView) {
        if (!selectionManager.isSelected(ownerTypeBoundingBoxBasedView)) {
            if (lastSelectedView != null) {
                symbolCommentsTextArea.textProperty().unbindBidirectional(lastSelectedView.getOwner().commentsProperty());
            }
            lastSelectedView = ownerTypeBoundingBoxBasedView;
            symbolCommentsTextArea.textProperty().bindBidirectional(lastSelectedView.getOwner().commentsProperty());

            selectionManager.select(ownerTypeBoundingBoxBasedView);
            ownerTypeBoundingBoxBasedView.beginEdit();

            if (ownerTypeBoundingBoxBasedView instanceof PageView) {

            } else if (ownerTypeBoundingBoxBasedView instanceof RegionView) {
                RegionView regionView = (RegionView) ownerTypeBoundingBoxBasedView;

                OMRRegion omrRegion = regionView.getOwner();
                try {
                    selectedStaffImageView.setImage(omrImage.getImage());
                    selectedStaffImageView.setViewport(new Rectangle2D(omrRegion.getFromX(), omrRegion.getFromY(), omrRegion.getWidth(), omrRegion.getHeight()));
                    selectedStaffImageView.setFitHeight(omrRegion.getHeight());
                    selectedStaffImageView.setFitWidth(omrRegion.getWidth());

                    if (phase == Phase.eSymbols) {
                        loadAgnosticStaff(regionView);
                    } else if (phase == Phase.eMusic) {
                        loadDiplomaticEditionStaff(regionView);
                    }
                } catch (IM3Exception e) {
                    Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Cannot select region", e);
                    showError("Cannot select region", e);
                }
            } else if (ownerTypeBoundingBoxBasedView instanceof SymbolView) {
                selectedSymbolView.set((SymbolView) ownerTypeBoundingBoxBasedView);
                if (phase == Phase.eSymbols) {
                    agnosticStaffView.select((SymbolView) ownerTypeBoundingBoxBasedView);
                } else if (phase == Phase.eMusic) {

                }
            }
        }
    }

    private void loadAgnosticStaff(RegionView regionView) {
        OMRRegion omrRegion = regionView.getOwner();

        agnosticStaffView = new AgnosticStaffView(this,
                agnosticSymbolFont,
                scrollPaneSelectedStaff.widthProperty(), 200, -omrRegion.getFromX()); //TODO height
        staffEditViewPane.getChildren().setAll(agnosticStaffView);
        staffEditViewPane.setPrefHeight(300); //TODO Height
        staffEditViewPane.setMinHeight(200);
        this.selectedRegionView = regionView;

        symbolViewsGroup.getChildren().clear();
        symbolViewsGroup.setTranslateY(-omrRegion.getFromY());
        symbolViewsGroup.setTranslateX(-omrRegion.getFromX());

        loadSelectedRegionSymbols();
    }

    private void loadDiplomaticEditionStaff(RegionView regionView) {
        try {
            //TODO Comprobar que no es una región no pentagrama
            OMRRegion omrRegion = regionView.getOwner();
            OMRInstrument omrInstrument = omrRegion.getInstrumentHierarchical();
            if (omrInstrument == null) {
                throw new IM3Exception("Instrument not assigned yet to the region, page, or image");
            }

            GraphicalMensuralSymbolsAutomaton graphicalMensuralSymbolsAutomaton = new GraphicalMensuralSymbolsAutomaton();

            List<AgnosticToken> agnosticTokens = new LinkedList<>();
            for (OMRSymbol omrSymbol: omrRegion.symbolsProperty()) {
                agnosticTokens.add(omrSymbol.getGraphicalSymbol());
            }

            OMRTransduction transduction = graphicalMensuralSymbolsAutomaton.probabilityOf(agnosticTokens, true);
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Probability {0} of sequence {1}", new Object[]{transduction.getProbability(), agnosticTokens});

            ScoreSong mensuralSong = transduction.getSong();

            MensuralToModern mensuralToModern = new MensuralToModern(new Clef[] {new ClefG2()}); // sólo para el ejemplo
            ScoreSong modern = mensuralToModern.convertIntoNewSong(mensuralSong, Intervals.UNISON_PERFECT);

            mensuralToModern.merge(mensuralSong, modern);

            //TODO QUITAR DE AQUI - refactorizar todo ---
            /*MEISongImporter importer = new MEISongImporter();
            ScoreSong scoreSong = importer.importSong(new File("/Users/drizo/Desktop/harpa.mei"));*/
            // TODO: 17/9/17 Enlazar el modelo con el scoreSongView - usar ids como en JS
            ScoreLayout layout = new HorizontalLayout(mensuralSong,
                    new CoordinateComponent(2000),
                    new CoordinateComponent(200));
            ScoreSongView scoreSongView = new ScoreSongView(layout);


            /*iplomaticLayout diplomaticLayout = MuRET.getInstance().getModel().getDiplomaticScoreLayout(omrInstrument, omrRegion);
            ScoreSongView scoreSongView = new ScoreSongView(diplomaticLayout); //TODO Mostrar sólo los pentagramas que necesitamos*/

            staffEditViewPane.getChildren().setAll(scoreSongView.getMainPanel());
            staffEditViewPane.setPrefHeight(2000); //TODO Height
            staffEditViewPane.setMinHeight(200);

        } catch (IM3Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Cannot select region", e);
            showError("Cannot select region", e);
        }
    }


    @Override
    public <OwnerType extends IOMRBoundingBox> void onUnselected(BoundingBoxBasedView<OwnerType> ownerTypeBoundingBoxBasedView) {
        ownerTypeBoundingBoxBasedView.endEdit(true);         //TODO Command para Undo - debería devolver el anterior valor
        if (ownerTypeBoundingBoxBasedView instanceof SymbolView) {
            agnosticStaffView.unSelect((SymbolView) ownerTypeBoundingBoxBasedView);
        }

        ownerTypeBoundingBoxBasedView.endEdit(true); //TODO Command para Undo
        selectionManager.clearSelection();
    }

    @Override
    protected Node getRoot() {
        return rootBorderPane;
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

    @FXML
    private void handleFileSave() {
        try {
            MuRET.getInstance().getModel().save();
        } catch (IM3Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Cannot save", e);
            showError( "Cannot save", e);
        }
    }
}
