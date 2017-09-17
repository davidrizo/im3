package es.ua.dlsi.im3.gui.score;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.layout.FontFactory;
import es.ua.dlsi.im3.core.score.layout.HorizontalLayout;
import es.ua.dlsi.im3.core.score.layout.LayoutFont;
import es.ua.dlsi.im3.core.score.layout.graphics.Canvas;
import es.ua.dlsi.im3.core.score.layout.graphics.GraphicsElement;
import es.ua.dlsi.im3.gui.javafx.GUIException;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

// TODO: 17/9/17 Ahora sólo usamos el HorizontalLayout
public class ScoreSongView {
    private final Canvas canvas;
    private final Pane mainPanel;
    private final LayoutFont bravura;

    public ScoreSongView(ScoreSong scoreSong) throws IM3Exception {
        bravura = FontFactory.getInstance().getBravuraFont();
        HorizontalLayout layout = new HorizontalLayout(scoreSong);
        layout.layout();
        canvas = layout.getCanvases()[0]; // it returns just one canvas
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
            Node node = element.getJavaFXRoot(bravura);
            if (node != null) {
                mainPanel.getChildren().add(node);
            }
            // TODO: 17/9/17 La interacción la debemos hacer enviándole a un controller el ID del objeto pulsado para unificarlo con web
        }
    }
}
