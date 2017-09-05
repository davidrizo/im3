/*
 * Created on 10-ene-2004
 */
package es.ua.dlsi.im3.core.score.io;

import java.io.File;
import java.io.InputStream;

import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.io.ImportException;

/**
 * Used to create songs from an external file
 * @author david
 * @param <SongType>
 */
public interface IScoreSongImporter {
	/**
	 * Import a song from an external file
	 * @param file Source file
	 * @return The imported song in a PlayedSong class
	 * @throws ImportException Exception importing file
	 */
	ScoreSong importSong(File file) throws ImportException; 
	ScoreSong importSong(InputStream is) throws ImportException; 
}
