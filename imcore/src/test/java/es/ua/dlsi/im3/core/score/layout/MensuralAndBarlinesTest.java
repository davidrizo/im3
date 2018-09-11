package es.ua.dlsi.im3.core.score.layout;

import es.ua.dlsi.im3.core.TestFileUtils;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.io.mei.MEISongImporter;
import es.ua.dlsi.im3.core.score.layout.coresymbols.*;
import es.ua.dlsi.im3.core.score.layout.fonts.LayoutFonts;
import es.ua.dlsi.im3.core.score.layout.svg.SVGExporter;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import static org.junit.Assert.*;

public class MensuralAndBarlinesTest {
    /**
     * See: https://github.com/music-encoding/sibmei/issues/38
     * @throws Exception
     */
    @Test
    public void mensuralAndBarlinesTest() throws Exception {
        MEISongImporter importer = new MEISongImporter();
        File file = TestFileUtils.getFile("/testdata/core/score/layout/patriarca/mensural_accidentals_barlines.mei");
        ScoreSong song = importer.importSong(file);

        List<AtomPitch> pitches = song.getStaves().get(0).getAtomPitches();
        assertEquals("Number of pitches", 13, pitches.size());

        assertEquals("Sound #0", PitchClasses.B_FLAT.getPitchClass(), pitches.get(0).getScientificPitch().getPitchClass());
        assertEquals("Sound #1", PitchClasses.E_FLAT.getPitchClass(), pitches.get(1).getScientificPitch().getPitchClass());
        assertEquals("Sound #2", PitchClasses.B.getPitchClass(), pitches.get(2).getScientificPitch().getPitchClass());
        assertEquals("Sound #3", PitchClasses.B_FLAT.getPitchClass(), pitches.get(3).getScientificPitch().getPitchClass());
        assertEquals("Sound #4", PitchClasses.B_FLAT.getPitchClass(), pitches.get(4).getScientificPitch().getPitchClass());
        assertEquals("Sound #5", PitchClasses.F.getPitchClass(), pitches.get(5).getScientificPitch().getPitchClass());
        assertEquals("Sound #6", PitchClasses.F_SHARP.getPitchClass(), pitches.get(6).getScientificPitch().getPitchClass());
        assertEquals("Sound #7", PitchClasses.F_SHARP.getPitchClass(), pitches.get(7).getScientificPitch().getPitchClass());
        assertEquals("Sound #8", PitchClasses.F_SHARP.getPitchClass(), pitches.get(8).getScientificPitch().getPitchClass());
        assertEquals("Sound #9", PitchClasses.F.getPitchClass(), pitches.get(9).getScientificPitch().getPitchClass());
        assertEquals("Sound #10", PitchClasses.A_FLAT.getPitchClass(), pitches.get(10).getScientificPitch().getPitchClass());
        assertEquals("Sound #11", PitchClasses.A_FLAT.getPitchClass(), pitches.get(11).getScientificPitch().getPitchClass());
        assertEquals("Sound #12", PitchClasses.B.getPitchClass(), pitches.get(12).getScientificPitch().getPitchClass());

        HorizontalLayout layout = new HorizontalLayout(song,
                new CoordinateComponent(960), new CoordinateComponent(700));
        layout.layout(true);

        TreeSet<LayoutCoreSymbolInStaff> layoutSymbols = layout.getSystem().getTopStaff().getLayoutSymbolsInStaff();
        assertEquals("Number of layout symbols", 22, layoutSymbols.size()); // clef, key signature, time signature, notes and barlines
        ArrayList<LayoutCoreSingleFigureAtom> v = new ArrayList<>();
        Iterator<LayoutCoreSymbolInStaff> iter = layoutSymbols.iterator();
        for (int i=0; i<22; i++) {
            LayoutCoreSymbolInStaff coreSymbolInStaff = iter.next();
            if (coreSymbolInStaff instanceof LayoutCoreSingleFigureAtom) {
                v.add((LayoutCoreSingleFigureAtom)coreSymbolInStaff);
                assertEquals("Just 1 pitch", 1, ((LayoutCoreSingleFigureAtom)coreSymbolInStaff).getNotePitches().size());
            }
        }
        assertEquals("Number of notes", 13, v.size());
        assertNull("Written accidental #0", v.get(0).getNotePitches().get(0).getAccidental());
        assertNull("Written accidental #1", v.get(1).getNotePitches().get(0).getAccidental());
        assertNotNull("Written accidental #2", v.get(2).getNotePitches().get(0).getAccidental());
        assertEquals("Written accidental #2", Accidentals.NATURAL, v.get(2).getNotePitches().get(0).getAccidental().getAccidental());
        assertNotNull("Written accidental #3", v.get(3).getNotePitches().get(0).getAccidental());
        assertEquals("Written accidental #3", Accidentals.FLAT, v.get(3).getNotePitches().get(0).getAccidental().getAccidental());

        assertNull("Written accidental #4", v.get(4).getNotePitches().get(0).getAccidental());

        assertNull("Written accidental #5", v.get(5).getNotePitches().get(0).getAccidental());
        assertNotNull("Written accidental #6", v.get(6).getNotePitches().get(0).getAccidental());
        assertEquals("Written accidental #6", Accidentals.SHARP, v.get(6).getNotePitches().get(0).getAccidental().getAccidental());
        assertNull("Written accidental #7", v.get(7).getNotePitches().get(0).getAccidental());
        assertNull("Written accidental #8", v.get(8).getNotePitches().get(0).getAccidental());

        assertNull("Written accidental #9", v.get(9).getNotePitches().get(0).getAccidental());
        assertNull("Written accidental #10", v.get(10).getNotePitches().get(0).getAccidental());
        assertNull("Written accidental #11", v.get(11).getNotePitches().get(0).getAccidental());

        assertNotNull("Written accidental #12", v.get(12).getNotePitches().get(0).getAccidental());
        assertEquals("Written accidental #12", Accidentals.SHARP, v.get(12).getNotePitches().get(0).getAccidental().getAccidental());

        /*
    <note pname="b" dur="minima" oct="4" accid.ges="f"/> <!-- sound expected: Bb, written B without flat -->
    <note pname="e" dur="minima" oct="3"/> <!-- sound expected Eb because accid.ges is optional, written E without flat -->
    <note pname="b" dur="minima" oct="4" accid="n"/> <!-- sound expected natural B, written B with natural -->
    <note pname="b" dur="minima" oct="4" accid="f"/> <!-- sound expected flat B, written B with flat -->
    <barLine/>
    <note pname="b" dur="semibrevis" oct="4"/> <!-- sound expected: Bb, written B without flat -->
    <barLine/>
    <note pname="f" dur="minima" oct="4"/> <!-- sound expected: F, written F without any accidental -->
    <note pname="f" dur="minima" oct="4" accid="s"/> <!-- sound expected: F#, written F with a sharp -->
    <note pname="f" dur="minima" oct="4"/> <!-- sound expected: F#, written F without a sharp -->
    <note pname="f" dur="minima" oct="4" accid.ges="s"/> <!-- sound expected: F#, written F without a sharp -->
    <barLine/>
    <note pname="f" dur="semibrevis" oct="4"/> <!-- sound expected: F, written F without any accidental -->
 */
    }

}

