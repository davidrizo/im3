package es.ua.dlsi.im3.omr.wrimus;

import es.ua.dlsi.im3.core.TestFileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class HomusReaderTest {
    public HomusDataset buildHomus() throws IOException {
        ArrayList<File> files = new ArrayList<>();
        files.add(TestFileUtils.getFile("/testdata/homus/1-1.txt"));
        files.add(TestFileUtils.getFile("/testdata/homus/1-2.txt"));
        files.add(TestFileUtils.getFile("/testdata/homus/2-1.txt"));
        files.add(TestFileUtils.getFile("/testdata/homus/7-39.txt"));
        files.add(TestFileUtils.getFile("/testdata/homus/7-40.txt"));

        HomusReader reader = new HomusReader();
        HomusDataset homusDataset = reader.read(files);
        return homusDataset;
    }

    @Test
    public void read() throws Exception {
        HomusDataset homusDataset = buildHomus();
        assertEquals("Symbols", 2, homusDataset.getGlyphs().size());
        Symbol symbolCClef = new Symbol("C-Clef");
        Symbol symbol12_8_Time = new Symbol("12-8-Time");

        assertEquals("12-8-Time", 3, homusDataset.getGlyphs().get(symbol12_8_Time).size());
        assertEquals("C-Clef", 2, homusDataset.getGlyphs().get(symbolCClef).size());
    }
    @Test
    public void readIndividual() throws Exception {
        HomusReader reader = new HomusReader();
        ArrayList<File> files = new ArrayList<>();
        files.add(TestFileUtils.getFile("/testdata/homus/1-1.txt"));
        HomusDataset homusDataset = reader.read(files);

        assertEquals("Symbols", 1, homusDataset.getGlyphs().size());
        Symbol symbol12_8_Time = new Symbol("12-8-Time");

        List<Glyph> glyphs = homusDataset.getGlyphs().get(symbol12_8_Time);
        assertEquals("12-8-Time", 1, glyphs.size());
        List<Stroke> strokes = glyphs.get(0).getStrokes();
        assertEquals("Strokes", 3, strokes.size());
        int [] expectedPoints = {10, 11, 18};
        int i=0;
        for (Stroke stroke: strokes) {
            assertEquals("Stroke #" + i, expectedPoints[i], stroke.getPoints().size());
            i++;
        }
    }
}