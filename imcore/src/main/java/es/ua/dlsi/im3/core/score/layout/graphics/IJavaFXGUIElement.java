package es.ua.dlsi.im3.core.score.layout.graphics;

import es.ua.dlsi.im3.core.io.ExportException;
import es.ua.dlsi.im3.core.score.layout.LayoutFont;
import es.ua.dlsi.im3.gui.javafx.GUIException;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;
import javafx.scene.paint.Color;

public interface IJavaFXGUIElement {
    public abstract Node getJavaFXRoot() throws GUIException, ExportException;
    public abstract void setJavaFXColor(Color color);
}
