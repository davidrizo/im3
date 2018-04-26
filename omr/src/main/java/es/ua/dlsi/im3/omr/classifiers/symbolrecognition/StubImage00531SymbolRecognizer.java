package es.ua.dlsi.im3.omr.classifiers.symbolrecognition;


import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.adt.graphics.BoundingBox;
import es.ua.dlsi.im3.core.adt.graphics.BoundingBoxXY;
import es.ua.dlsi.im3.core.score.PositionInStaff;
import es.ua.dlsi.im3.omr.conversions.Calco2Agnostic;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbol;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbolType;
import es.ua.dlsi.im3.omr.model.entities.Symbol;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

/**
 * It just returns the values of a specific file name as a stub
 */
public class StubImage00531SymbolRecognizer implements IImageSymbolRecognizer {
    @Override
    public List<Symbol> recognize(File file) throws IM3Exception {
        if (!file.getName().endsWith("00531.JPG")) {
            throw new IM3Exception("Unsupported image, must be just 00531.JPG");
        }
        List<Symbol> result = new LinkedList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/data/stubs/00531.symbols.txt")));
        StringBuilder out = new StringBuilder();
        String line;
        int n=1;
        Calco2Agnostic calco2Agnostic = new Calco2Agnostic();
        try {
            while ((line = reader.readLine()) != null) {
                String [] tokens = line.split(";");
                if (tokens.length != 4) {
                    throw new IM3Exception("Expected 4 tokens <from>;<to>;<symbol type>;<>position in staff>, and found " + tokens.length + " in line " + n);
                }

                String [] coordsFrom = tokens[0].split(",");
                if (coordsFrom.length != 2) {
                    throw new IM3Exception("Bounding box from, expected 2 real numbers separated by a comma, and found " + tokens[0].length() + " in line " + n);
                }

                String [] coordsTo = tokens[1].split(",");
                if (coordsFrom.length != 2) {
                    throw new IM3Exception("Bounding box to, expected 2 real numbers separated by a comma, and found " + tokens[1].length() + " in line " + n);
                }

                Symbol symbol = new Symbol();
                symbol.setBoundingBox(new BoundingBoxXY(Double.parseDouble(coordsFrom[0]),
                        Double.parseDouble(coordsFrom[1]),
                        Double.parseDouble(coordsTo[0]),
                        Double.parseDouble(coordsTo[1])));
                AgnosticSymbolType agnosticSymbolType = calco2Agnostic.convert(tokens[2]);
                AgnosticSymbol agnosticSymbol = new AgnosticSymbol(agnosticSymbolType, PositionInStaff.parseString(tokens[3]));
                symbol.setAgnosticSymbol(agnosticSymbol);
                result.add(symbol);
                n++;
            }
        } catch (IOException e) {
            throw new IM3Exception(e);
        }
        return result;
    }



    /*public void recognize(URL imagesURL, List<Page> pages) throws IM3Exception {
        //TODO Datos puestos a piñón, sólo funciona con la primera página de la partitura 12608.JPG
        for (Page page: pages) {
            if (page.getImageRelativeFileName().equals("12608.JPG")) {
                recognize(page);
            } else {
                throw new IM3Exception("Unsupported image, must be just 12608.JPG");
            }
        }
    }

    private void recognize(Image page) {
        //TODO Abril
        Symbol [][] symbols = new Symbol[][] { // an array for each region
                {
                    new Symbol(new GraphicalToken(GraphicalSymbol.text, "Tiple 1º Coro a/2", PositionsInStaff.SPACE_6), 1782, 58, 57, 72),
                },
                {
                        new Symbol(new GraphicalToken(GraphicalSymbol.text, "Jalon", PositionsInStaff.SPACE_6), 2633, 29, 239, 107),
                },
                {
                        new Symbol(new GraphicalToken(GraphicalSymbol.clef, "g", PositionsInStaff.LINE_2), 1605, 207, 135, 191),
                        new Symbol(new GraphicalToken(GraphicalSymbol.metersign, "c", PositionsInStaff.LINE_2), 1695, 162, 212, 128),
                        new Symbol(new GraphicalToken(GraphicalSymbol.rest, "minima", PositionsInStaff.LINE_4), 1761, 162, 164, 128),
                        new Symbol(new GraphicalToken(GraphicalSymbol.rest, "semibreve", PositionsInStaff.SPACE_4), 1788,162,  180, 128)
                        //TODO El resto
                }
        };

        int i=0;
        for (Region region: page.getRegions()) {
            if (i<symbols.length) {
                for (Symbol symbol: symbols[i]) {
                    region.getSymbols().add(symbol);
                }
            }
            i++;
        }
    }*/
}
