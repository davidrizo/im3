package es.ua.dlsi.im3.omr.classifiers.traced;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class BimodalDatasetReader implements IBimodalDatasetReader {
    //TODO Abril StringToSymbolFactory stringToSymbolFactory;

    public BimodalDatasetReader() {
        //TODO Abril this.stringToSymbolFactory = new StringToSymbolFactory();
    }

    @Override
    public List<BimodalSymbol> read(File file) throws IOException {
		InputStream is = new FileInputStream(file);
		return read(is);
	}
	@Override
	public List<BimodalSymbol> read(InputStream is) throws IOException {
		if (is == null) {
			throw new IOException("Inputstream is null");
		}
		ArrayList<BimodalSymbol> result = new ArrayList<>();
        //TODO Abril
		/*InputStreamReader isr = new InputStreamReader(is);
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
			
			BimodalSymbol symbol = new BimodalSymbol(stringToSymbolFactory.parseString(label));
			
			String [] coordList = strokes.split(";");
			for (String sl : coordList) {
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
		}*/
		return result;
		
	}
}
