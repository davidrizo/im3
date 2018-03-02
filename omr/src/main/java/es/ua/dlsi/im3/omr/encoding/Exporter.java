package es.ua.dlsi.im3.omr.encoding;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

/**
 * @autor drizo
 */
public abstract class Exporter<SymbolType> {
    private static final char SEP = '\t';
    private static final char SEPVERTICALPOS = '-';
    private StringBuilder sb;
    private Sequence<SymbolType> encoding;

    public String export(Sequence<SymbolType>  encoding) {
        this.encoding = encoding;
        sb = new StringBuilder();

        doExport();

        return sb.toString();
    }

    public void export(Sequence<SymbolType>  encoding, File output) throws FileNotFoundException {
        String exported = export(encoding);
        PrintStream ps = new PrintStream(output);
        ps.println(exported);
        ps.close();
    }

    private void doExport() {
        boolean first = true;
        for (SymbolType symbol: encoding.getSymbols()) {
            if (first) {
                first = false;
            } else {
                sb.append(SEP);
            }
            sb.append(export(symbol));
        }
    }

    protected abstract String export(SymbolType symbol);
}
