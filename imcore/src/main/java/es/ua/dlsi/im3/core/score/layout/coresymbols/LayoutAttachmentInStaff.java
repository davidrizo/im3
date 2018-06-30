package es.ua.dlsi.im3.core.score.layout.coresymbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.AttachmentInStaff;
import es.ua.dlsi.im3.core.score.ITimedElement;
import es.ua.dlsi.im3.core.score.layout.LayoutCoreSymbol;
import es.ua.dlsi.im3.core.score.layout.LayoutFont;

/**
 *
 */
public abstract class LayoutAttachmentInStaff<CoreSymbolType extends AttachmentInStaff<? extends ITimedElement>, AttachedToViewType> extends LayoutCoreSymbolInStaff<CoreSymbolType> {
    AttachedToViewType attachedToView;

    public LayoutAttachmentInStaff(LayoutFont layoutFont, CoreSymbolType coreSymbol) throws IM3Exception {
        super(layoutFont, coreSymbol);
    }

    public AttachedToViewType getAttachedToView() {
        return attachedToView;
    }

    public void setAttachedToView(AttachedToViewType attachedToView) {
        this.attachedToView = attachedToView;
    }
}
