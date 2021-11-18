package es.grfia.hmm.score.features;

//import org.opencv.core.Core;
//import org.opencv.core.Mat;
// Removed for avoiding interactions of this OpenCV with Keras in MuRET
public class GradientFeatures implements FeatureExtraction {
	/*
	@Override
	public double [][] getFeatures(Mat input) {
		int imageRow = input.rows();
		double [][] features = new double[imageRow*3][input.cols()];
		
		for(int i = 0; i < input.rows(); i++) {
			for(int j = 0; j < input.cols(); j++) {
				double [] f = new double[3];
				
				f[0] = input.get(i,j)[0]/255.0;

				if(j == input.cols()-1)	f[1] = 0;
				else 					f[1] = ((input.get(i, j)[0] - input.get(i, j+1)[0]) + 255.0) / 510.0;
				
				if(i == 0) 	f[2] = 0;
				else 		f[2] = ((input.get(i, j)[0] - input.get(i-1, j)[0]) + 255.0) / 510.0;
				
				for(int k = 0; k < 3; k++) {
					features[k*imageRow + i][j] = f[k];				
				}
				
				
			}
		}
		
		
		return features;
	}*/

}
