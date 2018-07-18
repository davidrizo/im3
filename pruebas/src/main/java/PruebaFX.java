import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.net.MalformedURLException;

public class PruebaFX extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        FlowPane pane = new FlowPane();
        pane.setPrefHeight(200);
        pane.setPrefWidth(500);
        Scene scene = new Scene(pane);
        for (int i=0; i<5; i++) {
            TextField textField = new TextField(i+"");
            pane.getChildren().add(textField);
        }
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
