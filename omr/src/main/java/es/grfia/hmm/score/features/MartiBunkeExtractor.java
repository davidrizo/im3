package es.grfia.hmm.score.features;


//import org.opencv.core.Mat;

// Removed for avoiding interactions of this OpenCV with Keras in MuRET
public class MartiBunkeExtractor implements FeatureExtraction {
	/*@Override
	public double[][] getFeatures(Mat image) {		
		double [][] features = new double[image.cols()][9];
		
		// Calculamos por caracteristica (hay dependencia)
		for(int i = 0; i < 9; i++) {
			for(int j = 0; j < image.cols(); j++) {
				double acc = 0;
				switch(i) {
				case 0:
					for(int row = 0; row < image.rows(); row++) {
						double v = image.get(row, j)[0];
						acc += (Utils.isInk(v) ? 1 : 0);
					}					
					features[j][i] = acc;
					
					break;
				case 1:		
					if(features[j][0] == 0) {
						features[j][i] = 0;
					} else {
						for(int row = 0; row < image.rows(); row++) {
							acc += ((row+1) * (Utils.isInk(image.get(row, j)[0]) ? 1 : 0));
						}					
						features[j][i] = acc / features[j][0];					
					}
					break;				
				case 2:		
					if(features[j][0] == 0) {
						features[j][i] = 0;
					} else {
						for(int row = 0; row < image.rows(); row++) {
							acc += ( Math.pow( (row+1) - features[j][1],2) * (Utils.isInk(image.get(row, j)[0]) ? 1 : 0));
						}					
						features[j][i] = acc / features[j][0];
					}
					
					break;					
				case 3:
					
					if(features[j][0] == 0) {
						features[j][i] = 0;
					} else {						
						double minNegro = image.rows();
						
						for(int row = 0; row < image.rows(); row++) {
							if(Utils.isInk(image.get(row, j)[0])) {
								minNegro = Math.min(row+1,minNegro);
							}
						}
											
						features[j][i] = minNegro;
					}
					
					break;										
				case 4:
					if(features[j][0] == 0) {
						features[j][i] = 0;
					} else {			
						double maxNegro = 0;
						
						for(int row = 0; row < image.rows(); row++) {
							if(Utils.isInk(image.get(row, j)[0])) {
								maxNegro = Math.max(row+1,maxNegro);
							}
						}
					
						features[j][i] = maxNegro;	
					}
					
					break;
				case 5:
					if(j == 0) {
						features[j][i] = (features[j+1][3] - features[j][3])/2;
					} else if(j == image.cols()-1) {
						features[j][i] = (features[j][3] - features[j-1][3])/2;
					} else {
						features[j][i] = (features[j+1][3] - features[j-1][3])/2;
					}
										
					break;
				case 6:
					if(j == 0) {
						features[j][i] = (features[j+1][4] - features[j][4])/2;
					} else if(j == image.cols()-1) {
						features[j][i] = (features[j][4] - features[j-1][4])/2;
					} else {
						features[j][i] = (features[j+1][4] - features[j-1][4])/2;
					}
					
					break;
				case 7:
					for(int row = (int) features[j][3]-1; row < features[j][4]-1; row++) {
						if(!Utils.isInk(image.get(row+1, j)[0]) && Utils.isInk(image.get(row, j)[0])) {
							acc++;							
						}
					}
					
					features[j][i] = acc;
					
					break;
				case 8:
					
					for(int row = (int) features[j][3]; row < features[j][4]-1; row++) {
						if(Utils.isInk(image.get(row, j)[0])) {
							acc++;							
						}
					}
					features[j][i] = acc;
					
					break;
				}
			}			
		}
				
		
		return features;
	}*/
}
