package es.ua.dlsi.im3.omr.wrimus;

import es.ua.dlsi.im3.omr.IStringToSymbolFactory;
import es.ua.dlsi.im3.omr.traced.BimodalSymbol;
import es.ua.dlsi.im3.omr.traced.Coordinate;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HomusReader {
    HomusDataset homusDataset;
    public HomusDataset read(List<File> files) throws IOException {
        homusDataset = new HomusDataset();
        for (File file : files) {
            InputStream is = new FileInputStream(file);
            read(is);
        }
        return homusDataset;
    }
    public HomusDataset read(InputStream is) throws IOException {
        if (is == null) {
            throw new IOException("Inputstream is null");
        }
        if (homusDataset == null) {
            homusDataset = new HomusDataset();
        }

        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String line;
        int iline = 0;
        Glyph glyph = null;
        while ((line=br.readLine())!=null) {
            if (iline == 0) {
                glyph = new Glyph(new Symbol(line.trim()));
            } else if (iline >= 1) {
                Stroke stroke = new Stroke();
                String[] pointList = line.trim().split(";");
                for (String sl : pointList) {
                    String[] coords = sl.split(",");
                    if (coords.length != 2) {
                        throw new IOException("Invalid coordinate, must have 2 components and it has just " + coords.length);
                    }
                    stroke.addPoint(new Coordinate(Double.parseDouble(coords[0]), Double.parseDouble(coords[1])));
                }
                glyph.addStroke(stroke);
            }
            iline++;
        }
        homusDataset.addGlyph(glyph);
        return homusDataset;

    }
}
