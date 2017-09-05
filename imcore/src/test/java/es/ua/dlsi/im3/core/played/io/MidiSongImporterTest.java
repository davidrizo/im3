package es.ua.dlsi.im3.core.played.io;


import java.io.File;
import java.util.Collection;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
	
	@Before
	public void setUp() throws Exception {
	}
	
	@Test
	public final void testTimeSignatureChanges() throws Exception {
		String [] expectedTimeSignatures = {
				"4/4", "2/4", "3/4", "7/4", "5/4", "1/4", "3/8", "9/8", "6/8", "15/8", "6/2", "12/16", "15/16", "3/64", "2/2"
		};
		File metersFile = TestFileUtils.getFile("/testdata/core/io/meters.mid");
		MidiSongImporter importer = new MidiSongImporter();
		PlayedSong song = importer.importSong(metersFile);
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
	}
	
	@Test
	public final void testTimeSignatureChanges2() throws Exception {
		File metersFile = TestFileUtils.getFile("/testdata/core/io/Pop-Pop-American-Garth_Brooks-callinbatonrouge.mid");
		MidiSongImporter importer = new MidiSongImporter();
		PlayedSong song = importer.importSong(metersFile);
		
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
	}	
	
	@Test
	public final void testImportSongFile() throws ImportException {
		MidiSongImporter importer = new MidiSongImporter();
		PlayedSong song = importer.importSong(TestFileUtils.getFile("/testdata/core/io/ejemplo.mid"));
		//System.out.println(song.toString());
		assertTrue(song.getTracks().size() > 0);
	}
	
}
