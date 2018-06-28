package es.ua.dlsi.im3.mavr.gui;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.layout.CoordinateComponent;
import es.ua.dlsi.im3.core.score.layout.HorizontalLayout;
import es.ua.dlsi.im3.core.score.layout.fonts.LayoutFonts;
import es.ua.dlsi.im3.gui.score.javafx.ScoreSongView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class MotivesController implements Initializable {
    @FXML
    ListView<MotiveView> lvMotives;

    @FXML
    AnchorPane scoreViewPane;

    ScoreSongView scoreSongView;

    private Model model;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setModel(Model model) throws IM3Exception {
        this.model = model;
        load();
    }

    private void load() throws IM3Exception {
        CoordinateComponent width = new CoordinateComponent(1000); //TODO
        CoordinateComponent height = new CoordinateComponent(500); //TODO
        HorizontalLayout horizontalLayout = new HorizontalLayout(model.getScoreSong(), LayoutFonts.bravura, width, height);
        scoreSongView = new ScoreSongView(horizontalLayout);
        scoreViewPane.getChildren().add(scoreSongView.getMainPanel());
    }

    public Model getModel() {
        return model;
    }
}
