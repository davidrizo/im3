package net.sf.saxon.aelfred;

import javax.xml.parsers.*;
import org.xml.sax.*;

public class SAXParserImpl extends SAXParser {

    private SAXDriver parser;
    
    public SAXParserImpl() {
        parser = new SAXDriver();
    }

    public Parser getParser() throws SAXException {
        throw new SAXException("The AElfred parser is a SAX2 XMLReader");
    }
    
    public Object getProperty(String name)
    throws SAXNotRecognizedException {
        return parser.getProperty(name);
    }
    
    public XMLReader getXMLReader() {
        return parser;
    }
    
    public boolean isNamespaceAware() {
        return true;
    }
    
    public boolean isValidating() {
        return false;
    }
    
/*
    public void parse(File f, DefaultHandler handler)
    throws IOException, SAXException {
        setDefaultHandler(handler);
        parseFile(f);
    }
    
    public void parse(File f, HandlerBase handler)
    throws IOException, SAXException {
        setHandlerBase(handler);
        parseFile(f);
    }

    public void parse(InputSource i, DefaultHandler handler)
    throws IOException, SAXException {
        setDefaultHandler(handler);
        parser.parse(i);
    }    

    public void parse(InputSource i, HandlerBase handler)
    throws IOException, SAXException {
        setHandlerBase(handler);
        parser.parse(i);
    }    

    public void parse(InputStream i, DefaultHandler handler)
    throws IOException, SAXException {
        setDefaultHandler(handler);
        parser.parse(new InputSource(i));
    }    

    public void parse(InputStream i, HandlerBase handler)
    throws IOException, SAXException {
        setHandlerBase(handler);
        parser.parse(new InputSource(i));
    }
    
    public void parse(InputStream i, DefaultHandler handler, String systemId)
    throws IOException, SAXException {
        setDefaultHandler(handler);
        InputSource inp = new InputSource(i);
        inp.setSystemId(systemId);
        parser.parse(inp);
    }    

    public void parse(InputStream i, HandlerBase handler, String systemId)
    throws IOException, SAXException {
        setHandlerBase(handler);
        InputSource inp = new InputSource(i);
        inp.setSystemId(systemId);
        parser.parse(inp);
    }          

    public void parse(String systemId, DefaultHandler handler)
    throws IOException, SAXException {
        setDefaultHandler(handler);
        InputSource inp = new InputSource(systemId);
        parser.parse(inp);
    }    

    public void parse(String systemId, HandlerBase handler)
    throws IOException, SAXException {
        setHandlerBase(handler);
        InputSource inp = new InputSource(systemId);
        parser.parse(inp);
    } 
*/

    public void setProperty(String name, Object value)
    throws SAXNotRecognizedException, SAXNotSupportedException {
        parser.setProperty(name, value);
    }
}

//
// The contents of this file are subject to the Mozilla Public License Version 1.0 (the "License");
// you may not use this file except in compliance with the License. You may obtain a copy of the
// License at http://www.mozilla.org/MPL/ 
//
// Software distributed under the License is distributed on an "AS IS" basis,
// WITHOUT WARRANTY OF ANY KIND, either express or implied.
// See the License for the specific language governing rights and limitations under the License. 
//
// The Original Code is: all this file. 
//
// The Initial Developer of the Original Code is
// Michael Kay of International Computers Limited (michael.h.kay@ntlworld.com).
//
// Portions created by (your name) are Copyright (C) (your legal entity). All Rights Reserved. 
//
// Contributor(s): none. 
//
                       