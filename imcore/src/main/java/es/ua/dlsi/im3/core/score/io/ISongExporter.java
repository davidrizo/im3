package es.ua.dlsi.im3.core.score.io;

import java.io.File;

import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.io.ExportException;

/**
@author drizo
 * @param <SongType>
@date 24/06/2011
 **/
public interface ISongExporter {
	void exportSong(File file, ScoreSong song) throws ExportException;
}
