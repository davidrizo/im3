package es.grfia.hmm.score.preprocessing;

//import org.opencv.core.Mat;
//import org.opencv.core.Point;
//import org.opencv.core.Scalar;

// Removed for avoiding interactions of this OpenCV with Keras in MuRET
public class Pre_Straight_StablePaths {
	/*public static int kRIGHT = 15;
	public static int kLEFT = 15;
	
	public static class StablePath {
		public List<Point> points;
		
		public StablePath() {
			points = new ArrayList<Point>();
		}		
	}

	public Mat run(Mat input, String stable_paths_file) {
		List<StablePath> paths = getStablePaths(stable_paths_file); 
		
		Mat output = new Mat(input.rows(),input.cols()-kRIGHT-kLEFT,input.type());
		output.setTo(Scalar.all(255.0));
		
		
		int [] directions = new int[paths.size()];		
		int decision = 0;		
		for(int col = kLEFT; col < input.cols()-kRIGHT; col++) {
			
			double acc_dev = 0;
			for(int i = 0; i < paths.size(); i++) {
				directions[i] += (int) (paths.get(i).points.get(col-1).y - paths.get(i).points.get(col-2).y);
				
				acc_dev += directions[i];
			}
			
			
			int current_decision = (int) Math.round(acc_dev/paths.size());
			
			for(int i = 0; i < paths.size(); i++) {
				directions[i] -= current_decision;
			}
			
			decision += current_decision;
			
			// Perform movement
			if(decision <= 0) {
				for(int r = 0; r < Math.abs(decision); r++) {
					output.put(r, col-kLEFT, input.get(0,col));
				}
				
				for(int r = Math.abs(decision); r < input.rows(); r++) {
					output.put(r, col-kLEFT, input.get(r+decision,col));
				}					
			} else if(decision > 0){
				
				for(int r = 0; r < input.rows()-decision; r++) {
					output.put(r, col-kLEFT, input.get(r+decision,col));
				}	
				
				for(int r = input.rows()-decision; r < input.rows(); r++) {
					output.put(r, col-kLEFT, input.get(input.rows()-1,col));
				}			
			}
		}
				
		return output;
	}

	private List<StablePath> getStablePaths(String stable_paths_file) {
		List<StablePath> stablePaths = new ArrayList<StablePath>(5);
		
		File file = new File(stable_paths_file);		
		BufferedReader br = null;		
		try {
			br = new BufferedReader(new FileReader(file));
			
			String line = null;			
			while((line = br.readLine()) != null) {
				StablePath path = new StablePath();
				
				String [] points = line.split(",");
				
				for(int p = 0; p < points.length; p += 2) {
					path.points.add( new Point(	Integer.parseInt(points[p]), 
												Integer.parseInt(points[p+1])) 
									);
				}
				
				stablePaths.add(path);
			}
			
			br.close();
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
		
		
		return stablePaths;	
	}*/
}
