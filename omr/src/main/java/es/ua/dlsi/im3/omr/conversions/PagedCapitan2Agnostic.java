package es.ua.dlsi.im3.omr.conversions;

import es.ua.dlsi.im3.core.io.ImportException;
import es.ua.dlsi.im3.core.score.PositionInStaff;
import es.ua.dlsi.im3.core.score.PositionsInStaff;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticEncoding;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbol;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbolType;
import es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.*;
import es.ua.dlsi.im3.omr.encoding.enums.ClefNote;
import es.ua.dlsi.im3.omr.encoding.enums.MeterSigns;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * It converts the old Calvo's Capitan dataset to the new Hispamus Agnostic encoding (not the one with staves split
 * but the one with the whole pages).
 * @autor drizo
 */
public class PagedCapitan2Agnostic {
    private static final String TOKEN_SEPARATOR = " ";
    private static final String SYMBOL_SEPARATOR = "\\.";
    private static final String SLASH = "/";
    private final HashMap<String, NoteFigures> noteFigures;
    private final HashMap<String, RestFigures> restFigures;

    public PagedCapitan2Agnostic() {
        noteFigures = new HashMap<>();
        restFigures = new HashMap<>();

        // TODO: 2/3/18 Acabar figuras
        noteFigures.put("MAXIMA3", NoteFigures.quadrupleWholeStem);
        noteFigures.put("MAXIMA3", NoteFigures.tripleWholeStem);
        noteFigures.put("MAXIMA2", NoteFigures.doubleWholeStem);
        noteFigures.put("COLOUREDMAXIMA2", NoteFigures.doubleWholeBlackStem);
        noteFigures.put("MAXIMA", NoteFigures.longa);
        noteFigures.put("BREVIS", NoteFigures.breve);
        noteFigures.put("COLOUREDBREVIS", NoteFigures.breveBlack);
        noteFigures.put("SEMIBREVIS", NoteFigures.whole);
        noteFigures.put("COLOUREDSEMIBREVIS", NoteFigures.wholeBlack);
        noteFigures.put("MINIMA", NoteFigures.half);
        noteFigures.put("COLOUREDMINIMA", NoteFigures.quarter);
        noteFigures.put("COLOUREDSEMIMINIMA", NoteFigures.eighth);


        //restFigures.put("MAXIMAREST", RestFigures.); // tagged as vertical line
        restFigures.put("LONGAREST", RestFigures.longa);
        restFigures.put("BREVISREST", RestFigures.breve);
        restFigures.put("MINIMAREST", RestFigures.half);
        restFigures.put("SEMIBREVISREST", RestFigures.whole);
        restFigures.put("MINIMAREST", RestFigures.half);
        restFigures.put("SEMIMINIMAREST", RestFigures.seminima);

        /*
        noteFigures.put("SEMIMINIMA", FiguresMensural.SEMINIMA_WHITE); // no existe
        noteFigures.put("BCOLOUREDSEMIMINIMA", FiguresMensural.SEMINIMA_BLACK); // with
        // beam
        noteFigures.put("FUSA", FiguresMensural.FUSA_WHITE); // no existe
        noteFigures.put("COLOUREDFUSA", FiguresMensural.FUSA_BLACK); // no existe
        noteFigures.put("BCOLOUREDFUSA", FiguresMensural.FUSA_BLACK);
        restFigures.put("FUSAREST", FiguresMensural.FUSA_WHITE); // no existe

        noteFigures.put("SEMIFUSA", FiguresMensural.SEMIFUSA_WHITE); // no existe
        noteFigures.put("COLOUREDSEMIFUSA", FiguresMensural.SEMIFUSA_BLACK); // no existe
        noteFigures.put("BCOLOUREDSEMIFUSA", FiguresMensural.SEMIFUSA_BLACK); // no existe
        restFigures.put("SEMIFUSAREST", FiguresMensural.SEMIFUSA_WHITE); // no existe
        */
    }

    /**
     *
     * @param input Calvo's encoding. Each line of the file represents a staff.
     * @return An encoding for each staff
     */
    public List<AgnosticEncoding> convert(File input) throws ImportException {
        List<AgnosticEncoding> result = new LinkedList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(input))) {
            for (String line; (line = br.readLine()) != null;) {
                if (!line.trim().isEmpty()) {
                    result.add(parseLine(line));
                }
            }
        } catch (Exception e) {
            Logger.getLogger(PagedCapitan2Agnostic.class.getName()).log(Level.WARNING, "Cannot import " + input.getName(), e);
            throw new ImportException(e);
        }

        return result;
    }

    /**
     * Package for tests
     * @param line
     * @return
     * @throws ImportException
     */
    AgnosticEncoding parseLine(String line) throws ImportException {
        String [] tokens = line.split(TOKEN_SEPARATOR);
        AgnosticEncoding agnosticEncoding = new AgnosticEncoding();
        for (String token: tokens) {
            // in Capitan encoding, when a symbol lies on the same horizontal position, it is denoted with a /
            // In AgnosticEncoding we encode it using top-down
            String[] compound = token.split(SLASH);
            ArrayList<AgnosticSymbol> psts = new ArrayList<>();
            for (String symbol: compound) {
                String [] subsymbols = token.split(SYMBOL_SEPARATOR);
                if (subsymbols.length != 2) {
                    throw new ImportException("Expected two subsymbols at '" + token  + "' and found " + subsymbols.length);
                }

                AgnosticSymbolType symbolType = parseNotationSymbol(subsymbols[0]);
                PositionInStaff positionInStaff = parsePosition(subsymbols[1]);
                AgnosticSymbol pst = new AgnosticSymbol(symbolType, positionInStaff);
                psts.add(pst);
            }
            psts.sort(new Comparator<AgnosticSymbol>() {
                @Override
                public int compare(AgnosticSymbol o1, AgnosticSymbol o2) {
                    // position in staff is lower for bottom elements, this is why we sort reverse
                    int diff = o2.getPositionInStaff().compareTo(o1.getPositionInStaff());
                    if (diff == 0) {
                        return o1.hashCode() - o2.hashCode();
                    }
                    return diff;
                }
            });
            for (AgnosticSymbol agnosticSymbol: psts) {
                agnosticEncoding.add(agnosticSymbol);
            }
        }

        correctBeams(agnosticEncoding);
        return agnosticEncoding;
    }

    /**
     * Change just beams for left, both and right
     * @param agnosticEncoding
     */
    private void correctBeams(AgnosticEncoding agnosticEncoding) {
        AgnosticSymbol lastSymbol = null;
        for (AgnosticSymbol agnosticSymbol: agnosticEncoding.getSymbols()) {
            Note lastNoteBeamedSymbol = null;
            Note noteBeamedSymbol = null;

            if (lastSymbol != null && lastSymbol.getSymbol() instanceof Note && ((Note) lastSymbol.getSymbol()).getDurationSpecification() instanceof Beam) {
                lastNoteBeamedSymbol = (Note) lastSymbol.getSymbol();
            }

            if (agnosticSymbol != null && agnosticSymbol.getSymbol() instanceof Note && ((Note) agnosticSymbol.getSymbol()).getDurationSpecification() instanceof Beam) {
                noteBeamedSymbol = (Note) lastSymbol.getSymbol();
            }

            if (lastNoteBeamedSymbol == null && noteBeamedSymbol != null) {
                ((Beam)noteBeamedSymbol.getDurationSpecification()).setBeamType(BeamType.left);
            } else if (lastNoteBeamedSymbol != null && noteBeamedSymbol == null) {
                ((Beam)lastNoteBeamedSymbol.getDurationSpecification()).setBeamType(BeamType.right);// (2)
            } else if (lastNoteBeamedSymbol != null && noteBeamedSymbol != null) {
                ((Beam)lastNoteBeamedSymbol.getDurationSpecification()).setBeamType(BeamType.both); // it may be corrected by condition above (2)
            }

            lastSymbol = agnosticSymbol;
        }
    }

    private AgnosticSymbolType parseNotationSymbol(String symbol) throws ImportException {
        switch (symbol) {
            case "BARLINE": case "MAXIMAREST":
                return new VerticalLine();
            case "CUSTOS":
                return new Custos();
            case "GCLEF":
                return new Clef(ClefNote.G);
            case "CCLEF":
                return new Clef(ClefNote.C);
            case "FCLEF":
                return new Clef(ClefNote.F);
            case "PROPORTIOMINOR": // it was a mistake in Capitán
                return new MeterSign(MeterSigns.CcutZ);
            case "COMMONTIME":
                return new MeterSign(MeterSigns.C);
            case "CUTTIME":
                return new MeterSign(MeterSigns.Ccut);
            case "FERMATA":
                return new Fermata();
            case "DOT":
                return new Dot();
            case "FLAT":
                return new Accidental(Accidentals.flat);
            case "SHARP":
                return new Accidental(Accidentals.sharp);
            case "GREGORIAN":
                return new Ligature(); //TODO
            case "SMUDGE":
                return new Smudge();
            case "BCOLOUREDSEMIMINIMA":
                // we leave it empty to change it later
                return new Note(new Beam(1));
            case "BCOLOUREDFUSA":
                return new Note(new Beam(2));
            default:
                return parseFigure(symbol);
        }
    }

    private PositionInStaff parsePosition(String positionStr) throws ImportException {
        switch (positionStr) {
            case "-4":
                return PositionsInStaff.FOURTH_TOP_LEDGER_LINE;
            case "-3-4":
                return PositionsInStaff.SPACE_8;
            case "-3":
                return PositionsInStaff.THIRD_TOP_LEDGER_LINE;
            case "-2-3":
                return PositionsInStaff.SPACE_7;
            case "-2":
                return PositionsInStaff.SECOND_TOP_LEDGER_LINE;
            case "-1-2":
                return PositionsInStaff.SPACE_6;
            case "-1":
                return PositionsInStaff.FIRST_TOP_LEDGER_LINE;
            case "-10":
            case "0-1":
                return PositionsInStaff.SPACE_5;
            case "0":
                return PositionsInStaff.LINE_5;
            case "01":
                return PositionsInStaff.SPACE_4;
            case "1":
                return PositionsInStaff.LINE_4;
            case "12":
                return PositionsInStaff.SPACE_3;
            case "2":
                return PositionsInStaff.LINE_3;
            case "23":
                return PositionsInStaff.SPACE_2;
            case "3":
                return PositionsInStaff.LINE_2;
            case "34":
                return PositionsInStaff.SPACE_1;
            case "4":
                return PositionsInStaff.LINE_1;
            case "45":
                return PositionsInStaff.SPACE_0;
            case "5":
                return PositionsInStaff.FIRST_BOTTOM_LEDGER_LINE;
            case "56":
                return PositionsInStaff.SPACE_MINUS_1;
            case "6":
                return PositionsInStaff.SECOND_BOTTOM_LEDGER_LINE;
            case "67":
                return PositionsInStaff.SPACE_MINUS_2;
            case "7":
                return PositionsInStaff.THIRD_BOTTOM_LEDGER_LINE;
            case "78":
                return PositionsInStaff.SPACE_MINUS_3;
            case "8":
                return PositionsInStaff.FOURTH_BOTTOM_LEDGER_LINE;
            default:
                throw new ImportException("Unkown position " + positionStr);
        }
    }

    private AgnosticSymbolType parseFigure(String symbol) throws ImportException {
        // TODO Beams
        if (noteFigures.containsKey(symbol)) {
            return new Note(noteFigures.get(symbol));
        } else if (restFigures.containsKey(symbol)) {
            return new Rest(restFigures.get(symbol));
        } else {
            throw new ImportException("Symbol not recognized '" + symbol + "'");
        }
    }


}