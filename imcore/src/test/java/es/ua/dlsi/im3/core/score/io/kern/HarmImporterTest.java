package es.ua.dlsi.im3.core.score.io.kern;

import es.ua.dlsi.im3.core.score.Key;
import es.ua.dlsi.im3.core.score.Mode;
import es.ua.dlsi.im3.core.score.harmony.Harm;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * KernImporter has the HarmImporter embedded
 * Created by drizo on 20/6/17.
 */
public class HarmImporterTest {
    @Test
    public void readHarmony() throws Exception {
        //TODO asserts - p.ej. creo que no pilla bien los extensions ni las inversiones
        String[] chords = {
                "V[I/V]",
        // see http://extras.humdrum.org/example/harm2kern/#test011
        // Test 001:
            "I", "ii", "iii", "IV", "V", "vi", "viio",
        // Test 002:
                "i", "iio", "III", "iv", "v", "VI", "VII",
                // Test 004:
                "Ib", "iib", "iiib", "IVb", "Vb", "vib", "viiob",
                // Test 005:
                "Ic", "iic", "iiic", "IVc", "Vc", "vic", "viioc",
                // Test 006:
                "I7", "ii7", "iii7", "IV7", "V7", "vi7", "viio7", "I7a", "ii7b", "iii7c", "IV7d", "V7a", "vi7b", "viio7c",
                // Test 009
                "I", "iib", "iiic", "IVa", "Vb", "vic", "viio",
                // Test 013
                "I", "V", "V/V", "V/V/V", "V/V/V/V", "V/V/V/V/V", "viio", "viio/V", "viio/VI", "V7", "V7/ii", "V7/II", "V7/-VII", "V7/I", "V7/V/II",
                // http://extras.humdrum.org/man/harm2kern/
                // Chord qualities
                "III", "iii", "III+", "iiio",
                //"V7", "VM7", "Vm7", "VA7", "VD7" TODO
                // Inversions
                "I", "Ia", "Ib", "Ic", "V7", "V7a", "V7b", "V7c", "V7d",
                // chromatic chords
                "Nb",
                // others
                "V[ii7]"

        };

        //String t1 = "V[ii7]";

        KernImporter importer = new KernImporter();
        HarmExporter exporter = new HarmExporter();

        Key key = new Key(0, Mode.MAJOR); //TODO pasarle las tonalidades correctas para comprobar que extrae bien las notas concretas como en los tests de humdrum
        for (int i=0; i<chords.length; i++) {
            //System.out.println("Parsing " + chords[i]);
            Harm chord = importer.readHarmony(key, chords[i]);

            String exportedChord = exporter.exportHarm(chord);
            assertEquals(i+"th chord", chords[i], exportedChord);

            //System.out.println("\t" + chord);
        }

        String [] justImport = {"viioD7", "vii7D", "vii7Dc"};
        for (int i=0; i<justImport.length; i++) {
            Harm chord = importer.readHarmony(key, justImport[i]);
        }
    }

}