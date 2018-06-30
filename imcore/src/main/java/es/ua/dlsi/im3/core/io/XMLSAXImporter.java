package es.ua.dlsi.im3.core.io;

import com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl;
import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.adt.StackMap;
import es.ua.dlsi.im3.core.score.io.XMLSAXScoreSongImporter;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class XMLSAXImporter {
    protected StringBuilder currentStringBuilder;
    protected static final String NULL_STRING = "null";
    protected DefaultHandler handler;
    protected SAXParser saxParser;
    protected ArrayList<String> elementStack; // implemented as a list to be able to check the previous contents
    protected StackMap<String, String> expectedContentForElement;
    private String lastElement;
    private ArrayList<IXMLSAXImporterExtension> extensions;

    public XMLSAXImporter() {
        extensions = new ArrayList<>();

    }

    public void registerExtension(IXMLSAXImporterExtension extension) {
        extensions.add(extension);
    }

    protected void initSAX() throws ParserConfigurationException, SAXException {
        elementStack = new ArrayList<>();
        expectedContentForElement = new StackMap<>();

        //org.apache.xerces.jaxp.SAXParserFactoryImpl
        //SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParserFactory factory = new SAXParserFactoryImpl(); // if not, the GNU Aelfred Parser (used in gnujaxp loaded by cmme.org) is wrongly used
        factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        saxParser = factory.newSAXParser();

        handler = new DefaultHandler() {

            public void startElement(String uri, String localName, String qName, Attributes attributes)
                    throws SAXException {
				/*System.out.println("Start:" + qName);
				for (int i = 0; i < attributes.getLength(); i++) {
					System.out.println("\t" + attributes.getQName(i) + "=" + attributes.getValue(i));
				}*/
                try {
                    lastElement = qName;
                    HashMap<String, String> attributesMap = getAttributes(qName, attributes);
                    handleOpenElement(qName, attributesMap);
                    for (IXMLSAXImporterExtension extension: extensions) {
                        extension.handleOpenElement(qName, attributesMap);
                    }
                    currentStringBuilder = null;
                } catch (ImportException e) {
                    throw new SAXException(e);
                }
            }

            public void endElement(String uri, String localName, String qName) throws SAXException {
                //System.out.println("End:" + qName);
                try {
                    handleCloseElement(qName);
                    //for (IXMLSAXImporterExtension extension: extensions) {
                    //    extension.handleCloseElement(qName);
                    //}

                } catch (ImportException e) {
                    throw new SAXException(e);
                }
            }

            public void characters(char ch[], int start, int length) {
                if (currentStringBuilder == null) {
                    currentStringBuilder = new StringBuilder();
                }
                for (int i = 0; i < length; i++) {
                    currentStringBuilder.append(ch[i + start]);
                }
            }
        };
    }

    protected void init() {
    }

    protected void postProcess() throws ImportException, IM3Exception {
    }

    public HashMap<String, String> getAttributes(String element, Attributes saxAttributes) {
        HashMap<String, String> result = new HashMap<>();
        for (int i = 0; i < saxAttributes.getLength(); i++) {
            result.put(saxAttributes.getQName(i), saxAttributes.getValue(i));
        }
        return result;
    }

    protected String getAttribute(HashMap<String, String> attributes, String key) throws ImportException {
        String result = attributes.get(key);
        if (result == null) {
            throw new ImportException("Attribute '" + key + "' not found among " + attributes + " in " + this.lastElement);
        }
        return result;
    }

    protected String getOptionalAttribute(HashMap<String, String> attributes, String key) {
        String result = attributes.get(key);
        return result;
    }

    protected boolean parseYesOrNo(String elementOrAttribute, String value) throws ImportException {
        if (value == null) {
            throw new ImportException("Cannot parse a null value for element or attribute '" + elementOrAttribute);
        }
        if (value.equals("yes")) {
            return true;
        } else if (value.equals("no")) {
            return false;
        } else {
            throw new ImportException("Expected 'yes' or 'no' and found '" + value + "' for element or attribute '" + elementOrAttribute);
        }
    }

    public void handleCloseElement(String element) throws ImportException {
        String closingElement = elementStack.remove(elementStack.size()-1);
        if (!closingElement.equals(element)) {
            throw new ImportException("Expected " + closingElement + " and found " + element);
        }
        if (currentStringBuilder != null) {
            String content = currentStringBuilder.toString().trim();
            if (!content.isEmpty() && !(content.equals(NULL_STRING))) {
                handleElementContent(closingElement, content);
                for (IXMLSAXImporterExtension extension: extensions) {
                    extension.handleElementContent(elementStack, closingElement, content);
                }
            }
            currentStringBuilder = null;
        }
        try {
            handleElementClose(closingElement);
            for (IXMLSAXImporterExtension extension: extensions) {
                extension.handleCloseElement(element);
            }

        } catch (IM3Exception e) {
            throw new ImportException(e);
        }
    }

    protected abstract void handleElementClose(String closingElement) throws ImportException, IM3Exception;

    public void handleOpenElement(String element, HashMap<String, String> attributesMap) throws ImportException {
        //System.out.println("Element:  " + element);
        elementStack.add(element);
        doHandleOpenElement(element, attributesMap);
    }

    protected abstract void doHandleOpenElement(String element, HashMap<String,String> attributesMap) throws ImportException;

    protected abstract void handleElementContent(String currentElement, String content) throws ImportException;


    protected void showUnimplemented(String element) {
        Logger.getLogger(XMLSAXScoreSongImporter.class.getName()).log(Level.WARNING, "Unimplemented element {0}", element);
    }

    protected String getElementContentFor(String expectedElement) throws ImportException {
        Map.Entry<String, String> entry = expectedContentForElement.pop();
        if (!expectedElement.equals(entry.getKey())) {
            throw new ImportException("Expected element " + expectedElement + " and found " + entry.getKey());
        }
        return entry.getValue();
    }

    protected String getParentElement() {
        if (elementStack.size() < 1) {
            return null;
        } else {
            return elementStack.get(elementStack.size()-1);
        }
    }

}
