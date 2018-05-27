package es.grfia.hmm.score.features;

import java.util.ArrayList;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Range;
import org.opencv.core.Rect;
import es.grfia.hmm.utils.Utils;

public class PuginExtraction implements FeatureExtraction {
	private Mat getStaffMap(Mat staff) {
		Mat mask = new Mat(staff.rows(),staff.cols(),CvType.CV_8S);
		
		final int windowSpan = 45;
		
		for(int w = windowSpan; w < staff.cols()-windowSpan-1; w++) {
			
			Mat range = staff.colRange(w-windowSpan,w+windowSpan);
			
			int [] wHist = new int[range.rows()];
			
			for(int i = 0; i < range.rows(); i++) {
				for(int j = 0; j < range.cols(); j++) {
					wHist[i] += (Utils.isInk(range.get(i, j)[0]) ? 1 : 0);
				}
			}
			
			int [] five_peaks = Utils.getPeaks(wHist,5,20);
			
			for(int peak : five_peaks) {				
				if(peak-1 >= 0) mask.put(peak-1,w, 1);
				mask.put(peak,w, 1);
				if(peak+1 < mask.rows()) mask.put(peak+1,w, 1);				
			}
		}
		
		// Fill margins
		for(int c = windowSpan-1; c >= 0; c--) {
			for(int r = 0; r < mask.rows(); r++) {
				mask.put(r,c, mask.get(r,c+1) );
			}
		}
		
		for(int c = staff.cols()-windowSpan-1; c < staff.cols(); c++) {
			for(int r = 0; r < mask.rows(); r++) {
				mask.put(r,c, mask.get(r,c-1) );
			}
		}
		
		return mask;
	}

	private int w = 2;
	private boolean overlap = false;
	
	@Override
	public double[][] getFeatures(Mat image) {			
		Mat staffMask = getStaffMap(image);
		
		if(overlap) {
			int frames = image.cols()-w+1;
			
			double [][] features = new double[frames][6];
			
			for(int i = 0; i < frames; i++) {
				Mat frame = image.colRange(new Range( i, i+w ));		
				features[i] = getFrameFeatures( frame , staffMask.colRange( i, i+w) );
			}
			
			return features;
			
		} else {
			int frames = image.cols()/w;
			
			double [][] features = new double[frames][6];
			
			for(int i = 0; i < frames; i++) {
				Mat frame = image.colRange(new Range( i*w, (i+1)*w ));		
				features[i] = getFrameFeatures( frame , staffMask.colRange( i*w, (i+1)*w ) );
			}
			
			return features;
		}
	}

	private double[] getFrameFeatures(Mat frame, Mat staffMask) {
		double h = frame.rows();
		double S = h * w;
		double A = 0;

		ArrayList< ArrayList<Point> > black_blobs = getBlobs(frame,Utils.getInkValue());
		
		ArrayList< ArrayList<Point> > white_blobs = getBlobs(frame,Utils.getBackgroundValue());		
		
		double Cx = 0, Cy = 0;
		
		double maxBlackArea = 0;
		for(ArrayList<Point> blob : black_blobs) {	
			A += blob.size();
			
			double a = blob.size();
			double cix = 0;
			double ciy = 0;
			
			for(Point point : blob) {
				cix += point.x;
				ciy += point.y;				
			}
			
			Cx += cix;
			Cy += ciy;
			maxBlackArea = Math.max(a,maxBlackArea);			
		}
						
		
		Cx = Cx / (A * w);
		Cy = Cy / (A * h);
	    
		double minWhiteArea = Integer.MAX_VALUE;
		for(ArrayList<Point> blob : white_blobs) {
			minWhiteArea = Math.min( minWhiteArea , blob.size() );
		}

		double Ap = 0;
		double M = 0;
		for(int i = 0; i < frame.rows(); i++) {
			for(int j = 0; j < frame.cols(); j++) {
				Ap += (Utils.isInk(frame.get(i, j)[0]) ? 1 : 0) * staffMask.get(i, j)[0];
				M += staffMask.get(i, j)[0];
			}
		}
	    
	    double f1, f2, f3, f4, f5, f6;	    
	    f1 = 1.0 / (1 + black_blobs.size());	    
	    f2 = (Cx == 0 || A == 0) ? 0.5 : 1.0 / Cx;
	    f3 = (Cy == 0 || A == 0) ? 0.5 : 1.0 / Cy;
	    f4 = maxBlackArea / S;
	    f5 = minWhiteArea / S;	    
	    f6 = Ap / M;
	    
	    
	    double [] features = { f1 , f2 , f3 , f4 , f5, f6 };

	    return features;
	}

	private ArrayList<ArrayList<Point>> getBlobs(Mat frame, double value) {		
		Mat label_image = frame.clone();		 
		
		ArrayList< ArrayList<Point> > blobs = new ArrayList<ArrayList<Point>>();
		
		int label_count = 2;

	    for(int y = 0; y < frame.rows(); y++) {
	        for(int x = 0; x < frame.cols(); x++) {
	            if(label_image.get(y,x)[0] != value) {
	                continue;
	            }
	 
	            Rect rect = floodFill(label_image, y, x, label_count);
	            
	            ArrayList<Point> blob = new ArrayList<Point>();	            

	            for(int i=rect.y; i < (rect.y+rect.height); i++) {
	                for(int j=rect.x; j < (rect.x+rect.width); j++) {
	                    if(label_image.get(i,j)[0] != label_count) {
	                        continue;
	                    }
	 
	                    blob.add(new Point(j,i));
	                }
	            }

	            blobs.add(blob);
	 
	            label_count++;
	        }
	    }
	    
	    return blobs;
	}

	
	// Rellena label_image de label_count empezando por (y,x) y devuelve el bounding box
	private Rect floodFill(Mat label_image, int y, int x, int label_count) {
		double value = label_image.get(y, x)[0];
		int rows = label_image.rows();
		int cols = label_image.cols();
		
		int xi = cols;
		int yi = rows;
		int xf = 0;
		int yf = 0;
				
		
		ArrayList<Point> points = new ArrayList<Point>();
		points.add(new Point(y,x));
		label_image.put(y, x, label_count );	
		
		while(!points.isEmpty()) {
			Point p = points.remove(0);
			
			int cx = (int) p.y;
			int cy = (int) p.x;
			
			xi = Math.min(xi, cx);
			xf = Math.max(xf, cx);
			yi = Math.min(yi, cy);
			yf = Math.max(yf, cy);
			

			if(cx+1 < cols) {
				double cvalue = label_image.get(cy,cx+1)[0];
				if(cvalue == value) {
					label_image.put(cy, cx+1, label_count );
					points.add(new Point(cy, cx+1));
				}
			}

			if(cx-1 >= 0) {
					double cvalue = label_image.get(cy,cx-1)[0];
					if(cvalue == value) {
						label_image.put(cy, cx-1, label_count );
						points.add(new Point(cy,cx-1));
					}				
			}
			if(cy-1 >= 0) { 
					double cvalue = label_image.get(cy-1,cx)[0];
					if(cvalue == value) {
						label_image.put(cy-1, cx, label_count );
						points.add(new Point(cy-1,cx));
					}		
			}
			if(cy+1 < rows) {
				double cvalue = label_image.get(cy+1,cx)[0];
				if(cvalue == value) {
					label_image.put(cy+1, cx, label_count );
					points.add(new Point(cy+1,cx));
				}
			}
						
		}
		
		return new Rect(xi, yi, xf-xi+1, yf-yi+1);
	}

}
