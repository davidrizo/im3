package es.ua.dlsi.im3.omr.muret.editpage.symbols;

import es.ua.dlsi.im3.omr.muret.Event;
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
