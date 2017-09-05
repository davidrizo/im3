package es.ua.dlsi.im3.core.score.io.harmony;

import es.ua.dlsi.im3.core.score.Key;
import es.ua.dlsi.im3.core.score.Mode;
import es.ua.dlsi.im3.core.score.PitchClasses;
import es.ua.dlsi.im3.core.score.TonalFunction;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by drizo on 23/6/17.
 */
public class HarmonyImporterTest {
    @Test
    public void readKey() throws Exception {

        HarmonyImporter importer = new HarmonyImporter();

        Key[] key = importer.readKey("CM");
        assertEquals(1, key.length);
        assertEquals(new Key(PitchClasses.C, Mode.MAJOR), key[0]);

        key = importer.readKey("C");
        assertEquals(1, key.length);
        assertEquals(new Key(PitchClasses.C, Mode.MAJOR), key[0]);

        key = importer.readKey("Abm[F#]");
        assertEquals(2, key.length);
        assertEquals(new Key(PitchClasses.A_FLAT, Mode.MINOR), key[0]);
        assertEquals(new Key(PitchClasses.F_SHARP, Mode.MAJOR), key[1]);

        key = importer.readKey("FM[Dm]");
        assertEquals(2, key.length);
        assertEquals(new Key(PitchClasses.F, Mode.MAJOR), key[0]);
        assertEquals(new Key(PitchClasses.D, Mode.MINOR), key[1]);

        key = importer.readKey("GM[FM]");
        assertEquals(2, key.length);
        assertEquals(new Key(PitchClasses.G, Mode.MAJOR), key[0]);
        assertEquals(new Key(PitchClasses.F, Mode.MAJOR), key[1]);




    }

    @Test
    public void readTonalFunction() throws Exception {
        HarmonyImporter importer = new HarmonyImporter();

        TonalFunction[] tf = importer.readTonalFunction("T");
        assertEquals(1, tf.length);
        assertEquals(TonalFunction.TONIC, tf[0]);

        tf = importer.readTonalFunction("D");
        assertEquals(1, tf.length);
        assertEquals(TonalFunction.DOMINANT, tf[0]);

        tf = importer.readTonalFunction("S");
        assertEquals(1, tf.length);
        assertEquals(TonalFunction.SUBDOMINANT, tf[0]);

        tf = importer.readTonalFunction("SD");
        assertEquals(1, tf.length);
        assertEquals(TonalFunction.SUBDOMINANT, tf[0]);

        tf = importer.readTonalFunction("SD[T]");
        assertEquals(2, tf.length);
        assertEquals(TonalFunction.SUBDOMINANT, tf[0]);
        assertEquals(TonalFunction.TONIC, tf[1]);

    }

}