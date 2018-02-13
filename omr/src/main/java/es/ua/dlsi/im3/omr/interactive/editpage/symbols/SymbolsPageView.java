package es.ua.dlsi.im3.omr.interactive.editpage.symbols;

import es.ua.dlsi.im3.omr.interactive.Event;
import es.ua.dlsi.im3.omr.interactive.editpage.RegionBaseView;
import es.ua.dlsi.im3.omr.interactive.editpage.RegionBasedPageView;
import es.ua.dlsi.im3.omr.interactive.model.OMRPage;
import es.ua.dlsi.im3.omr.interactive.model.OMRRegion;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class SymbolsPageView extends RegionBasedPageView<PageSymbolsEditController, SymbolsRegionView, SymbolViewState> {
    private SymbolView editingSymbol;

    public SymbolsPageView(OMRPage omrPage, PageSymbolsEditController pageController, ReadOnlyDoubleProperty widthProperty) {
        super(omrPage, pageController, widthProperty);
    }

    @Override
    protected void initStateMachine() {
        state = SymbolViewState.idle;
    }

    @Override
    public SymbolsRegionView createRegionView(OMRRegion region) {
        return new SymbolsRegionView(this, region);
    }

    public void handleEvent(Event t) {
        KeyEvent keyEvent = null;
        MouseEvent mouseEvent = null;
        if (t.getContent() instanceof MouseEvent) {
            mouseEvent = (MouseEvent) t.getContent();
        } else if (t.getContent() instanceof KeyEvent) {
            keyEvent = (KeyEvent) t.getContent();
        }
        switch (state) {
            case idle:
                if (t instanceof SymbolEditEvent) {
                    editingSymbol = ((SymbolEditEvent)t).getSymbolView();
                    RegionBaseView regionView = (RegionBaseView) editingSymbol.getParent();
                    //regionView.bringToTop(editingSymbol); // if not, the handlers do not receive drag events when overlapped with other region
                    editingSymbol.beginEdit();
                    ((SymbolEditEvent)t).getContent().consume();
                    changeState(SymbolViewState.editing);
                }
                break;
            case editing:
                if (mouseEvent != null && mouseEvent.isPrimaryButtonDown()) {
                    editingSymbol.acceptEdit();
                    changeState(SymbolViewState.idle);
                    mouseEvent.consume();
                } else if (keyEvent != null) {
                    if (keyEvent.getCode() == KeyCode.ENTER) {
                        editingSymbol.acceptEdit(); //TODO Comando
                        changeState(SymbolViewState.idle);
                        keyEvent.consume();
                    } else if (keyEvent.getCode() == KeyCode.ESCAPE) {
                        editingSymbol.cancelEdit();
                        changeState(SymbolViewState.idle);
                        keyEvent.consume();
                    } else if (keyEvent.getCode() == KeyCode.DELETE) {
                        editingSymbol.getRegionView().getOmrRegion().removeSymbol(editingSymbol.getOmrSymbol());
                        changeState(SymbolViewState.idle);
                        keyEvent.consume();
                    }
                }
                break;
        }
    }

    /*private void bringToTop(RegionBaseView editingRegion) {
        this.getChildren().remove(editingRegion);
        this.getChildren().add(editingRegion); // put on top
    }*/

    /*private void showSymbols(boolean show) {
        for (RegionView regionView: regions.values()) {
            regionView.showSymbols(show);
        }
    }*/

}
