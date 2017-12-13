package es.ua.dlsi.im3.omr.interactive.pageedit.events;

import es.ua.dlsi.im3.omr.interactive.pageedit.Event;
import es.ua.dlsi.im3.omr.interactive.pageedit.SymbolView;
import javafx.scene.input.MouseEvent;

public class SymbolEditEvent extends Event<MouseEvent> {
    SymbolView symbolView;
    public SymbolEditEvent(MouseEvent event, SymbolView symbolView) {
        super(event);
        this.symbolView = symbolView;
    }

    public SymbolView getSymbolView() {
        return symbolView;
    }
}
