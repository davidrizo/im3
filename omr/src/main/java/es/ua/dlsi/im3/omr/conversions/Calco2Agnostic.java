package es.ua.dlsi.im3.omr.conversions;

import es.ua.dlsi.im3.core.io.ImportException;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbolType;
import es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.*;
import es.ua.dlsi.im3.omr.encoding.enums.ClefNote;
import es.ua.dlsi.im3.omr.encoding.enums.MeterSigns;

/**
 * @autor drizo
 */
public class Calco2Agnostic {
    public AgnosticSymbolType convert(String from) throws ImportException {
        switch (from) {
            case "barline":
                return new VerticalLine();
            case "beam":
                return new Unknown();
            case "brevis":
                return new Note(NoteFigures.breve);
            case "brevis_rest":
                return new Rest(RestFigures.breve);
            case "c_clef":
                return new Clef(ClefNote.C);
            case "coloured_brevis":
                return new Note(NoteFigures.breveBlack);
            case "coloured_minima":
                return new Note(NoteFigures.quarter);
            case "coloured_semibrevis":
                return new Note(NoteFigures.wholeBlack);
            case "coloured_semiminima":
                return new Note(NoteFigures.eighth);
            case "common_time":
                return new MeterSign(MeterSigns.C);
            case "custos":
                return new Custos();
            case "cut_time":
                return new MeterSign(MeterSigns.C);
            case "dot":
                return new Dot();
            case "double_barline":
                return new Unknown();
            case "f_clef_1":
                return new Clef(ClefNote.F);
            case "f_clef_2":
                return new Clef(ClefNote.Fpetrucci);
            case "fermata":
                return new Fermata();
            case "flat":
                return new Accidental(Accidentals.flat);
            case "g_clef":
                return new Clef(ClefNote.G);
            case "ligature":
                return new Unknown();
            case "longa_2":
                return new Note(NoteFigures.doubleWholeStem);
            case "longa":
                return new Note(NoteFigures.longa);
            case "longa_rest":
                return new Rest(RestFigures.longa);
            case "minima":
                return new Note(NoteFigures.half);
            case "minima_rest":
                return new Note(NoteFigures.half);
            case "proportio_maior":
                return new MeterSign(MeterSigns.CZ); // it was incorrect
            case "proportio_minor":
                return new MeterSign(MeterSigns.CcutZ); // it was incorrect
            case "semibrevis":
                return new Note(NoteFigures.whole);
            case "semibrevis_rest":
                return new Rest(RestFigures.whole);
            case "semiminima":
                return new Note(NoteFigures.eighthVoid);
            case "semiminima_rest":
                return new Rest(RestFigures.seminima);
            case "sharp":
                return new Accidental(Accidentals.sharp);
            case "undefined":
                return new Unknown();
            default:
                throw new ImportException("Unsupported '" + from + "'");
        }
    }


}
