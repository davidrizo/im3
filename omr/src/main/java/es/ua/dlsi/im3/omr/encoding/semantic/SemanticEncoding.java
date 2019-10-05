package es.ua.dlsi.im3.omr.encoding.semantic;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.io.ExportException;
import es.ua.dlsi.im3.core.score.NotationType;
import es.ua.dlsi.im3.omr.encoding.Sequence;

/**
 * @author drizo
 */
public class SemanticEncoding extends Sequence<SemanticSymbol> {
    public String generateKernSemanticString(NotationType notationType) throws IM3Exception {
        StringBuilder stringBuilder = new StringBuilder();
        if (notationType == NotationType.eMensural) {
            stringBuilder.append("**mens\n");
        } else if (notationType == NotationType.eModern) {
            stringBuilder.append("**kern\n");
        } else {
            throw new ExportException("Cannot write other than mensural or modern kern files: " + notationType);
        }

        for (SemanticSymbol semanticSymbol: getSymbols()) {
            if (semanticSymbol.getSymbol() == null) {
                throw new IM3Exception("Cannot export because symbol in semantic is null: " + semanticSymbol);
            }
            stringBuilder.append(semanticSymbol.getSymbol().toKernSemanticString());
            stringBuilder.append('\n');
        }
        return stringBuilder.toString();
    }

    public void add(SemanticSymbolType semanticSymbolType) {
        add(new SemanticSymbol(semanticSymbolType));
    }
}
