package es.grfia.hmm.score.preprocessing;

import org.opencv.core.Mat;

import es.grfia.hmm.utils.Utils;

public class PreprocessingUtilities {
	public static class StaffParams {
		public int thickness;
		public int spacing;
		
		public StaffParams(int thickness, int spacing) {
			this.thickness = thickness;
			this.spacing = spacing;
		}
	}
	
	public static StaffParams getStaffParams(Mat input) {
		int [] inkruns = new int[input.rows()+1];
		int [] backruns = new int[input.rows()+1];
		
		for(int i = 0; i < input.cols(); i++) {			
			
			int inkcount = 0;
			int backcount = 0;
			
			boolean currentBack = true;
			
			for(int j = 0; j < input.rows(); j++) {					
				boolean isInk = Utils.isInk(input.get(j, i)[0]);
					
				if(isInk) {					
					if(currentBack) {						
						backruns[backcount]++;
						backcount = 0;					
						
						inkcount = 1;					
					} else {					
						inkcount++;
					}
					
					currentBack = false;
				} else {					
					if(currentBack) {
						backcount++;
					} else {						
						inkruns[inkcount]++;
						inkcount = 0;
						
						backcount = 1;
					}
					
					currentBack = true;
				}
			}
		}
		
		// Get maximums
		int maxInk = 0;
		int maxInkCount = 0;
		
		for(int i = 0; i < inkruns.length; i++) {
			if(inkruns[i] > maxInkCount) {
				maxInkCount = inkruns[i];
				maxInk = i;
			}
		}
		
		int maxBack = 0;
		int maxBackCount = 0;
		
		for(int i = 0; i < backruns.length; i++) {
			if(backruns[i] > maxBackCount) {
				maxBackCount = backruns[i];
				maxBack = i;
			}
		}
		
		return new StaffParams(maxInk, maxBack);
	}
}
