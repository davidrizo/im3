package es.ua.dlsi.im3.omr.encoding;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

/**
 * @autor drizo
 */
public abstract class Exporter<SymbolType> {
    private static final char SEP = ' ';
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
        int size = encoding.getSymbols().size();
        for (int i=0; i<size; i++) {
            SymbolType symbol = encoding.getSymbols().get(i);
            sb.append(export(symbol));
            if (i<size-1 && requiresSeparator(symbol)) {
                sb.append(SEP);
            }
        }
    }

    protected abstract boolean requiresSeparator(SymbolType symbol);

    protected abstract String export(SymbolType symbol);
}
