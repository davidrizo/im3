package es.ua.dlsi.im3.midrepresentations.melodic.dl.unstable;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.TestFileUtils;
import es.ua.dlsi.im3.core.io.ImportException;
import es.ua.dlsi.im3.core.score.DiatonicPitch;
import es.ua.dlsi.im3.core.score.io.musicxml.MusicXMLImporter;
import es.ua.dlsi.im3.midrepresentations.Alphabet;
import es.ua.dlsi.im3.midrepresentations.sequences.DiatonicPitchSequenceFromScoreSongEncoder;
import es.ua.dlsi.im3.midrepresentations.sequences.Sequence;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LSTMDiatonicPitchSequenceModelTest {
    //TODO Set to false to avoid training the Neural Network - time consuming
    private static final boolean train = true;

    // We just test it compiles and works, not the result
    @Test
    public void testLearn() throws ImportException, IM3Exception {
        if (train) {
            Alphabet<DiatonicPitch> alphabet = new Alphabet<DiatonicPitch>(DiatonicPitch.values(), DiatonicPitch.NONE);
            LSTMDiatonicPitchSequenceModel model = new LSTMDiatonicPitchSequenceModel(alphabet, 1, 1);

            File file = TestFileUtils.getFile("/testdata/26.xml");

            MusicXMLImporter importer = new MusicXMLImporter();

            List<Sequence<DiatonicPitch>> sequences = new ArrayList<>();
            DiatonicPitchSequenceFromScoreSongEncoder encoder = new DiatonicPitchSequenceFromScoreSongEncoder();
            sequences.add(encoder.encode(importer.importSong(file)));

            model.train(sequences);
        } else {
            System.err.println("Skipping LSTMDiatonicPitchSequenceModelTest.testLearn tests for saving time");
        }
    }

}