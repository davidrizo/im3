package es.ua.dlsi.im3.omr.classifiers.symbolrecognition;

import es.ua.dlsi.im3.core.patternmatching.IMetricPrototype;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbol;
import es.ua.dlsi.im3.omr.model.entities.Point;
import es.ua.dlsi.im3.omr.model.entities.Stroke;
import es.ua.dlsi.im3.omr.model.entities.Strokes;
import org.apache.commons.text.similarity.LevenshteinDistance;

/**
 * It classifies using the image information, and, if present, the strokes
 * Bimodal ptorotype
 * @autor drizo
 */
public class SymbolPointsPrototype implements IMetricPrototype<AgnosticSymbol> {
    AgnosticSymbol prototypeClass;
    PointsData pointsData;
    /**
     * Freeman code
     */
    char [] fcc;

    CharSequence fccCharSequence;
    /**
     * @param prototypeClass
     */
    public SymbolPointsPrototype(AgnosticSymbol prototypeClass, PointsData pointsData) {
        this.prototypeClass = prototypeClass;
        this.pointsData = pointsData;
        initFCC();
    }

    private void initFCC() {
        if (this.pointsData != null) {
            String chainCode = FCC.getChainCode(this.pointsData.getPoints());
            fcc = chainCode.toCharArray();
            fccCharSequence = chainCode.subSequence(0, chainCode.length());
        }
    }

    public SymbolPointsPrototype(AgnosticSymbol prototypeClass, Strokes strokes) {
        this.prototypeClass = prototypeClass;
        this.pointsData = new PointsData();
        for (Stroke stroke: strokes.getStrokeList()) {
            for (Point point: stroke.pointsProperty()) {
                this.pointsData.addPoint(point);
            }
        }
        initFCC();
    }

    public PointsData getPointsData() {
        return pointsData;
    }

    @Override
    public AgnosticSymbol getPrototypeClass() {
        return prototypeClass;
    }

    @Override
    public double computeDistance(IMetricPrototype<AgnosticSymbol> to) {
        SymbolPointsPrototype other = (SymbolPointsPrototype) to;
        double fccDistance = new FCC().distance(fcc, other.fcc);
        return fccDistance;
    }

    public CharSequence getFCCCharSequence() {
        return fccCharSequence;
    }
}
