package es.ua.dlsi.im3.core.played.io;


import java.io.File;
import java.util.Collection;
import java.util.function.Function;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import es.ua.dlsi.im3.core.played.Key;
import es.ua.dlsi.im3.core.score.Mode;
import org.junit.Before;
import org.junit.Test;

import es.ua.dlsi.im3.core.TestFileUtils;
import es.ua.dlsi.im3.core.played.Meter;
import es.ua.dlsi.im3.core.played.PlayedSong;
import es.ua.dlsi.im3.core.io.ImportException;

/**
@author drizo
@date 23/02/2010
 **/
public class MidiSongImporterTest {
	private static boolean testExportImport = true;
	@Before
	public void setUp() {
	}

    public static void doTest(Function<PlayedSong, Void> validationFunction, PlayedSong song) throws Exception {
        validationFunction.apply(song);
        MidiSongExporter exporter = new MidiSongExporter();
        File file = TestFileUtils.createTempFile("aa.mid");
        exporter.exportSong(file, song);

        if (testExportImport) {
            PlayedSong importedSong = importMIDI(file);
            validationFunction.apply(importedSong);
        }
    }

    private static PlayedSong importMIDI(File file) throws ImportException {
        MidiSongImporter importer = new MidiSongImporter();
        PlayedSong song = importer.importSong(file);
        return song;
    }

    //----------------------------------------------------------------------------------------
    public static Void assertTimeSignatureChanges(PlayedSong song) {
	    try {
            String [] expectedTimeSignatures = {
                    "4/4", "2/4", "3/4", "7/4", "5/4", "1/4", "3/8", "9/8", "6/8", "15/8", "6/2", "12/16", "15/16", "3/64", "2/2"
            };
            Collection<Meter> meters = song.getMeters();

            assertEquals("Number of meter changes", expectedTimeSignatures.length, meters.size());
            long time=0;
            //System.out.println("Resolution: " + song.getResolution());
            int i=0;
            for (Meter timeSignature : meters) {
                Meter em = Meter.parseTimeSignature(expectedTimeSignatures[i]);
                assertEquals("Meter #"+timeSignature, em, timeSignature);
                time += em.getMeasureDurationAsTicks(song.getResolution())*2;
                // each meter has two bars length
                i++;
            }
            assertTrue(time>0); // TODO
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        return null;
	}

    @Test
    public final void testTimeSignatureChanges() throws Exception {
        doTest(MidiSongImporterTest::assertTimeSignatureChanges, importMIDI(TestFileUtils.getFile("/testdata/core/io/meters.mid")));
    }

    //----------------------------------------------------------------------------------------
	private static Void assertTimeSignatureChanges2(PlayedSong song) {
	    try {
            Meter m44 = new Meter(4, 4);
            Meter m24 = new Meter(2, 4);

            /*Meter [] expectedTimeSignatures = {
                new Meter(0, 4, 4),
                new Meter(m44.getMeasureDuration(song.getResolution())*47, 2,4),
                new Meter(m44.getMeasureDuration(song.getResolution())*47+m24.getMeasureDuration(song.getResolution()), 4,4)
            };*/
            Meter [] expectedTimeSignatures = {
                new Meter(4, 4),
                new Meter(2,4),
                new Meter(4,4)
            };

            int resolution = song.getResolution();
            long [] expectedTimes = {0, m44.getMeasureDurationAsTicks(resolution)*47, m44.getMeasureDurationAsTicks(resolution)*47+m24.getMeasureDurationAsTicks(resolution)};
            Collection<Meter> meters = song.getMeters();
            assertEquals("Number of meter changes", expectedTimeSignatures.length, meters.size());

            int i=0;
            for (Meter timeSignature : meters) {
                assertEquals("Meter #"+i, expectedTimeSignatures[i], timeSignature );
                assertEquals("Meter #"+i + " time", expectedTimes[i], timeSignature.getTime() );
                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        return null;
	}

    @Test
    public final void testTimeSignatureChanges2() throws Exception {
        doTest(MidiSongImporterTest::assertTimeSignatureChanges2, importMIDI(TestFileUtils.getFile("/testdata/core/io/Pop-Pop-American-Garth_Brooks-callinbatonrouge.mid")));
    }

    //----------------------------------------------------------------------------------------
    public static Void assertTimeKeyChanges(PlayedSong song) {
        try {
            Key[] expectedKeys = {
                    new Key(2, Key.Mode.MAJOR),
                    new Key(1, Key.Mode.MAJOR),
                    new Key(-5, Key.Mode.MAJOR),
                    new Key(0, Key.Mode.MAJOR)
            };

            long [] expectedTimes = {
                    0,8,12, 20
            };
            int resolution = song.getResolution();

            Collection<Key> keys = song.getKeys();
            assertEquals("Keys", expectedKeys.length, keys.size());

            int i=0;
            for (Key key: keys) {
                Key expectedKey = expectedKeys[i];
                long expectedTime = expectedTimes[i] * resolution;

                assertEquals("Key #" + i, expectedKey, key);
                assertEquals("Time #" + i, expectedTime, key.getTime());
                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        return null;
    }

    @Test
    public final void testKeyChanges() throws Exception {
        doTest(MidiSongImporterTest::assertTimeKeyChanges, importMIDI(TestFileUtils.getFile("/testdata/core/io/keys.mid")));
    }


    //----------------------------------------------------------------------------------------
    private static Void assertImportSongFile(PlayedSong song) {
	    try {
            //System.out.println(song.toString());
            assertTrue(song.getTracks().size() > 0);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        return null;
	}

    @Test
    public final void testImportSongFile() throws Exception {
        doTest(MidiSongImporterTest::assertImportSongFile, importMIDI(TestFileUtils.getFile("/testdata/core/io/ejemplo.mid")));
    }

}
