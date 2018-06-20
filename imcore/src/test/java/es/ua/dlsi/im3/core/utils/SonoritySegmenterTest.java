package es.ua.dlsi.im3.core.utils;

import es.ua.dlsi.im3.core.TestFileUtils;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.Segment;
import es.ua.dlsi.im3.core.score.Time;
import es.ua.dlsi.im3.core.score.io.musicxml.MusicXMLImporter;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by drizo on 12/6/17.
 */
public class SonoritySegmenterTest {
    @Test
    public void segmentSonorities() throws Exception {
        File file = TestFileUtils.getFile("/testdata/core/score/simple_sonority_segments.xml");
        MusicXMLImporter importer = new MusicXMLImporter();
        ScoreSong song = importer.importSong(file);

        SonoritySegmenter segmenter = new SonoritySegmenter();
        List<Segment> segments = segmenter.segmentSonorities(song);
        assertEquals("Number of segments", 5, segments.size());

        assertEquals("Segment #0 onset", Time.TIME_ZERO, segments.get(0).getFrom());
        assertEquals("Segment #0 duration", new Time(4, 1), segments.get(0).getDuration());


        assertEquals("Segment #1 onset", new Time(4, 1), segments.get(1).getFrom());
        assertEquals("Segment #1 duration", new Time(1,2), segments.get(1).getDuration());

    }

}