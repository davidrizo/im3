package es.ua.dlsi.im3.gui.score.javafx;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.layout.ScoreLayout;
import es.ua.dlsi.im3.core.score.layout.graphics.Canvas;
import es.ua.dlsi.im3.core.score.layout.graphics.GraphicsElement;
import es.ua.dlsi.im3.gui.javafx.GUIException;
import es.ua.dlsi.im3.gui.javafx.dialogs.ShowError;
import es.ua.dlsi.im3.gui.score.EventType;
import es.ua.dlsi.im3.gui.score.InteractionPresenter;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.effect.DisplacementMap;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;

import java.time.Instant;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

// TODO: 17/9/17 Ahora sólo usamos el HorizontalLayout y siempre espaciando proporcionalmente
public class ScoreSongView {
    private Group mainPanel;
    private ScoreLayout layout;
    InteractionPresenter interactionController;

    public ScoreSongView(ScoreSong scoreSong, ScoreLayout layout) throws IM3Exception {
        //layout = new HorizontalLayout(scoreSong, font, new CoordinateComponent(width.doubleValue()), new CoordinateComponent(height.doubleValue()));
        this.layout = layout;
        interactionController = new InteractionPresenter();
        init(layout);
    }

    private void init(ScoreLayout layout) throws IM3Exception {
        layout.layout(true);
        mainPanel = new Group();
        /*mainPanel = new Pane();
        mainPanel.setPrefWidth(canvas.getWidth());
        mainPanel.setPrefHeight(canvas.getHeight());
        mainPanel.setBackground(new Background(new BackgroundFill(Color.WHEAT, CornerRadii.EMPTY, Insets.EMPTY)));*/

        createNodes();
    }

    public Group getMainPanel() {
        return mainPanel;
    }

    private void createNodes() throws GUIException {
        for (Canvas canvas: layout.getCanvases()) {
            for (GraphicsElement element : canvas.getElements()) {
                try {
                    final Node node = element.generateJavaFXRoot();
                    node.setId(element.getID());
                    registerNodeInteraction(element, node);
                    mainPanel.getChildren().add(node);
                } catch (Exception e) {
                    throw new GUIException(e);
                }
            }
        }
    }


    public void repaint() throws IM3Exception {
        for (Canvas canvas: layout.getCanvases()) {
            for (GraphicsElement element : canvas.getElements()) {
                // TODO: 26/3/18 ¿LLevar esto a GraphicsElement?
                if (element.getNotationSymbol() != null) {
                    Instant lastNotationSymbolChange = element.getNotationSymbol().getLastLayout();
                    if (lastNotationSymbolChange == null || lastNotationSymbolChange.isAfter(element.getLastRepaint())) {
                        element.updateJavaFXRoot();
                    }
                }
            }
        }
    }


    private void registerNodeInteraction(GraphicsElement element, Node node) throws IM3Exception {
        // used to identify the interaction - we use ID and not directly object to unify the method with web services
        interactionController.register(element);
        node.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
            @Override
            public void handle(ContextMenuEvent event) {
                doHandleMenuRequest(node);
            }
        });
        node.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 1) {
                    doHandleClickRequest(node);
                } else {
                    doHandleDblClickRequest(node);
                }
            }
        });
        node.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                doHandleMouseEnteredRequest(node);
            }
        });
        node.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                doHandleMouseExitedRequest(node);
            }
        });

        if (element instanceof es.ua.dlsi.im3.core.score.layout.graphics.Group) {
            es.ua.dlsi.im3.core.score.layout.graphics.Group group = (es.ua.dlsi.im3.core.score.layout.graphics.Group) element;
            for (Map.Entry<GraphicsElement, Node> gen: group.getJavaFXNodes().entrySet()) {
                registerNodeInteraction(gen.getKey(), gen.getValue());
            }
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

    private void doHandleMouseExitedRequest(Node node) {
        String ID = node.getId(); // this ID comes from the score
        try {
            interactionController.handleEvent(EventType.mouseExited, node.getId());
        } catch (IM3Exception e) {
            String message = "Cannot handle context menu request event";
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, message, e);
            ShowError.show(null, message, e);
        }
    }

    private void doHandleMouseEnteredRequest(Node node) {
        String ID = node.getId(); // this ID comes from the score
        try {
            interactionController.handleEvent(EventType.mouseEntered, node.getId());
        } catch (IM3Exception e) {
            String message = "Cannot handle context menu request event";
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, message, e);
            ShowError.show(null, message, e);
        }
    }

    private void doHandleDblClickRequest(Node node) {
        String ID = node.getId(); // this ID comes from the score
        try {
            interactionController.handleEvent(EventType.dblClick, node.getId());
        } catch (IM3Exception e) {
            String message = "Cannot handle context menu request event";
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, message, e);
            ShowError.show(null, message, e);
        }
    }

    private void doHandleClickRequest(Node node) {
        String ID = node.getId(); // this ID comes from the score
        try {
            interactionController.handleEvent(EventType.click, node.getId());
        } catch (IM3Exception e) {
            String message = "Cannot handle context menu request event";
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, message, e);
            ShowError.show(null, message, e);
        }
    }


    private void doHandleMenuRequest(Node node) {
        String ID = node.getId(); // this ID comes from the score
        try {
            interactionController.handleEvent(EventType.contextMenuRequest, node.getId());
        } catch (IM3Exception e) {
            String message = "Cannot handle context menu request event";
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, message, e);
            ShowError.show(null, message, e);
        }
    }

    public InteractionPresenter getInteractionController() {
        return interactionController;
    }

    public ScoreLayout getLayout() {
        return layout;
    }
}
