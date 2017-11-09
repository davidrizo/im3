package es.ua.dlsi.im3.gui.javafx;

import es.ua.dlsi.im3.core.utils.FileUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The fxml:controller cannot be set in the FXML file
 * @author drizo
 */
public abstract class FXMLViewController {
    Parent root;

    protected FXMLViewController(Stage stage, String fxmlName) {
        this(stage, fxmlName, true);
    }

    protected FXMLViewController(Stage w, String fxmlName, boolean createScene) {
        init(fxmlName);
        if (createScene) {
            Scene scene = new Scene(root); // root loaded in init
            w.setScene(scene);
        }
    }

    private void init(String fxmlName) {
        try {
            final URL resource = this.getClass().getResource(fxmlName);
            final URL css = this.getClass().getResource(FileUtils.getFileNameWithoutExtension(fxmlName)+".css");
            if (resource == null) {
                String msg = "The resource with name /fxml/" + fxmlName + " has not been found";
                Logger.getLogger(FXMLViewController.class.getName()).log(Level.SEVERE, msg);
                throw new RuntimeException(msg);
            }

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(resource);
            loader.setController(this);
            try {
                loader.load();
                root = loader.getRoot();
                if (css != null) {
                    String cssStr = css.toString();
                    root.getStylesheets().add(cssStr);
                    Logger.getLogger(FXMLViewController.class.getName()).log(Level.INFO, "Adding style sheet {0}", cssStr);
                }
            } catch (IOException ex) {
                Logger.getLogger(FXMLViewController.class.getName()).log(Level.SEVERE, null, ex);
                throw new RuntimeException("Possible error: The fxml:controller tag in the FXML cannot be set when using FXMLViewController: correct it in " + fxmlName , ex);
            }
        } catch (IOException ex) {
            Logger.getLogger(FXMLViewController.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
    }

    public Parent getRoot() {
        return root;
    }
}