package es.ua.dlsi.im3.core.score.io.mei;

import java.io.File;
import java.io.InputStream;

import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.io.IScoreSongImporter;
import es.ua.dlsi.im3.core.io.IXMLSAXImporterExtension;
import es.ua.dlsi.im3.core.io.ImportException;

/**
 * @author drizo
 *
 */
public class MEISongImporter implements IScoreSongImporter {
	private MEISAXScoreSongImporter importer;

	public MEISongImporter() {
		importer = new MEISAXScoreSongImporter();
	}
	@Override
	public ScoreSong importSong(File file) throws ImportException {
		ScoreSong song = importer.importFileToScoreSong(file);
		return song;
	}

	@Override
	public ScoreSong importSong(InputStream is) throws ImportException {
		ScoreSong song = importer.importStream(is);
		return song;
	}

	public void registerExtension(IXMLSAXImporterExtension extension) {
		importer.registerExtension(extension);
	}
	/*public AMTimedElement findXMLID(String xmlid) throws ImportException {
		return importer.findXMLID(xmlid);
	}
	public double decodeTStamp(AMMeasure measure, String tstamp) {
		return importer.decodeTStamp(measure, tstamp);
	}*/
	/*FRACCIONES public ISymbolInLayer findXMLID(String xmlid) throws ImportException {
		AMTimedElement timedElement = importer.findXMLID(xmlid);
		return abstractModel2ModernSong.findCoreSymbol(timedElement);		
	}
	public Time decodeTStamp(String measureid, String tstamp) throws ImportException {
		AMTimedElement measure = importer.findXMLID(measureid);		
		Time quarters = importer.decodeTStamp((AMMeasure)measure, tstamp);
		return quarters;
	}*/
	
	
	//TODO Añadir la lectura de group, mdiv....

}
