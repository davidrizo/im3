package es.ua.dlsi.im3.omr.muret.symbols;

import es.ua.dlsi.im3.omr.muret.model.OMRPage;
import es.ua.dlsi.im3.omr.muret.BoundingBoxBasedView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 * @autor drizo
 */
public class PageView extends BoundingBoxBasedView<OMRPage> {
    VBox vBox;

    public PageView(OMRPage owner, Color color) {
        super(null, owner, color);
    }

    @Override
    protected void onLabelContextMenuRequested(ContextMenuEvent event) {

    }

    @Override
    protected void onRegionMouseClicked(MouseEvent event) {

    }
}
