package es.ua.dlsi.im3.omr.encoding.semantic;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.Atom;
import es.ua.dlsi.im3.core.score.CompoundAtom;
import es.ua.dlsi.im3.omr.encoding.Exporter;
import es.ua.dlsi.im3.omr.encoding.semantic.semanticsymbols.SemanticCompoundAtom;
import es.ua.dlsi.im3.omr.encoding.semantic.semanticsymbols.SemanticLigature;

import java.util.Arrays;

/**
 * @author drizo
 */
public class KernSemanticExporter extends Exporter<SemanticSymbol> {
    public static final String IDS_SEPARATOR = "@";
    public static final char TOKEN_SEPARATOR = '\n';

    public KernSemanticExporter() {
        super(TOKEN_SEPARATOR);
        this.setEndWithSeparator(true);
    }

    @Override
    protected boolean requiresSeparator(SemanticSymbol lastSymbol) {
        return true;
    }

    @Override
    protected String export(SemanticSymbol symbol) throws IM3Exception {
        if (symbol.getSymbol().getAgnosticIDs() == null) {
            return symbol.toKernSemanticString();
        } else {
            StringBuilder stringBuilderIDS = new StringBuilder();
            stringBuilderIDS.append(IDS_SEPARATOR);
            for (int i = 0; i < symbol.getSymbol().getAgnosticIDs().length; i++) {
                if (i > 0) {
                    stringBuilderIDS.append(',');
                }
                stringBuilderIDS.append(symbol.getSymbol().getAgnosticIDs()[i]);
            }
//TODO Ver symbol.getSymbol.toKern y symbol.toKern
            return symbol.getSymbol().toKernSemanticString(stringBuilderIDS.toString());

            /*StringBuilder stringBuilder = new StringBuilder();
            String kernLines = symbol.toKernSemanticString();

            if (symbol.getSymbol() instanceof SemanticCompoundAtom) {
                String [] lines = kernLines.split("\n");
                for (String line: lines) {
                    stringBuilder.append(line);
                    stringBuilder.append(stringBuilderIDS);
                    stringBuilder.append('\n');
                }
            } else {
                stringBuilder.append(kernLines);
                stringBuilder.append(stringBuilderIDS);
                stringBuilder.append('\n');
            }
            return stringBuilder.toString();*/
        }
    }
}
