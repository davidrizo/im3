package es.ua.dlsi.im3.omr.muret;

import es.ua.dlsi.im3.gui.interaction.ISelectable;
import es.ua.dlsi.im3.gui.interaction.ISelectableTraversable;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;

/**
 * It is a usual shape related to a symbol view
 * @autor drizo
 */
public class AgnosticSymbolView implements ISelectable {
    private static final Color DEFAULT_COLOR = Color.BLACK;
    private static final Color HOVER_COLOR = Color.BLUE;
    private static final Color SELECTED_COLOR = Color.RED;
    public static final String PREFIX = "ASV ";

    DocumentAnalysisSymbolsDiplomaticMusicController controller;
    AgnosticStaffView agnosticStaffView;
    Shape shape;
    SymbolView symbolView;
    boolean selected;

    public AgnosticSymbolView(DocumentAnalysisSymbolsDiplomaticMusicController controller, AgnosticStaffView agnosticStaffView, Shape shape, SymbolView symbolView) {
        this.controller = controller;
        this.agnosticStaffView = agnosticStaffView;
        this.shape = shape;
        this.shape.setFill(DEFAULT_COLOR);
        this.symbolView = symbolView;

        initInteraction();
    }

    private void initInteraction() {
        shape.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                onStartHover();
            }
        });

        shape.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                onEndHover();
            }
        });

        shape.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                agnosticStaffView.getSelectionManager().select(AgnosticSymbolView.this);
            }
        });
    }

    @Override
    public ISelectableTraversable getSelectionParent() {
        return agnosticStaffView;
    }

    @Override
    public void onSelect() {
        if (!selected) {
            selected = true;
            shape.setFill(SELECTED_COLOR);
            controller.doSelect(symbolView);
        }
    }

    @Override
    public void onUnselect() {
        if (selected) {
            selected = false;
            shape.setFill(DEFAULT_COLOR);
            controller.onUnselected(symbolView);
        }

    }

    @Override
    public void onStartHover() {
        if (!selected) {
            shape.setFill(HOVER_COLOR);
        }
    }

    @Override
    public void onEndHover() {
        if (!selected) {
            shape.setFill(DEFAULT_COLOR);
        }
    }

    @Override
    public String getUniqueID() {
        return PREFIX + hashCode();
    }

    public Shape getRoot() {
        return shape;
    }

    public SymbolView getSymbolView() {
        return symbolView;
    }

    public boolean isSelected() {
        return selected;
    }
}
