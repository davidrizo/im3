package es.ua.dlsi.im3.omr.classifiers.symbolrecognition;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.patternmatching.EditDistance;
import es.ua.dlsi.im3.core.patternmatching.IEditDistanceOperations;
import es.ua.dlsi.im3.core.patternmatching.IMetricPrototype;
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
public class SymbolImageAndPointsPrototype implements IMetricPrototype<AgnosticSymbol> {
    AgnosticSymbol prototypeClass;
    GrayscaleImageData imageData;
    PointsData pointsData;
    /**
     * Used to combine results from points and images edit distance
     */
    double bimodalDistanceImagesWeight = 0.5;
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

    public double getBimodalDistanceImagesWeight() {
        return bimodalDistanceImagesWeight;
    }

    /**
     *
     * @param bimodalDistanceImagesWeight From 0 to 1
     */
    public void setBimodalDistanceImagesWeight(double bimodalDistanceImagesWeight) throws IM3Exception {
        if (bimodalDistanceImagesWeight < 0.0 || bimodalDistanceImagesWeight > 1.0) {
            throw new IM3Exception("This value should be in the range [0..1], and it is " + bimodalDistanceImagesWeight);
        } else {
            this.bimodalDistanceImagesWeight = bimodalDistanceImagesWeight;
        }
    }

    @Override
    public double computeDistance(IMetricPrototype<AgnosticSymbol> to) throws IM3Exception {
        SymbolImageAndPointsPrototype other = (SymbolImageAndPointsPrototype) to;
        double imageDistance = imageData.computeDistance(other.imageData);

        if (pointsData != null && !pointsData.isEmpty() && other.pointsData != null && !other.pointsData.isEmpty()) {
            EditDistance<Point> pointsDataEditDistance = new EditDistance(PointsData.POINT_EDIT_DISTANCE_OPERATIONS);
            double pointsDistance = pointsDataEditDistance.computeDistance(pointsData.getPoints(), other.pointsData.getPoints()) / Math.max(pointsData.getPoints().size(), other.pointsData.getPoints().size());
            double result = imageDistance * bimodalDistanceImagesWeight + (1.0-bimodalDistanceImagesWeight)*pointsDistance;
            return result;
        } else {
            System.out.println("Only image: " + imageDistance);
            return imageDistance;
        }

    }
}
