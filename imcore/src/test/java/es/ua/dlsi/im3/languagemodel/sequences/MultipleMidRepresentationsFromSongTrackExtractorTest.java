package es.ua.dlsi.im3.languagemodel.sequences;

import es.ua.dlsi.im3.core.TestFileUtils;
import es.ua.dlsi.im3.core.played.PlayedSong;
import es.ua.dlsi.im3.core.played.io.MidiSongImporter;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;


public class MultipleMidRepresentationsFromSongTrackExtractorTest {
    @Test
    public void test() throws Exception {
        MidiSongImporter importer = new MidiSongImporter();
        File file = TestFileUtils.getFile("/testdata/core/midrepresentations/B0619_Fmaj.mid");
        PlayedSong playedSong = importer.importSong(file);
        MultipleMidRepresentationsFromSongTrackExtractor extractor = new MultipleMidRepresentationsFromSongTrackExtractor(playedSong.getUniqueVoice());

        assertEquals("Intervals and IOI sizes", extractor.getPitchIntervals().size(), extractor.getIOIs().size());
        assertEquals("IOI and IOR sizes", extractor.getIOIs().size(), extractor.getIORs().size());

        List<Integer> pitchIntervals =
                Arrays.asList(0,-2,0, // represented here as measures
                        -2,2,3,
                        2,0,-2,0,
                        -1,0,
                        1,0,-3,0,
                        5,0,-3,0,
                        1,0,-3,0,
                        5,0,-3,
                        1,-3,0,
                        -2);
        assertEquals("Pitch intervals", pitchIntervals, extractor.getPitchIntervals());

        double [] durationRatios = new double [] {
                0, 1, 0.5,
                0.5, 1.5, 0.25,
                0.25, 0.5, 0.5, 0.5,
                0.5, 1.5,
                0.5, 0.5, 0.5, 0.5,
                0.5, 0.5, 0.5, 0.5,
                0.5, 0.5, 0.5, 0.5,
                0.5, 0.5, 0.5,
                1, 1, 0.5,
                0.5
        };

        List<Long> IOIs = new ArrayList<>();
        for (int i=0; i<durationRatios.length; i++) {
            IOIs.add((long) (durationRatios[i] * playedSong.getResolution()));
        }

        assertEquals("IOIs", IOIs, extractor.getIOIs());
    }

}