package es.ua.dlsi.im3.omr.classifiers.symbolrecognition;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.patternmatching.IMetricPrototype;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbol;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbolType;

import java.util.ArrayList;

/**
 * @autor drizo
 */
public class SymbolImagePrototype implements IMetricPrototype<AgnosticSymbol> {
    AgnosticSymbol prototypeClass;
    GrayscaleImageData imageData;

    /**
     * @param prototypeClass
     * @param imageData
     */
    public SymbolImagePrototype(AgnosticSymbol prototypeClass, GrayscaleImageData imageData) {
        this.prototypeClass = prototypeClass;
        this.imageData = imageData;
    }


    @Override
    public AgnosticSymbol getPrototypeClass() {
        return prototypeClass;
    }

    @Override
    public double computeDistance(IMetricPrototype<AgnosticSymbol> to) throws IM3Exception {
        SymbolImagePrototype other = (SymbolImagePrototype) to;
        return imageData.computeDistance(((SymbolImagePrototype) to).imageData);
    }

    @Override
    public String toString() {
        return "SymbolImagePrototype{" +
                "prototypeClass=" + prototypeClass +
                ", imageData=" + imageData +
                '}';
    }
}
