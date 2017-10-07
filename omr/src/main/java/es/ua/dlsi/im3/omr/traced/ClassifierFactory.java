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
package es.ua.dlsi.im3.omr.traced;

import es.ua.dlsi.im3.omr.IStringToSymbolFactory;
import es.ua.dlsi.im3.omr.model.SymbolType;

/**
 *
 * @author drizo
 */
public class ClassifierFactory<SymbolType> {
    static ClassifierFactory instance = null;
    
    private ClassifierFactory() {	
    }
    
    public static synchronized ClassifierFactory getInstance() {
	if (instance == null) {
	    instance = new ClassifierFactory();
	}
	return instance;
    }
    
    
    public IClassifier createClassifier(IBimodalDatasetReader<SymbolType> reader, IStringToSymbolFactory<SymbolType> symbolFactory) {
    	return new TracedClassifier(reader, symbolFactory);
    }
}
