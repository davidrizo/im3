package es.ua.dlsi.im3.core.score.layout.layoutengines;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.Time;
import es.ua.dlsi.im3.core.score.layout.*;
import es.ua.dlsi.im3.core.score.layout.graphics.BoundingBox;

/**
 * It just guaranties the symbols are not overlapped
 */
public class NonProportionalLayoutEngine implements ILayoutEngine {
    public NonProportionalLayoutEngine() {
    }

    @Override
    public void doHorizontalLayout(Simultaneities simultaneities) throws IM3Exception {
        preprocess(simultaneities);
        doJustification(simultaneities);
    }

    /**
     * Compute time spans (simultaneities durations)
     * @param simultaneities
     */
    private void preprocess(Simultaneities simultaneities) {
        Simultaneity prevSimultaneity = null;
        for (Simultaneity simultaneity: simultaneities.getSimiltaneities()) {
            if (prevSimultaneity != null) {
                prevSimultaneity.setTimeSpan(simultaneity.getTime().substract(prevSimultaneity.getTime()));
            }

            prevSimultaneity = simultaneity;
        }

        if (prevSimultaneity != null) {
            prevSimultaneity.setTimeSpanFromElementsDuration();
        }
    }

    /**
     * @param simultaneities
     */
    private void doJustification(Simultaneities simultaneities) throws IM3Exception {
        double x = 0;
        for (Simultaneity s: simultaneities.getSimiltaneities()) {
            BoundingBox boundingBox = s.computeBoundingBox();
            if (s.getTimeSpan().isZero()) { // add margins
                boundingBox.setLeftEnd(boundingBox.getLeftEnd()-LayoutConstants.NON_DURATION_SYMBOLS_LATERIAl_INSET);
                boundingBox.setRightEnd(boundingBox.getRightEnd()+LayoutConstants.NON_DURATION_SYMBOLS_LATERIAl_INSET);
            }
            x += -(boundingBox.getLeftEnd());
            if (s.isSystemBreak()) {
                x = 0;
            }
            s.setX(x);
            //System.out.println(boundingBox + ", width " + s.getLayoutWidth() + ", \t"+s.toString());
            x += (s.getLayoutWidth() + boundingBox.getRightEnd());
            //System.out.println("\tx=" + x);
        }
    }

    @Override
    public void reset(Simultaneities simultaneities) {
        for (Simultaneity s: simultaneities.getSimiltaneities()) {
            for (LayoutCoreSymbol ss: s.getSymbols()) {
                ss.setX(0);
            }
        }
    }

}
