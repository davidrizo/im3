package es.ua.dlsi.im3.omr.muret.symbols;


import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.PositionInStaff;
import es.ua.dlsi.im3.core.score.layout.LayoutConstants;
import es.ua.dlsi.im3.gui.javafx.dialogs.ShowError;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbolType;
import es.ua.dlsi.im3.omr.muret.model.OMRSymbol;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Orientation;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.HashMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A staff containing just information about positions
 * @autor drizo
 */
public class AgnosticStaffView extends VBox {
    Group staffGroup;

    private final AgnosticSymbolFont agnosticSymbolFont;
    private final Rectangle background;
    Group linesGroup;
    Group symbolsGroup;
    double lineBottomPosition;
    FlowPane correctionPane;
    private ObjectProperty<SymbolView> correctingSymbol;
    HashMap<OMRSymbol, Shape> shapesInStaff;
    private double regionXOffset;

    public AgnosticStaffView(AgnosticSymbolFont agnosticSymbolFont, double width, double height, double regionXOffset) throws IM3Exception {
        shapesInStaff = new HashMap<>();
        this.regionXOffset = regionXOffset;
        correctingSymbol = new SimpleObjectProperty();
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

        createCorrectionPane();
        correctionPane.visibleProperty().bind(correctingSymbol.isNotNull());

        this.getChildren().addAll(staffGroup, correctionPane);
    }

    private double getPosition(PositionInStaff positionInStaff) {
        double heightDifference = -(LayoutConstants.SPACE_HEIGHT * ((double)positionInStaff.getLineSpace()) / 2.0);
        return lineBottomPosition + heightDifference;
    }

    public Shape addSymbol(OMRSymbol symbol) throws IM3Exception {
        try {
            Shape shape = agnosticSymbolFont.createShape(symbol.getGraphicalSymbol().getSymbol());
            shape.setLayoutX(symbol.getX()+regionXOffset);

            symbolsGroup.getChildren().add(shape);
            updateVerticalLayoutInStaff(symbol, shape);

            shapesInStaff.put(symbol, shape);
            return shape;
            //TODO Ledger lines
        } catch (IM3Exception e) {
            e.printStackTrace();
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Cannot find a glyph for agnostic symbol '{0}'", symbol.getGraphicalSymbol().getAgnosticString());
            throw e;
        }
    }

    private void updateVerticalLayoutInStaff(OMRSymbol omrSymbol, Shape shape) {
        shape.setLayoutY(getPosition(omrSymbol.getGraphicalSymbol().getPositionInStaff()));
    }

    /**
     * We do not create the same for all staves because the order of symbols may change
     */
    private void createCorrectionPane() throws IM3Exception {
        correctionPane = new FlowPane();
        correctionPane.prefWrapLengthProperty().bind(background.widthProperty());
        correctionPane.setOrientation(Orientation.HORIZONTAL);
        correctionPane.setRowValignment(VPos.CENTER);
        correctionPane.setColumnHalignment(HPos.CENTER);

        TreeSet<String> orderedAgnosticStrings =  new TreeSet<>(agnosticSymbolFont.getGlyphs().keySet());

        //TODO Diseñar la usabilidad de todo esto
        // see http://aalmiray.github.io/ikonli/cheat-sheet-openiconic.html
        Button buttonPositionDown = new Button("Position up", new FontIcon("oi-arrow-bottom"));
        buttonPositionDown.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                doChangePosition(-1);
            }
        });
        Button buttonPositionUp = new Button("Position down", new FontIcon("oi-arrow-top"));
        buttonPositionUp.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                doChangePosition(1);
            }
        });
        correctionPane.getChildren().add(buttonPositionDown);
        correctionPane.getChildren().add(buttonPositionUp);

        for (String agnosticString: orderedAgnosticStrings) {
            Shape shape = agnosticSymbolFont.createShape(agnosticString);
            shape.setLayoutX(25);
            shape.setLayoutY(45);
            Pane pane = new Pane(shape); // required
            pane.setPrefHeight(100);
            pane.setPrefWidth(50);
            Button button = new Button(agnosticString, pane);
            correctionPane.getChildren().add(button);
            button.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    doChangeSymbolType(agnosticString);
                }
            });
        }

        Button buttonClose = new Button("Close correction", new FontIcon("oi-x"));
        correctionPane.getChildren().add(buttonClose);
        buttonClose.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                correctingSymbol.setValue(null);
            }
        });
    }


    public void correctSymbol(SymbolView symbolView) {
        this.correctingSymbol.setValue(symbolView);
    }


    /**
     * Change symbol position
     * @param lineSpaces
     */
    private void doChangePosition(int lineSpaces) {
        try {
            //TODO Commands, undo, redo, cancel
            correctingSymbol.get().changePosition(lineSpaces);
            Shape shape = shapesInStaff.get(correctingSymbol.get().getOwner());
            if (shape == null) {
                throw new IM3Exception("Cannot find the shape associated to symbol " + correctingSymbol.get());
            }
            updateVerticalLayoutInStaff(correctingSymbol.get().getOwner(), shape);
        } catch (IM3Exception e) {
            e.printStackTrace(); //TODO logs
            ShowError.show(null, "Cannot change position", e); // TODO stage
        }
    }

    private void doChangeSymbolType(String agnosticString) {
        AgnosticSymbolType agnosticSymbolType = agnosticSymbolFont.getAgnosticSymbolType(agnosticString);
        Shape shape = shapesInStaff.get(correctingSymbol.get().getOwner());
        if (shape == null) {
            ShowError.show(null, "Cannot find the shape associated to symbol " + correctingSymbol.get());
            return;
        }

        if (agnosticSymbolType == null) {
            ShowError.show(null, "Cannot find an agnostic symbol type for " + agnosticString);
            return;
        } else {
            this.correctingSymbol.get().changeSymbolType(agnosticSymbolType);
            this.shapesInStaff.remove(correctingSymbol.get().getOwner());
            symbolsGroup.getChildren().remove(shape);
            try {
                Shape newShape = addSymbol(correctingSymbol.get().getOwner());
                correctingSymbol.get().setShapeInStaff(newShape); //TODO Esto está acoplado
            } catch (IM3Exception e) {
                e.printStackTrace(); //TODO log
                ShowError.show(null, "Cannot find an add symbol", e);
            }
        }
    }



}
