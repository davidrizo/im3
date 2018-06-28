package es.grfia.hmm.score.features;

import org.opencv.core.Mat;

public interface FeatureExtraction {
	double [][] getFeatures(Mat image);
}
