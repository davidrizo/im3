package es.grfia.hmm.score.features;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

import es.grfia.hmm.utils.Utils;

public class StaffFocus {

	/**
	 * The idea is to seek for the five parallel lines (five peaks in the horizontal projection).
	 * Then, the middle peak (staff line) is used as reference value. The image is cropped from that point
	 * to 'span' pixels in each direction.
	 * @param original Image to be cropped
	 * @param span Width of the span in each direction
	 * @return The image after cropping.
	 */
	public Mat crop(Mat original, int span) {
		int [] hist = new int[original.rows()];
		
		for(int i = 0; i < original.rows(); i++) {
			for(int j = 0; j < original.cols(); j++) {
				hist[i] += (Utils.isInk(original.get(i, j)[0]) ? 1 : 0);
			}
		}
		
		int [] five_peaks = Utils.getPeaks(hist,5,20);
		Arrays.sort(five_peaks); // Ordered		
		
		return cropFromReference(original,span,five_peaks[2]);
		
	}

	private Mat cropFromReference(Mat original, int span, int reference) {
		return original.rowRange(reference-span,reference+span);
	}
	
}
