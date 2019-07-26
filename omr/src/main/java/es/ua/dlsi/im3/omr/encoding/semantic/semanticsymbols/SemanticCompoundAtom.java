package es.ua.dlsi.im3.omr.encoding.semantic.semanticsymbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.Atom;
import es.ua.dlsi.im3.core.score.CompoundAtom;
import es.ua.dlsi.im3.core.score.io.kern.KernExporter;
import es.ua.dlsi.im3.omr.encoding.semantic.SemanticSymbolType;

public abstract class SemanticCompoundAtom<CompoundAtomType extends CompoundAtom> extends SemanticSymbolType<CompoundAtomType> {
    public SemanticCompoundAtom(CompoundAtomType coreSymbol) {
        super(coreSymbol);
    }

    @Override
    public String toKernSemanticString() throws IM3Exception {
        return KernExporter.encodeAtom(coreSymbol);
    }
}
