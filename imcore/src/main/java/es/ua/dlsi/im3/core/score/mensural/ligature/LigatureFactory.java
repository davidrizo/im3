package es.ua.dlsi.im3.core.score.mensural.ligature;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.Figures;
import es.ua.dlsi.im3.core.score.LigaturaBinaria;
import es.ua.dlsi.im3.core.score.SimpleNote;

import java.util.List;

/**
 * It creates a ligature from a list of notes
 * @autor drizo
 */
public class LigatureFactory {
    public static LigaturaBinaria createLigature(List<SimpleNote> noteList) throws IM3Exception {
        if (noteList.size() != 2) {
            throw new IM3Exception("Currenly just ligatures of 2 notes are supported, and " + noteList.size() + " have been provided");
        }

        SimpleNote note1 = noteList.get(0);
        SimpleNote note2 = noteList.get(1);
        LigaturaBinaria result = null;
        if (note1.getAtomFigure().getFigure() == Figures.BREVE) {
            if (note2.getAtomFigure().getFigure() == Figures.LONGA) {
                result = new LigatureCumPropietateEtCumPerfectione(note1.getPitch(), note1.getAtomFigure().getDots(), note2.getPitch(), note2.getAtomFigure().getDots());
            } else if (note2.getAtomFigure().getFigure() == Figures.BREVE) {
                result = new LigatureCumPropietateEtSinePerfectione(note1.getPitch(), note1.getAtomFigure().getDots(), note2.getPitch(), note2.getAtomFigure().getDots());
            }
        } else if (note1.getAtomFigure().getFigure() == Figures.LONGA) {
            if (note2.getAtomFigure().getFigure() == Figures.LONGA) {
                result = new LigatureSinePropietateEtCumPerfectione(note1.getPitch(), note1.getAtomFigure().getDots(), note2.getPitch(), note2.getAtomFigure().getDots());
            } else if (note2.getAtomFigure().getFigure() == Figures.BREVE) {
                result = new LigatureSinePropietateEtSinePerfectione(note1.getPitch(), note1.getAtomFigure().getDots(), note2.getPitch(), note2.getAtomFigure().getDots());
            }
        } else if (note1.getAtomFigure().getFigure() == Figures.SEMIBREVE && note2.getAtomFigure().getFigure() == Figures.SEMIBREVE) {
            result = new LigaturaCumOppositaPropietate(note1.getPitch(), note1.getAtomFigure().getDots(), note2.getPitch(), note2.getAtomFigure().getDots());
        }

        if (result == null) {
            throw new IM3Exception("Figures not valid, must be a combination of BREVIS and LONGA, and are " +
                    note1.getAtomFigure().getFigure() + " and " +
                    note2.getAtomFigure().getFigure());
        }
        return result;
    }
}
