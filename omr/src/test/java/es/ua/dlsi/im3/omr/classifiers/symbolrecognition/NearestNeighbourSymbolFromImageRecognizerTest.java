package es.ua.dlsi.im3.omr.classifiers.symbolrecognition;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.TestFileUtils;
import es.ua.dlsi.im3.core.patternmatching.NearestNeighbourClassesRanking;
import es.ua.dlsi.im3.core.patternmatching.RankingItem;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbol;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticVersion;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

import static org.junit.Assert.*;

public class NearestNeighbourSymbolFromImageRecognizerTest {


    @Test
    public void classify() throws IOException, IM3Exception {
        File inputTrainingFile = TestFileUtils.getFile("/testdata/training/B-53.781.symbolsimages-extract.txt");
        NearestNeighbourSymbolFromImageRecognizer nearestNeighbourSymbolFromImageRecognizer = new NearestNeighbourSymbolFromImageRecognizer(AgnosticVersion.v1);
        nearestNeighbourSymbolFromImageRecognizer.trainFromFile(inputTrainingFile);
        assertEquals("Training instances", 250, nearestNeighbourSymbolFromImageRecognizer.getTrainingSet().size());

        // existing prototype
        List<Integer> pixelsWholeBlackExisting = Arrays.asList(87,130,192,208,211,193,191,192,194,197,200,201,207,208,210,208,197,181,142,128,104,80,49,47,68,147,183,199,206,200,81,124,189,207,210,192,191,192,195,199,204,207,212,210,205,188,165,141,100,88,70,53,42,49,77,152,186,197,203,199,75,119,185,204,209,193,192,193,197,201,206,210,212,206,194,158,127,100,61,57,48,38,43,54,83,155,186,193,199,199,77,119,184,202,207,192,194,198,201,203,205,207,199,189,166,117,83,59,32,34,33,28,38,49,78,152,184,193,199,200,94,132,185,199,203,195,201,206,207,201,196,189,157,138,106,56,35,30,35,44,45,37,29,29,50,131,172,191,207,203,91,131,185,201,206,199,202,207,200,188,176,165,123,101,75,46,35,33,36,42,44,41,31,24,37,113,157,180,205,201,83,125,187,205,213,202,206,206,188,171,155,141,93,73,57,56,51,48,41,40,40,44,38,24,31,97,140,169,203,201,78,122,190,209,219,203,200,194,164,144,124,110,66,51,44,62,62,57,45,40,40,47,42,26,28,73,114,148,197,198,79,123,195,215,225,204,193,177,138,118,98,84,54,47,45,61,61,55,46,45,45,47,40,32,31,51,86,125,191,196,82,126,198,218,227,199,180,158,112,95,77,69,55,53,54,57,53,46,46,51,50,45,36,37,37,35,65,109,184,192,90,132,199,215,219,182,157,130,83,70,56,51,48,51,53,52,47,40,44,51,50,43,35,43,45,27,50,97,180,189,111,149,194,195,186,135,110,89,55,47,41,42,43,46,46,42,40,40,42,45,43,39,41,48,50,29,51,100,179,183,111,145,176,170,154,101,81,68,45,42,41,44,45,44,41,36,39,42,43,41,39,37,41,48,48,25,45,91,175,182,106,136,155,146,125,74,62,57,46,45,48,51,48,43,38,35,38,43,43,38,36,36,41,46,45,18,38,84,170,182,96,119,127,114,93,50,46,49,47,47,50,53,48,43,36,34,38,45,46,39,36,37,40,42,39,16,36,80,169,187,88,106,102,89,70,40,41,48,50,49,50,51,46,42,37,34,40,48,47,40,37,38,40,40,37,18,40,84,174,190,79,91,84,73,58,40,44,52,51,48,48,47,43,42,40,38,42,48,48,42,39,41,39,36,33,22,47,93,181,194,69,76,64,56,46,42,47,53,52,49,47,44,41,42,40,40,42,46,44,40,39,41,38,32,29,34,66,113,192,198,63,65,54,49,46,47,50,52,50,47,45,43,41,39,38,40,41,42,41,37,38,38,35,26,29,49,87,133,201,202,57,50,43,46,50,58,54,49,44,44,44,45,42,40,40,43,41,38,36,37,39,41,32,21,26,86,132,170,215,206,58,48,40,45,52,59,55,47,43,45,45,45,44,42,43,47,44,40,39,43,45,46,32,19,26,96,144,177,214,208,56,44,38,44,51,58,51,44,41,42,44,47,48,46,46,50,47,42,42,45,47,49,33,18,26,108,157,185,213,208,45,32,26,33,39,45,40,35,37,41,46,51,52,50,51,54,51,47,47,50,51,51,35,19,27,119,167,191,211,206,36,25,21,27,30,34,31,28,34,40,47,50,54,52,51,52,51,48,47,50,50,51,34,20,31,127,174,193,208,204,43,40,43,49,49,42,37,33,33,36,40,42,46,45,44,42,41,40,39,40,40,41,30,21,35,136,182,198,205,201,65,72,83,86,83,62,53,43,28,23,21,20,23,25,24,19,17,16,15,15,17,18,20,17,34,143,189,202,204,199,95,125,166,173,170,138,125,111,77,65,51,42,40,46,45,34,32,33,38,39,39,37,29,23,40,145,189,204,205,198,88,124,182,197,200,176,166,155,127,114,97,88,83,87,87,77,76,78,89,94,91,82,51,36,44,139,183,201,208,199,85,125,193,214,221,204,198,191,172,162,149,142,135,137,136,130,130,133,147,152,146,131,78,51,51,134,178,199,209,199,89,130,199,219,229,213,208,203,193,189,181,175,172,172,171,169,170,172,185,189,178,160,95,60,55,132,174,195,208,199);
        GrayscaleImageData grayScaleImage = new GrayscaleImageData(pixelsWholeBlackExisting);
        assertEquals("Same image distance", 0.0, grayScaleImage.computeDistance(grayScaleImage), 0.0001);

        SymbolImagePrototype symbolImagePrototype = new SymbolImagePrototype(null, grayScaleImage);
        NearestNeighbourClassesRanking<AgnosticSymbol, SymbolImagePrototype> rank = nearestNeighbourSymbolFromImageRecognizer.classify(symbolImagePrototype, false);

        // the exact match should be obtained
        assertEquals("Best match", "note.wholeBlack-S4", rank.first().getAgnosticString());
    }
}