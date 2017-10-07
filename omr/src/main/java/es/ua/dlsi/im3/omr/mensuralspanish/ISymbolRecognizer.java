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
import es.ua.dlsi.im3.omr.PositionedSymbolType;
import es.ua.dlsi.im3.omr.model.Symbol;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 *
 * @author drizo
 */
public interface ISymbolRecognizer {
    /**
     * 
     * @param symbol
     * @return Notation symbols sorted as they have to be shown to the user
     */
    ArrayList<PositionedSymbolType> recognize(Symbol symbol) throws IM3Exception;
    public void learn(File file) throws IOException;
    public void learn(InputStream is) throws IOException;
    public Long getNumberOfTrainingSymbols();
}
