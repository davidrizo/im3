package es.ua.dlsi.im3.gui.score;

import es.ua.dlsi.im3.core.adt.Pair;
import es.ua.dlsi.im3.core.score.NotationType;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.io.ScoreSongImporter;
import es.ua.dlsi.im3.core.utils.FileUtils;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.File;

public class ScoreViewApp extends Application {
    ScoreViewController controller;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        controller = new ScoreViewController(primaryStage);

        Scene scene = new Scene(controller.getRoot());
        primaryStage.setScene(scene);
        primaryStage.setWidth(960);
        primaryStage.setHeight(700);
        primaryStage.setTitle("IM3 Score view application");
        primaryStage.show();
    }

}
