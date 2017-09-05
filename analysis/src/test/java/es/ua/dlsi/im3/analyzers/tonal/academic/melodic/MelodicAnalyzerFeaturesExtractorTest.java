package es.ua.dlsi.im3.analyzers.tonal.academic.melodic;

import es.ua.dlsi.im3.analyzers.tonal.TonalAnalysis;
import es.ua.dlsi.im3.core.TestFileUtils;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.io.musicxml.MusicXMLImporter;
import es.ua.dlsi.im3.core.utils.SonoritySegmenter;
import org.junit.Test;

import java.io.File;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by drizo on 13/6/17.
 */
public class MelodicAnalyzerFeaturesExtractorTest {
    @Test
    public void extractFeatures() throws Exception {
        File file = TestFileUtils.getFile("/testdata/analysis/tonal/academic/simple_melodic.xml");
        MusicXMLImporter importer = new MusicXMLImporter();
        ScoreSong song = importer.importSong(file);

        MelodicAnalyzerFeaturesExtractor extractor = new MelodicAnalyzerFeaturesExtractor();
        SonoritySegmenter segmenter = new SonoritySegmenter();
        ArrayList<Segment> segments = segmenter.segmentSonorities(song);
        TonalAnalysis tonalAnalysis = new TonalAnalysis(song);
        HashMap<AtomPitch, NoteMelodicAnalysisFeatures> features = extractor.computeFeatures(song, segments, tonalAnalysis);
        TreeSet<AtomPitch> pitches = song.getAtomPitchesSortedByTimeStaffAndPitch();
        assertEquals(12, pitches.size());

        String [] pitchNames = new String [] {"C", "D", "C", "E", "F", "G", "G", "F", "G", "E", "D", "C"};
        IntervalEmpty intervalEmpty = new IntervalEmpty();
        Intervals [] pi = new Intervals[] {null, Intervals.SECOND_MAJOR_ASC, Intervals.SECOND_MAJOR_DESC,
                null, Intervals.SECOND_MINOR_ASC, Intervals.SECOND_MAJOR_ASC,
                null, Intervals.SECOND_MAJOR_DESC, Intervals.SECOND_MAJOR_ASC,
                null, Intervals.SECOND_MAJOR_DESC, Intervals.SECOND_MAJOR_DESC};


        int i=0;
        for (AtomPitch pitch: pitches) {
            assertEquals(pitchNames[i], pitch.getScientificPitch().getPitchClass().getNoteName().name().toUpperCase());

            NoteMelodicAnalysisFeatures f = features.get(pitch);
            assertNotNull(f);

            if (pi[i] == null) {
                assertEquals(i+"th prev interval", intervalEmpty, f.getPrevInterval());
            } else {
                assertEquals(i+"th prev interval", pi[i].createInterval(), f.getPrevInterval());
            }
            i++;
        }

        //TODO El resto
    }

}