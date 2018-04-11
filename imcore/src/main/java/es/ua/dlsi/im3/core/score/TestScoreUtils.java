package es.ua.dlsi.im3.core.score;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @autor drizo
 */
public class TestScoreUtils {
    public static void checkEqual(String aType, ScoreSong a, String bType, ScoreSong b) throws IM3Exception {
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

            assertEquals("Layers", astaff.getLayers().size(), bstaff.getLayers().size());

            System.out.println(astaff.getName());
            for (int i=0; i<bstaff.getAtoms().size(); i++) {
                System.out.println(bstaff.getAtoms().get(i));
            }
            assertEquals("Atoms in staff " + astaff.getName(), astaff.getAtoms().size(), bstaff.getAtoms().size());

            for (int i=0; i<astaff.getAtoms().size(); i++) {
                Atom atomA = astaff.getAtoms().get(i);
                Atom atomB = bstaff.getAtoms().get(i);
                checkEqualAtom(astaff.getName(), i, atomA, atomB);
            }
        }

        // TODO: 27/3/18 Fermate...
        // TODO: 10/4/18 Coloration ... 
    }

    /**
     * Done this way to have a finer control over what we are comparing
     * @param name
     * @param i
     * @param atomA
     * @param atomB
     */
    private static void checkEqualAtom(String name, int i, Atom atomA, Atom atomB) {
        assertEquals("Staff " + name+ ", atom #" + i + ", atom class", atomA.getClass(), atomB.getClass());

        assertEquals("Staff " + name+ ", atom #" + i + ", onset", atomA.getTime(), atomB.getTime());
        assertEquals("Staff " + name+ ", atom #" + i + ", duration", atomA.getDuration(), atomB.getDuration());

        if (atomA instanceof SimpleNote) {
            SimpleNote noteA = (SimpleNote) atomA;
            SimpleNote noteB = (SimpleNote) atomB;
            assertEquals("Staff " + name+ ", atom #" + i + ", pitch", noteA.getPitch(), noteB.getPitch());
            assertEquals("Staff " + name+ ", atom #" + i + ", optional accidental", noteA.getAtomPitch().isOptionalAccidental(), noteB.getAtomPitch().isOptionalAccidental());
            assertEquals("Staff " + name+ ", atom #" + i + ", tied from previous", noteA.getAtomPitch().isTiedFromPrevious(), noteB.getAtomPitch().isTiedFromPrevious());
            assertEquals("Staff " + name+ ", atom #" + i + ", colored", noteA.getAtomFigure().isColored(), noteB.getAtomFigure().isColored());
        } else if (atomA instanceof SimpleRest) {
            //no-op
        } else {
            throw new IM3RuntimeException("Unsupported comparison between " + atomA.getClass());
        }
    }

}
