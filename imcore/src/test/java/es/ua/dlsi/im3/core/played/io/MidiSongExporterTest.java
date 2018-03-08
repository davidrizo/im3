package es.ua.dlsi.im3.core.played.io;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;

import static org.junit.Assert.*;
import org.junit.Before;

import es.ua.dlsi.im3.core.played.Meter;
import es.ua.dlsi.im3.core.played.PlayedNote;
import es.ua.dlsi.im3.core.played.PlayedSong;
import es.ua.dlsi.im3.core.played.SongTrack;
import es.ua.dlsi.im3.core.io.ExportException;
import es.ua.dlsi.im3.core.io.ImportException;


/**
@author drizo
@date 02/04/2009
 **/
public class MidiSongExporterTest {
	MidiSongImporter importer; 
		

	MidiSongExporter exporter;
	

	@Before
	public void setUp() {
		importer = new MidiSongImporter(); 
		exporter = new MidiSongExporter();
	}

	//20160621 @Test
	public final void testTimeSignatureChange() throws ExportException, ImportException {
	    PlayedSong psong = new PlayedSong();
		Meter m1 = new Meter(3,4);
		psong.addMeter(0,m1);
		Meter m2 = new Meter(6,8);
		psong.addMeter(PlayedSong.DEFAULT_RESOLUTION*6, m2);
		SongTrack v = psong.addTrack();
	    //v.addNote(new PlayedNote(v,0,60,120));
		PlayedNote pn = new PlayedNote(PlayedSong.DEFAULT_RESOLUTION/2,PlayedSong.DEFAULT_RESOLUTION); //TODO podría fallar porque antes la resolución era 120
		v.addNote(0, pn);
		File out = new File("/tmp/_test.mid");
		exporter.exportSong(out, psong);
		
		PlayedSong inSong = importer.importSong(out);
		Collection<Meter> inmeters = psong.getMeters();
		assertEquals("Meter count", 2, inmeters.size());
		Iterator<Meter> iter = inmeters.iterator();
		Meter inm1 = iter.next();
		Meter inm2 = iter.next();
		assertEquals("Meter 1", m1, inm1);
		assertEquals("Meter 2", m2, inm2);
	}

}
