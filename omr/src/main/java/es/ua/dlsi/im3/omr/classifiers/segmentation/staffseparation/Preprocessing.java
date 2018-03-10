package es.ua.dlsi.im3.omr.classifiers.segmentation.staffseparation;

import java.util.ArrayList;
import java.util.Arrays;

import org.opencv.core.Core;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

/**
 * Modified by drizo
 * @author jcalvo
 */

public class Preprocessing {
	/*public static void main(String [] args) {
		System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
		
		// PoC				
		Mat staff = Highgui.imread("/home/jcalvo/Escritorio/HMM/Preprocessing/data/b1.jpg",Highgui.IMREAD_GRAYSCALE);		
		Imgproc.threshold(staff, staff, 128, 255.0, Imgproc.THRESH_BINARY);
		
		Preprocessing preprocessing = new Preprocessing();
		Mat prepared = preprocessing.run(staff);
		
		Highgui.imwrite("/home/jcalvo/Escritorio/prepared.jpg", prepared);		
	}
	*/

	public class StaffSegment {
		public int start;
		public int end;
		
		public StaffSegment(int start, int end) {
			this.start = start;
			this.end = end;
		}

		public double distance(StaffSegment segment) {
			double cpos = getMiddle();
			double opos = segment.getMiddle();
			
			return cpos - opos;
		}

		private double getMiddle() {
			return start + ((end-start)/2.0);
		}

	}

	private Mat run(Mat input) {
		PreprocessingUtilities.StaffParams params = PreprocessingUtilities.getStaffParams(input);
		int [] staffParams = { params.thickness , params.spacing };

		Mat staff = runlengthThresholding(input,staffParams);
		
		double [] orientations = getOrientations(staff,20,3);
		
		double row = staff.rows()/2; 
			
		for(int col = 0; col < 200; col++) {
			int srow = (int) Math.round(row);
			
			staff.put(srow, col, 128.0);
			
			row += orientations[col];
		}
		
//		Mat template = getTemplate(staffParams, 1, input.type());
//		
//		Mat matching = applyTemplate(input,template);
		
		return staff;
	}

	private double[] getOrientations(Mat input, int k, int T) {
		ArrayList<StaffSegment> [] columns = new ArrayList[input.cols()];
		
		for(int i = 0; i < input.cols(); i++) {		
			columns[i] = new ArrayList<StaffSegment>();
			
			int start = -1;			
			boolean currentBack = true;
			
			for(int j = 0; j < input.rows(); j++) {					
				boolean isInk = Utils.isInk(input.get(j, i)[0]);
					
				if(isInk) {					
					if(currentBack) {	
						start = j;		

						currentBack = false;
					}					
				} else {					
					if(!currentBack) {
						// Component!
						columns[i].add(new StaffSegment(start,j-1));
						start = -1;		

						currentBack = true;
					}					
				}
			}
			
			if(!currentBack) {
				columns[i].add(new StaffSegment(start,input.rows()-1));
			}
		}
		
		// Loop columns
		double [] O = new double[input.cols()];
		Arrays.fill(O,Double.NaN);
		
		for(int i = 0; i < input.cols()-k; i++) {			
			int p = columns[i].size();
			if(p >= T) { // Skip void columns
				O[i] = 0;
				
				for(StaffSegment segment : columns[i]) {
					double o = 0;
					for(int j = 1; j <= k; j++) {
						double min = Double.NaN;
						for(StaffSegment next : columns[i+j]) {
							double dis = segment.distance(next);
							
							if(Double.isNaN(min)) {
								min = dis;
							} else {
								if(Math.abs(dis) < Math.abs(min)) {
									min = dis;
								}
							}
						}								
						
						if(!Double.isNaN(min)) {
							o += (1/j) * (min);
						}
					}
					
					if(o > 0) {
						boolean stop = true;
					}

					O[i] += o / p;
					
				}
			}
		}
		
		// Where there no are orientation, interpolate them!
		// Search the first and last with value
		int first = input.cols()-1;
		int last = 0;
		for(int i = 0; i < input.cols(); i++) {
			if(!Double.isNaN(O[i])) {
				first = Math.min(first,i);
				last = Math.max(last,i);
			}
		}
		
		// Frm zero to first
		for(int i = 0; i < first; i++) {
			O[i] = 0;
		}
		
		// From last to end
		for(int i = last; i < input.cols(); i++) {
			O[i] = 0;
		}
		
		// Rest
		for(int i = 0; i < input.cols(); i++) {
			if(Double.isNaN(O[i])) {
				O[i] = 0;
			}
		}
						
		return O;
	}

	private Mat runlengthThresholding(Mat input, int[] staffParams) {
		Mat staff = new Mat(input.rows(),input.cols(),input.type());
		input.copyTo( staff );
		  
		int T = Math.min(2*staffParams[0], staffParams[1]+staffParams[0]);
		
		for(int i = 0; i < input.cols(); i++) {						
			int inkcount = 0;
			
			boolean currentBack = true;
			
			for(int j = 0; j < input.rows(); j++) {					
				boolean isInk = Utils.isInk(input.get(j, i)[0]);
					
				if(isInk) {					
					if(currentBack) {	
						inkcount = 1;					
					} else {
						inkcount++;
					}
					
					currentBack = false;
				} else {					
					if(!currentBack) {
						if(inkcount > T) {
							for(int r = j-1; r >= 0 && r >= j-inkcount; r--) {
								staff.put(r,i,Utils.getBackgroundValue());
							}								
						}
						
						inkcount = 0;						
					}
					
					currentBack = true;
				}
			}
			if(!currentBack) {
				if(inkcount > T) {
					for(int r = input.rows()-1; r >= 0 && r >= input.rows()-inkcount; r--) {
						staff.put(r,i,Utils.getBackgroundValue());
					}								
				}
			}
		}
		
		return staff;
	}

	private Mat applyTemplate(Mat img, Mat templ) {
		  /// Source image to display
		  Mat img_display = new Mat(img.rows(),img.cols(),img.type());
		  img.copyTo( img_display );

		  /// Create the result matrix
		  int result_cols =  img.cols() - templ.cols() + 1;
		  int result_rows = img.rows() - templ.rows() + 1;

		  Mat result = new Mat( result_rows, result_cols, CvType.CV_32FC1 );

		  /// Do the Matching and Normalize
		  Imgproc.matchTemplate( img, templ, result, Imgproc.TM_CCORR_NORMED );
		  Core.normalize(result, result);

		  /// Localizing the best match with minMaxLoc		  
		  MinMaxLocResult locResult = Core.minMaxLoc(result);

		  Point matchLoc = locResult.maxLoc;

		  /// Show me what you got
		    //drizo OpenCV2 Core.rectangle( img_display, matchLoc, new Point( matchLoc.x + templ.cols() , matchLoc.y + templ.rows() ), Scalar.all(128), 2, 8, 0 );
            //drizo OpenCV2 Core.rectangle( result, matchLoc, new Point( matchLoc.x + templ.cols() , matchLoc.y + templ.rows() ), Scalar.all(128), 2, 8, 0 );

            //drizo OpenCV3
            Imgproc.rectangle( img_display, matchLoc, new Point( matchLoc.x + templ.cols() , matchLoc.y + templ.rows() ), Scalar.all(128), 2, 8, 0 );
            Imgproc.rectangle( result, matchLoc, new Point( matchLoc.x + templ.cols() , matchLoc.y + templ.rows() ), Scalar.all(128), 2, 8, 0 );
		  return img_display;
	}

	private Mat getTemplate(int[] staffParams, int width, int type) { 
		
		int height = (staffParams[0]+staffParams[1])*5 + staffParams[1] - 1;
		
		Mat template = new Mat(height,width,type);
		template.setTo(new Scalar(Utils.getBackgroundValue()));
		
		int current = staffParams[1];
		for(int i = 0; i < 5; i++) {
			
			for(int j = 0; j < staffParams[0]; j++) {
				
				for(int k = 0; k < width; k++) {
					template.put(current, k, Utils.getInkValue());
				}
				
				current++;
			}
			
			current += staffParams[1];
		}
		
		return template;
	}
}
