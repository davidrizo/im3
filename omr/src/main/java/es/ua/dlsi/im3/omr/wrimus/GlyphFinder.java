package es.ua.dlsi.im3.omr.wrimus;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.clefs.ClefG2;
import es.ua.dlsi.im3.core.score.meters.FractionalTimeSignature;
import es.ua.dlsi.im3.core.score.meters.TimeSignatureCommonTime;
import es.ua.dlsi.im3.core.score.meters.TimeSignatureCutTime;

import java.util.Date;
import java.util.List;
import java.util.Random;

public class GlyphFinder {
    private final HomusDataset homusDataset;

    public GlyphFinder(HomusDataset homusDataset) {
        this.homusDataset = homusDataset;
    }

    private Glyph chooseRandomGlyph(List<Glyph> glyphList) {
        Random random = new Random(new Date().getTime());
        return glyphList.get(random.nextInt(glyphList.size()));
    }

    public Glyph findClef(Clef clef) throws IM3Exception {
        String name = clef.getNote().name().toUpperCase() + "-Clef";
        List<Glyph> glyphs = homusDataset.getGlyphs(name);
        return chooseRandomGlyph(glyphs);
    }

    public Glyph findTimeSignature(TimeSignature symbol) throws IM3Exception {
        if (symbol instanceof FractionalTimeSignature) {
            FractionalTimeSignature fractionalTimeSignature = (FractionalTimeSignature) symbol;
            String name = fractionalTimeSignature.getNumerator() + "-" + fractionalTimeSignature.getDenominator()+"-Time";
            List<Glyph> glyphs = homusDataset.getGlyphs(name);
            return chooseRandomGlyph(glyphs);

        } else if (symbol instanceof TimeSignatureCommonTime) {
            String name = "Common-Time";
            List<Glyph> glyphs = homusDataset.getGlyphs(name);
            return chooseRandomGlyph(glyphs);

        } else if (symbol instanceof TimeSignatureCutTime) {
            String name = "Cut-Time";
            List<Glyph> glyphs = homusDataset.getGlyphs(name);
            return chooseRandomGlyph(glyphs);
        } else {
            throw new IM3Exception("Unsupported time signature: " + symbol);
        }
    }

    public Glyph findAcidental(Accidentals accidentals) throws IM3Exception {
        String name;
        switch (accidentals) {
            case FLAT: name =  "Flat"; break;
            case SHARP: name =  "Sharp";break;
            case NATURAL: name =  "Natural";break;
            case DOUBLE_SHARP: name = "Double-Sharp";break;
            default: throw new IM3Exception("Unsupported accidental " + accidentals);
        }
        List<Glyph> glyphs = homusDataset.getGlyphs(name);
        return chooseRandomGlyph(glyphs);

    }

    public Glyph findDot() throws IM3Exception {
        List<Glyph> glyphs = homusDataset.getGlyphs("dot");
        return chooseRandomGlyph(glyphs);
    }

    public Glyph findFigure(Figures figure, String suffix) throws IM3Exception {
        String name;
        switch (figure) {
            case WHOLE: name = "Whole"; break;
            case HALF: name = "Half"; break;
            case QUARTER: name = "Quarter"; break;
            case EIGHTH: name = "Eighth"; break;
            case SIXTEENTH: name = "Sixteenth"; break;
            case THIRTY_SECOND: name = "Thirty-Second"; break;
            case SIXTY_FOURTH: name = "Sixty-Fourth"; break;
            default: throw new IM3Exception("Unsupported figure " + figure);
        }

        List<Glyph> glyphs = homusDataset.getGlyphs(name+"-" + suffix);
        return chooseRandomGlyph(glyphs);

    }

    public Glyph findBarline() throws IM3Exception {
        List<Glyph> glyphs = homusDataset.getGlyphs("Barline");
        return chooseRandomGlyph(glyphs);

    }
}
