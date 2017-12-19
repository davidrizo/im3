package es.ua.dlsi.im3.omr.interactive.editpage.symbols;

import es.ua.dlsi.im3.omr.interactive.Event;
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
