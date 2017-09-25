package es.ua.dlsi.im3.core.score.layout.coresymbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.TestFileUtils;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.clefs.ClefG2;
import es.ua.dlsi.im3.core.score.io.musicxml.MusicXMLImporter;
import es.ua.dlsi.im3.core.score.layout.Coordinate;
import es.ua.dlsi.im3.core.score.layout.CoordinateComponent;
import es.ua.dlsi.im3.core.score.layout.HorizontalLayout;
import es.ua.dlsi.im3.core.score.layout.fonts.LayoutFonts;
import es.ua.dlsi.im3.core.score.layout.pdf.PDFExporter;
import es.ua.dlsi.im3.core.score.layout.svg.SVGExporter;
import es.ua.dlsi.im3.core.score.staves.Pentagram;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.junit.Assert.*;

public class LayoutBeamedGroupTest {
    private ScoreSong createBeamedGroup(boolean includeEachNoteInStaff) throws IM3Exception {
        ScoreSong song = new ScoreSong();
        ScorePart part = song.addPart();
        Staff staff = new Pentagram(song, "1", 1);
        staff.addClef(new ClefG2());
        staff.addKeySignature(new KeySignature(NotationType.eModern, new Key(0, Mode.MAJOR)));
        staff.setNotationType(NotationType.eModern);
        song.addStaff(staff);
        ScoreLayer layer = part.addScoreLayer();
        staff.addLayer(layer);

        BeamedGroup eighth = new BeamedGroup(Figures.EIGHTH.getDuration(), NotationType.eModern, false);
        assertEquals(1, eighth.getNumBeams());
        SimpleNote n1 = new SimpleNote(Figures.EIGHTH, 0, new ScientificPitch(PitchClasses.C, 4));
        eighth.addSubatom(n1);

        BeamedGroup sixteen = new BeamedGroup(Figures.SIXTEENTH.getDuration(),  NotationType.eModern, false);
        SimpleNote n2 = new SimpleNote(Figures.SIXTEENTH, 0, new ScientificPitch(PitchClasses.D, 4));
        SimpleNote n3 = new SimpleNote(Figures.SIXTEENTH, 0, new ScientificPitch(PitchClasses.E, 4));
        sixteen.addSubatom(n2);
        sixteen.addSubatom(n3);

        eighth.addSubatom(sixteen);

        if (includeEachNoteInStaff) {
            staff.addCoreSymbol(n1);
            staff.addCoreSymbol(n2);
            staff.addCoreSymbol(n3);
        } else {
            staff.addCoreSymbol(eighth);
        }
        return song;
    }

    private void testElementsInStaff(ScoreSong song) throws IM3Exception {
        assertEquals(1, song.getStaves().size());
        Staff staff = song.getStaves().get(0);
        assertEquals(3, staff.getAtomPitches().size());
    }

    private void exportLayout(ScoreSong scoreSong, String name) throws IM3Exception {
        HorizontalLayout layout = new HorizontalLayout(scoreSong, LayoutFonts.bravura,
                new CoordinateComponent(960), new CoordinateComponent(700));
        layout.layout();

        SVGExporter svgExporter = new SVGExporter();
        File svgFile = TestFileUtils.createTempFile(name + ".svg");
        svgExporter.exportLayout(svgFile, layout);

        PDFExporter pdfExporter = new PDFExporter();
        File pdfFile = TestFileUtils.createTempFile(name + ".pdf");
        pdfExporter.exportLayout(pdfFile, layout);
    }


    // Just test it does not crash
    @Test
    public void layoutBeamedGroup() throws Exception {
        ScoreSong songOneGroupInStaff = createBeamedGroup(false);
        ScoreSong songNotesInStaff = createBeamedGroup(true);

        testElementsInStaff(songOneGroupInStaff);
        testElementsInStaff(songNotesInStaff);

        exportLayout(songOneGroupInStaff, "beamed_staff_group");
        exportLayout(songNotesInStaff, "beamed_staff_notes");
    }

}