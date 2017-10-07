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

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.omr.PositionedSymbolType;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 *
 * @author drizo
 */
public interface IClassifier {
    List<PositionedSymbolType> classify(int[][] grayscaleImage, List<Stroke> strokes) throws IM3Exception;
    void learn(File file) throws IOException;
    void learn(InputStream is) throws IOException;
    Long getNumberOfTrainingSymbols();
}
