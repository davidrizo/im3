package es.ua.dlsi.im3.core.score.layout.layoutengines;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.Time;
import es.ua.dlsi.im3.core.score.layout.*;
import es.ua.dlsi.im3.core.score.layout.graphics.BoundingBox;

/**
 * Based on the paper:
 * Bellini, P., & Nesi, P. (2004). Automatic justification and line-breaking of music sheets. International Journal of Human-Computer Studies, 61(1), 104–137.
 * http://doi.org/10.1016/j.ijhcs.2003.12.001
 */
public class BelliniLayoutEngine implements ILayoutEngine {
    private final double K;
    /**
     * LOG2
     */
    private static final double LOG2 = Math.log(2);
    private double noteHeadWidth;


    /**
     * @param k Spacing constant. See Bellini's paper for documentation about this constant
     * @scalingFactorAlpha scale factor betweeb duration and width
     */
    public BelliniLayoutEngine(double k, double scalingFactorAlpha, double noteHeadWidth) {
        this.K = k;
        this.noteHeadWidth = noteHeadWidth;
    }

    @Override
    public void doHorizontalLayout(Simultaneities simultaneities) throws IM3Exception {
        Time m = computeMinFigureDuration(simultaneities); // TODO: 22/9/17 Paper suggests computing it different for each measure
        preprocess(simultaneities);
        doJustification(simultaneities, m);
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
     * @param m Minimum duration
     */
    private void doJustification(Simultaneities simultaneities, Time m) throws IM3Exception {
        Time minDur = Time.min(m, new Time(1, 8)); // used in T(m,K), eq (3a)
        double log2MinM_8th = Math.log(minDur.getComputedTime()) / LOG2;
        for (Simultaneity s: simultaneities.getSimiltaneities()) {
            double sw = J(s.getTimeSpan(), log2MinM_8th); // eq. (4)
            s.setLayoutWidth(sw * noteHeadWidth);
        }

        // FIXME: 22/9/17 Esto no es Bellini
        double x = 0;
        for (Simultaneity s: simultaneities.getSimiltaneities()) {
            BoundingBox boundingBox = s.computeBoundingBox();
            if (s.getTimeSpan().isZero()) { // add margins
                boundingBox.setLeftEnd(boundingBox.getLeftEnd()-LayoutConstants.NON_DURATION_SYMBOLS_LATERIAl_INSET);
                boundingBox.setRightEnd(boundingBox.getRightEnd()+LayoutConstants.NON_DURATION_SYMBOLS_LATERIAl_INSET);
            }
            x += -(boundingBox.getLeftEnd());
            s.setX(x);
            //System.out.println(boundingBox + ", width " + s.getLayoutWidth() + ", \t"+s.toString());
            x += (s.getLayoutWidth() + boundingBox.getRightEnd());
            //System.out.println("\tx=" + x);
        }
    }

    /**
     * Associates a certain space (width) with a certain time duration
     * @param duration
     * @param log2MinM_8th
     * @return
     */
    private double J(Time duration, double log2MinM_8th) {
        if (duration.isZero()) {
            //System.out.println("J=0");
            return 0;
        } else {
            // This is the implementation of Gourlay detailed in Bellini's paper (eq. 3)
            double log2 = Math.log(duration.getComputedTime()) / LOG2; // eq 3
            double TmK = K - log2MinM_8th; // eq. 3a
            double result = log2 + TmK;
            //System.out.println("dur= " + duration.getComputedTime() + ", log2=" + log2 + ", TmK=" + TmK + ", J=" + result);
            return result;
        }
    }

    private Time computeMinFigureDuration(Simultaneities simultaneities) {
        Time minimum = Time.TIME_MAX;
        for (Simultaneity s: simultaneities.getSimiltaneities()) {
            for (LayoutCoreSymbol ss: s.getSymbols()) {
                if (!ss.getDuration().isZero()) {
                    minimum = Time.min(minimum, ss.getDuration());
                }
            }
        }
        return minimum;
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
