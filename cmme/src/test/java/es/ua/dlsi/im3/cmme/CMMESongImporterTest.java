package es.ua.dlsi.im3.cmme;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.TestFileUtils;
import es.ua.dlsi.im3.core.io.ImportException;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.clefs.ClefC1;
import es.ua.dlsi.im3.core.score.clefs.ClefC3;
import es.ua.dlsi.im3.core.score.clefs.ClefC4;
import es.ua.dlsi.im3.core.score.clefs.ClefF3;
import es.ua.dlsi.im3.core.score.io.kern.KernExporter;
import es.ua.dlsi.im3.core.score.io.kern.KernImporter;
import es.ua.dlsi.im3.core.score.io.mei.MEISongExporter;
import es.ua.dlsi.im3.core.score.meters.TimeSignatureCutTime;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.junit.Assert.*;

public class CMMESongImporterTest {
    @Test
    public void importSong1() throws Exception {
        CMMESongImporter importer = new CMMESongImporter();
        File file = TestFileUtils.getFile("/testdata/cmme/missa-mort-et-fortune_01kyrie.cmme.xml");
        ScoreSong scoreSong = importer.importSong(file);
        assertEquals("Composer", "Jachet Berchem", scoreSong.getComposer());

        assertEquals("Staves", 4, scoreSong.getStaves().size());

        assertEquals("Staff 1", "Cantus", scoreSong.getStaves().get(0).getName());
        assertEquals("Part 1", "Cantus", scoreSong.getParts().get(0).getName());
        assertTrue("Cantus first clef", scoreSong.getStaves().get(0).getClefAtTime(Time.TIME_ZERO) instanceof ClefC1);

        assertEquals("Staff 2", "Altus", scoreSong.getStaves().get(1).getName());
        assertEquals("Part 2", "Altus", scoreSong.getParts().get(1).getName());
        assertTrue("Altus first clef", scoreSong.getStaves().get(1).getClefAtTime(Time.TIME_ZERO) instanceof ClefC3);

        assertEquals("Staff 3", "Tenor", scoreSong.getStaves().get(2).getName());
        assertEquals("Part 3", "Tenor", scoreSong.getParts().get(2).getName());
        assertTrue("Tenor first clef", scoreSong.getStaves().get(2).getClefAtTime(Time.TIME_ZERO) instanceof ClefC4);

        assertEquals("Staff 4", "Bassus", scoreSong.getStaves().get(3).getName());
        assertEquals("Part 4", "Bassus", scoreSong.getParts().get(3).getName());
        assertTrue("Bassus first clef", scoreSong.getStaves().get(3).getClefAtTime(Time.TIME_ZERO) instanceof ClefF3);

        assertTrue("First meter sign", scoreSong.getUniqueMeterWithOnset(Time.TIME_ZERO) instanceof TimeSignatureCutTime);

        List<Atom> cantusAtoms = scoreSong.getParts().get(0).getAtomsSortedByTime();
        assertTrue("First event in cantus", cantusAtoms.get(0) instanceof SimpleRest);
        assertEquals("Duration of first cantus event", new Time(4, 1), cantusAtoms.get(0).getDuration());

        assertTrue("Second event in cantus", cantusAtoms.get(1) instanceof SimpleNote);
        assertEquals("Duration of second cantus event", new Time(2, 1), cantusAtoms.get(1).getDuration());
        assertEquals("Pitch of second cantus event", new ScientificPitch(PitchClasses.A, 4), ((SimpleNote)cantusAtoms.get(1)).getPitch());

        assertTrue("11th event in cantus", cantusAtoms.get(10) instanceof SimpleNote);
        assertEquals("Duration of second cantus event", new Time(3, 2), cantusAtoms.get(10).getDuration());
        assertEquals("Pitch of second cantus event", new ScientificPitch(PitchClasses.C, 5), ((SimpleNote)cantusAtoms.get(10)).getPitch());
        assertEquals("Dots of 11th event in cantus", 1, ((SimpleNote) cantusAtoms.get(10)).getAtomFigure().getDots());


        assertEquals("Penultimate cantus note pitch", PitchClasses.G_SHARP.getPitchClass(), ((SimpleNote) cantusAtoms.get(cantusAtoms.size()-2)).getPitch().getPitchClass());


        List<Atom> altusAtoms = scoreSong.getParts().get(1).getAtomsSortedByTime();
        assertEquals("Flat in altus note pitch", PitchClasses.B_FLAT.getPitchClass(), ((SimpleNote) altusAtoms.get(185)).getPitch().getPitchClass());
        assertFalse("Non optional accidental", ((SimpleNote) altusAtoms.get(185)).getAtomPitch().isOptionalAccidental());

        SimpleNote lastAltusNote = (SimpleNote) altusAtoms.get(altusAtoms.size()-1);
        assertEquals("Pitch of last altus note", PitchClasses.C_SHARP.getPitchClass(), lastAltusNote.getPitch().getPitchClass());
        assertTrue("Optional accidental", lastAltusNote.getAtomPitch().isOptionalAccidental());

        //TODO Editorial accidentals

        //TODO Hacer ejemplo completo en kern y compararlos

        MEISongExporter meiSongExporter = new MEISongExporter();
        meiSongExporter.exportSong(new File("/tmp/cmme.mei"), scoreSong);

        KernExporter kernExporter = new KernExporter();
        kernExporter.exportSong(new File("/tmp/cmme.krn"), scoreSong);
    }

    private void checkKrnVsCMME(String filenameWithoutExtension) throws IM3Exception {
        File cmmeFile = TestFileUtils.getFile("/testdata/cmme/incipits/" + filenameWithoutExtension + ".xml");
        File kernFile = TestFileUtils.getFile("/testdata/cmme/incipits/" + filenameWithoutExtension + ".krn");

        CMMESongImporter cmmeImporter = new CMMESongImporter();
        ScoreSong cmmeSong = cmmeImporter.importSong(cmmeFile);

        KernImporter kernImporter = new KernImporter();
        ScoreSong kernSong = kernImporter.importSong(kernFile);

        // TODO: 27/3/18 Comprobar
        checkEqual("cmme", cmmeSong, "kern", kernSong);

    }

    private void checkEqual(String aType, ScoreSong a, String bType, ScoreSong b) throws IM3Exception {
        assertEquals("Staves", a.getStaves().size(), b.getStaves().size());
        for (Staff astaff: a.getStaves()) {
            Staff bstaff = b.getStaffByName(astaff.getName());
            if (bstaff == null) {
                fail("No staff with name '" + astaff.getName() + "' found in " + bType);
            }

            assertEquals("Clefs", astaff.getClefs().size(), bstaff.getClefs().size());
            for (Clef aclef: astaff.getClefs()) {
                Clef bclef = bstaff.getClefAtTime(aclef.getTime());
                if (bclef == null) {
                    fail("No clef in " + bType + " at time " + aclef.getTime() + ", expected " + aclef.getTime());
                }
                assertEquals("Staff " + astaff.getName(), aclef, bclef);
            }

            assertEquals("Time signatures", astaff.getTimeSignatures().size(), bstaff.getTimeSignatures().size());
            for (TimeSignature ats: astaff.getTimeSignatures()) {
                TimeSignature bts = bstaff.getTimeSignatureWithOnset(ats.getTime());
                if (bts == null) {
                    fail("No time signature in " + bType + " at time " + ats.getTime() + ", expected " + ats.getTime());
                }
                assertEquals("Staff " + astaff.getName(), ats.toString(), bts.toString());
            }

            assertEquals("Key signatures", astaff.getKeySignatures().size(), bstaff.getKeySignatures().size());
            for (KeySignature ats: astaff.getKeySignatures()) {
                KeySignature bts = bstaff.getKeySignatureWithOnset(ats.getTime());
                if (bts == null) {
                    fail("No key signature in " + bType + " at time " + ats.getTime() + ", expected " + ats.getTime());
                }
                assertEquals("Staff " + astaff.getName(), ats.toString(), bts.toString());
            }

            //assertEquals("Layers", astaff.getLayers().size(), bstaff.getLayers().size());

            assertEquals("Atoms", astaff.getAtoms().size(), bstaff.getAtoms().size());

            for (int i=0; i<astaff.getAtoms().size(); i++) {
                Atom atomA = astaff.getAtoms().get(i);
                Atom atomB = bstaff.getAtoms().get(i);
                assertEquals("Staff " + astaff.getName() + ", atom #" + i, atomA.toString(), atomB.toString());
            }
        }

        // TODO: 27/3/18 Fermate...
    }

    @Test
    public void importIncipit() throws Exception {
        checkKrnVsCMME("01-4r-anonymous-o_salutaris_hostia.cmme");
        //checkKrnVsCMME("03-4v_5r-larue-o_salutaris_hostia.cmme");
    }

}