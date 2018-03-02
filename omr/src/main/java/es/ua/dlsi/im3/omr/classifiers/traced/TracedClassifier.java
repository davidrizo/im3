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
import es.ua.dlsi.im3.core.score.PositionsInStaff;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbol;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbolType;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author drizo
 */
public class TracedClassifier implements IBimodalClassifier {
    private List<BimodalSymbol> trainSet;
	IBimodalDatasetReader datasetReader;
	
	public TracedClassifier(IBimodalDatasetReader reader) {
		this.datasetReader = reader;
	}
	public void learn(File file) throws IOException {
		trainSet = datasetReader.read(file);
		Logger.getLogger(TracedClassifier.class.getName()).log(Level.INFO, "Learnt with {0} symbols", trainSet.size());
	}

	public void learn(InputStream is) throws IOException {
		trainSet = datasetReader.read(is);
		Logger.getLogger(TracedClassifier.class.getName()).log(Level.INFO, "Learnt with {0} symbols", trainSet.size());
	}

    @Override
    public Long getNumberOfTrainingSymbols() {
        return new Long(trainSet.size());
    }

    @Override
	public List<AgnosticSymbol> classify(int[][] grayscaleImage, List<Stroke> strokes) throws IM3Exception {
		BimodalSymbol bs = new BimodalSymbol();
		for (int i=0; i<grayscaleImage.length; i++) {
			for (int j=0; j<grayscaleImage[i].length; j++) {
				bs.addGrayscalePixel(grayscaleImage[j][i]);
			}
		}
		for (Stroke stroke : strokes) {
			for (Point point: stroke.pointsProperty()) {
				bs.addPoint(new Coordinate(point.getX(), point.getY()));	
			}
		}
		
		TreeSet<Ranking> ranking = classify(bs);
		ArrayList<AgnosticSymbol> result = new ArrayList<>();
		for (Ranking item : ranking) {
            //TODO Hacer Javi - clasificar altura
			result.add(new AgnosticSymbol(item.getSymbol(), PositionsInStaff.LINE_3));
		}
		return result;
	}
	
	class Ranking implements Comparable<Ranking>{
		AgnosticSymbolType symbol;
		double distance;
		
		public Ranking(AgnosticSymbolType symbol, double distance) {
			super();
			this.symbol = symbol;
			this.distance = distance;
		}
		
		public AgnosticSymbolType getSymbol() {
			return symbol;
		}

		public double getDistance() {
			return distance;
		}

		@Override
		public int compareTo(Ranking o) {
			if (distance < o.distance) {
				return -1;
			} else if (distance > o.distance) {
				return 1;
			} else {
				return symbol.compareTo(o.symbol);
			}
		}
	}
	public TreeSet<Ranking> classify(BimodalSymbol x) throws IM3Exception {
		TreeMap<AgnosticSymbolType, Double> symbols = new TreeMap<>();
		
		for(BimodalSymbol symbol : trainSet) {
			double distance = distance2(x,symbol);
			AgnosticSymbolType symbolType = symbol.getLabel();
			if (symbolType == null) {
				throw new IM3Exception("Uknown symbol type: " + symbol.getLabel());
			}
			Double prevDist = symbols.get(symbolType);
			if (prevDist == null || distance < prevDist) {
				symbols.put(symbolType, distance); // to avoid repeating symbols
			}
		}
		TreeSet<Ranking> result = new TreeSet<>();
		for (Map.Entry<AgnosticSymbolType, Double> symbolEntry: symbols.entrySet()) {
			result.add(new Ranking(symbolEntry.getKey(), symbolEntry.getValue()));
		} 
		
		return result;
	}

	public double distance2(BimodalSymbol sa, BimodalSymbol sb) throws IM3Exception {
		double acc = 0;

		ArrayList<Integer> lgra = sa.getGrayscalePixels();
		ArrayList<Integer> lgrb = sb.getGrayscalePixels();

		if (sa.getGrayscalePixels().size() != sb.getGrayscalePixels().size()) {
			throw new IM3Exception("Compared symbols don't have the same image sizes: " + 
					sa.getGrayscalePixels().size() + " vs. " +  sb.getGrayscalePixels().size());
		}
		for(int i = 0; i < lgra.size(); i++) {		
			acc += Math.pow(lgra.get(i)-lgrb.get(i), 2);
		}
		
		return Math.sqrt(acc);
	}
	
}
