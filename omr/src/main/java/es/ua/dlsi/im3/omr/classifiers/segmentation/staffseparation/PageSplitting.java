package es.ua.dlsi.im3.omr.classifiers.segmentation.staffseparation;

import java.io.File;
import java.util.List;

import es.ua.dlsi.im3.core.IM3Exception;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

/**
 * Modified by drizo
 * @author jcalvo
 */

public class PageSplitting {
	/*public static void main(String [] args) {
		System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
		eclipse();
	}

	private static void eclipse() {
		Mat original = Highgui.imread("/home/jcalvo/Escritorio/Tesis/Investigacion/TracedOMR/ISMIR_DB/12612.JPG_1425128911385/12612.JPG");
		Mat score = Highgui.imread("/home/jcalvo/Escritorio/Tesis/Investigacion/TracedOMR/ISMIR_DB/12612.JPG_1425128911385/12612.JPG",Highgui.IMREAD_GRAYSCALE);						
						
		PageSplitting split = new PageSplitting();
		int point = split.run(score);
		
		Highgui.imwrite("/home/jcalvo/Escritorio/_"+0+".png", original.colRange(0,point));
		Highgui.imwrite("/home/jcalvo/Escritorio/_"+1+".png", original.colRange(point,original.cols()));
	}*/

    /**
     *
     * @param score
     * @return Return the column where the page is to be splitted
     */
	public int run(Mat score) {
		double hit = 255.0;	
		
		Mat binary = new Mat(score.rows(),score.cols(),score.type());
		Imgproc.threshold(score, binary, 128, hit, Imgproc.THRESH_BINARY);
		
		PreprocessingUtilities.StaffParams staffParams = new PreprocessingUtilities.StaffParams(5, 30);
		
		Imgproc.threshold(score, binary, 128, hit, Imgproc.THRESH_BINARY_INV);
		
		
		Mat kernel_1 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(10,1) );		
		Imgproc.erode(binary, binary, kernel_1);
			
		Mat kernel_2 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(1,50) );
		Imgproc.dilate(binary, binary, kernel_2);
		
		
		// vertical histogram
		int [] vhist = new int[binary.cols()];
		
		for(int i = 0; i < binary.rows(); i++) {
			for(int j = 0; j < binary.cols(); j++) {
				if(binary.get(i, j)[0] == hit) {
					vhist[j] ++;
				}
			}
		}
		
		int start = vhist.length/4;
		int end = 3*start;
		
		boolean sp1 = false;
		int p1 = 0;
		for(int i = start; i < end; i++) {
			if(vhist[i] > 500) {
				sp1 = true;
			} else if(sp1){
				p1 = i;
				break;
			}
		}
		
		boolean sp2 = false;
		int p2 = 0;
		for(int i = end; i >= start; i--) {
			if(vhist[i] > 500) {
				sp2 = true;
			} else if(sp2){
				p2 = i;
				break;
			}
		}
		
		return p1 + (p2-p1)/2;
	}

    /**
     * It returns two images
     * @param original
     * @return
     */
    public Mat[] splitIntoPages(Mat original) {
        Mat [] result;
        int point = run(original);
        if (point == 0 || point >= original.cols()-1) {
            result = new Mat[1];
            result[0] = original;
        } else {
            result = new Mat[2];
            result[0] = original.colRange(0,point);
            result[1] = original.colRange(point,original.cols());
        }
        return result;
        //Highgui.imwrite("/home/jcalvo/Escritorio/_"+0+".png", original.colRange(0,point));
        //Highgui.imwrite("/home/jcalvo/Escritorio/_"+1+".png", original.colRange(point,original.cols()));

    }
}
