package es.ua.dlsi.im3.core.score.layout.graphics;

import es.ua.dlsi.im3.core.score.layout.LayoutFont;
import es.ua.dlsi.im3.gui.javafx.GUIException;
import javafx.scene.Node;

public interface IJavaFXGUIElement {
    public abstract Node getJavaFXRoot() throws GUIException;
}
