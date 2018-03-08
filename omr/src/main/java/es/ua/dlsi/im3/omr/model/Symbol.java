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
import es.ua.dlsi.im3.omr.classifiers.traced.Point;
import es.ua.dlsi.im3.omr.classifiers.traced.Stroke;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

//TODO Cambiar de paquete
/**
 * @deprecated Use model.pojo.Symbol
 * @author drizo
 */
public class Symbol<SymbolType> {
	private static final int MARGIN = 5;
	boolean symbolFinished;
	ArrayList<Stroke> strokes;
	AgnosticSymbol positionedSymbolType;

	BufferedImage symbolImage;
	// falta imagen de lo que hay bajo + margen (5px todos los lados):
	// imagen en OpenCV - mat - me envía correo
	ArrayList<Symbol> sortedPossibleNotationSymbols;
	private int minX;
	private int maxX;
	private int minY;
	private int maxY;
	private final BufferedImage pageImage;

	/**
	 * @param pageImage
	 *            Complete image of the page from which the symbol image is
	 *            taken
	 */
	public Symbol(BufferedImage pageImage) {
		strokes = new ArrayList<>();
		this.pageImage = pageImage;
		symbolFinished = false;
	}

	public void addStroke(Stroke stroke) {
		strokes.add(stroke);
	}

	public void finishSymbol() throws IM3Exception {
		this.symbolFinished = true;
		recomputeBoundingBox();
	}

	public List<Stroke> getStrokes() {
		return strokes;
	}

	public void removeStroke(Stroke stroke) {
		strokes.remove(stroke);
		this.symbolFinished = false;
	}

	@Override
	public String toString() {
		if (strokes.isEmpty()) {
			return strokes.size() + " strokes";
		} else {
			return strokes.size() + " strokes, first = " + strokes.get(0).toString();
		}
	}

	public void setPositionedSymbolType(AgnosticSymbol pst) {
		this.positionedSymbolType = pst;
	}

	public AgnosticSymbol getPositionedSymbolType() {
		return positionedSymbolType;
	}

	public ArrayList<Symbol> getSortedPossibleNotationSymbols() {
		return sortedPossibleNotationSymbols;
	}

	public void setSortedPossibleNotationSymbols(ArrayList<Symbol> sortedPossibleNotationSymbols) {
		this.sortedPossibleNotationSymbols = sortedPossibleNotationSymbols;
	}

	public BufferedImage getSymbolImage() {
		return symbolImage;
	}

	public int getMinX() {
		return this.minX;
	}

	public int getMaxX() {
		return this.maxX;
	}

	public int getMinY() {
		return this.minY;
	}

	public int getMaxY() {
		return this.maxY;
	}

	private void recomputeBoundingBox() throws IM3Exception {
		minY = Integer.MAX_VALUE;
		minX = Integer.MAX_VALUE;
		maxY = Integer.MIN_VALUE;
		maxX = Integer.MIN_VALUE;
		for (Stroke stroke : strokes) {
			for (Point point : stroke.pointsProperty()) {
				minY = Math.min(minY, (int) point.getY());
				minX = Math.min(minX, (int) point.getX());

				maxY = Math.max(maxY, (int) point.getY());
				maxX = Math.max(maxX, (int) point.getX());
			}
		}

		int x = Math.max(0, minX - MARGIN);
		int y = Math.max(0, minY - MARGIN);

		int _maxX = Math.min(pageImage.getWidth(), maxX + MARGIN);
		int _maxY = Math.min(pageImage.getHeight(), maxY + MARGIN);

		int w = _maxX - x;
		int h = _maxY - y;
		Logger.getLogger(Symbol.class.getName()).log(Level.INFO,
				"Extracting from ({0},{1}), w={2}, h={3} in a image of size {4}x{5}",
				new Object[] { x, y, w, h, pageImage.getWidth(), pageImage.getHeight() });

		if (w <= 0) {
			throw new IM3Exception("w cannot be <=0");
		}

		if (h <= 0) {
			throw new IM3Exception("h cannot be <=0");
		}

		maxX = _maxX;
		maxY = _maxY;
		minX = x;
		minY = y;

		symbolImage = pageImage.getSubimage(x, y, w, h);
	}

	public int getWidth() {
		return maxX - minX;
	}

	public int getHeight() {
		return maxY - minY;
	}

	public PositionInStaff getPositionInStaff() {
		return positionedSymbolType.getPositionInStaff();
	}

	public void setPositionInStaff(PositionInStaff positionInStaff) {
		this.positionedSymbolType.setPositionInStaff(positionInStaff);
	}

	
}
