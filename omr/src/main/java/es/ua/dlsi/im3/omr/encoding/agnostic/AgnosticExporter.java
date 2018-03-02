package es.ua.dlsi.im3.omr.encoding.agnostic;

import es.ua.dlsi.im3.omr.encoding.Exporter;

/**
 * @author drizo
 */
public class AgnosticExporter extends Exporter<AgnosticSymbol> {
    private static final char SEP = '\t';
    private static final char SEPVERTICALPOS = '-';
    private AgnosticEncoding encoding;

    @Override
    protected String export(AgnosticSymbol symbol) {
        AgnosticSymbolType specificSymbol = symbol.getSymbol();
        StringBuilder sb = new StringBuilder();
        sb.append(specificSymbol.toAgnosticString());
        sb.append(SEPVERTICALPOS);
        sb.append(symbol.getPositionInStaff().toString());
        return sb.toString();
    }

}