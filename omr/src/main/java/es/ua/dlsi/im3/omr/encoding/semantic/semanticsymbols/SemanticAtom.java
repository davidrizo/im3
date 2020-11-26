package es.ua.dlsi.im3.omr.encoding.semantic.semanticsymbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.Atom;
import es.ua.dlsi.im3.core.score.io.kern.KernExporter;
import es.ua.dlsi.im3.omr.encoding.semantic.SemanticSymbolType;

public abstract class SemanticAtom<AtomType extends Atom> extends SemanticSymbolType<AtomType> {
    protected static final String FERMATA = "fermata";
    protected static final String TRILL = "trill";

    public SemanticAtom(AtomType coreSymbol) {
        super(coreSymbol);
    }

    @Override
    public String toKernSemanticString() throws IM3Exception {
        try {
            return KernExporter.encodeAtom(coreSymbol);
        } catch (Throwable t) {
            throw new IM3Exception("Cannot encode into kern the atom '" + coreSymbol.toString() +  "'", t);
        }
    }
}
