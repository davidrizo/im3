package es.ua.dlsi.im3.gui.score;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.io.ExportException;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.Staff;
import es.ua.dlsi.im3.core.score.layout.*;
import es.ua.dlsi.im3.core.score.layout.fonts.LayoutFonts;
import es.ua.dlsi.im3.core.score.layout.graphics.Canvas;
import es.ua.dlsi.im3.core.score.layout.graphics.GraphicsElement;
import es.ua.dlsi.im3.core.score.layout.graphics.Group;
import es.ua.dlsi.im3.gui.javafx.GUIException;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.effect.DisplacementMap;
import javafx.scene.effect.FloatMap;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.util.HashMap;

// TODO: 17/9/17 Ahora sólo usamos el HorizontalLayout
public class ScoreSongView {
    private Canvas canvas;
    private Pane mainPanel;
    private ScoreLayout layout;

    public ScoreSongView(ScoreSong scoreSong, ScoreLayout layout) throws IM3Exception {
        //layout = new HorizontalLayout(scoreSong, font, new CoordinateComponent(width.doubleValue()), new CoordinateComponent(height.doubleValue()));
        this.layout = layout;
        init(layout);
    }

    private void init(ScoreLayout layout) throws IM3Exception {
        layout.layout();
        canvas = layout.getCanvases().get(0); // it returns just one canvas
        mainPanel = new Pane();
        mainPanel.setPrefWidth(canvas.getWidth());
        mainPanel.setPrefHeight(canvas.getHeight());
        mainPanel.setBackground(new Background(new BackgroundFill(Color.WHEAT, CornerRadii.EMPTY, Insets.EMPTY)));

        createNodes();
    }

    public Pane getMainPanel() {
        return mainPanel;
    }

    private void createNodes() throws GUIException {
        for (GraphicsElement element: canvas.getElements()) {
            Node node = null;
            try {
                node = element.getJavaFXRoot();
            } catch (ExportException e) {
                throw new GUIException(e);
            }
            if (node != null) {
                // FIXME: 24/9/17 He puesto esto para probar, debe ir en PRIMUS
                /*int width = 220;
                int height = 100;

                FloatMap floatMap = new FloatMap();
                floatMap.setWidth(width);
                floatMap.setHeight(height);

                for (int i = 0; i < width; i++) {
                    double v = (Math.sin(i / 20.0 * Math.PI) - 0.5) / 40.0;
                    for (int j = 0; j < height; j++) {
                        floatMap.setSamples(i, j, 0.0f, (float) v);
                    }
                }
                DisplacementMap displacementMap = new DisplacementMap();
                displacementMap.setMapData(floatMap);

                addEffectRecursive(node, displacementMap);*/

                mainPanel.getChildren().add(node);
            }
            // TODO: 17/9/17 La interacción la debemos hacer enviándole a un controller el ID del objeto pulsado para unificarlo con web
        }
    }

    private void addEffectRecursive(Node node, DisplacementMap displacementMap) {
        node.setEffect(displacementMap);

        if (node instanceof javafx.scene.Group) {
            javafx.scene.Group group = (javafx.scene.Group) node;
            for (Node child: group.getChildren()) {
                addEffectRecursive(child, displacementMap);
            }
        }
    }
}
