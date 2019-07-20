package es.ua.dlsi.im3.core.score.io.mei;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.clefs.ClefG2;
import es.ua.dlsi.im3.core.score.meters.TimeSignatureCommonTime;
import org.junit.Test;

import static org.junit.Assert.*;

public class MEISongExporterTest {

    @Test
    public void exportMensuralDivisionDotTest() throws IM3Exception {
        ScoreSong song = ScoreSong.createEmptyOneVoiceEmptySong(NotationType.eMensural);
        Staff staff = song.getStaves().get(0);
        ScoreLayer layer = staff.getLayers().get(0);
        staff.addElementWithoutLayer(new ClefG2());
        staff.addElementWithoutLayer(new TimeSignatureCommonTime(NotationType.eMensural));

        SimpleNote note = new SimpleNote(Figures.SEMIBREVE, 0, new ScientificPitch(PitchClasses.A, 4));
        note.getAtomFigure().setFollowedByMensuralDivisionDot(true);
        layer.add(note);

        MEISongExporter exporter = new MEISongExporter();
        String mei = exporter.exportSong(song);
        //System.out.println(mei);
        assertTrue("Contains dot", mei.indexOf("<dot form=\"div\"/>")>0);
    }
}
