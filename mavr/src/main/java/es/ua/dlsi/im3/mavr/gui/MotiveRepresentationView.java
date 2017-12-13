package es.ua.dlsi.im3.mavr.gui;

import es.ua.dlsi.im3.core.io.ExportException;
import es.ua.dlsi.im3.core.score.layout.graphics.Shape;
import es.ua.dlsi.im3.gui.javafx.GUIException;
import es.ua.dlsi.im3.mavr.model.MotiveRepresentation;
import javafx.scene.Group;
import javafx.scene.Node;

public class MotiveRepresentationView extends Group {
    MotiveRepresentation motiveRepresentation;

    public MotiveRepresentationView(MotiveRepresentation motiveRepresentation) throws ExportException, GUIException {
        this.motiveRepresentation = motiveRepresentation;
        draw();
    }

    private void draw() throws ExportException, GUIException {
        for (Shape shape: motiveRepresentation.getShapes()) {
            Node node = shape.getJavaFXRoot();
            this.getChildren().add(node);
        }
    }


}
