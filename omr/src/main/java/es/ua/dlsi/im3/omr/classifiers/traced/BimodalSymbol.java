package es.ua.dlsi.im3.omr.classifiers.traced;

import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbolType;

import java.util.ArrayList;

public class BimodalSymbol {
	AgnosticSymbolType label;
	ArrayList<Coordinate> points;
	ArrayList<Integer> grayscalePixels;

	public BimodalSymbol(AgnosticSymbolType label) {
		this.label = label;
		points = new ArrayList<>();
		grayscalePixels = new ArrayList<>();
	}
	
	public BimodalSymbol() {
		points = new ArrayList<>();
		grayscalePixels = new ArrayList<>();
	}

	public void addPoint(Coordinate p) {
		points.add(p);
	}
	
	public void addGrayscalePixel(int gs) {
		grayscalePixels.add(gs);
	}

	public AgnosticSymbolType getLabel() {
		return label;
	}

	public ArrayList<Coordinate> getPoints() {
		return points;
	}

	public ArrayList<Integer> getGrayscalePixels() {
		return grayscalePixels;
	}

	
	
}
