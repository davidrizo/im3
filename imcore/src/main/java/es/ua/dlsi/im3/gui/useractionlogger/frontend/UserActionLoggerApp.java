package es.ua.dlsi.im3.gui.useractionlogger.frontend;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @author drizo
 */
public class UserActionLoggerApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/useractionlogger/SceneUserActionLogger.fxml"));
        
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/fxml/useractionlogger/StylesUserActionLogger.css");
        
        stage.setTitle("User action logger viewer");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
