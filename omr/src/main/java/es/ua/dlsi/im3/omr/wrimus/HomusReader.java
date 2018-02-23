package es.ua.dlsi.im3.omr.wrimus;

import es.ua.dlsi.im3.omr.classifiers.traced.Coordinate;

import java.io.*;
import java.util.List;

public class HomusReader {
    HomusDataset homusDataset;
    public HomusDataset read(List<File> files) throws IOException {
        homusDataset = new HomusDataset();
        for (File file : files) {
            Glyph glyph = read(file);
            homusDataset.addGlyph(glyph);
        }
        return homusDataset;
    }

    public Glyph read(File file) throws IOException {
        InputStream is = new FileInputStream(file);
        return read(is);
    }
    public Glyph read(InputStream is) throws IOException {
        if (is == null) {
            throw new IOException("Inputstream is null");
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
        return glyph;

    }
}
