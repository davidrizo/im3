package es.ua.dlsi.im3.gui.score;

import es.ua.dlsi.im3.core.adt.Pair;
import es.ua.dlsi.im3.core.score.NotationType;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.io.ScoreSongImporter;
import es.ua.dlsi.im3.core.score.layout.CoordinateComponent;
import es.ua.dlsi.im3.core.score.layout.HorizontalLayout;
import es.ua.dlsi.im3.core.score.layout.ScoreLayout;
import es.ua.dlsi.im3.core.score.layout.fonts.LayoutFonts;
import es.ua.dlsi.im3.core.utils.FileUtils;
import es.ua.dlsi.im3.gui.javafx.dialogs.OpenSaveFileDialog;
import es.ua.dlsi.im3.gui.javafx.dialogs.ShowError;
import es.ua.dlsi.im3.gui.score.javafx.ScoreSongView;
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

public class ScoreEditController {
    private final Stage stage;
    BorderPane root;
    ScoreSongView scoreSongView;
    private ScrollPane scrollMainPane;

    public ScoreEditController(Stage stage) {
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

        MenuItem menuItemModernMusicXML = new MenuItem("Open MusicXML modern notation file");
        fileMenu.getItems().add(menuItemModernMusicXML);
        menuItemModernMusicXML.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                doOpenFile(NotationType.eModern, false,
                        "MusicXML", "xml");
            }
        });

        MenuItem menuItemModernMEI = new MenuItem("Open MEI modern notation file");
        fileMenu.getItems().add(menuItemModernMEI);
        menuItemModernMEI.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                doOpenFile(NotationType.eModern, false,
                        "MEI", "mei");
            }
        });

        MenuItem menuItemMensural = new MenuItem("Open international mensural notation file");
        fileMenu.getItems().add(menuItemMensural);
        menuItemMensural.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                doOpenFile(NotationType.eMensural, false, "MEI", "mei");
            }
        });

        MenuItem menuItemHispanicMensural = new MenuItem("Open hispanic mensural notation file");
        fileMenu.getItems().add(menuItemHispanicMensural);
        menuItemHispanicMensural.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                doOpenFile(NotationType.eMensural,  true,"MEI", "mei");
            }
        });

        return menuBar;
    }

    // FIXME: 16/10/17 Quitar notationType
    private void doOpenFile(NotationType notationType, boolean useHispanicVariant, String fileTypeName, String fileExtension) {
        try {
            OpenSaveFileDialog dlg = new OpenSaveFileDialog();
            File file = dlg.openFile("Open notation file", fileTypeName, fileExtension);
            //TODO MEI ....
            ScoreSongImporter importer = new ScoreSongImporter();

            String extension = FileUtils.getFileNameExtension(file.getName());
            ScoreSong song = importer.importSong(file, extension);
            // TODO: 17/9/17 Enlazar el modelo con el scoreSongView - usar ids como en JS
            ScoreLayout layout = new HorizontalLayout(song,
                    new CoordinateComponent(stage.widthProperty().doubleValue()),
                    new CoordinateComponent(stage.heightProperty().doubleValue()));
            scoreSongView = new ScoreSongView(layout);
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
