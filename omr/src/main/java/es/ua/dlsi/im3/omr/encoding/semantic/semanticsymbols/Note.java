package es.ua.dlsi.im3.omr.encoding.semantic.semanticsymbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.Figures;
import es.ua.dlsi.im3.core.score.ScientificPitch;
import es.ua.dlsi.im3.core.score.ScoreLayer;
import es.ua.dlsi.im3.core.score.SimpleNote;
import es.ua.dlsi.im3.core.score.io.kern.KernExporter;
import es.ua.dlsi.im3.omr.encoding.semantic.SemanticSymbolType;
import org.apache.commons.lang3.math.Fraction;

/**
 * @autor drizo
 */
public class Note extends DurationalSymbol {
    private static final String SEMANTIC_NOTE = "note" + SEPSYMBOL;
    private static final String SEMANTIC_GRACENOTE = "gracenote" + SEPSYMBOL;
    private static final String TRILL = "trill";

    boolean trill;
    boolean graceNote;

    int beamsStart;
    int beamsEnd;
    boolean tieStart;
    boolean tieMiddle;
    boolean tieEnd;
    int slursStart;
    int slursEnd;

    ScientificPitch scientificPitch;

    /**
     * @param graceNote
     * @param scientificPitch
     * @param figures
     * @param dots
     * @param fermata
     * @param trill
     * @param tupletNumber If null, it is not a tuplet
     */
    public Note(boolean graceNote, ScientificPitch scientificPitch, Figures figures, int dots, boolean fermata, boolean trill, Integer tupletNumber) {
        super(figures, dots, fermata, tupletNumber);
        this.trill = trill;
        this.graceNote = graceNote;
        this.scientificPitch = scientificPitch;
    }

    public boolean isTrill() {
        return trill;
    }

    public void setTrill(boolean trill) {
        this.trill = trill;
    }

    public boolean isGraceNote() {
        return graceNote;
    }

    public void setGraceNote(boolean graceNote) {
        this.graceNote = graceNote;
    }

    public int getBeamsStart() {
        return beamsStart;
    }

    public void setBeamsStart(int beamsStart) {
        this.beamsStart = beamsStart;
    }

    public int getBeamsEnd() {
        return beamsEnd;
    }

    public void setBeamsEnd(int beamsEnd) {
        this.beamsEnd = beamsEnd;
    }

    public boolean isTieStart() {
        return tieStart;
    }

    public void setTieStart(boolean tieStart) {
        this.tieStart = tieStart;
    }

    public boolean isTieMiddle() {
        return tieMiddle;
    }

    public void setTieMiddle(boolean tieMiddle) {
        this.tieMiddle = tieMiddle;
    }

    public boolean isTieEnd() {
        return tieEnd;
    }

    public void setTieEnd(boolean tieEnd) {
        this.tieEnd = tieEnd;
    }

    public int getSlursStart() {
        return slursStart;
    }

    public void setSlursStart(int slursStart) {
        this.slursStart = slursStart;
    }

    public int getSlursEnd() {
        return slursEnd;
    }

    public void setSlursEnd(int slursEnd) {
        this.slursEnd = slursEnd;
    }

    public ScientificPitch getScientificPitch() {
        return scientificPitch;
    }

    public void setScientificPitch(ScientificPitch scientificPitch) {
        this.scientificPitch = scientificPitch;
    }

    @Override
    public String toSemanticString() {
        StringBuilder sb = new StringBuilder();
        if (graceNote) {
            sb.append(SEMANTIC_GRACENOTE);
        } else {
            sb.append(SEMANTIC_NOTE);
        }

        sb.append(scientificPitch.getPitchClass().getNoteName());
        if (scientificPitch.getPitchClass().isAltered()) {
            sb.append(scientificPitch.getPitchClass().getAccidental().getAbbrName());
        }
        sb.append(scientificPitch.getOctave());
        sb.append(SEPVALUES);
        sb.append(figures.name().toLowerCase()); //TODO ¿Para moderno y mensural?

        for (int i=0; i<dots; i++) {
            sb.append('.');
        }

        if (tupletNumber != null) {
            sb.append(SEPVALUES);
            sb.append(TUPLET);
            sb.append(tupletNumber);
        }

        if (fermata) {
            sb.append(SEPVALUES);
            sb.append(FERMATA);
        }
        if (trill) {
            sb.append(SEPVALUES);
            sb.append(TRILL);
        }

        return sb.toString();
    }

    @Override
    public String toKernSemanticString() throws IM3Exception {
        //TODO ties, slurs...
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(KernExporter.generateDuration(figures, dots, Fraction.ONE)); //TODO fracciones para tresillos...
        stringBuilder.append(KernExporter.generatePitch(scientificPitch));
        return stringBuilder.toString();
    }


    @Override
    public SemanticSymbolType semantic2ScoreSong(ScoreLayer scoreLayer, SemanticSymbolType propagatedSymbolType) throws IM3Exception {
        //TODO tuplets, fermata, trill, stems, beams ...
        SimpleNote note = new SimpleNote(figures, dots, scientificPitch);

        if (propagatedSymbolType instanceof Tie) {
            if (!(scoreLayer.getLastAtom() instanceof SimpleNote)) {
                throw new IM3Exception("Last atom should be a note, and it is a " + scoreLayer.getLastAtom().getClass());
            }
            SimpleNote lastNote = (SimpleNote) scoreLayer.getLastAtom();
            note.tieFromPrevious(lastNote);
            //lastNote.tieToNext(note);
        }
        scoreLayer.add(note);

        scoreLayer.getStaff().addCoreSymbol(note);
        return null;
    }
}
