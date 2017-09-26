package es.ua.dlsi.im3.core.score.layout.layoutengines;

import es.ua.dlsi.im3.core.score.layout.*;

public class NaiveEngine implements ILayoutEngine {
    @Override
    public void doHorizontalLayout(Simultaneities simultaneities) {
        double x = 0;
        Simultaneity prevSimultaneity = null;
        for (Simultaneity simultaneity: simultaneities.getSimiltaneities()) {
            if (prevSimultaneity != null) {
                prevSimultaneity.setTimeSpan(simultaneity.getTime().substract(prevSimultaneity.getTime()));
                computeWidth(prevSimultaneity);
                prevSimultaneity.setX(x);
                x += prevSimultaneity.getLayoutWidth();
            }

            prevSimultaneity = simultaneity;
        }

        if (prevSimultaneity != null) {
            prevSimultaneity.setTimeSpanFromElementsDuration();
            computeWidth(prevSimultaneity);
            prevSimultaneity.setX(x);
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

    private void computeWidth(Simultaneity prevSimultaneity) {
        double timeToWidth = Math.log(prevSimultaneity.getTimeSpan().getComputedTime())* LayoutConstants.EM;
        double layoutWidth = Math.max(prevSimultaneity.getMinimumWidth(), timeToWidth);
        prevSimultaneity.setLayoutWidth(layoutWidth);
        /*System.out.println("Width from time span " + prevSimultaneity.getTimeSpan().getComputedTime() + ", min " +
                prevSimultaneity.getMinimumWidth() + " and timeToWidth  " +
                timeToWidth + " = "  + layoutWidth);*/
    }


}
