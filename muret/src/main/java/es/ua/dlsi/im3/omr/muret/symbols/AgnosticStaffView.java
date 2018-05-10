package es.ua.dlsi.im3.omr.muret.symbols;


import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.PositionInStaff;
import es.ua.dlsi.im3.core.score.layout.LayoutConstants;
import es.ua.dlsi.im3.omr.muret.model.OMRSymbol;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A staff containing just information about positions
 * @autor drizo
 */
public class AgnosticStaffView extends Group {
    private final AgnosticSymbolFont agnosticSymbolFont;
    Group linesGroup;
    Group symbolsGroup;
    double lineBottomPosition;

    public AgnosticStaffView(AgnosticSymbolFont agnosticSymbolFont, double width, double height) {
        this.agnosticSymbolFont = agnosticSymbolFont;
        this.linesGroup = new Group();
        this.symbolsGroup = new Group();
        Rectangle background = new Rectangle();
        background.setHeight(height);
        background.setWidth(width);
        background.setFill(Color.WHITESMOKE);

        getChildren().addAll(background, linesGroup, symbolsGroup); // order is important

        //TODO hacer una clase que sepa hacer esto y manipular cosas?
        for (int i=0; i<5; i++) {
            double y = LayoutConstants.EM+i*LayoutConstants.SPACE_HEIGHT;
            Line line = new Line();
            line.setStartX(0);
            line.setStartY(y);
            line.setEndY(y);
            line.setEndX(background.getWidth());
            line.setStroke(Color.BLACK);
            line.setStrokeWidth(1); //TODO
            linesGroup.getChildren().add(line);

            lineBottomPosition = y;
        }
    }

    private double getPosition(PositionInStaff positionInStaff) {
        double heightDifference = -(LayoutConstants.SPACE_HEIGHT * ((double)positionInStaff.getLineSpace()) / 2.0);
        return lineBottomPosition + heightDifference;
    }

    public void addSymbol(double xoffset, OMRSymbol symbol) {
        try {
            Shape shape = agnosticSymbolFont.createShape(symbol.getGraphicalSymbol().getSymbol());
            shape.setLayoutX(symbol.getX()+xoffset);

            shape.setLayoutY(getPosition(symbol.getGraphicalSymbol().getPositionInStaff()));
            symbolsGroup.getChildren().add(shape);

            //TODO Ledger lines
        } catch (IM3Exception e) {
            e.printStackTrace(); //TODO Lanzar la excepción
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Cannot find a glyph for agnostic symbol '{0}'", symbol.getGraphicalSymbol().getAgnosticString());
        }

    }
}
