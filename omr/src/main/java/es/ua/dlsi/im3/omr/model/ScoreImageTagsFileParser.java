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
package es.ua.dlsi.im3.omr.model;


import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.PositionInStaff;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbol;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbolType;
import es.ua.dlsi.im3.omr.old.mensuraltagger.components.ScoreImageTags;
import es.ua.dlsi.im3.omr.classifiers.traced.Point;
import es.ua.dlsi.im3.omr.classifiers.traced.Stroke;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.logging.Level;
import java.util.logging.Logger;

//TODO Cambiar de paquete
/**
 *
 * @author drizo
 */
public class ScoreImageTagsFileParser {
    StringToSymbolFactory factory;

    public ScoreImageTagsFileParser() {
        this.factory = new StringToSymbolFactory();
    }

    static enum State {
		eReadingSymbol, eReadingStrokes
	};

	public ScoreImageTags parse(File file, BufferedImage pageImage) throws Exception {
		ScoreImageTags result = new ScoreImageTags(file);
		State state = State.eReadingSymbol;

		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			int expectedStrokes = -1;
			int currentStroke = -1;
			Symbol currentSymbol = null;
			int lineNumber = 1;
			for (String line; (line = br.readLine()) != null;) {
				if (!line.trim().isEmpty()) {
					if (state == State.eReadingSymbol) {
						if (currentSymbol != null) {
							currentSymbol.finishSymbol();
						}
						// #stroke[;symbol;position]
						String[] strokesLineElements = line.split(";");
						expectedStrokes = Integer.parseInt(strokesLineElements[0]);
						AgnosticSymbol pst = null;
						PositionInStaff positionInStaff;
						if (strokesLineElements.length == 3) {
							positionInStaff = parsePositionInStaff(strokesLineElements[2]);
							AgnosticSymbolType symbolType = parseNotationSymbol(strokesLineElements[1]);
							pst = new AgnosticSymbol(symbolType, positionInStaff);
						} else if (strokesLineElements.length != 1) {
							throw new Exception("Expected 1 or 3 symbols in symbol description in line " + lineNumber + " in file " + file.getName());
						}

						if (expectedStrokes > 0) {
							state = State.eReadingStrokes;
							currentStroke = 0;
							currentSymbol = new Symbol(pageImage);
							currentSymbol.setPositionedSymbolType(pst);
							result.addSymbol(currentSymbol);
						} else {
							Logger.getLogger(ScoreImageTagsFileParser.class.getName()).log(Level.INFO, "File " + file.getName() + " in line " + lineNumber + " has just 0 strokes, not added");
						}
					} else {
						// read the stroke
						Stroke stroke = new Stroke();
						if (currentSymbol == null) {
							throw new Exception("currentSymbol should not be null");
						}
						currentSymbol.addStroke(stroke);
						String[] pointsStr = line.split(";");
						for (String pointStr : pointsStr) {
							long time;
							double x;
							double y;
							String[] pointComponents = pointStr.split(",");
							/*if (pointComponents.length == 5) {
								// commas are used instead of points to separate decimal positions
								time = Long.parseLong(pointComponents[0]);
								x = Double.parseDouble(pointComponents[1] + "." + pointComponents[2]);
								y = Double.parseDouble(pointComponents[3] + "." + pointComponents[4]);
							} else */if (pointComponents.length == 3) {
								time = Long.parseLong(pointComponents[0]);
								x = Double.parseDouble(pointComponents[1]);
								y = Double.parseDouble(pointComponents[2]);
							} else {
								throw new Exception(
										"Expected 3 symbols in point, and found " + pointComponents.length + " in string " + pointStr + " in line " + lineNumber + " of file " + file.getName());
							} 
							Point point = new Point(time, x, y);
							stroke.addPoint(point);
						}

						currentStroke++;
						if (currentStroke == expectedStrokes) {
							state = State.eReadingSymbol;
						}
					}
				}
				lineNumber++;
			}
			if (currentSymbol != null) {
				currentSymbol.finishSymbol();
			}
		}

		return result;
	}

	private AgnosticSymbolType parseNotationSymbol(String string) {
		return factory.parseString(string);
	}

	private PositionInStaff parsePositionInStaff(String string) throws IM3Exception {
	    return PositionInStaff.parseString(string);
	}
}
