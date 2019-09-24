package es.ua.dlsi.im3.omr.muret.old.symbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.adt.graphics.BoundingBox;
import es.ua.dlsi.im3.core.adt.graphics.BoundingBoxXY;
import es.ua.dlsi.im3.core.patternmatching.NearestNeighbourClassesRanking;
import es.ua.dlsi.im3.core.score.PositionInStaff;
import es.ua.dlsi.im3.core.score.PositionsInStaff;
import es.ua.dlsi.im3.core.score.layout.LayoutConstants;
import es.ua.dlsi.im3.gui.command.ICommand;
import es.ua.dlsi.im3.gui.command.IObservableTaskRunner;
import es.ua.dlsi.im3.gui.interaction.ISelectableTraversable;
import es.ua.dlsi.im3.gui.javafx.DraggableRectangle;
import es.ua.dlsi.im3.gui.javafx.dialogs.ShowError;
import es.ua.dlsi.im3.omr.classifiers.symbolrecognition.GrayscaleImageData;
import es.ua.dlsi.im3.omr.classifiers.symbolrecognition.SymbolImageAndPointsPrototype;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbol;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticVersion;
import es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.Defect;
import es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.Directions;
import es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.Note;
import es.ua.dlsi.im3.omr.encoding.enums.Defects;
import es.ua.dlsi.im3.omr.model.entities.Strokes;
import es.ua.dlsi.im3.omr.muret.old.ImageBasedAbstractController;
import es.ua.dlsi.im3.omr.muret.old.OMRApp;
import es.ua.dlsi.im3.omr.muret.model.OMRRegion;
import es.ua.dlsi.im3.omr.muret.old.BoundingBoxBasedView;
import es.ua.dlsi.im3.omr.muret.model.OMRStroke;
import es.ua.dlsi.im3.omr.muret.model.OMRStrokes;
import es.ua.dlsi.im3.omr.muret.model.OMRSymbol;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @autor drizo
 */
public class RegionView extends BoundingBoxBasedView<OMRRegion> {
    public static final Color STROKES_COLOR = Color.LIGHTGREEN;
    private static final double STAFF_HEIGHT = LayoutConstants.EM * 3;
    VBox vBox;
    /**
     * For overlaying imageView and symbols
     */
    Group imageViewWithSymbols;
    /**
     * Extract of the full image using a viewport over the full image
     */
    ImageView imageView;
    /**
     * Bounding boxes
     */
    Group symbolsBoundingBoxesGroup;
    /**
     * Strokes
     */
    Group strokesGroup;
    /**
     * Full image from the file
     */
    Image fullImage;
    /**
     * Contains a staff with agnostic symbols on top
     */
    AgnosticStaffView agnosticStaffView;
    /**
     * Contains a staff view of a rendered score
     */
    SemanticStaffView semanticStaffView;
    /**
     * The font for the agnostic staff view
     */
    private AgnosticSymbolFont agnosticSymbolFont;

    DraggableRectangle newSymbolBoundingBox;
    private OMRStrokes newOMRStrokes;
    private OMRStroke newOMRStroke;
    private StrokesView newStrokesView;
    private Timer strokesTimer;

    public RegionView(String ID, ImageBasedAbstractController controller, AgnosticSymbolFont agnosticSymbolFont, PageView parentBoundingBox, OMRRegion owner, Color color) throws IM3Exception {
        super(ID, controller, parentBoundingBox, 0.0, 0.0, owner.getWidth(), owner.getHeight() + STAFF_HEIGHT, owner, color);
        this.agnosticSymbolFont = agnosticSymbolFont;
        createImageView();
        createAgnosticStaffView();
        createSemanticStaffView();
        vBox = new VBox(5);
        vBox.getChildren().addAll(imageViewWithSymbols, agnosticStaffView);
        this.getChildren().add(vBox);
        initSemantic();
    }

    private void initSemantic() {
        vBox.getChildren().add(semanticStaffView);
        SymbolCorrectionController scontroller = (SymbolCorrectionController) controller;
        semanticStaffView.visibleProperty().bind(scontroller.agnosticModeProperty().not());
    }

    private void createImageView() throws IM3Exception {
        fullImage = owner.getOMRPage().getOMMRImage().getImage();
        imageView = new ImageView(fullImage);
        imageView.setViewport(new Rectangle2D(owner.getFromX(), owner.getFromY(), owner.getWidth(), owner.getHeight()));
        symbolsBoundingBoxesGroup = new Group();
        strokesGroup = new Group();
        imageViewWithSymbols = new Group();
        imageViewWithSymbols.getChildren().addAll(imageView, symbolsBoundingBoxesGroup, strokesGroup); // order is important
    }

    private void createAgnosticStaffView() {
        agnosticStaffView = new AgnosticStaffView(controller, agnosticSymbolFont, owner.getWidth(), STAFF_HEIGHT, -getOwner().getFromX());
    }

    private void createSemanticStaffView() {
        semanticStaffView = new SemanticStaffView();
    }

    @Override
    protected void onLabelContextMenuRequested(ContextMenuEvent event) {

    }

    @Override
    protected void onRegionMouseClicked(MouseEvent event) {
        controller.unselect();
    }

    public AgnosticStaffView getAgnosticStaffView() {
        return agnosticStaffView;
    }

    public void addSymbolView(SymbolView symbolView) {
        symbolsBoundingBoxesGroup.getChildren().add(symbolView);
        if (symbolView.getStrokesView() != null) {
            strokesGroup.getChildren().add(symbolView.getStrokesView());
        }
    }

    /*@Override
    public void handle(KeyEvent event) {
        super.handle(event);
        switch (event.getCode()) {
            default:
                agnosticStaffView.handle(event);
        }
    }*/

    public void delete(SymbolView symbolView) {
        ICommand command = new ICommand() {
            OMRSymbol deletedSymbol = symbolView.getOwner();
            @Override
            public void execute(IObservableTaskRunner observer) {
                // it changes the model and ImageBasedAbstractController, that is bound to model changes propagates all changes in the view
                owner.removeSymbol(deletedSymbol);
            }

            @Override
            public boolean canBeUndone() {
                return true;
            }

            @Override
            public void undo() {
                owner.addSymbol(deletedSymbol);
            }

            @Override
            public void redo() {
                owner.addSymbol(deletedSymbol);
            }

            @Override
            public String getEventName() {
                return "Delete symbol " + deletedSymbol.toString();
            }
        };

        try {
            controller.getDashboard().getCommandManager().executeCommand(command);
        } catch (IM3Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Cannot delete symbol", e);
            ShowError.show(OMRApp.getMainStage(), "Cannot delete symbol", e);
        }

    }

    @Override
    public void onSymbolRemoved(BoundingBoxBasedView elementView) {
        super.onSymbolRemoved(elementView);
        symbolsBoundingBoxesGroup.getChildren().remove(elementView);
        if (elementView instanceof SymbolView) {
            SymbolView symbolView = (SymbolView) elementView;
            if (symbolView.getStrokesView() != null) {
                strokesGroup.getChildren().remove(symbolView.getStrokesView());
            }
        }
        agnosticStaffView.onSymbolRemoved(elementView);
    }

    @Override
    protected void doMousePressed(MouseEvent event) {
        super.doMousePressed(event);

        SymbolCorrectionController symbolCorrectionController = (SymbolCorrectionController) controller;
        if (event.isPrimaryButtonDown()) {
            if  (event.getClickCount() == 2) {
                newSymbolBoundingBox = new DraggableRectangle(event.getX(), event.getY(), 1, 1, Color.GOLD);
                newSymbolBoundingBox.setFill(Color.TRANSPARENT);
                newSymbolBoundingBox.setStroke(Color.GOLD);
                newSymbolBoundingBox.setStrokeWidth(2);
                this.getChildren().add(newSymbolBoundingBox);
            } else {
                // try to create a stroke list
                newOMRStroke = new OMRStroke();
                newOMRStroke.addPoint(event.getX()+owner.getFromX(), event.getY()+owner.getFromY());

                if (newStrokesView == null) {
                    newOMRStrokes = new OMRStrokes();
                    newStrokesView = new StrokesView(newOMRStrokes, -owner.getFromX(), -owner.getFromY(), STROKES_COLOR);
                    this.imageViewWithSymbols.getChildren().add(newStrokesView);
                }

                newOMRStrokes.addStroke(newOMRStroke);
            }
        }
    }



    @Override
    protected void doMouseReleased(MouseEvent event) {
        super.doMouseReleased(event);
        if (newSymbolBoundingBox != null) { // if adding a symbol
            addNewSymbolWithBoundingBox(
                    newSymbolBoundingBox.xProperty().get()+owner.getFromX(),
                    newSymbolBoundingBox.yProperty().get()+owner.getFromY(),
                    newSymbolBoundingBox.widthProperty().get(),
                    newSymbolBoundingBox.heightProperty().get(), null);

            this.getChildren().remove(newSymbolBoundingBox); // it will be added on the insertion of the symbol
            newSymbolBoundingBox = null;
        } else if (newOMRStroke != null) {
            newOMRStroke = null;
            startStrokesTimer(); // when it finishes, the strokes object is closed
            //TODO Añadir símbolo - timer creación nuevo símbolo sólo si tiene más de 1 punto
        }
    }

    @Override
    protected void doMouseDragged(MouseEvent event) {
        super.doMouseDragged(event);
        if (newSymbolBoundingBox != null) {
            newSymbolBoundingBox.widthProperty().setValue(event.getX() - newSymbolBoundingBox.xProperty().getValue() );
            newSymbolBoundingBox.heightProperty().setValue(event.getY() - newSymbolBoundingBox.yProperty().getValue());
        } else if (newOMRStroke != null) {
            newOMRStroke.addPoint(event.getX()+owner.getFromX(), event.getY()+owner.getFromY());
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
        strokesTimer.schedule(completeSymbolTask, (long) (0.3 * 1000.0)); //TODO Parametrizar - preferences - ahora son 300 ms
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
        this.imageViewWithSymbols.getChildren().remove(newStrokesView); // add it
        newStrokesView = null;
    }


    private void cancelStrolesTimer() {
        if (strokesTimer != null) {
            strokesTimer.cancel();
            strokesTimer = null;
        }
    }

    private void addNewSymbol(NearestNeighbourClassesRanking<AgnosticSymbol, SymbolImageAndPointsPrototype> orderedRecognizedSymbols, double x, double y, double width, double height, OMRStrokes strokes) {
        AgnosticSymbol agnosticSymbol = null;
        if (orderedRecognizedSymbols != null) {
            try {
                //TODO Mostrar barra corrección con símbolos ordenados
                AgnosticSymbol firstRankedElement = orderedRecognizedSymbols.first();
                //TODO Version
                agnosticSymbol = AgnosticSymbol.parseAgnosticString(AgnosticVersion.v2, firstRankedElement.getAgnosticString());

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
                ShowError.show(OMRApp.getMainStage(), "Cannot classify symbol", e);
            }
        }

        if (agnosticSymbol == null) {
            agnosticSymbol = new AgnosticSymbol(AgnosticVersion.v2, new Defect(Defects.smudge), PositionInStaff.fromLine(3)); //TODO Version
        }
        try {
            OMRSymbol omrSymbol = new OMRSymbol(owner, agnosticSymbol, x, y, width, height);
            if (strokes != null) {
                omrSymbol.setStrokes(strokes);
            }
            ICommand command = new ICommand() {
                OMRSymbol newSymbol;
                @Override
                public void execute(IObservableTaskRunner observer) {
                    newSymbol = omrSymbol;
                    owner.addSymbol(omrSymbol); // ImageBasedAbstractController is listening the model for changes and it propagates any change
                }

                @Override
                public boolean canBeUndone() {
                    return true;
                }

                @Override
                public void undo() {
                    owner.removeSymbol(newSymbol);

                }

                @Override
                public void redo() {
                    owner.addSymbol(newSymbol);
                }

                @Override
                public String getEventName() {
                    return "Add symbol " + newSymbol.toString();
                }
            };

            controller.getDashboard().getCommandManager().executeCommand(command);
            SymbolView symbolView = (SymbolView) controller.doSelect(omrSymbol);
        } catch (IM3Exception e) {
            ShowError.show(OMRApp.getMainStage(), "Cannot add symbol", e);
        }
    }


    private void addNewSymbolWithBoundingBox(double x, double y, double width, double height, OMRStrokes strokes) {
        if (width > 1 && height > 1) {
            NearestNeighbourClassesRanking<AgnosticSymbol, SymbolImageAndPointsPrototype> orderedRecognizedSymbols = null;
            try {
                GrayscaleImageData grayscaleImageData = getGrayScaleImage(x, y, width, height);
                Strokes strokesPOJO = null;
                if (strokes != null) {
                    strokesPOJO = strokes.createPOJO();
                }
                orderedRecognizedSymbols = controller.getDashboard().getModel().classifySymbolFromImage(grayscaleImageData, strokesPOJO);
                //symbolView.doEdit();

            } catch (IM3Exception e) {
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Cannot add symbol", e);
                ShowError.show(OMRApp.getMainStage(), "Cannot add a symbol", e);
            }
            addNewSymbol(orderedRecognizedSymbols, x, y, width, height, strokes);
        } else {
            ShowError.show(OMRApp.getMainStage(), "Cannot add a symbols with bounding box of width or height with less than 2 pixels");
        }
    }

    private GrayscaleImageData getGrayScaleImage(double x, double y, double width, double height) throws IM3Exception {
        es.ua.dlsi.im3.omr.model.entities.Image image = this.owner.getOMRPage().getOMMRImage().createPOJO();
        BoundingBox boundingBox = new BoundingBoxXY(x, y, x+width, y+height);
        int[] pixels = image.getGrayscaleImagePixelsNormalized(owner.getOMRPage().getOMMRImage().getOmrProject().getImagesFolder(), boundingBox);
        return new GrayscaleImageData(pixels);
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
            controller.getDashboard().getCommandManager().executeCommand(command);
        } catch (IM3Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Cannot change position", e);
            ShowError.show(OMRApp.getMainStage(), "Cannot change position", e);
        }
    }

    public void doChangeSymbolType(SymbolView symbolView, String agnosticString) {
        ICommand command = new ICommand() {
            String previousType;
            @Override
            public void execute(IObservableTaskRunner observer) {
                previousType = agnosticStaffView.doChangeSymbolType(agnosticString, symbolView);
            }

            @Override
            public boolean canBeUndone() {
                return true;
            }

            @Override
            public void undo() {
                agnosticStaffView.doChangeSymbolType(previousType, symbolView);
            }

            @Override
            public void redo() {
                agnosticStaffView.doChangeSymbolType(agnosticString, symbolView);
            }

            @Override
            public String getEventName() {
                return "Change type";
            }
        };
        try {
            controller.getDashboard().getCommandManager().executeCommand(command);
        } catch (IM3Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Cannot change type", e);
            ShowError.show(OMRApp.getMainStage(), "Cannot change type", e);
        }
    }

    public void doFlipStem(SymbolView symbolView) {
        String agnosticString = symbolView.getOMRSymbol().getGraphicalSymbol().getSymbol().toAgnosticString();
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


    public ImageView getImageView() {
        return imageView;
    }

    @Override
    public ISelectableTraversable getSelectionParent() {
        return controller;
    }

    /*public void doEndEdit() {
        getAgnosticStaffView().doEndEdit();
    }*/
}
