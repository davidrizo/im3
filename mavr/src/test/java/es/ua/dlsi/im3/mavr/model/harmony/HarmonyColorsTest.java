package es.ua.dlsi.im3.mavr.model.harmony;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.TestFileUtils;
import es.ua.dlsi.im3.core.adt.graph.DirectedGraph;
import es.ua.dlsi.im3.core.io.ImportException;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.io.mei.MEISongImporter;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class HarmonyColorsTest {

    @Test
    public void computeColors() throws IM3Exception {
        File file = TestFileUtils.getFile("/testdata/tonalanalysis/26.mei");
        MEISongImporter meiSongImporter = new MEISongImporter();
        ScoreSong scoreSong = meiSongImporter.importSong(file);

        HarmonyColors harmonyColors = new HarmonyColors(scoreSong);

        DirectedGraph<NodeChordLabel, EdgeChordDistanceLabel> graph = harmonyColors.getChordGraph();
        assertTrue("At least 10 nodes", graph.getNodes().size() > 10);
        assertTrue("At least 10 edges", graph.getEdges().size() > 10);

        harmonyColors.computeColors(null, null, null, null, null);

    }
}