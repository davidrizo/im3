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

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbol;
import es.ua.dlsi.im3.omr.model.entities.Stroke;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @deprecated Use package es.ua.dlsi.im3.omr.classifiers.symbolrecognition
 * @author drizo
 */
public interface IBimodalClassifier {
    void learn(File file) throws IOException;
    void learn(InputStream is) throws IOException;

    // TODO: 15/2/18 Tenemos que ver si tiene sentido devolver SymbolType + posición en pentagrama cuando queramos devolver semántico
    // Quizás deberíamos devolver symbol type y que en su caso que éste se sustituya por PositionedSymbolType
    List<AgnosticSymbol> classify(int[][] grayscaleImage, List<Stroke> strokes) throws IM3Exception;
    Long getNumberOfTrainingSymbols();
}
