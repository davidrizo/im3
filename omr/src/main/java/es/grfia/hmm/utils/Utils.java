package es.grfia.hmm.utils;

// Removed for avoiding interactions of this OpenCV with Keras in MuRET
/*import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
*/
public class Utils {
	/*private static final double kInk = 0.0;	// Black
	private static final double kBackground = 255.0; // White
	
	public static boolean isInk(double v) {
		return v == kInk;
	}

	public static int[] getPeaks(int[] function, int num_peaks, int threshold) {
		
		Point [] p = new Point[function.length];
		
		for(int i = 0; i < function.length; i++) {
			p[i] = new Point();
			p[i].x = i;
			p[i].y = function[i];
		}
		
		Arrays.sort(p, new Comparator<Point>() {

			@Override
			public int compare(Point p1, Point p2) {
				return Double.compare(p2.y, p1.y);
			}
			
		});
			
		int idx;
		
		ArrayList<Point> currentPeaks = new ArrayList<Point>(5);

		for(idx = 0; idx < p.length && currentPeaks.size() < num_peaks; idx++) {
			
			boolean condition = true;
			for(Point peak : currentPeaks) {
				if(Math.abs(peak.x - p[idx].x) < threshold) {
					condition = false;
					break;
				}
			}
			
			if(condition) {
				currentPeaks.add(p[idx]);
			}				
		}

		
		int [] peaks = new int[num_peaks];
		Arrays.fill(peaks, -1);
		
		for(int i = 0; i < currentPeaks.size(); i++) {
			peaks[i] = (int) currentPeaks.get(i).x;
		}
		
		Arrays.sort(peaks);
		
		return peaks;
	}

	public static double getInkValue() {
		return kInk;
	}

	public static double getBackgroundValue() {
		return kBackground;
	}

	public static void exportFeaturestoHTK(double[][] features, PrintStream out) {
		int numVect = features.length;
		int numParam = features[0].length;
		
		out.println("NumParam\t"+numParam);
		out.println("NumVect\t"+numVect);
		out.println("Data");
		for(int i = 0; i < numVect; i++) {
			for(int j = 0; j < numParam; j++) {
				out.print(features[i][j] + " ");
			}
			out.println();
		}		
	}

	public static Mat correctDistortion(Mat input, 
			Point ul, Point ur,
			Point ll, Point lr) {
		Mat output = new Mat(input.rows(),input.cols(),input.type());
		output.setTo(Scalar.all(128));
		
		Mat src = new Mat(4,1,CvType.CV_32FC2);
		src.put(0,0, new double[] {ul.x,ul.y}); // top left
		src.put(1,0, new double[] {ur.x,ur.y}); // top right
		src.put(2,0, new double[] {lr.x,lr.y}); // bottom right
		src.put(3,0, new double[] {ll.x,ll.y}); // bottom left	
		
		Mat dst = new Mat(4,1,CvType.CV_32FC2);
		dst.put(0,0, new double[] {Math.min(ul.x,ll.x),Math.min(ul.y,ur.y)}); // top left
		dst.put(1,0, new double[] {Math.max(ur.x,lr.x),Math.min(ul.y,ur.y)}); // top right
		dst.put(2,0, new double[] {Math.max(ur.x,lr.x),Math.max(ll.y,lr.y)}); // bottom right
		dst.put(3,0, new double[] {Math.min(ul.x,ll.x),Math.max(ll.y,lr.y)}); // bottom left
		
		Mat M = Imgproc.getPerspectiveTransform(src, dst);
		
		Imgproc.warpPerspective(input, output, M, output.size());
		
		return output;
	}
*/
}
