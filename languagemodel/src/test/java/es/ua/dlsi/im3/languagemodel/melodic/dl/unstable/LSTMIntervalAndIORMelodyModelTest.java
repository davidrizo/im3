package es.ua.dlsi.im3.languagemodel.melodic.dl.unstable;

import es.ua.dlsi.im3.core.TestFileUtils;
import es.ua.dlsi.im3.core.played.PlayedSong;
import es.ua.dlsi.im3.core.played.io.MidiSongImporter;
import es.ua.dlsi.im3.languagemodel.melodic.dl.unstable.LSTMIntervalAndIORMelodyModel;
import es.ua.dlsi.im3.languagemodel.sequences.CoupledNoteSequence;
import es.ua.dlsi.im3.languagemodel.sequences.IntervalAndIORSequenceFromMonophonicPlayedSongEncoder;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LSTMIntervalAndIORMelodyModelTest {
    @Test
    public void train() throws Exception {
        System.setProperty("java.library.path","/Developer/NVIDIA/CUDA-8.0/lib");
        LSTMIntervalAndIORMelodyModel model = new LSTMIntervalAndIORMelodyModel(1, 1);

        MidiSongImporter importer = new MidiSongImporter();

        File file = TestFileUtils.getFile("/testdata/B0619_Fmaj.mid");
        PlayedSong playedSong = importer.importSong(file);

        List<CoupledNoteSequence<Integer, Double>> sequences = new ArrayList<>();

        IntervalAndIORSequenceFromMonophonicPlayedSongEncoder encoder = new IntervalAndIORSequenceFromMonophonicPlayedSongEncoder();
        CoupledNoteSequence<Integer, Double> encodedSong = encoder.encode(playedSong);
        sequences.add(encodedSong);

        model.train(sequences);
    }
}