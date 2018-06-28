package es.ua.dlsi.im3.omr.classifiers.symbolrecognition;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.patternmatching.EditDistance;
import es.ua.dlsi.im3.core.patternmatching.IEditDistanceOperations;
import es.ua.dlsi.im3.core.patternmatching.IMetricPrototype;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbolType;
import es.ua.dlsi.im3.omr.model.entities.Point;

import java.util.ArrayList;

/**
 * Bimodal ptorotype
 * @autor drizo
 */
public class SymbolImageAndPointsPrototype implements IMetricPrototype<AgnosticSymbolType> {
    AgnosticSymbolType prototypeClass;
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
    public SymbolImageAndPointsPrototype(AgnosticSymbolType prototypeClass, GrayscaleImageData imageData, PointsData pointsData) {
        this.prototypeClass = prototypeClass;
        this.imageData = imageData;
        this.pointsData = pointsData;
    }


    @Override
    public AgnosticSymbolType getPrototypeClass() {
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
    public double computeDistance(IMetricPrototype<AgnosticSymbolType> to) throws IM3Exception {
        SymbolImageAndPointsPrototype other = (SymbolImageAndPointsPrototype) to;
        double imageDistance = imageData.computeDistance(other.imageData);

        EditDistance<Point> pointsDataEditDistance = new EditDistance(PointsData.POINT_EDIT_DISTANCE_OPERATIONS);
        double pointsDistance = pointsDataEditDistance.computeDistance(pointsData.getPoints(), other.pointsData.getPoints());

        return imageDistance * bimodalDistanceImagesWeight + (1.0-imageDistance)*pointsDistance;
    }
}
