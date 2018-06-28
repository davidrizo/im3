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
            throw new IM3Exception("Cannot compute the distance between two images of different size: " +
                    thisPixels.size() + " vs. " +  otherPixels.size());
        }
        double acc = 0;
        for(int i = 0; i < thisPixels.size(); i++) {
            acc += Math.pow(thisPixels.get(i)-otherPixels.get(i), 2);
        }

        return Math.sqrt(acc);
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
