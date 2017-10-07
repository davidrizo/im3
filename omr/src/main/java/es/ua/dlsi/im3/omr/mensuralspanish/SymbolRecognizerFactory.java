/*
 * Copyright (C) 2016 David Rizo Valero
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package es.ua.dlsi.im3.omr.mensuralspanish;


import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.Staff;
import es.ua.dlsi.im3.omr.IStringToSymbolFactory;
import es.ua.dlsi.im3.omr.traced.IBimodalDatasetReader;

/**
 *
 * @author drizo
 */
public class SymbolRecognizerFactory<SymbolType> {
    static SymbolRecognizerFactory instance = null;
    
    private SymbolRecognizerFactory() {	
    }
    
    public static synchronized SymbolRecognizerFactory getInstance() {
	if (instance == null) {
	    instance = new SymbolRecognizerFactory();
	} 
	return instance;
    }
    
    public ISymbolRecognizer buildRecognizer(Staff staff, IBimodalDatasetReader<SymbolType> reader, IStringToSymbolFactory<SymbolType> symbolFactory) throws IM3Exception {
    	//return new RandomSymbolRecognizer(staff); //TODO
        return new TracedSymbolRecognizer(staff, reader, symbolFactory);
    }
}
