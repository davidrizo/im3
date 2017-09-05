package es.ua.dlsi.im3.analyzers.tonal;

import es.ua.dlsi.im3.core.TestFileUtils;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.harmony.Harm;
import es.ua.dlsi.im3.core.score.harmony.RomanNumberChordSpecification;
import es.ua.dlsi.im3.core.score.io.harmony.HarmonyExporter;
import es.ua.dlsi.im3.core.score.io.kern.HarmExporter;
import es.ua.dlsi.im3.core.score.io.mei.MEISongExporter;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by drizo on 22/6/17.
 */
public class ImporterExporterTest {
    @Test
    public void readMusicXMLExportMEI() throws Exception {
        File file = TestFileUtils.getFile("/testdata/analysis/tonal/academic/euromac2014/musicxml/26.xml");
        ImporterExporter importerExporter = new ImporterExporter();
        ScoreSong song = importerExporter.readMusicXML(file, false);

        ArrayList<Harm> harms = song.getOrderedHarms();
        assertEquals("Harmony count", 44, harms.size());

        Harm firstHarm = harms.get(0);
        assertEquals("First harmony time", Time.TIME_ZERO, firstHarm.getTime());
        assertEquals("First harmony key", new Key(PitchClasses.G, Mode.MINOR), firstHarm.getKey());
        assertEquals("First harmony tonal function", TonalFunction.TONIC, firstHarm.getTonalFunction());
        assertEquals("First harmony tonal degree", Degree.I, ((RomanNumberChordSpecification)firstHarm.getChordSpecifications().get(0)).getRoot().getDegree());


        File meiFile = TestFileUtils.createTempFile("26.mei");

        MEISongExporter exporter = new MEISongExporter();
        exporter.setUseHarmTypes(true);
        exporter.exportSong(meiFile, song);

        //TODO IMPORT MusicXML - EXPORT MEI - IMPORT MEI - que sea lo mismo - éste y el otro test - probar alternate, modulaciones...
    }

    /**
     * Test of readDegree method, of class ImporterExporter. Ported from imcore3
     */
    @Test
    public void testImportExport272() throws Exception {
        ImporterExporter io = new ImporterExporter();
        ScoreSong song = io.readMusicXML(TestFileUtils.getFile("/testdata/analysis/tonal/academic/euromac2014/musicxml/272.xml"),
                false);
        List<Harm> harmonies = song.getOrderedHarms();
        HarmExporter harmExporter = new HarmExporter();

        String[] degrees = { "I", "I", "VII", "I", "II", "V", "I", "I", "III", "VII", "VII/V", "V", "I", "I", "VII",
                "I", "V", "I[V/V]", "V", "I", "I", "II", "VII/V", "V", "I", "V", "I", "IV", "V", "I[III]", "IV", "VII",
                "VI", "I", "VII/V", "V", "I", "VII", "I", "V", "I" };

        assertEquals("# Harmonies", degrees.length, harmonies.size());

        for (int i = 0; i < degrees.length; i++) {
            String degreeString = harmExporter.exportHarm(harmonies.get(i));
            assertEquals("Degree #" + i + " in harmony " + harmonies.get(i), degrees[i],
                    degreeString);
        }

        HarmonyExporter exporter = new HarmonyExporter();

        String[] functions = { "T", "T", "D", "T", "S", "D", "T", "T", "S", "D", "D", "D", "T", "T", "D", "T", "D",
                "T[D]", "D", "T", "T", "S", "D", "D", "T", "D", "T", "S", "D", "T[S]", "S", "D", "T", "T", "D", "D", "T",
                "D", "T", "D", "T" };

        for (int i = 0; i < functions.length; i++) {
            assertEquals("Function #" + i, functions[i], exporter.exportTonalFunction(harmonies.get(i)));
        }

        String[] keys = new String[functions.length];
        String[] currentKey = new String[functions.length];
        keys[0] = "Dm";
        currentKey[0] = "Dm";
        keys[4] = "FM";
        keys[8] = "Am";
        keys[16] = "GM";
        keys[17] = "GM[FM]";
        keys[18] = "FM";
        currentKey[17] = "FM";
        keys[25] = "Dm";
        keys[28] = "FM";
        keys[29] = "FM[Dm]";
        keys[30] = "Dm";
        currentKey[29] = "Dm";
        String lastKey = keys[0];
        for (int i = 0; i < keys.length; i++) {
            String key;
            if (keys[i] == null) {
                key = lastKey;
            } else {
                key = keys[i];
                lastKey = key; //TODO modulación
            }
            assertEquals("Key #" + i, key, exporter.exportKey(harmonies.get(i)));
        }
    }

}