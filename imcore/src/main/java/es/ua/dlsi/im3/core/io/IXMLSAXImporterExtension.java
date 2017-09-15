package es.ua.dlsi.im3.core.io;

import java.util.HashMap;

import es.ua.dlsi.im3.core.io.ImportException;

/**
 * Add extra processing to the base one  
 * @author drizo
 */
public interface IXMLSAXImporterExtension {

	void handleOpenElement(String element, HashMap<String, String> attributesMap) throws ImportException;

	void handleCloseElement(String elementTag) throws ImportException;

	void handleElementContent(String closingElementTag, String content) throws ImportException;


}
