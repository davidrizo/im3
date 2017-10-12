package es.ua.dlsi.im3.omr.interactive.model;


import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.layout.CoordinateComponent;
import es.ua.dlsi.im3.core.score.layout.fonts.LayoutFonts;
import es.ua.dlsi.im3.core.score.staves.Pentagram;
import es.ua.dlsi.im3.gui.score.ScoreSongView;
import es.ua.dlsi.im3.omr.interactive.OMRController;
import es.ua.dlsi.im3.omr.model.Symbol;
import es.ua.dlsi.im3.omr.old.mensuraltagger.components.SymbolView;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;

import java.util.HashMap;
import java.util.List;

public class OMRStaff<SymbolType> {
    private final OMRPage page;
    private List<Symbol<SymbolType>> symbols;
    private Group root;
    private Rectangle boundingBox;
    private BooleanProperty selected;
    private ScoreSong scoreSong;
    private Pentagram sourceStaff;
    private ScoreLayer sourceLayer;
    //private Pentagram transducedStaff;
    //private ScoreLayer transducedLayer;
    private AnchorPane scoreViewPane;
    private StaffSymbolsInteraction staffSymbolsInteraction;
    private SymbolView<SymbolType> currentSymbolView;

    public OMRStaff(OMRPage page, ScoreSong scoreSong, double leftTopX, double leftTopY, double bottomRightX, double bottomRightY) throws IM3Exception {
        this.page = page;
        this.scoreSong = scoreSong;

        root = new Group();
        selected = new SimpleBooleanProperty(false);

        createControls(leftTopX, leftTopY, bottomRightX, bottomRightY);

        createScoreSongStaff();
        createScoreView();

        staffSymbolsInteraction = new StaffSymbolsInteraction(this, page.getOMRController().getSliderTimer().valueProperty());

        selected.addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue != null && newValue) {
                    staffSymbolsInteraction.enable();
                } else {
                    staffSymbolsInteraction.disable();
                }
            }
        });

        toggleSelected(); // activate and notify parent page to hide other staves
    }

    public OMRController getOmrController() {
        return page.getOMRController();
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
        scoreViewPane = new AnchorPane();
        scoreViewPane.setPrefWidth(boundingBox.getWidth());
        scoreViewPane.setPrefHeight(boundingBox.getHeight());
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
                event.consume();
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

    public List<Symbol<SymbolType>> getSymbols() {
        return symbols;
    }

    public Group getRoot() {
        return root;
    }

    public BooleanProperty selectedProperty() {
        return selected;
    }

    // TODO: 11/10/17 Tipos de staff
    private void createScoreSongStaff() throws IM3Exception {
        sourceStaff = new Pentagram(scoreSong, "1", 1); //TODO
        sourceStaff.setNotationType(NotationType.eMensural); //TODO
        scoreSong.addStaff(sourceStaff);
        ScorePart part = scoreSong.addPart();
        part.addStaff(sourceStaff);
        sourceLayer = part.addScoreLayer(sourceStaff);

        /*transducedStaff = new Pentagram(scoreSong, "2", 2); //TODO
        transducedStaff.setNotationType(NotationType.eModern); 
        scoreSong.addStaff(transducedStaff);
        ScorePart transducedPart = scoreSong.addPart();
        transducedPart.addStaff(transducedStaff);
        transducedLayer = transducedPart.addScoreLayer(transducedStaff);*/

        // FIXME: 11/10/17 REMOVE NOW!!!
        //sourceStaff.addClef(new ClefG2());
    }

    private void createScoreView() throws IM3Exception {
        HashMap<Staff, LayoutFonts> layoutFontsHashMap = new HashMap<>();
        layoutFontsHashMap.put(sourceStaff, LayoutFonts.capitan);
        //layoutFontsHashMap.put(transducedStaff, LayoutFonts.bravura);
        ImitationLayout imitationLayout = new ImitationLayout(scoreSong, layoutFontsHashMap,
                new CoordinateComponent(this.boundingBox.widthProperty().getValue()),
                        new CoordinateComponent(this.boundingBox.heightProperty().getValue()));

        ScoreSongView scoreSongView = new ScoreSongView(scoreSong, imitationLayout);
        scoreViewPane.getChildren().add(0, scoreSongView.getMainPanel()); // add to back to keep interaction triangle visible
        AnchorPane.setLeftAnchor(scoreSongView.getMainPanel(), 0.0);
        AnchorPane.setRightAnchor(scoreSongView.getMainPanel(), 0.0);
        AnchorPane.setTopAnchor(scoreSongView.getMainPanel(), 0.0);
        AnchorPane.setBottomAnchor(scoreSongView.getMainPanel(), 0.0);
    }

    public boolean contains(double x, double y) {
        //return boundingBox.contains(x, y);
        // FIXME: 12/10/17
        return true;
    }

    public SymbolView<SymbolType> getCurrentSymbolView() {
        return currentSymbolView;
    }

    public void createNewSymbol() throws IM3Exception {
        // FIXME: 12/10/17
        Symbol<SymbolType> symbol = new Symbol<>(page.getBufferedImage());
        currentSymbolView = new SymbolView<>(symbol, Color.BLUE); // TODO Intercalarlos
        root.getChildren().add(currentSymbolView);
        //currentSymbolView =
    }

    public Symbol<SymbolType> newSymbolComplete() throws IM3Exception {
        // FIXME: 12/10/17
        return currentSymbolView.getSymbol();
    }

    public void addSymbol(Symbol<SymbolType> symbol) {
        // FIXME: 12/10/17
        //currentSymbolView =
    }

    public void removeSymbol(Symbol<SymbolType> symbol) {
        // FIXME: 12/10/17
        //currentSymbolView =
    }
}
