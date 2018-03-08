package es.ua.dlsi.im3.gui.javafx;

import es.ua.dlsi.im3.core.adt.Pair;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;

public class ViewLoader {
    public static final <T> Pair<T, Parent> loadView(String fxml) throws IOException {
        FXMLLoader loader = new FXMLLoader(ViewLoader.class.getResource("/fxml/" + fxml));
        Parent sceneMain = loader.load();
        T controller = loader.getController();
        return new Pair<>(controller, sceneMain);
    }
}
