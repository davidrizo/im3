package es.ua.dlsi.im3.core.conversions;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.TestFileUtils;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.clefs.ClefG2;
import es.ua.dlsi.im3.core.score.layout.CoordinateComponent;
import es.ua.dlsi.im3.core.score.layout.HorizontalLayout;
import es.ua.dlsi.im3.core.score.layout.MarkBarline;
import es.ua.dlsi.im3.core.score.layout.fonts.LayoutFonts;
import es.ua.dlsi.im3.core.score.layout.svg.SVGExporter;
import es.ua.dlsi.im3.core.score.mensural.BinaryDurationEvaluator;
import es.ua.dlsi.im3.core.score.meters.TimeSignatureCommonTime;
import es.ua.dlsi.im3.core.score.staves.Pentagram;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;

import static org.junit.Assert.*;

public class MensuralToModernTest {
    private void printSVG(ScoreSong song, String filename) throws IM3Exception {
        HorizontalLayout layout = new HorizontalLayout(song,
                new CoordinateComponent(960), new CoordinateComponent(700));
        layout.layout(true);

        SVGExporter msvgExporter = new SVGExporter();
        File msvgFile = TestFileUtils.createTempFile(filename);
        msvgExporter.exportLayout(msvgFile, layout);
    }

    @Test
    public void convert() throws Exception {
        ScoreSong song = new ScoreSong(new BinaryDurationEvaluator(new Time(2))); // TODO: 7/10/17 ¿Sería mejor cambiar las Figures o sólo en la traducción?
        //ScoreSong song = new ScoreSong(new DurationEvaluator()); // TODO: 7/10/17 ¿Sería mejor cambiar las Figures o sólo en la traducción?
        Staff staff = new Pentagram(song, "1", 1);
        staff.setNotationType(NotationType.eMensural);
        staff.setName("Soprano");
        song.addStaff(staff);
        ScorePart part = song.addPart();
        part.setName("Soprano");
        part.addStaff(staff);
        ScoreLayer layer = part.addScoreLayer(staff);
        TimeSignatureCommonTime ts = new TimeSignatureCommonTime(NotationType.eMensural);
        KeySignature ks = new KeySignature(NotationType.eMensural, new Key(PitchClasses.F, Mode.MAJOR));
        Clef clef = new ClefG2();
        staff.addCoreSymbol(clef);
        staff.addCoreSymbol(ks);
        staff.addCoreSymbol(ts);

        SimpleRest r1 = new SimpleRest(Figures.MINIM, 0);
        add(staff, layer, r1);
        SimpleNote n0 = new SimpleNote(Figures.MINIM, 0, new ScientificPitch(DiatonicPitch.D, null, 5));
        add(staff, layer, n0);
        staff.addCoreSymbol(new MarkBarline(n0.getOffset()));
        SimpleNote n1 = new SimpleNote(Figures.SEMIMINIM, 0, new ScientificPitch(DiatonicPitch.E, null, 5));
        add(staff, layer, n1);
        SimpleNote n2 = new SimpleNote(Figures.SEMIMINIM, 0, new ScientificPitch(DiatonicPitch.F, null, 5));
        add(staff, layer, n2);
        SimpleNote n3 = new SimpleNote(Figures.SEMIBREVE, 0, new ScientificPitch(DiatonicPitch.G, null, 5));
        add(staff, layer, n3);
        SimpleNote n4 = new SimpleNote(Figures.MINIM, 0, new ScientificPitch(DiatonicPitch.F, Accidentals.SHARP, 5));
        add(staff, layer, n4);
        staff.addCoreSymbol(new MarkBarline(n4.getOffset()));
        SimpleNote n5 = new SimpleNote(Figures.MINIM, 0, new ScientificPitch(DiatonicPitch.G, null, 5));
        add(staff, layer, n5);
        SimpleNote n6 = new SimpleNote(Figures.MINIM, 0, new ScientificPitch(DiatonicPitch.G, null, 5));
        add(staff, layer, n6);
        staff.addCoreSymbol(new MarkBarline(n6.getOffset()));
        SimpleNote n7 = new SimpleNote(Figures.SEMIBREVE, 0, new ScientificPitch(DiatonicPitch.A, null, 5));
        add(staff, layer, n7);
        staff.addCoreSymbol(new MarkBarline(n7.getOffset()));
        SimpleNote n8 = new SimpleNote(Figures.SEMIBREVE, 0, new ScientificPitch(DiatonicPitch.A, null, 5));
        add(staff, layer, n8);
        staff.addCoreSymbol(new MarkBarline(n8.getOffset()));

        // convert into a new song
        MensuralToModern mensuralToModern = new MensuralToModern(new Clef[] {new ClefG2()});
        ScoreSong modernSong = mensuralToModern.convertIntoNewSong(song, Intervals.UNISON_PERFECT);

        assertEquals( "Modern parts",  1, modernSong.getParts().size());
        assertEquals( "Modern staves",  1, modernSong.getStaves().size());
        assertEquals( "Part layers",  1, modernSong.getParts().get(0).getLayers().size());
        assertEquals( "Staff layers",  1, modernSong.getStaves().get(0).getLayers().size());
        Staff modernStaff = modernSong.getStaves().get(0);
        ScoreLayer modernLayer = modernStaff.getLayers().get(0);

        //TODO assertEquals("Core symbols in staff", 17, modernStaff.getCoreSymbolsOrdered().size());
        //TODO assertEquals("Atoms in layer", 11, modernLayer.getAtoms().size());
        //TODO assertEquals("Core symbols in staff", 17, modernStaff.getCoreSymbolsOrdered().size());
        //TODO assertEquals("Atoms in layer", 11, modernLayer.getAtoms().size());

        //printSVG(song, LayoutFonts.capitan, "mensural.svg");
        //printSVG(modernSong, LayoutFonts.bravura, "mensural2modern.svg");

        // ---------
        // convert into a new staff in the same song
        Staff newModernStaff = new Pentagram(song, "2", 2);
        ScorePart newPart = song.addPart();
        ScoreLayer newLayer = newPart.addScoreLayer(newModernStaff);
        newPart.addStaff(newModernStaff);
        newModernStaff.setNotationType(NotationType.eModern); // TODO: 6/10/17 ¿Debería estar el tipo de notación en el constructor?
        song.addStaff(newModernStaff);

        Clef [] modernClefs = new Clef[] {new ClefG2()};
        MensuralToModern mensuralToModern2 = new MensuralToModern(modernClefs);

        mensuralToModern2.convertIntoStaff(staff, newModernStaff, newLayer, Intervals.FOURTH_PERFECT_DESC, modernClefs[0]);

        // render it putting in the top staff the mensural one and in the bottom staff the modern one
        HorizontalLayout layout2 = new HorizontalLayout(song,
                new CoordinateComponent(960), new CoordinateComponent(700));
        layout2.layout(true);

        SVGExporter svgExporter = new SVGExporter();
        File svgFile = TestFileUtils.createTempFile("mensuralAndmodern.svg");
        svgExporter.exportLayout(svgFile, layout2);


    }

    private void add(Staff staff, ScoreLayer layer, SingleFigureAtom atom) throws IM3Exception {
        staff.addCoreSymbol(atom);
        layer.add(atom);
    }

}
