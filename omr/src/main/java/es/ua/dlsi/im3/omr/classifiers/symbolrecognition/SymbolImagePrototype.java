package es.ua.dlsi.im3.omr.classifiers.symbolrecognition;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.patternmatching.IMetricPrototype;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbolType;

import java.util.ArrayList;

/**
 * @autor drizo
 */
public class SymbolImagePrototype implements IMetricPrototype<AgnosticSymbolType> {
    AgnosticSymbolType prototypeClass;
    GrayscaleImageData imageData;

    /**
     * @param prototypeClass
     * @param imageData
     */
    public SymbolImagePrototype(AgnosticSymbolType prototypeClass, GrayscaleImageData imageData) {
        this.prototypeClass = prototypeClass;
        this.imageData = imageData;
    }


    @Override
    public AgnosticSymbolType getPrototypeClass() {
        return prototypeClass;
    }

    @Override
    public double computeDistance(IMetricPrototype<AgnosticSymbolType> to) throws IM3Exception {
        SymbolImagePrototype other = (SymbolImagePrototype) to;
        return imageData.computeDistance(((SymbolImagePrototype) to).imageData);
    }
}
