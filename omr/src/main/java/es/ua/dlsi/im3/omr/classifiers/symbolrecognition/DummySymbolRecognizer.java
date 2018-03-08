package es.ua.dlsi.im3.omr.classifiers.symbolrecognition;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.PositionsInStaff;
import es.ua.dlsi.im3.omr.model.pojo.*;

import java.net.URL;
import java.util.List;

public class DummySymbolRecognizer implements ISymbolsRecognizer {
    @Override
    public void recognize(URL imagesURL, List<Page> pages) throws IM3Exception {
        //TODO Datos puestos a piñón, sólo funciona con la primera página de la partitura 12608.JPG
        for (Page page: pages) {
            if (page.getImageRelativeFileName().equals("12608.JPG")) {
                recognize(page);
            } else {
                throw new IM3Exception("Unsupported image, must be just 12608.JPG");
            }
        }
    }

    private void recognize(Page page) {
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
    }
}
