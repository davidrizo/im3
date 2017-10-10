package es.ua.dlsi.im3.omr.interactive.model;


import es.ua.dlsi.im3.omr.model.Symbol;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;

import java.util.List;

public class OMRStaff {
    private final OMRPage page;
    private List<Symbol> symbols;
    private Group root;
    private Rectangle boundingBox;
    private BooleanProperty selected;

    public OMRStaff(OMRPage page, double leftTopX, double leftTopY, double bottomRightX, double bottomRightY) {
        this.page = page;
        root = new Group();
        selected = new SimpleBooleanProperty(false);
        toggleSelected(); // notify parent page

        createControls(leftTopX, leftTopY, bottomRightX, bottomRightY);

        //scoreViewPane.visibleProperty().bind(btnShowTranscription.selectedProperty());
    }

    private void createControls(double leftTopX, double leftTopY, double bottomRightX, double bottomRightY) {
        boundingBox = new Rectangle(leftTopX, leftTopY, bottomRightX-leftTopX, bottomRightY-leftTopY);
        boundingBox.setStroke(Color.BLUE);
        boundingBox.setStrokeWidth(2);
        boundingBox.setStrokeDashOffset(3);
        boundingBox.setFill(Color.BLUE);
        boundingBox.setOpacity(0.1);
        root.getChildren().add(boundingBox);

        //ScoreSongView scoreSongView = new ScoreSongView();
        AnchorPane scoreViewPane = new AnchorPane();
        scoreViewPane.setPrefWidth(boundingBox.getWidth());
        scoreViewPane.setPrefHeight(boundingBox.getHeight() * 2); // TODO
        scoreViewPane.setStyle("-fx-background-color: whitesmoke");
        scoreViewPane.setLayoutX(boundingBox.getX());
        scoreViewPane.setLayoutY(boundingBox.getY()+boundingBox.getHeight());
        scoreViewPane.setOpacity(0.95);
        root.getChildren().add(scoreViewPane);

        Polygon showTriangle = new Polygon();
        double triangleSide = boundingBox.getHeight()*0.25;
        showTriangle.getPoints().add(bottomRightX);
        showTriangle.getPoints().add(bottomRightY-triangleSide);
        showTriangle.getPoints().add(bottomRightX);
        showTriangle.getPoints().add(bottomRightY);
        showTriangle.getPoints().add(bottomRightX-triangleSide);
        showTriangle.getPoints().add(bottomRightY);
        showTriangle.setFill(Color.BLUE);
        showTriangle.visibleProperty().bind(selected.not());

        Polygon hideTriangle = new Polygon();
        hideTriangle.getPoints().add(0.0);
        hideTriangle.getPoints().add(0.0);
        hideTriangle.getPoints().add(-triangleSide);
        hideTriangle.getPoints().add(0.0);
        hideTriangle.getPoints().add(0.0);
        hideTriangle.getPoints().add(triangleSide);
        hideTriangle.setFill(Color.BLUE);
        hideTriangle.visibleProperty().bind(selected);

        addInteraction(showTriangle);
        addInteraction(hideTriangle);

        root.getChildren().add(showTriangle);
        scoreViewPane.getChildren().add(hideTriangle);
        scoreViewPane.visibleProperty().bind(selected);
        AnchorPane.setRightAnchor(hideTriangle, 0.0);
        AnchorPane.setTopAnchor(hideTriangle, 0.0);
    }

    private void addInteraction(Polygon triangle) {
        triangle.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                triangle.setFill(Color.LIGHTBLUE);
            }
        });
        triangle.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                triangle.setFill(Color.BLUE);
            }
        });
        triangle.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                toggleSelected();
            }
        });
    }

    private void toggleSelected() {
        selected.set(!selected.getValue());
        if (selected.get()) {
            page.onStaffShown(this);
            Pane parentPane = (Pane) root.getParent(); // TODO: 11/10/17 Esto no está bien
            if (root.getParent() != null) {
                parentPane.getChildren().remove(root); // move to top
                parentPane.getChildren().add(root);
            }
        }
    }

    public OMRPage getPage() {
        return page;
    }

    public List<Symbol> getSymbols() {
        return symbols;
    }

    public Group getRoot() {
        return root;
    }

    public BooleanProperty selectedProperty() {
        return selected;
    }
}
