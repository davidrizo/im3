package es.grfia.hmm.wg;

import java.util.Arrays;
import java.util.List;

import es.grfia.hmm.wg.WordGraph.WGEdge;
import es.grfia.hmm.wg.WordGraph.WGState;

public class Viterbi implements IDecoding {
	private static final String kSeparator = ";";
	
	public static class ViterbiState {
		String sequence;
		double logprobability;
	}
	
	@Override
	public List<String> decoding(WordGraph wg) {
		int N = wg.getNumStates();
		
		/* Ends when W=!NULL is added to the sequence */		
		ViterbiState [] pd = blankPD(N);
		
		
		pd[0].sequence = "";
		pd[0].logprobability = 0;
		
		String sequence = null;
		double bestprob = Double.NEGATIVE_INFINITY;
		
		while(true) {
			// Check end
			boolean isEnd = true;
			
			for(int i = 0; i < N; i++) {
				if(pd[i].logprobability > Double.NEGATIVE_INFINITY) {
					isEnd = false;
				}
			}
			
			if(isEnd) {
				break;
			}			
			
			// -----------------------------
			
			// Check possible final string			
			for(int i = 0; i < N; i++) {
				if(wg.isFinalState(i) && pd[i].logprobability > bestprob) {
					sequence = pd[i].sequence;
					bestprob = pd[i].logprobability;
				}
			}			
			
			// -----------------------------
			
			// Iterate
			ViterbiState [] next = blankPD(N);
			
			for(int i = 0; i < N; i++) {
				WGState current = wg.getState(i);
				
				for(WGEdge e : current.getEdges()) {
					double prob = pd[e.start].logprobability + e.logacoustic;
									
					if(prob > next[e.end].logprobability) {
						next[e.end].logprobability = prob;
						next[e.end].sequence = pd[e.start].sequence + e.word + kSeparator;
					}								
				}				
			}
			
			pd = next;
		}
		
		return Arrays.asList(sequence.split(kSeparator));
	}

	private ViterbiState[] blankPD(int N) {
		ViterbiState [] pd = new ViterbiState[N];
		
		for(int i = 0; i < N; i++) {
			pd[i] = new ViterbiState();
			pd[i].sequence = null;
			pd[i].logprobability = Double.NEGATIVE_INFINITY;
		}
		
		return pd;
	}

}
