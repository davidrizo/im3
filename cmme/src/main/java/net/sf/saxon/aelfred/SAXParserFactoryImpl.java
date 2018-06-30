package net.sf.saxon.aelfred;

import javax.xml.parsers.*;
import org.xml.sax.*;

/**
* Implements the JAXP 1.1 ParserFactory interface.
* To use the AElfred parser, set the system property javax.xml.parsers.SAXParserFactory
* to the value "net.sf.saxon.aelfred.SAXParserFactoryImpl"; then call
* javax.xml.parsers.SAXParserFactory.newInstance().newSAXParser().
*/

public class SAXParserFactoryImpl extends SAXParserFactory {

    public SAXParserFactoryImpl() {
        setNamespaceAware(true);
        setValidating(false);
    };
    
    public boolean getFeature(String name)
    throws SAXNotRecognizedException {
        return new SAXDriver().getFeature(name);
    }

    public void setFeature(String name, boolean value)
    throws SAXNotRecognizedException, SAXNotSupportedException {
        // the only purpose of the following line is to throw the right exception
        new SAXDriver().setFeature(name, value);
    }
    
    public SAXParser newSAXParser()
    throws ParserConfigurationException {
        if (isValidating()) {
            throw new ParserConfigurationException("AElfred parser is non-validating");
        }
        if (!isNamespaceAware()) {
            throw new ParserConfigurationException("AElfred parser is namespace-aware");
        }
        return new SAXParserImpl();
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
                       