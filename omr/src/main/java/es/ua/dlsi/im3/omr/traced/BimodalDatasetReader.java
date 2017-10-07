package es.ua.dlsi.im3.omr.traced;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import es.ua.dlsi.im3.omr.IStringToSymbolFactory;

public class BimodalDatasetReader<SymbolType> implements IBimodalDatasetReader<SymbolType> {
	public List<BimodalSymbol<SymbolType>> read(File file, IStringToSymbolFactory<SymbolType> stringToSymbol) throws IOException {
		InputStream is = new FileInputStream(file);
		return read(is, stringToSymbol);
	}
	public List<BimodalSymbol<SymbolType>> read(InputStream is, IStringToSymbolFactory<SymbolType> stringToSymbol) throws IOException {
		if (is == null) {
			throw new IOException("Inputstream is null");
		}
		ArrayList<BimodalSymbol<SymbolType>> result = new ArrayList<>();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		String line;
		while ((line=br.readLine())!=null) {
			String [] components = line.split(":");
			if (components.length != 3) {
				throw new IOException("Invalid line, must have 3 components and it has just " + components.length);
			}
			String label = components[0];
			String strokes = components[1];
			String grayscalePixels = components[2];
			
			BimodalSymbol<SymbolType> symbol = new BimodalSymbol<>(stringToSymbol.parseString(label));
			
			String [] strokeList = strokes.split(";");
			for (String sl : strokeList) {
				String [] coords = sl.split(",");
				if (coords.length != 2) {
					throw new IOException("Invalid coordinate, must have 2 components and it has just " + coords.length);	
				}
				symbol.addPoint(new Coordinate(Double.parseDouble(coords[0]), Double.parseDouble(coords[1])));
			}
			String [] pixels = grayscalePixels.split(",");
			for (String px : pixels) {
				symbol.addGrayscalePixel(Integer.parseInt(px));
			}
			result.add(symbol);
		}
		return result;
		
	}
}
