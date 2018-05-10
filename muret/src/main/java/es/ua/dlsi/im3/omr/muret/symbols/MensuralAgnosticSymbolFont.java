package es.ua.dlsi.im3.omr.muret.symbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.layout.fonts.PatriarcaFont;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticVersion;
import es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.*;
import es.ua.dlsi.im3.omr.encoding.enums.ClefNote;
import es.ua.dlsi.im3.omr.encoding.enums.MeterSigns;

import java.util.HashMap;

/**
 * https://paper.dropbox.com/doc/Simbolos-en-representacion-agnostica-lpRHU8fKTfRTuiklJdRW7
 * @autor drizo
 */
public class MensuralAgnosticSymbolFont extends AgnosticSymbolFont {
    public MensuralAgnosticSymbolFont() throws IM3Exception {
        super(new PatriarcaFont());
        add(new Accidental(Accidentals.flat), "accidentalFlat"); //TODO Ver TODO en es.ua.dlsi.im3.core.score.layout.coresymbols.components
        add(new Accidental(Accidentals.sharp), "accidentalSharp"); //TODO Ver TODO en es.ua.dlsi.im3.core.score.layout.coresymbols.components
        add(new Accidental(Accidentals.natural), "accidentalNatural"); //TODO Ver TODO en es.ua.dlsi.im3.core.score.layout.coresymbols.components

        //TODO beams

        add(new Clef(ClefNote.C), "cClef");
        add(new Clef(ClefNote.F), "fClef");
        add(new Clef(ClefNote.G), "gClef");
        add(new Clef(ClefNote.Fpetrucci), "mensuralFclefPetrucci");

        add(new Custos(), "mensuralCustosUp");
        add(new Dot(), "augmentationDot");
        add(new Fermata(Positions.above), "fermataAbove");
        add(new Fermata(Positions.below), "fermataBelow");
        add(new MeterSign(MeterSigns.C), "timeSigCommon");
        add(new MeterSign(MeterSigns.Ccut), "timeSigCutTime");
        add(new MeterSign(MeterSigns.CcutZ), "timeSigProporcionMayor");
        add(new MeterSign(MeterSigns.CZ), "timeSigProporcionMenor");

        add(new Note(NoteFigures.doubleWholeStem), "mensuralWhiteMaxima");
        add(new Note(NoteFigures.longa), "mensuralWhiteLonga");
        add(new Note(NoteFigures.breve), "mensuralWhiteBrevis");
        add(new Note(NoteFigures.whole), "mensuralBlackSemibrevisVoid");
        add(new Note(NoteFigures.half), "mensuralWhiteMinima");
        add(new Note(NoteFigures.quarter), "mensuralWhiteSemiminima");
        add(new Note(NoteFigures.eighth), "mensuralWhiteFusa");
        add(new Note(NoteFigures.eighthCut), "mensuralWhiteSemifusa");

        add(new Rest(RestFigures.longa), "mensuralRestLongaImperfecta");
        add(new Rest(RestFigures.breve), "mensuralRestBrevis");
        add(new Rest(RestFigures.whole), "mensuralRestSemibrevis");
        add(new Rest(RestFigures.half), "mensuralRestMinima");
        add(new Rest(RestFigures.seminima), "mensuralRestSemiminima");
        add(new Rest(RestFigures.fusa), "mensuralRestFusa");

        add(new VerticalLine(AgnosticVersion.v2), "mensuralRestLongaPerfecta");
        add(new VerticalLine(AgnosticVersion.v1), "mensuralRestLongaPerfecta");
    }
}
