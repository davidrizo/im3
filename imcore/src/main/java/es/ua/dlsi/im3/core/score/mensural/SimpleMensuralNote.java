package es.ua.dlsi.im3.core.score.mensural;

import es.ua.dlsi.im3.core.score.Figures;
import es.ua.dlsi.im3.core.score.ScientificPitch;
import es.ua.dlsi.im3.core.score.SimpleNote;
import es.ua.dlsi.im3.core.score.mensural.meters.Perfection;

public class SimpleMensuralNote extends SimpleNote {
    NoteColor noteColor;
    Perfection perfection;

    public SimpleMensuralNote(Figures figure, int dots, ScientificPitch pitch) {
        super(figure, dots, pitch);
    }

    public NoteColor getNoteColor() {
        return noteColor;
    }

    public void setNoteColor(NoteColor noteColor) {
        this.noteColor = noteColor;
    }

    public Perfection getPerfection() {
        return perfection;
    }

    public void setPerfection(Perfection perfection) {
        this.perfection = perfection;
    }
}
