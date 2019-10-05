package es.ua.dlsi.im3.omr.encoding.semantic.semanticsymbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.io.ExportException;
import es.ua.dlsi.im3.core.score.Atom;
import es.ua.dlsi.im3.core.score.Ligature;
import es.ua.dlsi.im3.core.score.io.kern.KernExporter;

import java.util.List;

public class SemanticLigature extends SemanticCompoundAtom<Ligature> {
    public SemanticLigature(Ligature coreSymbol) {
        super(coreSymbol);
    }

    @Override
    public String toSemanticString() {
        throw new UnsupportedOperationException("Semantic ligature");
    }

    @Override
    public String toKernSemanticString() throws IM3Exception {
        return toKernSemanticString(null);
    }

    @Override
    public String toKernSemanticString(String suffix) throws IM3Exception {
        StringBuilder stringBuilder = new StringBuilder();
        char close;
        switch (coreSymbol.getLigatureType()) {
            case recta:
                stringBuilder.append('[');
                close = ']';
                break;
            case obliqua:
                stringBuilder.append('<');
                close = '>';
                break;
            default:
                throw new ExportException("Unsupported ligature type: " + coreSymbol.getLigatureType());
        }

        List<Atom> atoms = coreSymbol.getAtoms();
        for (int i=0; i<atoms.size(); i++) {
            stringBuilder.append(KernExporter.encodeAtom(atoms.get(i)));
            if (i == atoms.size() - 1) {
                stringBuilder.append(close);
                if (suffix != null) {
                    stringBuilder.append(suffix);
                }
            } else {
                if (suffix != null) {
                    stringBuilder.append(suffix);
                }
                stringBuilder.append('\n');
            }
        }
        return stringBuilder.toString();
    }

}
