package es.ua.dlsi.im3.core.score.layout.layoutengines;

import es.ua.dlsi.im3.core.score.layout.ILayoutEngine;
import es.ua.dlsi.im3.core.score.layout.LayoutConstants;
import es.ua.dlsi.im3.core.score.layout.Simultaneities;
import es.ua.dlsi.im3.core.score.layout.Simultaneity;

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

    private void computeWidth(Simultaneity prevSimultaneity) {
        double timeToWidth = Math.log(prevSimultaneity.getTimeSpan().getComputedTime())* LayoutConstants.EM;
        double layoutWidth = Math.max(prevSimultaneity.getMinimumWidth(), timeToWidth);
        prevSimultaneity.setLayoutWidth(layoutWidth);
        /*System.out.println("Width from time span " + prevSimultaneity.getTimeSpan().getComputedTime() + ", min " +
                prevSimultaneity.getMinimumWidth() + " and timeToWidth  " +
                timeToWidth + " = "  + layoutWidth);*/
    }


}
