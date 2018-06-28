package es.grfia.hmm.segmentation.lm.poc;

import es.grfia.hmm.segmentation.lm.LM;
import es.grfia.hmm.segmentation.lm.SegmentationLM;

public class CoS58 {

	public static void main(String[] args) {
		LM lm = SegmentationLM.getCoS59860_Amalgation12();
		
		System.out.println(lm.toHTK());
		
	}

}
