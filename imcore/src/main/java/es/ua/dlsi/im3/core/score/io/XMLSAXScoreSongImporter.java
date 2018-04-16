package es.ua.dlsi.im3.core.score.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl;
import es.ua.dlsi.im3.core.io.IXMLSAXImporterExtension;
import es.ua.dlsi.im3.core.io.XMLSAXImporter;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.adt.StackMap;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.io.ImportException;

public abstract class XMLSAXScoreSongImporter extends XMLSAXImporter {

	protected ScoreSong song;

	public XMLSAXScoreSongImporter() {
		super();
	}

	public ScoreSong importStream(InputStream is) throws ImportException {
		try {
			initSAX();
			init();
			saxParser.parse(is, handler);
			song.processMensuralImperfectionRules();
		} catch (ParserConfigurationException | SAXException | IOException | IM3Exception e) {
			throw new ImportException(e);
		}
	
		return song;
	}

	public ScoreSong importFileToScoreSong(File file) throws ImportException {
		try {
			initSAX();
			init();
			saxParser.parse(file, handler);
			postProcess();
		} catch (IM3Exception | ParserConfigurationException | SAXException | IOException e) {
			throw new ImportException(e);
		}
        try {
            song.processMensuralImperfectionRules();
        } catch (IM3Exception e) {
		    Logger.getLogger(XMLSAXScoreSongImporter.class.getName()).log(Level.WARNING, "Cannot apply mensural imperfection rules", e);
		    throw new ImportException(e);
        }
        return song;
	}
}