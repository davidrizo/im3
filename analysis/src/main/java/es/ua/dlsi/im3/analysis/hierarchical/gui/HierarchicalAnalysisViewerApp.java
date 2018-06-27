package es.ua.dlsi.im3.analysis.hierarchical.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @autor drizo
 */
public class HierarchicalAnalysisViewerApp extends Application {
    private static Stage mainStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        mainStage = primaryStage;
        Scene scene = new Scene(new Group());
        primaryStage.setScene(scene);

        Parent root = FXMLLoader.load(getClass().getResource("/fxml/hierarchical/viewer.fxml"));
        scene.setRoot(root);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    public static Stage getMainStage() {
        return mainStage;
    }
}
