package es.ua.dlsi.im3.omr.classifiers.symbolrecognition;

import es.ua.dlsi.im3.core.IM3Exception;

import java.util.ArrayList;
import java.util.List;

/**
 * @autor drizo
 */
public class GrayscaleImageData {
    ArrayList<Integer> grayScalePixels;

    public GrayscaleImageData(List<Integer> grayScalePixels) {
        this.grayScalePixels = new ArrayList<>(grayScalePixels);
    }

    public GrayscaleImageData(int[] pixels) {
        grayScalePixels = new ArrayList<>();
        for (int i=0; i<pixels.length; i++) {
            grayScalePixels.add(pixels[i]);
        }
    }

    public ArrayList<Integer> getGrayScalePixels() {
        return grayScalePixels;
    }

    public double computeDistance(GrayscaleImageData imageData) throws IM3Exception {
        ArrayList<Integer> thisPixels = grayScalePixels;
        ArrayList<Integer> otherPixels = imageData.grayScalePixels;

        if (thisPixels.size() != otherPixels.size()) {
            throw new IM3Exception("Cannot compute the distance between two imagesold of different size: " +
                    thisPixels.size() + " vs. " +  otherPixels.size());
        }
        double acc = 0;
        for(int i = 0; i < thisPixels.size(); i++) {
            //acc += Math.pow(thisPixels.get(i)-otherPixels.get(i), 2); L2

            // use L1
            acc += Math.abs(thisPixels.get(i)-otherPixels.get(i));
        }

        //return Math.sqrt(acc); L2
        return acc;
    }


    /**
     * If, while computing the distance, the accumulated distance is greater than threshold, the computation is stopped and a null value is returned
     * @param threshold
     * @return
     */
    public Double computeDistance(GrayscaleImageData imageData, double threshold) throws IM3Exception {
        ArrayList<Integer> thisPixels = grayScalePixels;
        ArrayList<Integer> otherPixels = imageData.grayScalePixels;

        if (thisPixels.size() != otherPixels.size()) {
            throw new IM3Exception("Cannot compute the distance between two imagesold of different size: " +
                    thisPixels.size() + " vs. " +  otherPixels.size());
        }
        double acc = 0;
        for(int i = 0; i < thisPixels.size(); i++) {
            // use L1
            acc += Math.abs(thisPixels.get(i)-otherPixels.get(i));
            if (acc > threshold) {
                return null;
            }
        }

        //return Math.sqrt(acc); L2
        return acc;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i=0; i<grayScalePixels.size(); i++) {
            if (i==0) {
                stringBuilder.append('[');
            } else {
                stringBuilder.append(", ");
            }
            stringBuilder.append(grayScalePixels.get(i));
        }
        stringBuilder.append(']');
        return stringBuilder.toString();
    }

}
