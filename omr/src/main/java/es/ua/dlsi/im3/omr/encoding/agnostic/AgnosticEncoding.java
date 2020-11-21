package es.ua.dlsi.im3.omr.encoding.agnostic;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.omr.encoding.Sequence;
import es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.*;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author drizo
 */
public class AgnosticEncoding extends Sequence<AgnosticToken> {
    public AgnosticEncoding() {
    }

    public AgnosticEncoding(AgnosticVersion agnosticVersion, String [] agnosticSequence) throws IM3Exception {
        for (String s: agnosticSequence) {
            add(AgnosticSymbol.parseAgnosticString(agnosticVersion, s));
        }
    }

    /**
     * It removes the last symbol if it is a separator
     */
    public void removeLastSymbolIfSeparator() {
        if (symbols.get(symbols.size()-1) instanceof AgnosticSeparator) {
            symbols.remove(symbols.size()-1);
        }
    }

    // it returns all symbols without agnostic separators
    public List<AgnosticToken> getSymbolsWithoutSeparators() {
        List<AgnosticToken> result = new LinkedList<>();
        for (AgnosticToken token: symbols) {
            if (!(token instanceof AgnosticSeparator)) {
                result.add(token);
            }
        }
        return result;
    }

    /**
     * It inserts contextual information (clef and key signature in the form of accidental and number of accidentals at the beginning after the clef) in the sequence that some translators may use to correctly interpret
     * each symbol.
     * USE WITH CARE because it modifies the AgnosticNote!!!
     * @param previousClef Used because in some datasets (e.g. FMT, the clef is not represented in all staves)
     * @return Last clef
     */
    public AgnosticToken insertContextInSequence(AgnosticToken previousClef) {
        boolean possibleKeySignature = true;

        AgnosticToken lastClefToken = previousClef;
        Accidentals lastKSAccidental = null;
        int lastKSAccidentalCount = 0;
        for (Iterator<AgnosticToken> iterator = symbols.listIterator(); iterator.hasNext(); ) {
            AgnosticToken token = iterator.next();
            if (token instanceof AgnosticSymbol) {
                AgnosticSymbol agnosticSymbol = (AgnosticSymbol) token;
                if (agnosticSymbol.getSymbol() instanceof Clef) {
                    lastClefToken = agnosticSymbol;
                    possibleKeySignature = true; // after a clef change it may happen
                } else if (agnosticSymbol.getSymbol() instanceof Accidental) {
                    if (possibleKeySignature) {
                        Accidentals accidentals = ((Accidental) agnosticSymbol.getSymbol()).getAccidentals();
                        if (lastKSAccidental == null || lastKSAccidental == accidentals) {
                            lastKSAccidental = accidentals;
                            lastKSAccidentalCount++;
                        } else {
                            // if changed, the new accidental does not belong to the key
                            possibleKeySignature = false;
                        }
                    }
                } else {
                    if (possibleKeySignature) {
                        // if in a possible key signature, if any symbol other than accidental is found, it is no longer a key signature
                        possibleKeySignature = false;
                    }

                    if (agnosticSymbol.getSymbol() instanceof Note) {
                        Note note = (Note) agnosticSymbol.getSymbol();
                        StringBuilder stringBuilder = new StringBuilder();
                        if (lastClefToken != null) {
                            stringBuilder.append(lastClefToken.getAgnosticString());
                        }
                        if (lastKSAccidental != null) {
                            if (stringBuilder.length() > 0) {
                                stringBuilder.append(Note.CONTEXT_SEP);
                                stringBuilder.append(lastKSAccidentalCount);
                                stringBuilder.append(lastKSAccidental.getAbbr());
                            }
                        }
                        if (stringBuilder.length() > 0) {
                            note.setAgnosticContext(stringBuilder.toString());
                        }
                    }

                }
            }
        }
        return lastClefToken;
    }


}
