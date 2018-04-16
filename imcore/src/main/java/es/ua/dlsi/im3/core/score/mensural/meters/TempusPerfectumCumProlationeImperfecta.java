package es.ua.dlsi.im3.core.score.mensural.meters;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.AtomFigure;
import es.ua.dlsi.im3.core.score.Figures;
import es.ua.dlsi.im3.core.score.SimpleRest;
import es.ua.dlsi.im3.core.score.Time;

import java.util.List;

/**
 * Visually rendered as O
 * 1 breve = 3 semibreves, 1 semibreve = 2 minim
 */
public class TempusPerfectumCumProlationeImperfecta extends TimeSignatureMensural {
    public TempusPerfectumCumProlationeImperfecta() {
        super(Perfection.perfectum, Perfection.imperfectum);
    }

    @Override
    public Time getDuration() {
        return getBreveDuration();
    }

    @Override
    public String getSignString() {
        return "O";
    }

    /**
     * Willi Apel, page 108 (rules of imperfection for [3,2])
     * @param figureList
     */
    @Override
    public void applyImperfectionRules(List<AtomFigure> figureList) throws IM3Exception {
        int size = figureList.size();
        for (int i=0; i<size; i++) {
            AtomFigure figure = figureList.get(i);
            if (!figure.isExplicitMensuralPerfection()) { // if explicitly set, don't change it
                AtomFigure next = i<size-1?figureList.get(i+1):null;

                int preceedingSemibreves = 0;
                boolean exit=false;
                for (int j=i-1; !exit && j>=0; j--) { //this could be more efficient
                    if (figureList.get(j).getFigure() == Figures.SEMIBREVE) {
                        preceedingSemibreves++;
                    } else {
                        exit = true;
                    }
                }

                int followingSemibreves = 0;
                exit=false;
                for (int j=i+1; !exit && j<size; j++) { //this could be more efficient
                    if (figureList.get(j).getFigure() == Figures.SEMIBREVE) {
                        followingSemibreves++;
                    } else {
                        exit = true;
                    }
                }

                if (figure.getFigure() == Figures.BREVE) {
                    Perfection perfection = null;
                    // rule 4 - implemented using this sequence of if - (without) else
                    if (next != null && next.getFigure() == Figures.BREVE) {
                        // rule 1
                        perfection = Perfection.perfectum;
                    }

                    if (followingSemibreves == 2 || followingSemibreves == 3) {
                        // rule 2
                        perfection = Perfection.perfectum;
                    }

                    if (preceedingSemibreves == 1 || preceedingSemibreves > 3 || followingSemibreves == 1 || followingSemibreves > 3) {
                        // rule 3
                        perfection = Perfection.imperfectum;
                    }

                    if (figure.getAtom() instanceof SimpleRest) {
                        // rule 5
                        perfection = Perfection.imperfectum;
                    }
                    // TODO: 16/4/18 Rule 4
                    if (perfection != null) {
                        figure.setComputedMensuralPerfection(perfection);
                    }
                }
            }
        }
    }

}
