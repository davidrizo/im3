package es.ua.dlsi.im3.omr.encoding.semantic.semanticsymbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.Atom;
import es.ua.dlsi.im3.core.score.io.kern.KernExporter;
import es.ua.dlsi.im3.core.score.PlainChant;

import java.util.List;

// TODO Usaremos place holders en *skm, pero, ¿también debemos crearlo aquí?
public class SemanticPlainChant extends SemanticCompoundAtom<PlainChant> {
    public SemanticPlainChant(PlainChant coreSymbol) {
        super(coreSymbol);
    }

    @Override
    public String toSemanticString() {
        throw new UnsupportedOperationException("Semantic plain chant");
    }

    @Override
    public String toKernSemanticString() throws IM3Exception {
        return toKernSemanticString(null);
    }

    @Override
    public String toKernSemanticString(String suffix) throws IM3Exception {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("*bpc");

        List<Atom> atoms = coreSymbol.getAtoms();
        for (int i=0; i<atoms.size(); i++) {
            stringBuilder.append(KernExporter.encodeAtom(atoms.get(i)));
            if (i == atoms.size() - 1) {
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
        stringBuilder.append("*epc");
        return stringBuilder.toString();
    }

}
