package es.ua.dlsi.im3.omr.encoding.semantic.semanticsymbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.LigatureType;
import es.ua.dlsi.im3.core.score.ScientificPitch;
import es.ua.dlsi.im3.core.score.mensural.ligature.LigaturaCumOppositaPropietate;
import es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.Ligature;
import org.junit.Test;

import static org.junit.Assert.*;

public class SemanticLigatureTest {

    @Test
    public void toKernSemanticString() throws IM3Exception {
        LigaturaCumOppositaPropietate ligature = new LigaturaCumOppositaPropietate(ScientificPitch.C4, 0, ScientificPitch.C4, 0, LigatureType.recta);
        SemanticLigature semanticLigature = new SemanticLigature(ligature);
        assertEquals("Semantic ligature", "[sc\nsc]", semanticLigature.toKernSemanticString());
    }
}
