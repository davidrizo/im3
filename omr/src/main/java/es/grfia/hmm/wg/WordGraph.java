package es.grfia.hmm.wg;

import java.util.ArrayList;
import java.util.List;

public class WordGraph {
	public class WGEdge {
		int idx;
		int start;
		int end;
		String word;
		int explanation;
		double logacoustic;
		double loglanguage;	
		
		public WGEdge(int idx, int start, int end, String word, int explanation, double logacoustic, double loglanguage) {
			this.idx = idx;
			this.start = start;
			this.end = end;
			this.word = word;
			this.explanation = explanation;
			this.logacoustic = logacoustic;
			this.loglanguage = loglanguage;		
		}
	}
	
	public class WGState {
		private String label;
		private ArrayList<WGEdge> edges;

		public WGState(String label) {
			this.label = label;
			this.edges = new ArrayList<WGEdge>();
		}

		public void addEdge(int idx, int start, int end, String word,
				int explanation, double logacoustic, double loglanguage) {
			edges.add( new WGEdge(idx,start,end,word,explanation,logacoustic,loglanguage) );
		}

		public List<WGEdge> getEdges() {
			return edges;
		}
	}
	
	private WGState[] states;
	
	public WordGraph(int numStates) {
		states = new WGState[numStates];
	}
	
	public void addState(int idx, String label) {
		states[idx] = new WGState(label);
	}

	public void addEdge(int idx, int start, int end, String word,
			int explanation, double logacoustic, double loglanguage) {
		states[start].addEdge(idx,start,end,word,explanation,logacoustic,loglanguage);		
	}

	public int getNumStates() {
		return states.length;
	}

	public WGState getState(int idx) {
		return states[idx];
	}

	public boolean isFinalState(int idx) {
		return states[idx].edges.size() == 0;
	}
}
