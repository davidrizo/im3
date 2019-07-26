package es.ua.dlsi.im3.core.score.mensural.ligature;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.*;

import java.util.List;

/**
 * It creates a ligature from a list of notes
 * @autor drizo
 */
public class LigatureFactory {
    /**
     *
     * @param noteList
     * @param ligatureType It can be null
     * @return
     * @throws IM3Exception
     */
    public static Ligature createLigature(List<SimpleNote> noteList, LigatureType ligatureType) throws IM3Exception {
        Ligature result = null;

        if (noteList.size() == 2) {
            SimpleNote note1 = noteList.get(0);
            SimpleNote note2 = noteList.get(1);
            if (note1.getAtomFigure().getFigure() == Figures.BREVE) {
                if (note2.getAtomFigure().getFigure() == Figures.LONGA) {
                    result = new LigatureCumPropietateEtCumPerfectione(note1.getPitch(), note1.getAtomFigure().getDots(), note2.getPitch(), note2.getAtomFigure().getDots());
                } else if (note2.getAtomFigure().getFigure() == Figures.BREVE) {
                    result = new LigatureCumPropietateEtSinePerfectione(note1.getPitch(), note1.getAtomFigure().getDots(), note2.getPitch(), note2.getAtomFigure().getDots(), ligatureType);
                }
            } else if (note1.getAtomFigure().getFigure() == Figures.LONGA) {
                if (note2.getAtomFigure().getFigure() == Figures.LONGA) {
                    result = new LigatureSinePropietateEtCumPerfectione(note1.getPitch(), note1.getAtomFigure().getDots(), note2.getPitch(), note2.getAtomFigure().getDots());
                } else if (note2.getAtomFigure().getFigure() == Figures.BREVE) {
                    result = new LigatureSinePropietateEtSinePerfectione(note1.getPitch(), note1.getAtomFigure().getDots(), note2.getPitch(), note2.getAtomFigure().getDots(), ligatureType);
                }
            } else if (note1.getAtomFigure().getFigure() == Figures.SEMIBREVE && note2.getAtomFigure().getFigure() == Figures.SEMIBREVE) {
                result = new LigaturaCumOppositaPropietate(note1.getPitch(), note1.getAtomFigure().getDots(), note2.getPitch(), note2.getAtomFigure().getDots(), ligatureType);
            }

            if (result == null) {
                throw new IM3Exception("Figures not valid, must be a combination of BREVIS and LONGA, and are " +
                        note1.getAtomFigure().getFigure() + " and " +
                        note2.getAtomFigure().getFigure());
            }
        } else if (noteList.size() == 1) {
            throw new IM3Exception("Cannot build a ligature with just 1 note");
        } else {
            Ligature ligature = new Ligature(ligatureType);
            noteList.forEach(simpleNote -> ligature.addSubatom(simpleNote));
        }
        return result;
    }
}
