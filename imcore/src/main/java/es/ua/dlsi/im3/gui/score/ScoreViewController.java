package es.ua.dlsi.im3.gui.score;

import es.ua.dlsi.im3.core.adt.Pair;
import es.ua.dlsi.im3.core.score.NotationType;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.io.ScoreSongImporter;
import es.ua.dlsi.im3.core.utils.FileUtils;
import es.ua.dlsi.im3.gui.javafx.dialogs.OpenSaveFileDialog;
import es.ua.dlsi.im3.gui.javafx.dialogs.ShowError;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.File;

public class ScoreViewController {
    private final Stage stage;
    BorderPane root;
    ScoreSongView scoreSongView;
    private ScrollPane scrollMainPane;

    public ScoreViewController(Stage stage) {
        this.stage = stage;
        init();
    }

    private void init() {
        root = new BorderPane();
        root.setTop(buildMenu());
        scrollMainPane = new ScrollPane();
        root.setCenter(scrollMainPane);
        BorderPane.setMargin(scrollMainPane, new Insets(30, 30, 30, 30)); // TODO: 17/9/17 Márgenes
    }


    private MenuBar buildMenu() {
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");
        menuBar.getMenus().add(fileMenu);

        MenuItem menuItemModern = new MenuItem("Open modern notation file");
        fileMenu.getItems().add(menuItemModern);
        menuItemModern.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                doOpenFile(NotationType.eModern,
                        new Pair<String, String>("MusicXML", "xml"),
                        new Pair<String, String>("MEI", "mei"));
            }
        });

        MenuItem menuItemMensural = new MenuItem("Open mensural notation file");
        fileMenu.getItems().add(menuItemMensural);
        menuItemMensural.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                doOpenFile(NotationType.eMensural, new Pair<String, String>("MEI", "mei"));
            }
        });

        return menuBar;
    }

    private void doOpenFile(NotationType notationType, Pair<String, String> ... fileTypes) {
        try {
            OpenSaveFileDialog dlg = new OpenSaveFileDialog();
            File file = dlg.openFile("Open notation file", "MusicXML files", "xml");
            //TODO MEI ....
            ScoreSongImporter importer = new ScoreSongImporter();

            String extension = FileUtils.getFileNameExtension(file.getName());
            ScoreSong song = importer.importSong(notationType, file, extension);
            // TODO: 17/9/17 Enlazar el modelo con el scoreSongView - usar ids como en JS
            scoreSongView = new ScoreSongView(song);
            scrollMainPane.setContent(scoreSongView.getMainPanel());
        } catch (Exception e) {
            e.printStackTrace();
            ShowError.show(stage, "Cannot open file", e);
        }
    }

    public BorderPane getRoot() {
        return root;
    }
}
