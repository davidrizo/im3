package es.ua.dlsi.im3.core.score.io.mei;

import java.io.File;
import java.io.InputStream;

import es.ua.dlsi.im3.core.score.DurationEvaluator;
import es.ua.dlsi.im3.core.score.Measure;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.Time;
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
		importer = new MEISAXScoreSongImporter(new DurationEvaluator());
	}

	/**
	 * @param allowErrors If true, errors such as trying to tie two notes of different pitches are reported as warnings and let the import continue
	 */
	public void setAllowErrors(boolean allowErrors) {
		importer.setAllowErrors(allowErrors);
	}

    /**
     *
     * @param durationEvaluator If null, the basic DurationEvaluator() is used
     */
    public MEISongImporter(DurationEvaluator durationEvaluator) {
	    if (durationEvaluator == null) {
	        importer = new MEISAXScoreSongImporter(new DurationEvaluator());
        } else {
            importer = new MEISAXScoreSongImporter(durationEvaluator);
        }
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

	public Object findXMLID(String xmlid) throws ImportException {
		Object timedElement = importer.findXMLID(xmlid);
		return timedElement;
	}
	public Time decodeTStamp(String measureid, String tstamp) throws ImportException {
		Object objMeasure = importer.findXMLID(measureid);
		if (!(objMeasure instanceof Measure)) {
		    throw new ImportException("Object with ID '" + measureid + "' is not a measure, it is a " + objMeasure.getClass());
        }
		Time quarters = importer.decodeTStamp((Measure) objMeasure, tstamp);
		return quarters;
	}
	
	
	//TODO Añadir la lectura de group, mdiv....

}
