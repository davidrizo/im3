package es.ua.dlsi.im3.similarity;

import es.ua.dlsi.im3.core.TestFileUtils;
import es.ua.dlsi.im3.core.io.ImportException;
import es.ua.dlsi.im3.core.score.DiatonicPitch;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.io.mei.MEISongImporter;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by drizo on 19/7/17.
 */
public class SearchSubsequenceInScoreSongsTest {
    private Collection<ScoreSong> createSongCollection() throws ImportException {
        ArrayList<ScoreSong> songs = new ArrayList<>();

        String [] fileNames = {"300511182-1_3_2", "300511182-1_7_1",
                "300511182-1_11_2", "300511182-1_15_1"};

        MEISongImporter importer = new MEISongImporter();

        for (String fn: fileNames) {
            ScoreSong song = importer.importSong(TestFileUtils.getFile("/testdata/similarity/fromRISM/" + fn, fn + ".mei"));
            song.addTitle(fn);
            songs.add(song);
        }
        return songs;
    }


    @Test
    public void searchSubsequence() throws Exception {
        SearchSubsequenceInScoreSongs searchSubsequenceInScoreSongs = new SearchSubsequenceInScoreSongs(createSongCollection());
        DiatonicPitch[] query = {DiatonicPitch.G, DiatonicPitch.A, DiatonicPitch.B};
        List<Match<ScoreSong>> result = searchSubsequenceInScoreSongs.searchSubsequence(query);

        System.out.println(result);

        //TODO Que haga algo
        //assertEquals(4, result.size());
        //assertEquals("300511182-1_3_2", result.get(0).getElement().getTitle());

        //assertTrue(result.get(0).getValue() > 0);
    }
}
