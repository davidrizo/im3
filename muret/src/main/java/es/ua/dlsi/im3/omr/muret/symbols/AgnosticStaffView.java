package es.ua.dlsi.im3.omr.muret.symbols;


import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.score.PositionInStaff;
import es.ua.dlsi.im3.core.score.Staff;
import es.ua.dlsi.im3.core.score.layout.LayoutConstants;
import es.ua.dlsi.im3.gui.javafx.dialogs.ShowError;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbolType;
import es.ua.dlsi.im3.omr.muret.BoundingBoxBasedView;
import es.ua.dlsi.im3.omr.muret.ImageBasedAbstractController;
import es.ua.dlsi.im3.omr.muret.model.OMRSymbol;
import javafx.scene.Group;
import javafx.scene.layout.*;
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
public class AgnosticStaffView extends VBox {
    private static final double LEDGER_LINE_EXTRA_LENGTH = 8;
    private static final double LEDGER_LINE_WIDTH = 20;
    private final ImageBasedAbstractController controller;
    private final Line[] lines;
    Group staffGroup;

    private final AgnosticSymbolFont agnosticSymbolFont;
    private final Rectangle background;
    Group linesGroup;
    Group symbolsGroup;
    double lineBottomPosition;
    FlowPane correctionPane;
    //private ObjectProperty<SymbolView> correctingSymbol;
    HashMap<OMRSymbol, Shape> shapesInStaff;
    private double regionXOffset;

    public AgnosticStaffView(ImageBasedAbstractController controller, AgnosticSymbolFont agnosticSymbolFont, double width, double height, double regionXOffset) throws IM3Exception {
        this.controller = controller;
        shapesInStaff = new HashMap<>();
        this.regionXOffset = regionXOffset;
        //correctingSymbol = new SimpleObjectProperty();
        staffGroup = new Group();
        this.agnosticSymbolFont = agnosticSymbolFont;
        this.linesGroup = new Group();
        this.symbolsGroup = new Group();
        background = new Rectangle();
        background.setHeight(height);
        background.setWidth(width);
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
            line.setEndX(background.getWidth());
            line.setStroke(Color.BLACK);
            line.setStrokeWidth(1); //TODO
            linesGroup.getChildren().add(line);

            lineBottomPosition = y;
        }

        this.getChildren().addAll(staffGroup);
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

            shapesInStaff.put(symbol, shape);
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
                Shape shape = shapesInStaff.get(symbolToCorrect.getOwner());
                if (shape == null) {
                    throw new IM3Exception("Cannot find the shape associated to symbol " + symbolToCorrect);
                }
                updateVerticalLayoutInStaff(symbolToCorrect.getOwner(), shape);
        } catch (IM3Exception e) {
            e.printStackTrace(); //TODO logs
            ShowError.show(null, "Cannot change position", e); // TODO stage
        }
        return prev;
    }

    /**
     * Previous agnostic string type
     * @param agnosticString
     * @param symbolToCorrect
     * @return
     */
    public String doChangeSymbolType(String agnosticString, SymbolView symbolToCorrect) {
        AgnosticSymbolType agnosticSymbolType = agnosticSymbolFont.getAgnosticSymbolType(agnosticString);
        Shape shape = shapesInStaff.get(symbolToCorrect.getOwner());
        if (shape == null) {
            ShowError.show(null, "Cannot find the shape associated to symbol " + symbolToCorrect);
            return null;
        }

        if (agnosticSymbolType == null) {
            ShowError.show(null, "Cannot find an agnostic symbol type for " + agnosticString); //TODO null
            return null;
        } else {
            String prev = symbolToCorrect.changeSymbolType(agnosticSymbolType).toAgnosticString();
            try {
                controller.onSymbolChanged(symbolToCorrect.getOwner());
            } catch (IM3Exception e) {
                e.printStackTrace();
                ShowError.show(null, "Cannot change symbol", e); //TODO null
            }
            this.shapesInStaff.remove(symbolToCorrect.getOwner());
            symbolsGroup.getChildren().remove(shape);
            try {
                Shape newShape = addSymbol(symbolToCorrect);
                symbolToCorrect.setShapeInStaff(newShape); //TODO Esto está acoplado
                symbolToCorrect.doHighlight(true); // force new symbol to be highlighted
            } catch (IM3Exception e) {
                e.printStackTrace(); //TODO log
                ShowError.show(null, "Cannot find an add symbol", e);
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

    public void onSymbolRemoved(BoundingBoxBasedView elementView) {
        Shape shape = shapesInStaff.remove(elementView.getOwner());
        if (shape == null) {
            throw new IM3RuntimeException("Cannot find shape for element " + elementView.getOwner());
        }
        symbolsGroup.getChildren().remove(shape);
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
}
