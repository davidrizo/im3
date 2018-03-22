package es.ua.dlsi.im3.omr.old.mensuraltagger;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.Staff;
import es.ua.dlsi.im3.core.score.layout.CoordinateComponent;
import es.ua.dlsi.im3.core.score.layout.HorizontalLayout;
import es.ua.dlsi.im3.core.score.layout.ScoreLayout;
import es.ua.dlsi.im3.core.score.layout.fonts.LayoutFonts;
import es.ua.dlsi.im3.gui.command.CommandManager;
import es.ua.dlsi.im3.gui.command.ICommand;
import es.ua.dlsi.im3.gui.command.IObservableTaskRunner;
import es.ua.dlsi.im3.gui.javafx.dialogs.OpenSaveFileDialog;
import es.ua.dlsi.im3.gui.javafx.dialogs.ShowError;
import es.ua.dlsi.im3.gui.score.javafx.ScoreSongView;
import es.ua.dlsi.im3.gui.useractionlogger.actions.Coordinate;
import es.ua.dlsi.im3.gui.useractionlogger.actions.MouseClickAction;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbol;
import es.ua.dlsi.im3.omr.muret.OMRApp;
import es.ua.dlsi.im3.omr.old.mensuraltagger.components.*;
import es.ua.dlsi.im3.omr.old.mensuraltagger.loggeractions.UserActionsPool;
import es.ua.dlsi.im3.gui.useractionlogger.ActionLogger;
import es.ua.dlsi.im3.gui.useractionlogger.actions.MouseMoveAction;
import es.ua.dlsi.im3.omr.mensuralspanish.MensuralSymbols;
import es.ua.dlsi.im3.omr.model.Symbol;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;
import javafx.util.Callback;

import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @deprecated Use OMRController
 */
public class OMRMainController implements Initializable{
    @FXML
    Button btnAddImage;

    @FXML
    Button btnRemoveImage;

    @FXML
    ListView<ScoreImageFile> lvImages;

    @FXML
    ImageView imageView;

    @FXML
    Pane marksPane;

    @FXML
    Slider sliderScale;

    @FXML
    ToolBar toolBar;

    @FXML
    ListView<SymbolView> lvSymbols;

    @FXML
    Label labelTrainingModelSymbols;
    @FXML
    ScrollPane scrollPane;
    @FXML
    Slider sliderTimer;

    @FXML
    AnchorPane anchorPaneScore;

    @FXML
    SplitPane splitPaneImages;

    @FXML
    SplitPane splitPaneSymbols;

    @FXML
    SplitPane splitPaneScoreView;

    // TODO: 8/10/17 Cambiar a un popup
    @FXML
    ToolBar toolbarCorrectionSymbols;

    MouseMoveAction mouseMoveAction;

    private Timer timer;

    ScoreImageTagsView currentScoreImageTags;

    ObjectProperty<OMRModel> model;

    SymbolView symbolViewToChange;

    // TODO: 8/10/17 Parametrizar MensuralSymbols
    HashMap<Integer, MensuralSymbols> changeSymbolShortcuts;

    /**
     * Used for action logger
     */
    String context = "NONE";


    private final CommandManager commandManager;
    private boolean editing;

    public OMRMainController() {
        commandManager = new CommandManager();
        changeSymbolShortcuts = new HashMap<>();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        editing = false;
        toolbarCorrectionSymbols.setVisible(false);
        btnRemoveImage.disableProperty().bind(lvImages.getSelectionModel().selectedItemProperty().isNull());
        model = new SimpleObjectProperty<>();
        btnAddImage.disableProperty().bind(model.isNull());
        lvImages.setCellFactory(new Callback<ListView<ScoreImageFile>, ListCell<ScoreImageFile>>() {
            @Override
            public ListCell<ScoreImageFile> call(ListView<ScoreImageFile> param) {
                return new ScoreImageFileListCell();
            }
        });

        lvImages.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ScoreImageFile>() {
            @Override
            public void changed(ObservableValue<? extends ScoreImageFile> observable, ScoreImageFile oldValue,
                                ScoreImageFile newValue) {
                try {
                    changeSelectedImageScore(newValue);
                } catch (IM3Exception e) {
                    ShowError.show(OMRApp.getMainStage(), "Cannot show selected image score", e);
                }
            }
        });

        lvSymbols.setCellFactory(new Callback<ListView<SymbolView>, ListCell<SymbolView>>() {
            @Override
            public ListCell<SymbolView> call(ListView<SymbolView> param) {
                return new SymbolListCell();
            }
        });


        toolBar.disableProperty().bind(lvImages.getSelectionModel().selectedItemProperty().isNull());

        initInteraction();
        initScaleSlider();
    }

    @FXML
    public void handleUndo() {
        doUndo();
    }

    @FXML
    public void handleRedo() {
        doRedo();
    }

    private void initScaleSlider() {
        sliderScale.valueProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
                if (currentScoreImageTags != null) {
                    if (t == null || t.doubleValue() < t1.doubleValue()) {
                        ActionLogger.log(UserActionsPool.zoomIn, currentScoreImageTags.getName());
                    } else {
                        ActionLogger.log(UserActionsPool.zoomOut, currentScoreImageTags.getName());
                    }
                /*
                 * stackPane.getTransforms().clear();
                 * stackPane.getTransforms().add(new Scale(t1.doubleValue(),
                 * t1.doubleValue(), 0, 0));
                 */
                }

                imageView.getTransforms().clear();
                marksPane.getTransforms().clear();
                imageView.getTransforms().add(new Scale(t1.doubleValue(), t1.doubleValue(), 0, 0));
                marksPane.getTransforms().add(new Scale(t1.doubleValue(), t1.doubleValue(), 0, 0));

            }
        });
    }

    private void changeSelectedImageScore(ScoreImageFile newValue) throws IM3Exception {
        //resetLoggingSession();
        imageView.imageProperty().unbind();
        if (newValue != null) {
            imageView.setPreserveRatio(false); // avoid the scaling of original
            // file
            imageView.setFitHeight(0);
            imageView.setFitWidth(0);
            imageView.imageProperty().bind(newValue.imageProperty());

            changeSelectedTagsFile(newValue.tagsFileProperty().get());

            //marksPane.getChildren().clear();
            marksPane.prefHeightProperty().unbind();
            marksPane.prefWidthProperty().unbind();
            marksPane.prefHeightProperty().bind(imageView.getImage().heightProperty());
            marksPane.prefWidthProperty().bind(imageView.getImage().widthProperty());

            fitToWindow();

            createScoreView();
        }
    }

    private void changeSelectedTagsFile(ScoreImageTags sit) throws IM3Exception {
        marksPane.getChildren().clear();
        lvSymbols.getItems().clear();

        if (sit != null) {
            currentScoreImageTags = new ScoreImageTagsView(sit);
            //btnSave.disableProperty().bind(sit.changedProperty().not());
            marksPane.getChildren().add(currentScoreImageTags); // ScoreImageTagsView

            // is a group
            // that contains
            // all symbols
            lvSymbols.setItems(currentScoreImageTags.symbolViewsProperty());
            // for (Symbol s: sit.symbolsProperty()) {
            // drawSymbol(s);
            // }

            //Rectangle rectangle = new Rectangle(0, 0, 200, 200);
            //rectangle.setFill(Color.BLUE);
            //borrar.getChildren().add(rectangle);
            //imageView.setOpacity(0.5);

        } else {
            currentScoreImageTags = null;
            /// lvSymbols.itemsProperty().unbind();
        }
    }

    private void fitToWindow() {
        if (imageView.getLayoutBounds().getWidth() > imageView.getLayoutBounds().getHeight()) {
            sliderScale.setValue((scrollPane.getViewportBounds().getWidth()) / imageView.getLayoutBounds().getWidth());
        } else {
            sliderScale.setValue((scrollPane.getViewportBounds().getHeight()) / imageView.getLayoutBounds().getHeight());
        }
    }

    @FXML
    private void handleTest() {
        // TODO: 8/10/17 Quitar
        createProject(new File("/Users/drizo/cmg/investigacion/training_sets/sources/tonalanalysis/TMP/A_IM3_MENSURAL_NEWTAGGER/data.train"));
        try {
            model.get().addImage(new File("/Users/drizo/cmg/investigacion/training_sets/sources/tonalanalysis/TMP/A_IM3_MENSURAL_NEWTAGGER/12633.jpg"));
        } catch (Exception e) {
            e.printStackTrace();
            ShowError.show(OMRApp.getMainStage(), "Cannot add test image", e);
        }
    }

    private void createScoreView() throws IM3Exception {
        HashMap<Staff, LayoutFonts> layoutFontsHashMap = new HashMap<>();
        layoutFontsHashMap.put(model.get().getStaff(), LayoutFonts.capitan);
        layoutFontsHashMap.put(model.get().getModernStaff(), LayoutFonts.bravura);

        ScoreLayout layout = new HorizontalLayout(model.get().getSong(), layoutFontsHashMap,
                new CoordinateComponent(anchorPaneScore.widthProperty().doubleValue()),
                new CoordinateComponent(anchorPaneScore.heightProperty().doubleValue()));

        ScoreSongView scoreSongView = new ScoreSongView(model.get().getSong(),
                layout);
        anchorPaneScore.getChildren().add(scoreSongView.getMainPanel());
        AnchorPane.setLeftAnchor(scoreSongView.getMainPanel(), 0.0);
        AnchorPane.setRightAnchor(scoreSongView.getMainPanel(), 0.0);
        AnchorPane.setTopAnchor(scoreSongView.getMainPanel(), 0.0);
        AnchorPane.setBottomAnchor(scoreSongView.getMainPanel(), 0.0);
    }


    @FXML
    private void handleSaveProject() {
        // TODO: 7/10/17
    }
    @FXML
    private void handleAddImage() {
        doAddImage();
    }

    @FXML
    private void handleRemoveImage() {

    }

    @FXML
    private void handleFitToWindow() {
        fitToWindow();
    }


    @FXML
    private void handleNewProject() {
        doNewProject();
    }

    @FXML
    public void handleSymbolComplete() {
        if (symbolViewToChange != null) {
            ActionLogger.log(UserActionsPool.symbolCompleteManual, currentScoreImageTags.getName(), symbolViewToChange.hashCode());
            doSymbolComplete();
        }
    }

    @FXML
    private void handleAcceptSymbol() {
        doAcceptSymbol();
    }

    @FXML
    private void handleCorrectSymbol() {
        doCorrectSymbol();
    }

    @FXML
    private void handleQuit() {
        // TODO: 7/10/17 Comprobar que está todo guardado
        OMRApp.getMainStage().close();
    }

    private void doNewProject() {
        OpenSaveFileDialog dlg = new OpenSaveFileDialog();
        File trainingFile = dlg.openFile("Select the training dataset", "Train dataset", "train");
        if (trainingFile == null) {
            ShowError.show(OMRApp.getMainStage(), "Cannot build an empty model");
            return;
        } else {
            createProject(trainingFile);
        }

        // TODO: 7/10/17 Nombre proyecto y mostrarlo en el caption
    }

    private void createProject(File trainingFile) {
        try {
            OMRModel project = new OMRModel(trainingFile);
            model.setValue(project);
            lvImages.setItems(project.filesProperty());
            labelTrainingModelSymbols.setText("Training symbols: " + project.getTrainingModelSymbolCount());
        } catch (IM3Exception e) {
            ShowError.show(OMRApp.getMainStage(), "Cannot create project", e);
        }
    }

    private void doAddImage() {
        OpenSaveFileDialog dlg = new OpenSaveFileDialog();
        File imageFile = dlg.openFile("Select a score image", "Images", "jpg");

        if (imageFile != null) {
            try {
                model.get().addImage(imageFile);
            } catch (Exception e) {
                ShowError.show(OMRApp.getMainStage(), "Cannot add image", e);
            }
        }
   }

    private void initInteraction() {
        marksPane.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                if (currentScoreImageTags != null) {
                    startStroke();
                    beginDrawing(t.getX(), t.getY());

                    if (mouseMoveAction != null) {
                        mouseMoveAction.endCapturing();
                        ActionLogger.log(mouseMoveAction, currentScoreImageTags.getName());
                    }
                    mouseMoveAction = null;
                    ActionLogger.log(new MouseClickAction(context, new Coordinate(t.getSceneX(), t.getSceneY())),
                            currentScoreImageTags.getName());
                }
            }

        });

        marksPane.addEventFilter(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                if (currentScoreImageTags != null) {
                    if (t.isPrimaryButtonDown()) {
                        if (mouseMoveAction == null) {
                            mouseMoveAction = new MouseMoveAction(context);
                            mouseMoveAction.startCapturing();
                        }
                        mouseMoveAction.addCoordinate(t.getSceneX(), t.getSceneY());
                        continueStroke(t.getX(), t.getY());
                    }
                }
            }

        });

        marksPane.addEventFilter(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                if (currentScoreImageTags != null) {
                    try {
                        endStroke();
                    } catch (IM3Exception ex) {
                        Logger.getLogger(OMRMainController.class.getName()).log(Level.SEVERE, null, ex);
                        ShowError.show(OMRApp.getMainStage(), "Cannot end stroke", ex);
                    }

                    if (mouseMoveAction != null) {
                        mouseMoveAction.endCapturing();
                        ActionLogger.log(mouseMoveAction, currentScoreImageTags.getName());
                    }
                    mouseMoveAction = null;
                }
                // ActionLogger.log(new M(context, new Coordinate(t.getSceneX(),
                // t.getSceneY())), currentScoreImageTags.getName());
            }
        });

        // create shortcut listener
        Scene scene = OMRApp.getMainStage().getScene();
        scene.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent t) {
                if (t.isShortcutDown() && t.getCode() == KeyCode.Z) {
                    doUndo();
                    t.consume();
                } else if (t.isShortcutDown() && t.getCode() == KeyCode.Y) {
                    doRedo();
                    t.consume();
                } else if (t.isShortcutDown() && t.getCode() == KeyCode.ADD) {
                    doZoomIn();
                    t.consume();
                } else if (t.isShortcutDown() && t.getCode() == KeyCode.SUBTRACT) {
                    doZoomOut();
                    t.consume();
                } else if (t.getCode() == KeyCode.SPACE) {
                    doSymbolComplete();
                    ActionLogger.log(UserActionsPool.symbolCompleteManual, currentScoreImageTags.getName(), symbolViewToChange.hashCode(), "Shortcut");
                    t.consume();
                } else if (t.getCode() == KeyCode.ENTER) {
                    doAcceptSymbol();
                    t.consume();
                } else if (t.getCode() == KeyCode.ESCAPE) {
                    doCancelSymbol();
                    t.consume();
                } else if (t.getCode() == KeyCode.C) {
                    doCorrectSymbol();
                    t.consume();
                } else if (!t.getText().isEmpty() && Character.isDigit(t.getText().charAt(0))) {
                    onShortCutChangeSymbol(Integer.parseInt(t.getText()));
                    t.consume();
                } else if (t.getCode() == KeyCode.S && t.isShortcutDown()) {
                    // FIXME: 8/10/17 doSave();
                } else if (t.getCode() == KeyCode.O && t.isShortcutDown()) {
                    // FIXME: 8/10/17 doOpenFolder();
                } else if (editing && t.getCode() == KeyCode.UP) {
                    doRepositionUpSelectedSymbol(RepositionDirection.UP);
                    t.consume();
                } else if (editing && t.getCode() == KeyCode.DOWN) {
                    doRepositionUpSelectedSymbol(RepositionDirection.DOWN);
                    t.consume();
                }
				/*
				 * else { ToggleButton btn =
				 * favoritesShortcuts.get(t.getText().toLowerCase()); if (btn !=
				 * null) { btn.setSelected(true); } }
				 */
            }
        });
    }

    private void continueStroke(double x, double y) {
        continueDrawing(x, y);
    }

    // TODO ICommand - undo - redo
    private void doCorrectSymbol() {
        if (currentScoreImageTags != null) {
            symbolViewToChange = lvSymbols.getSelectionModel().getSelectedItem();
            if (symbolViewToChange != null) {
                ActionLogger.log(UserActionsPool.symbolChange, currentScoreImageTags.getName(), symbolViewToChange.hashCode());
                editing = true;
                symbolViewToChange.startEdit();
                loadPossibleCorrections(symbolViewToChange);
                toolbarCorrectionSymbols.setVisible(true);
                lvSymbols.refresh(); // If not added it does not accept layout ¿?
            }
        }
    }

    private void loadPossibleCorrections(final SymbolView item) {
        try {
            toolbarCorrectionSymbols.getItems().clear();
            ArrayList<AgnosticSymbol> notationSymbols = model.get().recognize(item.getSymbol());
            item.setSortedPossibleNotationSymbols(notationSymbols);
            item.setPositionedSymbolType(notationSymbols.get(0));

            List<Text> symbols = item.getSortedPossibleNotationSymbols();
            int i = 0;
            changeSymbolShortcuts.clear();
            for (Text symbol : symbols) {
                final Button btn = new Button();

                final MensuralSymbols notationSymbol = (MensuralSymbols) symbol.getUserData();
                if (i < 10) { // only first 9 symbols have shortcuts
                    btn.setText("(" + (i) + ")");
                    changeSymbolShortcuts.put(i, notationSymbol);
                    i++;
                }
                final int ii=i;
                btn.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        doChangeSymbol(notationSymbol, ii);
                        // lvSymbols.refresh();
                    }

                });

                btn.setGraphic(symbol);
                toolbarCorrectionSymbols.getItems().add(btn);
            }
        } catch (IM3Exception ex) {
            Logger.getLogger(OMRMainController.class.getName()).log(Level.SEVERE, null, ex);
            ShowError.show(OMRApp.getMainStage(), "Cannot load the possible corrections", ex);
        }

    }


    // TODO ICommand - undo - redo
    private void onShortCutChangeSymbol(int shortcutNumber) {
        if (currentScoreImageTags != null) {
            // TODO: 8/10/17 Parametrizar symboltype 
            MensuralSymbols notationSymbol = this.changeSymbolShortcuts.get(shortcutNumber);
            if (notationSymbol != null) {
                doChangeSymbol(notationSymbol, shortcutNumber);
            }
        }
    }

    private void doChangeSymbol(MensuralSymbols st, int positionInList) {
        //try
        //{
            if (symbolViewToChange != null) {
                ActionLogger.log(UserActionsPool.symbolSelect, currentScoreImageTags.getName(), symbolViewToChange.hashCode(), positionInList, symbolViewToChange.getSymbol().getPositionedSymbolType().getSymbol(), st);
                //TODO symbolViewToChange.setSymbolType(st);
                symbolViewToChange.check();
                lvSymbols.refresh();
                lvSymbols.getSelectionModel().select(symbolViewToChange);
                //lvSymbols.getSelectionModel().selectNext();
            }
        //} catch (IM3Exception ex)
            //Logger.getLogger(OMRMainController.class.getName()).log(Level.SEVERE, null, ex);
            //ShowError.show(OMRApp.getMainStage(), "Cannot set the notation symbol", ex);

    }



    private void doRepositionUpSelectedSymbol(final RepositionDirection direction) {
        if (this.symbolViewToChange != null) {
            ICommand cmd = new ICommand() {

                @Override
                public void execute(IObservableTaskRunner observer) throws Exception {
                    ActionLogger.log(UserActionsPool.symbolSetPitch, currentScoreImageTags.getName(), symbolViewToChange.hashCode(), direction);
                    symbolViewToChange.changePosition(direction);
                }

                @Override
                public boolean canBeUndone() {
                    return true;
                }

                @Override
                public void undo() throws Exception {
                    if (direction == RepositionDirection.DOWN) {
                        symbolViewToChange.changePosition(RepositionDirection.UP);
                    } else {
                        symbolViewToChange.changePosition(RepositionDirection.DOWN);
                    }
                    // TODO Auto-generated method stub

                }

                @Override
                public void redo() throws Exception {
                    symbolViewToChange.changePosition(direction);

                }

                @Override
                public String getEventName() {
                    return "REPOSITION";
                }
            };

            try {
                commandManager.executeCommand(cmd);
            } catch (IM3Exception e) {
                Logger.getLogger(OMRMainController.class.getName()).log(Level.WARNING, null, e);
                ShowError.show(OMRApp.getMainStage(), "Cannot layout", e);
            }
        }

    }
    private ScoreImageFile getSelectedFile() {
        return lvImages.getSelectionModel().getSelectedItem();
    }

    private void startStroke() {
        cancelTimer();
        if (currentScoreImageTags.getCurrentSymbolView() == null) {
            try {
                currentScoreImageTags.createNewSymbol(getSelectedFile().getBufferedImage());
            } catch (IM3Exception e) {
                ShowError.show(OMRApp.getMainStage(), "Cannot paint symbol", e);
            }

            // TODOlvSymbols.getItems().add(currentScoreImageTags.getCurrentSymbolView());
        }
        currentScoreImageTags.getCurrentSymbolView().addNewStroke();
    }

    private void endStroke() throws IM3Exception {
        startTimer();
    }

    private void startTimer() {
        timer = new Timer();
        TimerTask completeSymbolTask = new TimerTask() {
            @Override
            public void run() {
                Logger.getLogger(OMRMainController.class.getName()).log(Level.INFO, "Time expired, symbol complete");
                // the timer runs in other thread
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        ActionLogger.log(UserActionsPool.symbolCompleteTimer, currentScoreImageTags.getName(), currentScoreImageTags.getCurrentSymbolView().hashCode());
                        doSymbolComplete();
                    }
                });

            }
        };
        timer.schedule(completeSymbolTask, (long) (sliderTimer.getValue() * 1000.0));
    }


    private void beginDrawing(double x, double y) {
        currentScoreImageTags.getCurrentSymbolView().getCurrentStrokeView().addPoint(x, y);
        ActionLogger.log(UserActionsPool.addStroke, currentScoreImageTags.getName(), currentScoreImageTags.getCurrentSymbolView().hashCode());
		/*
		 * lastStroke.addPoint(x, y);
		 *
		 * path = createPath(); //path.getElements().add(new MoveTo(x, y));
		 * //path.getElements().add(new LineTo(x+10, y+10));
		 * marksPane.getChildren().add(path);
		 *
		 * lastX = x; lastY = y;
		 */
    }

    private void continueDrawing(double x, double y) {
        currentScoreImageTags.getCurrentSymbolView().getCurrentStrokeView().addPoint(x, y);

		/*
		 * lastStroke.addPoint(x, y); drawLine(path, lastX, lastY, x, y); lastX
		 * = x; lastY = y;
		 */
    }

    private void doSymbolComplete() {
        if (currentScoreImageTags != null) {
            ICommand cmd = new ICommand() {
                Symbol symbol;

                @Override
                public void execute(IObservableTaskRunner observer) throws Exception {

                    symbol = currentScoreImageTags.newSymbolComplete();
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            // always show last created element
                            lvSymbols.getSelectionModel().selectLast();
                            SymbolView selectedSymbolView = lvSymbols.getSelectionModel().getSelectedItem();
                            if (selectedSymbolView != null) {
                                try {
                                    ArrayList<AgnosticSymbol> notationSymbols = model.get().recognize(selectedSymbolView.getSymbol());
                                    selectedSymbolView.setSortedPossibleNotationSymbols(notationSymbols);
                                    selectedSymbolView.setPositionedSymbolType(notationSymbols.get(0));

                                    lvSymbols.getFocusModel().focus(lvSymbols.getSelectionModel().getSelectedIndex());
                                    lvSymbols.scrollTo(lvSymbols.getSelectionModel().getSelectedIndex());
                                } catch (IM3Exception e) {
                                    ShowError.show(OMRApp.getMainStage(), "Cannot recognize last symbol", e);
                                }
                            }
                        }
                    });
                }

                @Override
                public boolean canBeUndone() {
                    return true;
                }

                @Override
                public void undo() throws Exception {
                    currentScoreImageTags.removeSymbol(symbol);
                }

                @Override
                public void redo() throws Exception {
                    currentScoreImageTags.addSymbol(symbol);
                }

                @Override
                public String getEventName() {
                    return "SYMBOL_COMPLETE";
                }
            };
            try {
                commandManager.executeCommand(cmd);
            } catch (IM3Exception ex) {
                Logger.getLogger(OMRMainController.class.getName()).log(Level.SEVERE, null, ex);
                ShowError.show(OMRApp.getMainStage(), "Cannot complete symbol", ex);
            }
        }
        cancelTimer();
    }

    private void cancelTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void doUndo() {
        try {
            if (!currentScoreImageTags.isDrawingSymbol()) {
                commandManager.undo();
                ActionLogger.log(UserActionsPool.undo, currentScoreImageTags.getName());
            }
        } catch (IM3Exception ex) {
            Logger.getLogger(OMRMainController.class.getName()).log(Level.SEVERE, null, ex);
            ShowError.show(OMRApp.getMainStage(), "Cannot undo", ex);
        }
    }

    private void doRedo() {
        try {
            if (!currentScoreImageTags.isDrawingSymbol()) {
                commandManager.redo();
                ActionLogger.log(UserActionsPool.redo, currentScoreImageTags.getName());
            }
        } catch (IM3Exception ex) {
            Logger.getLogger(OMRMainController.class.getName()).log(Level.SEVERE, null, ex);
            ShowError.show(OMRApp.getMainStage(), "Cannot redo", ex);
        }
    }

    private void doZoomOut() {
        double value = Math.max(sliderScale.getMin(), sliderScale.getValue() - getSliderZoomSteps());
        sliderScale.setValue(value);
    }

    private void doZoomIn() {
        double value = Math.min(sliderScale.getMax(), sliderScale.getValue() + getSliderZoomSteps());
        sliderScale.setValue(value);
    }

    private double getSliderZoomSteps() {
        return (sliderScale.getMax() - sliderScale.getMin()) / 5.0; // divide
        // into 5
        // zoom
        // steps
    }

    private void doAcceptSymbol() {
        if (currentScoreImageTags != null && lvSymbols.getSelectionModel().getSelectedItem() != null) {
            ActionLogger.log(UserActionsPool.symbolAccept, currentScoreImageTags.getName(), lvSymbols.getSelectionModel().getSelectedItem().hashCode());
            lvSymbols.getSelectionModel().getSelectedItem().check();
            this.editing = false;
            lvSymbols.refresh();
            //lvSymbols.getSelectionModel().selectNext();
        }
    }

    private void doCancelSymbol() {
        if (currentScoreImageTags != null) {
            ActionLogger.log(UserActionsPool.symbolCancel, currentScoreImageTags.getName(), lvSymbols.getSelectionModel().getSelectedItem().hashCode());
            currentScoreImageTags.cancelNewSymbol();
            cancelTimer();
            lvSymbols.getSelectionModel().getSelectedItem().cancelEdit();
            lvSymbols.refresh();
            this.editing = false;
        }
    }


}
