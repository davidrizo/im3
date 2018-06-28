package es.ua.dlsi.im3.core.score.mensural.meters;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.*;

import java.util.List;
import java.util.TreeSet;

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
        TreeSet<Integer> usedFigures = new TreeSet<>();
        TreeSet<Integer> possibleUsedFigures = new TreeSet<>();
        //System.out.println("Applying to " + figureList.size() + " items");
        for (int i=0; i<size; i++) {
            AtomFigure figure = figureList.get(i);
            if (!figure.isExplicitMensuralPerfection() && figure.getFigure() == Figures.BREVE && figure.getDots() == 0) { // if explicitly set, don't change it
                //System.out.println("Figure #" + i + " " + figure.getAtom());
                String perfectionRuleApplied = null;
                Perfection perfection = null;

                if (figure.getAtom() instanceof LigaturaBinaria) { // TODO: 17/4/18 Otras ligature más largas
                    perfectionRuleApplied = "drizo-ligature";
                    perfection = Perfection.perfectum;
                } else {

                    AtomFigure next = i < size - 1 ? figureList.get(i + 1) : null;
                    if (figure.getAtom() instanceof SimpleRest) {
                        // rule 5
                        perfection = Perfection.perfectum;
                        perfectionRuleApplied = "Rule 5";
                    } else if (next != null && next.getFigure() == Figures.BREVE) {
                        // rule 1
                        perfection = Perfection.perfectum;
                        perfectionRuleApplied = "Rule 1: similes ante simile perfecta";
                    } else {
                        int followingSemibreves = 0;
                        boolean exit = false;
                        for (int j = i + 1; !usedFigures.contains(j) && !exit && j < size; j++) { //this could be more efficient
                            AtomFigure jfig = figureList.get(j);
                            if (jfig.getFigure() == Figures.SEMIBREVE) {
                                possibleUsedFigures.add(j);
                                followingSemibreves++;
                            } else {
                                exit = true;
                            }
                        }

                        if (followingSemibreves == 2 || followingSemibreves == 3) {
                            // rule 2 - (first a parte post) - rule 4
                            perfection = Perfection.perfectum;
                            perfectionRuleApplied = "Rule 2, a p.p.";
                            usedFigures.addAll(possibleUsedFigures);
                        } else if (followingSemibreves == 1 || followingSemibreves > 3) {
                            // rule 3 - (first a parte post) - rule 4
                            perfection = Perfection.imperfectum;
                            perfectionRuleApplied = "Rule 3, a p.p.";
                            usedFigures.addAll(possibleUsedFigures);
                        } else {
                            possibleUsedFigures.clear();
                            int preceedingSemibreves = 0;
                            exit = false;
                            for (int j = i - 1; !usedFigures.contains(j) && !exit && j >= 0; j--) { //this could be more efficient
                                if (figureList.get(j).getFigure() == Figures.SEMIBREVE) {
                                    preceedingSemibreves++;
                                    possibleUsedFigures.add(j);
                                } else {
                                    exit = true;
                                }
                            }

                            if (preceedingSemibreves == 1 || preceedingSemibreves > 3) {
                                // rule 3
                                perfection = Perfection.imperfectum;
                                perfectionRuleApplied = "Rule 3, a p.a.";
                            }
                        }
                    }

                    if (perfection == null) {
                        TreeSet<Integer> possibleUsedFiguresLength1Semibreve = new TreeSet<>();
                        TreeSet<Integer> possibleUsedFiguresLength2Semibreves = new TreeSet<>();
                        TreeSet<Integer> possibleUsedFiguresLength3Semibreves = new TreeSet<>();
                        TreeSet<Integer> possibleUsedFiguresLength4Semibreves = new TreeSet<>();

                        Time followingSmallNotesDuration = Time.TIME_ZERO; // notes shorter than a breve
                        Time one = new Time(1);
                        Time two = new Time(2);
                        Time three = new Time(3);
                        Time four = new Time(4);
                        boolean exit = false;
                        for (int j = i + 1; !usedFigures.contains(j) && !exit && j < size; j++) { //this could be more efficient
                            AtomFigure jfigure = figureList.get(j);
                            if (jfigure.getFigure().getDuration().compareTo(Figures.SEMIBREVE.getDuration()) <= 0) {
                                possibleUsedFigures.add(j);
                                followingSmallNotesDuration = followingSmallNotesDuration.add(figureList.get(j).getFigure().getDuration());
                                Time division = followingSmallNotesDuration.divideBy(Figures.SEMIBREVE.getDuration());
                                if (division.equals(one)) {
                                    possibleUsedFiguresLength1Semibreve.addAll(possibleUsedFigures);
                                } else if (division.equals(two)) {
                                    possibleUsedFiguresLength2Semibreves.addAll(possibleUsedFigures);
                                } else if (division.equals(three)) {
                                    possibleUsedFiguresLength3Semibreves.addAll(possibleUsedFigures);
                                } else if (division.equals(four)) {
                                    possibleUsedFiguresLength4Semibreves.addAll(possibleUsedFigures);
                                }
                            } else {
                                exit = true;
                            }
                        }

                        //double numberOfSemibreves = followingSmallNotesDuration.divideBy(Figures.SEMIBREVE.getDuration()).getComputedTime();
                        if (!possibleUsedFiguresLength4Semibreves.isEmpty()) {
                            // rule 3 - (first a parte post) - rule 4
                            perfection = Perfection.imperfectum;
                            perfectionRuleApplied = "Rule 3, a p.p. (small notes summing 4 semibrevis)";
                            usedFigures.addAll(possibleUsedFiguresLength4Semibreves);
                        } else if (!possibleUsedFiguresLength3Semibreves.isEmpty()) {
                            // rule 2 - (first a parte post) - rule 4
                            perfection = Perfection.perfectum;
                            perfectionRuleApplied = "Rule 2, a p.p. (small notes summing 3 semibrevis)";
                            usedFigures.addAll(possibleUsedFiguresLength3Semibreves);
                        } else if (!possibleUsedFiguresLength2Semibreves.isEmpty()) {
                            // rule 2 - (first a parte post) - rule 4
                            perfection = Perfection.perfectum;
                            perfectionRuleApplied = "Rule 2, a p.p. (small notes summing 2 semibrevis)";
                            usedFigures.addAll(possibleUsedFiguresLength2Semibreves);
                        } else if (!possibleUsedFiguresLength1Semibreve.isEmpty()) {
                            // rule 3 - (first a parte post) - rule 4
                            perfection = Perfection.imperfectum;
                            perfectionRuleApplied = "Rule 3, a p.p. (small notes summing 1 semibrevis)";
                            usedFigures.addAll(possibleUsedFiguresLength1Semibreve);
                        } else {
                            possibleUsedFigures.clear();
                            possibleUsedFiguresLength1Semibreve.clear();
                            possibleUsedFiguresLength2Semibreves.clear();
                            possibleUsedFiguresLength3Semibreves.clear();
                            possibleUsedFiguresLength4Semibreves.clear();

                            Time preceedingSmallNotesDuration = Time.TIME_ZERO;
                            exit = false;
                            for (int j = i - 1; !usedFigures.contains(j) && !exit && j >= 0; j--) { //this could be more efficient
                                if (figureList.get(j).getFigure().getDuration().compareTo(Figures.SEMIBREVE.getDuration()) <= 0) {
                                    possibleUsedFigures.add(j);
                                    preceedingSmallNotesDuration = preceedingSmallNotesDuration.add(figureList.get(j).getFigure().getDuration());
                                    Time division = preceedingSmallNotesDuration.divideBy(Figures.SEMIBREVE.getDuration());
                                    if (division.equals(one)) {
                                        possibleUsedFiguresLength1Semibreve.addAll(possibleUsedFigures);
                                    } else if (division.equals(two)) {
                                        possibleUsedFiguresLength2Semibreves.addAll(possibleUsedFigures);
                                    } else if (division.equals(three)) {
                                        possibleUsedFiguresLength3Semibreves.addAll(possibleUsedFigures);
                                    } else if (division.equals(four)) {
                                        possibleUsedFiguresLength4Semibreves.addAll(possibleUsedFigures);
                                    }
                                } else {
                                    exit = true;
                                }
                            }

                            //numberOfSemibreves = preceedingSmallNotesDuration.divideBy(Figures.SEMIBREVE.getDuration()).getComputedTime();
                            if (!possibleUsedFiguresLength4Semibreves.isEmpty()) {
                                // rule 3 - (first a parte ante) - rule 4
                                perfection = Perfection.imperfectum;
                                perfectionRuleApplied = "Rule 3, a p.a. (small notes summing 4 semibrevis)";
                                usedFigures.addAll(possibleUsedFiguresLength4Semibreves);
                            } else if (!possibleUsedFiguresLength1Semibreve.isEmpty()) {
                                // rule 3 - (first a parte ante) - rule 4
                                perfection = Perfection.imperfectum;
                                perfectionRuleApplied = "Rule 3, a p.a. (small notes summing 1 semibrevis)";
                                usedFigures.addAll(possibleUsedFiguresLength1Semibreve);
                            }
                        }
                    }
                    if (perfection != null) {
                        figure.setComputedMensuralPerfection(perfection, perfectionRuleApplied);
                    }
                }
            }
        }
    }
}
