package es.ua.dlsi.im3.omr.classifiers.symbolrecognition;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.patternmatching.EditDistance;
import es.ua.dlsi.im3.core.patternmatching.IEditDistanceOperations;
import es.ua.dlsi.im3.core.patternmatching.IMetricPrototype;
import es.ua.dlsi.im3.core.patternmatching.IPrototype;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbol;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbolType;
import es.ua.dlsi.im3.omr.model.entities.Point;
import es.ua.dlsi.im3.omr.model.entities.Stroke;
import es.ua.dlsi.im3.omr.model.entities.Strokes;
import org.opencv.core.Mat;

import java.util.ArrayList;

/**
 * It classifies using the image information, and, if present, the strokes
 * Bimodal ptorotype
 * @autor drizo
 */
public class SymbolImageAndPointsPrototype implements IPrototype<AgnosticSymbol> {
    AgnosticSymbol prototypeClass;
    GrayscaleImageData imageData;
    PointsData pointsData;
    /**
     * @param prototypeClass
     * @param imageData
     */
    public SymbolImageAndPointsPrototype(AgnosticSymbol prototypeClass, GrayscaleImageData imageData, PointsData pointsData) {
        this.prototypeClass = prototypeClass;
        this.imageData = imageData;
        this.pointsData = pointsData;
    }

    public SymbolImageAndPointsPrototype(AgnosticSymbol prototypeClass, GrayscaleImageData imageData, Strokes strokes) {
        this.prototypeClass = prototypeClass;
        this.imageData = imageData;
        this.pointsData = new PointsData();
        for (Stroke stroke: strokes.getStrokeList()) {
            for (Point point: stroke.pointsProperty()) {
                this.pointsData.addPoint(point);
            }
        }
    }

    @Override
    public AgnosticSymbol getPrototypeClass() {
        return prototypeClass;
    }

    public GrayscaleImageData getImageData() {
        return imageData;
    }

    public PointsData getPointsData() {
        return pointsData;
    }
}
