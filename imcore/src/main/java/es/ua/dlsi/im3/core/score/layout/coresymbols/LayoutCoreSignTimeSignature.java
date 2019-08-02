package es.ua.dlsi.im3.core.score.layout.coresymbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.TimeSignature;
import es.ua.dlsi.im3.core.score.layout.LayoutFont;
import es.ua.dlsi.im3.core.score.layout.graphics.GraphicsElement;
import es.ua.dlsi.im3.core.score.layout.graphics.Pictogram;
import es.ua.dlsi.im3.core.score.mensural.meters.TempusImperfectumCumProlationeImperfecta;
import es.ua.dlsi.im3.core.score.mensural.meters.TempusImperfectumCumProlationeImperfectaDiminutum;
import es.ua.dlsi.im3.core.score.mensural.meters.hispanic.TimeSignatureProporcionMayor;
import es.ua.dlsi.im3.core.score.mensural.meters.hispanic.TimeSignatureProporcionMenor;
import es.ua.dlsi.im3.core.score.meters.SignTimeSignature;
import es.ua.dlsi.im3.core.score.meters.TimeSignatureCommonTime;
import es.ua.dlsi.im3.core.score.meters.TimeSignatureCutTime;

import java.util.HashMap;

public class LayoutCoreSignTimeSignature extends LayoutCoreTimeSignature<SignTimeSignature> {

    private final Pictogram pictogram;

    private static final HashMap<Class<? extends TimeSignature>, String> UNICODES = new HashMap<>();
    {
        UNICODES.put(TimeSignatureCommonTime.class, "timeSigCommon");
        UNICODES.put(TempusImperfectumCumProlationeImperfecta.class, "timeSigCommon");
        UNICODES.put(TimeSignatureCutTime.class, "timeSigCutCommon");
        UNICODES.put(TempusImperfectumCumProlationeImperfectaDiminutum.class, "timeSigCutCommon");
        UNICODES.put(TimeSignatureProporcionMenor.class, "timeSigProporcionMenor"); // Note this is not SMuFL compliant
        UNICODES.put(TimeSignatureProporcionMayor.class, "timeSigProporcionMayor"); // Note this is not SMuFL compliant
    }

    public LayoutCoreSignTimeSignature(LayoutFont layoutFont, SignTimeSignature coreSymbol) throws IM3Exception {
        super(layoutFont, coreSymbol);
        pictogram = new Pictogram(this, InteractionElementType.signTimeSignature, layoutFont, getUnicode(), position);//TODO IDS

    }

    private String getUnicode() throws IM3Exception {
        String unicode = UNICODES.get(coreSymbol.getClass());
        if (unicode == null) {
            throw new IM3Exception("Cannot find a font unicode for " + coreSymbol.getClass());
        }
        return unicode;
    }

    @Override
    public GraphicsElement getGraphics() {
        return pictogram;
    }

    @Override
    public void rebuild() {
        throw new UnsupportedOperationException("TO-DO Rebuild " + this.getClass().getName());
    }
    @Override
    protected void doLayout() throws IM3Exception {
        position.setReferenceY(layoutStaff.getYAtLine(3));
    }
}
