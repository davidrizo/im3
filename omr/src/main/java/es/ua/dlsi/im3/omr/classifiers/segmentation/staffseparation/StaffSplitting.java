package es.ua.dlsi.im3.omr.classifiers.segmentation.staffseparation;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

/**
 * Modified by drizo
 * @author jcalvo
 */

public class StaffSplitting {
	
	/*public static void main(String [] args) {
		System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
		console(args);
		eclipse();
	}

	private static void eclipse() {
		Mat score = Highgui.imread("/home/jcalvo/Dropbox/Estancia Valencia/Corpus/CoS/Pages/12666-B.JPG",Highgui.IMREAD_GRAYSCALE);						
		Mat original = Highgui.imread("/home/jcalvo/Dropbox/Estancia Valencia/Corpus/CoS/Pages/12666-B.JPG");
						
		Highgui.imwrite("/home/jcalvo/Escritorio/0.jpg", original);
		StaffSplitting split = new StaffSplitting();
		List<Integer> points = split.run(score);
		
		for(int i = 1; i < points.size(); i++) {
			Highgui.imwrite("/home/jcalvo/Escritorio/_"+i+".png", original.rowRange(points.get(i-1),points.get(i)) );
		}	
	}
	
	private static void console(String[] args) {		
		// PoC			
		Mat original = Highgui.imread(args[0],Highgui.IMREAD_UNCHANGED);
		Mat score = Highgui.imread(args[0],Highgui.IMREAD_GRAYSCALE);		
		
		StaffSplitting split = new StaffSplitting();
		List<Integer> points = split.run(score);
					
		for(int i = 1; i < points.size(); i++) {
			Highgui.imwrite(args[1]+"_"+i+".jpg", original.rowRange(points.get(i-1),points.get(i)) );
		}
	}*/

	public List<Integer> run(Mat score) {
		int T = (score.cols()/5);
		double hit = 255.0;				
		
		Mat binary = new Mat(score.rows(),score.cols(),score.type());
		Imgproc.threshold(score, binary, 128, hit, Imgproc.THRESH_BINARY);
		
//		StaffParams staffParams = PreprocessingUtilities.getStaffParams(score);
		PreprocessingUtilities.StaffParams staffParams = new PreprocessingUtilities.StaffParams(5, 30);
		
		Imgproc.threshold(score, binary, 128, hit, Imgproc.THRESH_BINARY_INV);
		
//		/* */ Highgui.imwrite("/home/jcalvo/Escritorio/1.jpg", binary);
		
		Mat kernel_1 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(10,1) );		
		Imgproc.erode(binary, binary, kernel_1);
		
//		/* */ Highgui.imwrite("/home/jcalvo/Escritorio/2.jpg", binary);
				
		Mat kernel_2 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(1,50) );
		Imgproc.dilate(binary, binary, kernel_2);
		
//		/* */ Highgui.imwrite("/home/jcalvo/Escritorio/3.jpg", binary);
		
		// horizontal histogram
		int [] hhist = new int[binary.rows()];
		
		for(int i = 0; i < binary.rows(); i++) {
			for(int j = 0; j < binary.cols(); j++) {
				if(binary.get(i, j)[0] == hit) {
					hhist[i] ++;
				}
			}
		}
		
		// smooth histogram
		int smoothWindow = 20;
		for(int i = smoothWindow; i < hhist.length-smoothWindow; i++) {
			int v = 0;
			
			for(int j = -smoothWindow; j < smoothWindow; j++) {
				v += hhist[i+j]/(1 + smoothWindow*2); 
			}
			
			hhist[i] = v;
		}
		
		// Cut
		List<Integer> points = new ArrayList<Integer>();
		
		boolean search = true;
		int start = 0;
		for(int i = 0; i < hhist.length; i++) {
			if(hhist[i] > T && search && (i-start) > staffParams.spacing*4) {
				search = false;
				points.add(start + (i-start)/2);
			} else if(hhist[i] < T && !search) {				
				search = true;
				start = i;
			}
		}
		
		if(search && hhist.length-1-start > staffParams.spacing*4) {
			points.add(start + (hhist.length-1-start)/2);
		}
		
		//int [] centers = { 250 , 547, 845, 1100, 1411, 1700 };

        Logger.getLogger(StaffSplitting.class.getName()).log(Level.INFO, "Found {0} staves", points.size());
		return points;
	}
}
