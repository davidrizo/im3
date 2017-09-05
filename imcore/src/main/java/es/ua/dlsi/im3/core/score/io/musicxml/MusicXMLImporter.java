package es.ua.dlsi.im3.core.score.io.musicxml;

import java.io.File;
import java.io.InputStream;

import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.io.IScoreSongImporter;
import es.ua.dlsi.im3.core.score.io.XMLSAXImporter;
import es.ua.dlsi.im3.core.io.ImportException;
/**
 * @author drizo
 *
 */
public class MusicXMLImporter implements IScoreSongImporter {

	@Override
	public ScoreSong importSong(File file) throws ImportException {
		XMLSAXImporter importer = new MusicXMLSAXImporter();
		return importer.importFileToScoreSong(file);
	}

	@Override
	public ScoreSong importSong(InputStream is) throws ImportException {
		XMLSAXImporter importer = new MusicXMLSAXImporter();
		return importer.importStream(is);
	}

}
