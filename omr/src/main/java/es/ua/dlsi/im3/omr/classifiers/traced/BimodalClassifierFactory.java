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
package es.ua.dlsi.im3.omr.classifiers.traced;

import es.ua.dlsi.im3.omr.IStringToSymbolFactory;

/**
 *
 * @author drizo
 */
public class BimodalClassifierFactory<SymbolType> {
    static BimodalClassifierFactory instance = null;
    
    private BimodalClassifierFactory() {
    }
    
    public static synchronized BimodalClassifierFactory getInstance() {
	if (instance == null) {
	    instance = new BimodalClassifierFactory();
	}
	return instance;
    }
    
    
    public IBimodalClassifier createClassifier(IBimodalDatasetReader<SymbolType> reader, IStringToSymbolFactory<SymbolType> symbolFactory) {
    	return new TracedClassifier(reader, symbolFactory);
    }
}
