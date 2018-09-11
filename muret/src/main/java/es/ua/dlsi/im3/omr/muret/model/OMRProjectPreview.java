package es.ua.dlsi.im3.omr.muret.model;

import es.ua.dlsi.im3.core.io.ImportException;
import es.ua.dlsi.im3.omr.model.entities.Project;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Used for showing the recent projects screen
 * @autor drizo
 */
public class OMRProjectPreview  {
    String title;
    String composer;
    String posterFrame;
    /**
     * Image shown as the preview, currently it is the first one in the imagesold folder
     */
    File posterFrameImage;

    private Stack<String> elements;

    public OMRProjectPreview(File mrtFile) throws ImportException {
        try {
            loadPreviewData(new InputSource(new FileInputStream(mrtFile)));
            posterFrameImage = new File(mrtFile.getParentFile(), Project.IMAGES_FOLDER + File.separator + posterFrame);
        } catch (FileNotFoundException e) {
            throw new ImportException(e);
        }
    }

    public String getTitle() {
        return title;
    }

    public String getComposer() {
        return composer;
    }

    public File getPosterFrameImage() {
        return posterFrameImage;
    }

    /**
     * Use SAX for reading fast just the data we need
     * @param mrt MuRET file input source
     */
    private void loadPreviewData(InputSource mrt) throws ImportException {
        try {
            elements = new Stack<>();
            XMLReader reader = XMLReaderFactory.createXMLReader();
            reader.setContentHandler(new SAXHandler());
            reader.parse(mrt);
        } catch (SAXException e) {
            if (!SAXHandler.FINISHED.equals(e.getMessage())) {
                throw new ImportException(e);
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "All required data read");
            }
        } catch (IOException e) {
            throw new ImportException(e);
        }
    }

    public String getPosterFrame() {
        return posterFrame;
    }

    private class SAXHandler extends DefaultHandler {
        private static final String ELEMNENT_PROJECT = "project";
        private static final String ELEMENT_NAME = "name";
        private static final String ELEMENT_COMPOSER = "composer";
        private static final String ELEMENT_IMAGE_FILENAME = "imageRelativeFileName";
        static final String FINISHED = "__Finished__";
        String currentElement;
        private String parentElement;

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            parentElement = currentElement;
            currentElement = localName;
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            currentElement = null;
            if (!elements.isEmpty()) {
                elements.pop();
            }
            if (!elements.isEmpty()) {
                parentElement = elements.peek();
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            if (currentElement != null) {
                if (parentElement != null && parentElement.equals(ELEMNENT_PROJECT)) {
                    switch (currentElement) {
                        case ELEMENT_NAME:
                            title = String.valueOf(ch, start, length);
                            checkComplete();
                            break;
                        case ELEMENT_COMPOSER:
                            composer = String.valueOf(ch, start, length);
                            checkComplete();
                            break;
                    }
                } else if (currentElement.equals(ELEMENT_IMAGE_FILENAME)) {
                    posterFrame = String.valueOf(ch, start, length);
                    checkComplete();
                }
            }
        }

        /**
         * If all elements that we need are already read the parser is stopped
         */
        private void checkComplete() throws SAXException {
            if (title != null && composer != null && posterFrame != null) {
                throw new SAXException(FINISHED);
            }
        }
    }
}
