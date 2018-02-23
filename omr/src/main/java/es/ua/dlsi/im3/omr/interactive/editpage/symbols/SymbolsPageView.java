package es.ua.dlsi.im3.omr.interactive.editpage.symbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.omr.interactive.Event;
import es.ua.dlsi.im3.omr.interactive.editpage.RegionBaseView;
import es.ua.dlsi.im3.omr.interactive.editpage.RegionBasedPageView;
import es.ua.dlsi.im3.omr.interactive.editpage.SlicedRegionsWithTranscriptionBasedPageView;
import es.ua.dlsi.im3.omr.interactive.model.OMRPage;
import es.ua.dlsi.im3.omr.interactive.model.OMRRegion;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class SymbolsPageView extends SlicedRegionsWithTranscriptionBasedPageView<SymbolsStaffView> {
    public SymbolsPageView(OMRPage page) throws IM3Exception {
        super(page);
    }

    @Override
    protected SymbolsStaffView createTranscriptionStaffViewType(OMRPage page, OMRRegion region) throws IM3Exception {
        return new SymbolsStaffView(page, region);
    }

}
