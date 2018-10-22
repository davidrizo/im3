package es.ua.dlsi.im3.omr.muret;


import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.score.PositionInStaff;
import es.ua.dlsi.im3.core.score.Staff;
import es.ua.dlsi.im3.core.score.layout.LayoutConstants;
import es.ua.dlsi.im3.gui.interaction.ISelectable;
import es.ua.dlsi.im3.gui.interaction.ISelectableTraversable;
import es.ua.dlsi.im3.gui.interaction.SelectionManager;
import es.ua.dlsi.im3.gui.javafx.dialogs.ShowError;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbol;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbolType;
import es.ua.dlsi.im3.omr.muret.model.OMRSymbol;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A staff containing just information about positions
 * @autor drizo
 */
public class AgnosticStaffView extends VBox implements ISelectableTraversable {
    private static final double LEDGER_LINE_EXTRA_LENGTH = 8;
    private static final double LEDGER_LINE_WIDTH = 20;
    private final DocumentAnalysisSymbolsDiplomaticMusicController controller;
    private final Line[] lines;
    Group staffGroup;

    private final AgnosticSymbolFont agnosticSymbolFont;
    private final Rectangle background;
    Group linesGroup;
    Group symbolsGroup;
    double lineBottomPosition;
    HashMap<OMRSymbol, AgnosticSymbolView> shapesInStaff;
    private double regionXOffset;
    SelectionManager selectionManager;

    public AgnosticStaffView(DocumentAnalysisSymbolsDiplomaticMusicController controller, AgnosticSymbolFont agnosticSymbolFont, ReadOnlyDoubleProperty widthProperty, double height, double regionXOffset)  {
        this.controller = controller;
        this.selectionManager = new SelectionManager();
        shapesInStaff = new HashMap<>();
        this.regionXOffset = regionXOffset;
        //correctingSymbol = new SimpleObjectProperty();
        staffGroup = new Group();
        this.agnosticSymbolFont = agnosticSymbolFont;
        this.linesGroup = new Group();
        this.symbolsGroup = new Group();
        background = new Rectangle();
        background.setHeight(height);
        background.widthProperty().bind(widthProperty);
        background.setFill(Color.WHITESMOKE);

        staffGroup.getChildren().addAll(background, linesGroup, symbolsGroup); // order is important

        //TODO hacer una clase que sepa hacer esto y manipular cosas?
        lines = new Line[5];
        for (int i=0; i<5; i++) {
            double y = LayoutConstants.EM+i*LayoutConstants.SPACE_HEIGHT;
            Line line = new Line();
            lines[i] = line;
            line.setStartX(0);
            line.setStartY(y);
            line.setEndY(y);
            line.endXProperty().bind(background.widthProperty());
            line.setStroke(Color.BLACK);
            line.setStrokeWidth(1); //TODO
            linesGroup.getChildren().add(line);

            lineBottomPosition = y;
        }

        this.getChildren().addAll(staffGroup);
    }

    public SelectionManager getSelectionManager() {
        return selectionManager;
    }

    private double getPosition(PositionInStaff positionInStaff) {
        double heightDifference = -(LayoutConstants.SPACE_HEIGHT * ((double)positionInStaff.getLineSpace()) / 2.0);
        return lineBottomPosition + heightDifference;
    }

    public Shape addSymbol(SymbolView symbolView) throws IM3Exception {
        try {
            OMRSymbol symbol = symbolView.getOwner();
            Shape shape = agnosticSymbolFont.createShape(symbol.getGraphicalSymbol().getSymbol());
            shape.setLayoutX(symbol.getCenterX()+regionXOffset);

            symbolsGroup.getChildren().add(shape);
            updateVerticalLayoutInStaff(symbol, shape);

            AgnosticSymbolView agnosticSymbolView = new AgnosticSymbolView(controller, this, shape, symbolView); // it adds also the interaction
            shapesInStaff.put(symbol, agnosticSymbolView);
            return shape;
            //TODO Ledger lines
        } catch (IM3Exception e) {
            e.printStackTrace();
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Cannot find a glyph for agnostic symbol '{0}'", symbolView.getOwner().getGraphicalSymbol().getAgnosticString());
            throw e;
        }
    }


    private void updateVerticalLayoutInStaff(OMRSymbol omrSymbol, Shape shape) {
        PositionInStaff positionInStaff = omrSymbol.getGraphicalSymbol().getPositionInStaff();
        shape.setLayoutY(getPosition(positionInStaff));

        // check if ledger lines are required
        Group ledgerLinesGroup;
        int numberOfLines = Staff.computeNumberLedgerLinesNeeded(positionInStaff, 5);
        if (numberOfLines < 0) {
            ledgerLinesGroup = new Group();
            int n = -numberOfLines;
            for (int i=1; i<=n; i++) {
                double y = lines[0].getStartY() -i*LayoutConstants.SPACE_HEIGHT;
                Line line = new Line(shape.getLayoutX()-LEDGER_LINE_EXTRA_LENGTH, y, shape.getLayoutX()+LEDGER_LINE_WIDTH, y);
                line.setStroke(Color.BLACK);
                line.setStrokeWidth(1);
                ledgerLinesGroup.getChildren().add(line);
            }
        } else {
            ledgerLinesGroup = new Group();
            int n = numberOfLines;
            for (int i=1; i<=n; i++) {
                double y = lines[4].getStartY() + i*LayoutConstants.SPACE_HEIGHT;
                Line line = new Line(shape.getLayoutX()-LEDGER_LINE_EXTRA_LENGTH, y, shape.getLayoutX()+LEDGER_LINE_WIDTH, y);
                line.setStroke(Color.BLACK);
                line.setStrokeWidth(1);
                ledgerLinesGroup.getChildren().add(line);
            }
        }
        if (symbolsGroup != null) {
            symbolsGroup.getChildren().add(ledgerLinesGroup);
        } //TODO Cuando borremos el elemento que se borre la línea

    }


    /*public void correctSymbol(SymbolView symbolView) {
        this.correctingSymbol.setValue(symbolView);
    }*/


    /**
     * Change symbol position
     * @param lineSpaces
     * @param symbolToCorrect
     */
    public PositionInStaff doChangePosition(int lineSpaces, SymbolView symbolToCorrect) {
        PositionInStaff prev = null;
        try {
                //TODO Commands, undo, redo, cancel
                prev = symbolToCorrect.changePosition(lineSpaces);
                updateVerticalLayoutInStaff(symbolToCorrect.getOwner(), getShape(symbolToCorrect));
        } catch (IM3Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Cannot change position", e);
            ShowError.show(this.background.getScene().getWindow(), "Cannot change position", e);
        }
        return prev;
    }

    private AgnosticSymbolView getAgnosticSymbolView(OMRSymbol omrSymbol) throws IM3Exception {
        AgnosticSymbolView agnosticSymbolView = shapesInStaff.get(omrSymbol);
        if (agnosticSymbolView == null) {
            throw new IM3Exception("Cannot find the shape associated to symbol " + omrSymbol);
        }
        return agnosticSymbolView;
    }

    private Shape getShape(SymbolView symbolView) throws IM3Exception {
        AgnosticSymbolView agnosticSymbolView = shapesInStaff.get(symbolView.getOwner());
        if (agnosticSymbolView == null) {
            throw new IM3Exception("Cannot find the shape associated to symbol " + symbolView.getOwner());
        }
        return agnosticSymbolView.getRoot();
    }
    /**
     * Previous agnostic string type
     * @param agnosticString
     * @param symbolToCorrect
     * @return
     */
    public String doChangeSymbolType(String agnosticString, SymbolView symbolToCorrect) throws IM3Exception {
        AgnosticSymbolType agnosticSymbolType = agnosticSymbolFont.getAgnosticSymbolType(agnosticString);
        Shape shape = getShape(symbolToCorrect);

        if (agnosticSymbolType == null) {
            ShowError.show(null, "Cannot find an agnostic symbol type for " + agnosticString); //TODO null
            return null;
        } else {
            String prev = symbolToCorrect.changeSymbolType(agnosticSymbolType).toAgnosticString();
            controller.onSymbolChanged(symbolToCorrect.getOwner());
            this.shapesInStaff.remove(symbolToCorrect.getOwner());
            symbolsGroup.getChildren().remove(shape);
            try {
                Shape newShape = addSymbol(symbolToCorrect);
                //symbolToCorrect.setShapeInStaff(newShape); //TODO Esto está acoplado
                symbolToCorrect.doHighlight(true); // force new symbol to be highlighted
            } catch (IM3Exception e) {
                Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Cannot find an add symbol", e);
                ShowError.show(this.background.getScene().getWindow(), "Cannot find an add symbol", e);
            }
            return prev;
        }
    }

    /*public void doEndEdit() {
        if (correctingSymbol != null && correctingSymbol.get() != null) {
            SymbolView correctingSymbolObject = correctingSymbol.get();
            correctingSymbol.setValue(null);
            correctingSymbolObject.endEdit();
        }
    }*/

    public void remove(BoundingBoxBasedView elementView) {
        AgnosticSymbolView agnosticSymbolView = shapesInStaff.remove(elementView.getOwner());
        if (agnosticSymbolView == null) {
            throw new IM3RuntimeException("Cannot find shape for element " + elementView.getOwner());
        }
        symbolsGroup.getChildren().remove(agnosticSymbolView.getRoot());
    }

    /*public void handle(KeyEvent event) {
        switch (event.getCode()) {
            case ESCAPE:
                doEndEdit();
                break;
            case UP:
                doChangePosition(-1);
                break;
            case DOWN:
                doChangePosition(1);
                break;
        }
    }*/

    public void select(SymbolView symbolView) {
        try {
            AgnosticSymbolView agnosticSymbolView = getAgnosticSymbolView(symbolView.getOwner());
            if (!agnosticSymbolView.isSelected()) {
                selectionManager.select(agnosticSymbolView);
            }
        } catch (IM3Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Cannot find symbol to select", e);
            ShowError.show(this.background.getScene().getWindow(), "Cannot find symbol to select", e);
        }
    }

    public void unSelect(SymbolView symbolView) {
        try {
            AgnosticSymbolView agnosticSymbolView = getAgnosticSymbolView(symbolView.getOwner());
            if (agnosticSymbolView.isSelected()) {
                selectionManager.unSelect(agnosticSymbolView);
            }
        } catch (IM3Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Cannot find symbol to select", e);
            ShowError.show(this.background.getScene().getWindow(), "Cannot find symbol to select", e);
        }
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