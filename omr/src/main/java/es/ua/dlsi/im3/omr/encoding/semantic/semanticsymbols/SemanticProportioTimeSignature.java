package es.ua.dlsi.im3.omr.encoding.semantic.semanticsymbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.NotationType;
import es.ua.dlsi.im3.core.score.TimeSignature;
import es.ua.dlsi.im3.core.score.io.ImportFactories;
import es.ua.dlsi.im3.core.score.mensural.meters.ProportioDupla;
import es.ua.dlsi.im3.core.score.mensural.meters.ProportioTripla;
import es.ua.dlsi.im3.core.score.mensural.meters.TimeSignatureMensural;
import es.ua.dlsi.im3.core.score.meters.SignTimeSignature;
import es.ua.dlsi.im3.omr.encoding.enums.MeterSigns;

/**
 * @autor drizo
 */
public class SemanticProportioTimeSignature extends SemanticTimeSignature<TimeSignatureMensural> {
    public SemanticProportioTimeSignature(NotationType notationType, int number) throws IM3Exception {
        super(number == 2 ? new ProportioDupla() : number == 3 ? new ProportioTripla() : null);
        if (this.coreSymbol == null) {
            throw new IM3Exception("Invalid number for proportion meter " + number);
        }
    }

    @Override
    public String toSemanticString() throws IM3Exception {
        if (coreSymbol instanceof ProportioDupla) {
            return "2";
        } else if (coreSymbol instanceof ProportioTripla) {
            return "3";
        } else {
            throw new IM3Exception("Invalid proportion in core symbol: " + coreSymbol.getClass());
        }
    }

    @Override
    public String toKernSemanticString() throws IM3Exception {
        StringBuilder sb = new StringBuilder("*met(");
        sb.append(toSemanticString());
        sb.append(')');
        return sb.toString();
    }
}
