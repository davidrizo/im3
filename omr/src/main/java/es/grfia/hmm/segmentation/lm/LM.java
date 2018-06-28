package es.grfia.hmm.segmentation.lm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import es.grfia.hmm.segmentation.lm.LM.LMState;
import es.grfia.hmm.segmentation.lm.SegmentationLM.LABEL;

public class LM {
	static class LMEdge {
		public LMState destiny;
		
		public LMEdge(LMState destiny) {
			this.destiny = destiny;
		}
	}
	
	public static class LMState {
		public List<LMEdge> edges;
		public LABEL label;
		public int id;
		
		public LMState(LABEL label, LM parent) {
			this.edges = new ArrayList<LMEdge>();
			this.label = label;
			
			parent.addState(this);
		}

		public void addEdge(LMState state) {
			edges.add( new LMEdge(state) );
		}

		public void addEdges(List<LMState> states) {
			for(LMState state : states) {
				addEdge(state);
			}
		}
	}
	

	public List<LMState> states;
	public LMState initial;
	
	public LM() {
		states = new ArrayList<LMState>();	
	}
	
	public void addState(LMState state) {
		states.add(state);		
	}

	public String toHTK() {
		String str = "";
		str += "VERSION=1.0\n";
		int numStates = 0;
		int numEdges = 0;
		
		String strStates = "";		
		String strEdges = "";
				
		for(LMState state : states) {
			state.id = numStates;
			strStates += "I=" + state.id + "\tW=" + state.label + "\n";
			numStates++;
		}
		
		for(LMState state : states) {
			double prob = Math.log(1.0 / state.edges.size());
			
			for(LMEdge edge : state.edges) {
				strEdges += "J=" + numEdges + "\tS=" + state.id + "\tE=" + edge.destiny.id + "\tl=" + prob + "\n";
				numEdges++;
			}
		}
		
		str += "N="+numStates+"\tL="+numEdges+"\n";
		str += strStates + strEdges;
		
		
		return str;
	}
}
