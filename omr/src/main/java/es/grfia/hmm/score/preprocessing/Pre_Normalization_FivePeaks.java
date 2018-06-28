package es.grfia.hmm.score.preprocessing;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import es.grfia.hmm.utils.Utils;

/**
 * Slightly changed from the Jorge Calvo's one (removed parameter input)
 */
public class Pre_Normalization_FivePeaks {
	public static final double REF_HEIGHT = 250.0;

	public Mat run(Mat original) {
		int [] wHist = new int[original.rows()];
		
		for(int i = 0; i < original.rows(); i++) {
			for(int j = 0; j < original.cols(); j++) {
				wHist[i] += (255.0 - original.get(i, j)[0]);
			}
		}
		
		int [] peaks = Utils.getPeaks(wHist, 5, 20);
		
		
		int span = Math.abs(peaks[4]-peaks[0])/2;		
		int top = peaks[0] - span;
		int bottom = peaks[4] + span;
		
		//
		int height = bottom-top+1;
		
		Mat output = new Mat(height,original.cols(),original.type());
		
		for(int i = 0; i < height; i++) {
			int ptr = top + i;
			
			for(int j = 0; j < original.cols(); j++) {
				if(ptr < 0){				
					output.put(i, j,original.get(0,j));
				}
				else if(ptr >= original.rows()) {
					output.put(i, j,original.get(original.rows()-1,j));
				} 
				else {						
					output.put(i,j,original.get(ptr,j));
				}
			}
		}
		
		double sfactor = REF_HEIGHT/height;
		
		Mat resized = new Mat();		
		Imgproc.resize(output,resized,new Size(0,0),sfactor,sfactor,Imgproc.INTER_CUBIC);
			
		return resized;	
	}
}
