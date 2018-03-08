package es.ua.dlsi.im3.omr.wrimus.homuscorrector;

import es.ua.dlsi.im3.core.score.PositionInStaff;
import es.ua.dlsi.im3.core.score.PositionsInStaff;
import es.ua.dlsi.im3.core.utils.FileUtils;
import es.ua.dlsi.im3.gui.javafx.dialogs.OpenFolderDialog;
import es.ua.dlsi.im3.gui.javafx.dialogs.ShowError;
import es.ua.dlsi.im3.omr.classifiers.traced.Coordinate;
import es.ua.dlsi.im3.omr.wrimus.Glyph;
import es.ua.dlsi.im3.omr.wrimus.HomusReader;
import es.ua.dlsi.im3.omr.wrimus.Stroke;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HomusCorrectorController implements Initializable {
    final int STAFF_TOP_MARGIN = 110; // TODO
    final int LINE_THICKNESS = 3; // as defined in the Homus Dataset
    final int LINE_SEPARATION = 14; // as defined in the Homus Dataset
    final int CENTER_ICON_SIZE = LINE_SEPARATION*2;
    double MAX_X = 500;


    @FXML
    ListView<File> lvNotCorrected;

    @FXML
    ListView<File> lvCorrected;

    @FXML
    Pane glyphPane;

    @FXML
    Label labelGlyphName;

    @FXML
    Label labelSymbolType;

    @FXML
    Button btnSave;

    @FXML
    ToolBar toolbar;

    ObjectProperty<Glyph> selectedGlyph;
    ObjectProperty<File> selectedFile;

    DoubleProperty centerXProperty;
    DoubleProperty centerYProperty;
    Group centerIcon;

    File homusFolder;
    private HomusReader homusReader;
    private Line[] pentagramLines;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        homusReader = new HomusReader();
        selectedFile = new SimpleObjectProperty<>();
        selectedGlyph = new SimpleObjectProperty<>();
        toolbar.disableProperty().bind(selectedGlyph.isNull());
        labelGlyphName.textProperty().bind(selectedFile.asString());
        labelGlyphName.visibleProperty().bind(selectedFile.isNotNull());
        labelSymbolType.visibleProperty().bind(selectedFile.isNotNull());
        createCenterIcon();

        lvNotCorrected.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<File>() {
            @Override
            public void changed(ObservableValue<? extends File> observable, File oldValue, File newValue) {
                try {
                    loadGlyph(newValue);
                } catch (IOException e) {
                    e.printStackTrace();
                    ShowError.show(HomusCorrectorApp.getMainStage(), "Cannot read HOMUS file", e);
                }
            }
        });
    }

    @FXML
    private void handleOpen() {
        OpenFolderDialog dlg = new OpenFolderDialog();
        File folder = dlg.openFolder("Select the folder with HOMUS samples");
        if (folder != null) {
            try {
                readHomusFolder(folder);
            } catch (IOException e) {
                e.printStackTrace();
                ShowError.show(HomusCorrectorApp.getMainStage(), "Cannot read HOMUS samples", e);
            }
        }
    }

    private void readHomusFolder(File folder) throws IOException {
        lvNotCorrected.getItems().clear();
        homusFolder = folder;
        ArrayList<File> files = new ArrayList<>();
        FileUtils.readFiles(folder, files, "txt");
        files.sort(new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        for (File file : files) {
            lvNotCorrected.getItems().add(file);
        }
    }

    @FXML
    private void handleSave() {

    }

    private void loadGlyph(File file) throws IOException {
        Logger.getLogger(HomusCorrectorController.class.getName()).log(Level.INFO, "Selected file {0}" , file);
        Glyph glyph = homusReader.read(file);
        selectedGlyph.setValue(glyph);
        selectedFile.setValue(file);
        drawGlyph(glyph);
    }

    private void drawGlyph(Glyph glyph) {
        labelSymbolType.setText(glyph.getSymbol().getName());
        glyphPane.getChildren().clear();
        glyphPane.getChildren().add(centerIcon);

        Group group = new Group();
        for (Stroke stroke: glyph.getStrokes()) {
            Polyline polyline = new Polyline();
            polyline.setStrokeWidth(LINE_THICKNESS);
            polyline.setStroke(Color.BLUE);
            for (Coordinate point: stroke.getPoints()) {
                polyline.getPoints().add(point.getX());
                polyline.getPoints().add(point.getY());
            }
            group.getChildren().add(polyline);
        }
        glyphPane.getChildren().add(group);

        drawPentagram();
        drawCenter(glyph);
    }

    private void drawPentagram() {
        pentagramLines = new Line[5];
        Group pentagramGroup = new Group();
        for (int i=0; i<5; i++) {
            double y = STAFF_TOP_MARGIN + LINE_SEPARATION*i;
            Line line = new Line(0, y, MAX_X, y);
            line.setStroke(Color.BLACK);
            line.setStrokeWidth(1);
            pentagramGroup.getChildren().add(line);
            pentagramLines[4-i] = line;
        }
        glyphPane.getChildren().add(pentagramGroup);
    }


    private void createCenterIcon() {
        centerIcon = new Group();
        centerXProperty = new SimpleDoubleProperty();
        centerYProperty = new SimpleDoubleProperty();
        // draw a cross
        Line left = new Line();
        Line right = new Line();

        DoubleBinding fromX = centerXProperty.subtract(CENTER_ICON_SIZE / 2);
        DoubleBinding toX = centerXProperty.add(CENTER_ICON_SIZE / 2);

        DoubleBinding fromY = centerYProperty.subtract(CENTER_ICON_SIZE / 2);
        DoubleBinding toY = centerYProperty.add(CENTER_ICON_SIZE / 2);

        left.startXProperty().bind(fromX);
        left.startYProperty().bind(fromY);
        left.endXProperty().bind(toX);
        left.endYProperty().bind(toY);
        left.setStrokeWidth(1);
        left.setStroke(Color.RED);
        centerIcon.getChildren().add(left);

        right.startXProperty().bind(fromX);
        right.startYProperty().bind(toY);
        right.endXProperty().bind(toX);
        right.endYProperty().bind(fromY);
        right.setStrokeWidth(1);
        right.setStroke(Color.RED);
        centerIcon.getChildren().add(right);
    }


    private void drawCenter(Glyph glyph) {
        centerXProperty.setValue(glyph.getHorizontalCentroid());
        centerYProperty.setValue(pentagramLines[2].getStartY());
    }


    private void changeCenterY(PositionInStaff positionsInStaff) {
        double y = pentagramLines[0].getStartY() - positionsInStaff.getLineSpace() * LINE_SEPARATION/2;
        centerYProperty.setValue(y);
    }

    @FXML
    private void handleScaleDown() {

    }

    @FXML
    private void handleScaleUp() {

    }

    @FXML
    private void handleL5() {
        changeCenterY(PositionsInStaff.LINE_5);
    }

    @FXML
    private void handleS4() {
        changeCenterY(PositionsInStaff.SPACE_4);
    }

    @FXML
    private void handleL4() {
        changeCenterY(PositionsInStaff.LINE_4);
    }

    @FXML
    private void handleS3() {
        changeCenterY(PositionsInStaff.SPACE_3);
    }

    @FXML
    private void handleL3() {
        changeCenterY(PositionsInStaff.LINE_3);
    }

    @FXML
    private void handleS2() {
        changeCenterY(PositionsInStaff.SPACE_2);
    }

    @FXML
    private void handleL2() {
        changeCenterY(PositionsInStaff.LINE_2);
    }

    @FXML
    private void handleS1() {
        changeCenterY(PositionsInStaff.SPACE_1);
    }

    @FXML
    private void handleL1() {
        changeCenterY(PositionsInStaff.LINE_1);
    }


}
