package es.ua.dlsi.im3.core.conversions;

import es.ua.dlsi.im3.core.TestFileUtils;
import es.ua.dlsi.im3.core.played.*;
import es.ua.dlsi.im3.core.played.PlayedNote;
import es.ua.dlsi.im3.core.played.io.MidiSongExporter;
import es.ua.dlsi.im3.core.played.io.MidiSongImporterTest;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.clefs.ClefG2;
import es.ua.dlsi.im3.core.score.io.musicxml.MusicXMLImporter;
import es.ua.dlsi.im3.core.score.meters.FractionalTimeSignature;
import es.ua.dlsi.im3.core.score.staves.Pentagram;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by drizo on 17/7/17.
 */
public class ScoreToPlayedTest {
    @Test
    public void createPlayedSongFromScore() throws Exception {
        ScoreSong scoreSong = new ScoreSong();
        ScorePart scorePart = scoreSong.addPart();
        Staff staff = new Pentagram(scoreSong, "1", 1);
        ScoreLayer layer = scorePart.addScoreLayer();
        staff.addLayer(layer);
        scoreSong.addStaff(staff);
        staff.addCoreSymbol(new ClefG2()); // new ClefF4()
        KeySignature ks = new KeySignature(NotationType.eModern, KeysEnum.CM.getKey()); //TODO Ahora no se está convirtiendo la tonalidad al PlayedSong
        staff.addCoreSymbol(ks);
        TimeSignature ts = new FractionalTimeSignature(4, 4);
        staff.addCoreSymbol(ts);

        // all notes are located in 4th octave
        // C, half
        layer.add(new SimpleNote(Figures.HALF, 0, new ScientificPitch(PitchClasses.C, 4)));

        // D, quarter with dot
        layer.add(new SimpleNote(Figures.QUARTER, 1, new ScientificPitch(PitchClasses.D, 4)));

        // E flat, 8th
        layer.add(new SimpleNote(Figures.EIGHTH, 0, new ScientificPitch(PitchClasses.E_FLAT, 4)));

        // whole rest
        layer.add(new SimpleRest(Figures.WHOLE, 0));

        // F sharp, whole
        layer.add(new SimpleNote(Figures.WHOLE, 0, new ScientificPitch(PitchClasses.F_SHARP, 4)));


        ScoreToPlayed conversor = new ScoreToPlayed();
        PlayedSong played = conversor.createPlayedSongFromScore(scoreSong);

        assertEquals("Tracks", 2, played.getTracks().size());
        assertEquals("Global track wih 0 notes", 0, played.getTracks().get(0).getNumNotes());
        SongTrack track = played.getTracks().get(1);
        assertEquals("Notes", 4, track.getNumNotes());
        ArrayList<PlayedNote> notes = track.getNotesAsArray();
        assertEquals("Note #0 pitch", 60, notes.get(0).getMidiPitch());
        assertEquals("Note #0 onset", 0, notes.get(0).getTime());
        assertEquals("Note #0 duration", played.getResolution()*2, notes.get(0).getDurationInTicks());
        assertEquals("Note #1 pitch", 62, notes.get(1).getMidiPitch());
        assertEquals("Note #1 onset", played.getResolution()*2, notes.get(1).getTime());
        assertEquals("Note #1 duration", played.getResolution() + played.getResolution()/2, notes.get(1).getDurationInTicks());
        assertEquals("Note #2 pitch", 63, notes.get(2).getMidiPitch());
        assertEquals("Note #2 onset", played.getResolution()*3+played.getResolution()/2, notes.get(2).getTime());
        assertEquals("Note #2 duration", played.getResolution()/2, notes.get(2).getDurationInTicks());
        assertEquals("Note #3 pitch", 66, notes.get(3).getMidiPitch());
        assertEquals("Note #3 onset", played.getResolution()*8, notes.get(3).getTime());
        assertEquals("Note #3 duration", played.getResolution()*4, notes.get(3).getDurationInTicks());

        // write in a midi file
        MidiSongExporter exporter = new MidiSongExporter();
        File file = TestFileUtils.createTempFile("scoretoplayed.mid");
        exporter.exportSong(file, played);
    }

    @Test
    public void createPlayedSongFromScoreKeyChanges() throws Exception {
        MusicXMLImporter importer = new MusicXMLImporter();
        ScoreSong scoreSong = importer.importSong(TestFileUtils.getFile("/testdata/core/io/keys.xml"));

        ScoreToPlayed conversor = new ScoreToPlayed();
        PlayedSong played = conversor.createPlayedSongFromScore(scoreSong);

        MidiSongImporterTest midiSongImporterTest = new MidiSongImporterTest();
        midiSongImporterTest.doTest(MidiSongImporterTest::assertTimeKeyChanges, played);
    }

    @Test
    public void createPlayedSongFromScoreMeterChanges() throws Exception {
        MusicXMLImporter importer = new MusicXMLImporter();
        ScoreSong scoreSong = importer.importSong(TestFileUtils.getFile("/testdata/core/io/meters.xml"));

        ScoreToPlayed conversor = new ScoreToPlayed();
        PlayedSong played = conversor.createPlayedSongFromScore(scoreSong);

        MidiSongImporterTest midiSongImporterTest = new MidiSongImporterTest();
        midiSongImporterTest.doTest(MidiSongImporterTest::assertTimeSignatureChanges, played);
    }
}
