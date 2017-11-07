package es.ua.dlsi.im3.omr.wrimus;

import es.ua.dlsi.im3.core.TestFileUtils;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.io.mei.MEISongImporter;
import es.ua.dlsi.im3.core.score.io.musicxml.MusicXMLImporter;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class StaffRendererTest {
    @Test
    public void render() throws Exception {
        MEISongImporter importer = new MEISongImporter();
        ScoreSong scoreSong = importer.importSong(TestFileUtils.getFile("/testdata/simple1.mei"));

        assertEquals("Staves", 1, scoreSong.getStaves().size());

        HomusReader reader = new HomusReader();
        ArrayList<File> files = new ArrayList<>();
        for (int folder=1; folder<=2; folder++) {
            for (int i = 1; i <= 152; i++) {
                String fileName = folder + "-" + i + ".txt";
                File file = TestFileUtils.getFile("/testdata/homus/" + folder, fileName);
                files.add(file);
            }
        }

        HomusDataset homusDataset = reader.read(files);
        homusDataset.checkCompleteness();
        StaffRenderer renderer = new StaffRenderer(homusDataset, scoreSong.getStaves().get(0));
        File svg = TestFileUtils.createTempFile("homus.svg");
        renderer.render(svg);
    }
}