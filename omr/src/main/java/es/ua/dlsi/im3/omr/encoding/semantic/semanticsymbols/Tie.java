package es.ua.dlsi.im3.omr.encoding.semantic.semanticsymbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.Atom;
import es.ua.dlsi.im3.core.score.ITimedElementInStaff;
import es.ua.dlsi.im3.core.score.ScoreLayer;
import es.ua.dlsi.im3.omr.encoding.semantic.SemanticConversionContext;
import es.ua.dlsi.im3.omr.encoding.semantic.SemanticSymbolType;

import java.util.List;

/**
 * @autor drizo
 */
public class Tie extends SemanticSymbolType {
    private static final String SEMANTIC = "tie";
    @Override
    public String toSemanticString() {
        return SEMANTIC;
    }

    @Override
    public String toKernSemanticString() {
        System.err.println("TO-DO Tie"); //TODO Barline
        return "";
    }

    @Override
    public void semantic2ScoreSong(SemanticConversionContext semanticConversionContext, List<ITimedElementInStaff> conversionResult) throws IM3Exception {
        semanticConversionContext.addPendingTie();
        if (semanticConversionContext.getPendingPitchesToTie().isEmpty()) { // if they do not come from previous system
            if (conversionResult.isEmpty()) {
                throw new IM3Exception("Missing previous notes to tie");
            }
            ITimedElementInStaff lastItem = conversionResult.get(conversionResult.size() - 1);
            if (!(lastItem instanceof Atom)) {
                throw new IM3Exception("Previous item should be an atom, and it is a " + lastItem.getClass());
            }
            Atom lastAtom = (Atom) lastItem;
            if (lastAtom.getAtomPitches().isEmpty()) {
                throw new IM3Exception("Last atom must contain pitches");
            }
            semanticConversionContext.getPendingPitchesToTie().addAll(lastAtom.getAtomPitches());
        }
    }
}
