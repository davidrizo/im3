package es.ua.dlsi.im3.core.conversions;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.TestFileUtils;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.clefs.ClefG2;
import es.ua.dlsi.im3.core.score.layout.CoordinateComponent;
import es.ua.dlsi.im3.core.score.layout.HorizontalLayout;
import es.ua.dlsi.im3.core.score.layout.fonts.LayoutFonts;
import es.ua.dlsi.im3.core.score.layout.svg.SVGExporter;
import es.ua.dlsi.im3.core.score.meters.TimeSignatureCommonTime;
import es.ua.dlsi.im3.core.score.staves.Pentagram;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class MensuralToModernTest {
    @Test
    public void convert() throws Exception {
        ScoreSong song = new ScoreSong();
        Staff staff = new Pentagram(song, "1", 1);
        staff.setNotationType(NotationType.eMensural);
        song.addStaff(staff);
        ScorePart part = song.addPart();
        part.addStaff(staff);
        ScoreLayer layer = part.addScoreLayer(staff);
        TimeSignatureCommonTime ts = new TimeSignatureCommonTime(NotationType.eMensural);
        KeySignature ks = new KeySignature(NotationType.eMensural, new Key(PitchClasses.F, Mode.MAJOR));
        Clef clef = new ClefG2();
        staff.addClef(clef);
        staff.addKeySignature(ks);
        staff.addTimeSignature(ts);

        SimpleRest r1 = new SimpleRest(Figures.MINIM, 0);
        add(staff, layer, r1);
        SimpleNote n0 = new SimpleNote(Figures.MINIM, 0, new ScientificPitch(DiatonicPitch.D, null, 5));
        add(staff, layer, n0);
        SimpleNote n1 = new SimpleNote(Figures.SEMIMINIM, 0, new ScientificPitch(DiatonicPitch.E, null, 5));
        add(staff, layer, n1);
        SimpleNote n2 = new SimpleNote(Figures.SEMIMINIM, 0, new ScientificPitch(DiatonicPitch.F, null, 5));
        add(staff, layer, n2);
        SimpleNote n3 = new SimpleNote(Figures.SEMIBREVE, 0, new ScientificPitch(DiatonicPitch.G, null, 5));
        add(staff, layer, n3);
        SimpleNote n4 = new SimpleNote(Figures.MINIM, 0, new ScientificPitch(DiatonicPitch.F, Accidentals.SHARP, 5));
        add(staff, layer, n4);
        SimpleNote n5 = new SimpleNote(Figures.MINIM, 0, new ScientificPitch(DiatonicPitch.G, null, 5));
        add(staff, layer, n5);
        SimpleNote n6 = new SimpleNote(Figures.MINIM, 0, new ScientificPitch(DiatonicPitch.G, null, 5));
        add(staff, layer, n6);
        SimpleNote n7 = new SimpleNote(Figures.SEMIBREVE, 0, new ScientificPitch(DiatonicPitch.A, null, 5));
        add(staff, layer, n7);
        SimpleNote n8 = new SimpleNote(Figures.SEMIBREVE, 0, new ScientificPitch(DiatonicPitch.A, null, 5));
        add(staff, layer, n8);

        MensuralToModern mensuralToModern = new MensuralToModern();
        ScoreSong modernSong = mensuralToModern.convert(song);

        assertEquals( "Modern parts",  1, modernSong.getParts().size());
        assertEquals( "Modern staves",  1, modernSong.getStaves().size());
        assertEquals( "Part layers",  1, modernSong.getParts().get(0).getLayers().size());
        assertEquals( "Staff layers",  1, modernSong.getStaves().get(0).getLayers().size());
        Staff modernStaff = modernSong.getStaves().get(0);
        ScoreLayer modernLayer = modernStaff.getLayers().get(0);

        //TODO assertEquals("Core symbols in staff", 17, modernStaff.getCoreSymbolsOrdered().size());
        //TODO assertEquals("Atoms in layer", 11, modernLayer.getAtoms().size());

        // render it putting in the top staff the mensural one and in the bottom staff the modern one
        // FIXME: 6/10/17
        HorizontalLayout layout = new HorizontalLayout(modernSong, LayoutFonts.bravura,
                new CoordinateComponent(960), new CoordinateComponent(700));
        layout.layout();

        SVGExporter svgExporter = new SVGExporter();
        File svgFile = TestFileUtils.createTempFile("mensural2modern.svg");
        svgExporter.exportLayout(svgFile, layout);

    }

    private void add(Staff staff, ScoreLayer layer, SingleFigureAtom atom) throws IM3Exception {
        staff.addCoreSymbol(atom);
        layer.add(atom);
    }

}