package es.ua.dlsi.im3.core.score.layout.coresymbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.TimeSignature;
import es.ua.dlsi.im3.core.score.layout.graphics.GraphicsElement;
import es.ua.dlsi.im3.core.score.layout.graphics.Pictogram;
import es.ua.dlsi.im3.core.score.mensural.meters.hispanic.TimeSignatureProporcionMenor;
import es.ua.dlsi.im3.core.score.meters.SignTimeSignature;
import es.ua.dlsi.im3.core.score.meters.TimeSignatureCommonTime;
import es.ua.dlsi.im3.core.score.meters.TimeSignatureCutTime;

import java.util.HashMap;

public class LayoutSignTimeSignature extends LayoutTimeSignature<SignTimeSignature> {

    private final Pictogram pictogram;

    private static final HashMap<Class<? extends TimeSignature>, String> UNICODES = new HashMap<>();
    {
        UNICODES.put(TimeSignatureCommonTime.class, "timeSigCommon");
        UNICODES.put(TimeSignatureCutTime.class, "timeSigCutCommon");
        UNICODES.put(TimeSignatureProporcionMenor.class, "timeSigProporcionMenor"); // Note this is not SMuFL compliant
    }

    public LayoutSignTimeSignature(LayoutStaff layoutStaff, SignTimeSignature coreSymbol) throws IM3Exception {
        super(layoutStaff, coreSymbol);
        position.setY(layoutStaff.getYAtLine(3));
        pictogram = new Pictogram(layoutStaff.getScoreLayout().getLayoutFont(), getUnicode(), position);

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
}
