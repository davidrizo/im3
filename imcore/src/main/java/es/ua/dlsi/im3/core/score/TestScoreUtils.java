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
                    System.err.println("Found time signatures in staff:" + bstaff.getName());
                    for (TimeSignature bbts: bstaff.getTimeSignatures()) {
                        System.err.println("\t" + bbts.getTime() + ": " + bbts);
                    }

                    fail("No time signature in " + bType + " at time " + ats.getTime() + ", expected " + ats.getTime() + " for time signature " + ats);
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

            /*System.out.println(astaff.getName());
            for (int i=0; i<bstaff.getAtoms().size(); i++) {
                System.out.println(bstaff.getAtoms().get(i));
            }*/
            assertEquals("Atoms in staff " + astaff.getName(), astaff.getAtoms().size(), bstaff.getAtoms().size());

            for (int i=0; i<astaff.getAtoms().size(); i++) {
                Atom atomA = astaff.getAtoms().get(i);
                Atom atomB = bstaff.getAtoms().get(i);
                checkEqualAtom(astaff.getName(), aType, i, atomA, atomB);
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
    private static void checkEqualAtom(String name, String songTypeOfExpected, int i, Atom atomA, Atom atomB) {
        assertEquals("Staff " + name+ ", atom #" + i + ", atom class, expected = "+ songTypeOfExpected, atomA.getClass(), atomB.getClass());

        assertEquals("Staff " + name+ ", atom #" + i + ", onset, expected = "+ songTypeOfExpected, atomA.getTime(), atomB.getTime());
        assertEquals("Staff " + name+ ", atom #" + i + ", duration, expected = "+ songTypeOfExpected, atomA.getDuration(), atomB.getDuration());

        if (atomA instanceof SimpleNote) {
            SimpleNote noteA = (SimpleNote) atomA;
            SimpleNote noteB = (SimpleNote) atomB;
            assertEquals("Staff " + name + ", atom #" + i + ", pitch, expected = " + songTypeOfExpected, noteA.getPitch(), noteB.getPitch());
            //assertEquals("Staff " + name + ", atom #" + i + ", pitch, expected = " + songTypeOfExpected, noteA.getAtomPitch().getWrittenExplicitAccidental().
            assertEquals("Staff " + name + ", atom #" + i + ", optional accidental, expected = " + songTypeOfExpected, noteA.getAtomPitch().isOptionalAccidental(), noteB.getAtomPitch().isOptionalAccidental());
            assertEquals("Staff " + name + ", atom #" + i + ", tied from previous, expected = " + songTypeOfExpected, noteA.getAtomPitch().isTiedFromPrevious(), noteB.getAtomPitch().isTiedFromPrevious());
            assertEquals("Staff " + name + ", atom #" + i + ", colored, expected = " + songTypeOfExpected, noteA.getAtomFigure().isColored(), noteB.getAtomFigure().isColored());

            assertEquals("Staff " + name + ", atom #" + i + ", explicit stem, expected = " + songTypeOfExpected, noteA.getExplicitStemDirection(), noteB.getExplicitStemDirection());

        } else if (atomA instanceof SimpleRest) {
            //no-op
        } else if (atomA instanceof LigaturaBinaria) {
            LigaturaBinaria la = (LigaturaBinaria) atomA;
            LigaturaBinaria lb = (LigaturaBinaria) atomB;
            assertEquals("Staff " + name + ", atom #" + i + ", number of ligature notes, expected " + songTypeOfExpected, la.getAtoms().size(), lb.getAtoms().size());
            for (int ia=0; ia<la.getAtoms().size(); ia++) {
                assertEquals("Staff " + name + ", ligature atom #" + i + ", subelement " + ia + ", pitch, expected = " + songTypeOfExpected, la.getAtomPitches().get(ia).getScientificPitch(), lb.getAtomPitches().get(ia).getScientificPitch());
                assertEquals("Staff " + name + ", ligature atom #" + ", subelement " + ia + + i + ", optional accidental, expected = " + songTypeOfExpected, la.getAtomPitches().get(ia).isOptionalAccidental(), lb.getAtomPitches().get(ia).isOptionalAccidental());
                assertEquals("Staff " + name + ", ligature atom #" + ", subelement " + ia + + i + ", colored, expected = " + songTypeOfExpected, la.getAtomFigures().get(ia).isColored(), lb.getAtomFigures().get(ia).isColored());
                assertEquals("Staff " + name + ", ligature atom #" + ", subelement " + ia + + i + ", duration, expected = " + songTypeOfExpected, la.getAtomFigures().get(ia).getDuration(), lb.getAtomFigures().get(ia).getDuration());
                assertEquals("Staff " + name + ", ligature atom #" + ", subelement " + ia + + i + ", figure, expected = " + songTypeOfExpected, la.getAtomFigures().get(ia).getFigure(), lb.getAtomFigures().get(ia).getFigure());

            }
        } else {
            throw new IM3RuntimeException("Unsupported comparison between " + atomA.getClass());
        }
    }

}
