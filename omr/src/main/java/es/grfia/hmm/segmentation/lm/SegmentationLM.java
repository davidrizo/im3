package es.grfia.hmm.segmentation.lm;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import es.grfia.hmm.segmentation.lm.LM.LMEdge;
import es.grfia.hmm.segmentation.lm.LM.LMState;
import es.grfia.hmm.segmentation.lm.SegmentationLM.LABEL;

public class SegmentationLM {
	public static enum LABEL {
		TLINE, // TITLE LINE
		BSPACE, // BLANK SPACE
		BFINAL, // BLANK FINAL
		PLINE, // PENTAGRAM LINE
		PLASC, // PENTAGRAM LINE WITH ASCENDENT NOTES
		PLDESC, // PENTAGRAM LINE WITH DESCENDENT NOTES
		PLDESCASC, // PENTAGRAM LINE WITH ASCENDENT AND DESCENDENT NOTES
		EMPTYP, // EMPTY PENTAGRAM
		WORDLINE,	// WORD LINE	
		SHORTWL,
		iNULL; // SHORT WORD LINE			
	}
	
	public static LM getCoS59860() {
		LM coslm = new LM();
		
		LMState state_initial = new LMState(LABEL.iNULL,coslm);
		coslm.initial = state_initial;
		
		LMState state_iBS = new LMState(LABEL.BSPACE,coslm);
		LMState state_iTL = new LMState(LABEL.TLINE,coslm);
		
		state_initial.addEdge(state_iBS);
		state_initial.addEdge(state_iTL);
				
		List<LMState> plEdges = getPLEdges(coslm);
		LMState ep = new LMState(LABEL.EMPTYP,coslm);
		
		state_iBS.addEdges(plEdges);
		state_iBS.addEdge(ep);
		
		state_iTL.addEdges(plEdges);
		state_iTL.addEdge(ep);
		
		LMState last_EP = ep;
		List<LMState> last_PLs = plEdges; 
		
		// Loop 
		for(int i = 0; i < 5; i++) {	
			List<LMState> WLs = getWLEdges(coslm);		
			LMState next_EP = new LMState(LABEL.EMPTYP,coslm);
			List<LMState> next_PLs = getPLEdges(coslm);
			
			LMState bs_ep = new LMState(LABEL.BSPACE,coslm);
			LMState bs_wl = new LMState(LABEL.BSPACE,coslm);
			
			for(LMState state : last_PLs) {
				for(LMState state_wl : WLs) {
					state.addEdge(state_wl);
				}
			}
			
			for(LMState state_wl : WLs) {
				state_wl.addEdges(next_PLs);
				state_wl.addEdge(bs_wl);
			}
			
			bs_wl.addEdges(next_PLs);
			
			last_EP.addEdge(bs_ep);
			bs_ep.addEdge(next_EP);
			
			
			//
			last_EP = next_EP;
			last_PLs = next_PLs;					
		}
		
		LMState bf = new LMState(LABEL.BFINAL,coslm);
		
		List<LMState> wls = getWLEdges(coslm);
		LMState bs_ep = new LMState(LABEL.BSPACE, coslm);
		
		for(LMState state : last_PLs) {
			for(LMState state_wl : wls) {
				state.addEdge(state_wl);
				state_wl.addEdge(bf);
			}
		}
		
		last_EP.addEdge(bs_ep);
		last_EP.addEdge(bf);
		bs_ep.addEdge(bf);		
		
		return coslm;
	}
	
	public static LM getCoS59860_Amalgation1() {
		LM coslm = new LM();
		
		LMState state_initial = new LMState(LABEL.iNULL,coslm);
		coslm.initial = state_initial;
		
		LMState state_iBS = new LMState(LABEL.BSPACE,coslm);
		LMState state_iTL = new LMState(LABEL.TLINE,coslm);
		
		state_initial.addEdge(state_iBS);
		state_initial.addEdge(state_iTL);
				
		List<LMState> plEdges = getPLEdge(coslm);
		LMState ep = new LMState(LABEL.EMPTYP,coslm);
		
		state_iBS.addEdges(plEdges);
		state_iBS.addEdge(ep);
		
		state_iTL.addEdges(plEdges);
		state_iTL.addEdge(ep);
		
		LMState last_EP = ep;
		List<LMState> last_PLs = plEdges; 
		
		// Loop 
		for(int i = 0; i < 5; i++) {	
			List<LMState> WLs = getWLEdges(coslm);		
			LMState next_EP = new LMState(LABEL.EMPTYP,coslm);
			List<LMState> next_PLs = getPLEdge(coslm);
			
			LMState bs_ep = new LMState(LABEL.BSPACE,coslm);
			LMState bs_wl = new LMState(LABEL.BSPACE,coslm);
			
			for(LMState state : last_PLs) {
				for(LMState state_wl : WLs) {
					state.addEdge(state_wl);
				}
			}
			
			for(LMState state_wl : WLs) {
				state_wl.addEdges(next_PLs);
				state_wl.addEdge(bs_wl);
			}
			
			bs_wl.addEdges(next_PLs);
			
			last_EP.addEdge(bs_ep);
			bs_ep.addEdge(next_EP);
			
			
			//
			last_EP = next_EP;
			last_PLs = next_PLs;					
		}
		
		LMState bf = new LMState(LABEL.BFINAL,coslm);
		
		List<LMState> wls = getWLEdges(coslm);
		LMState bs_ep = new LMState(LABEL.BSPACE, coslm);
		
		for(LMState state : last_PLs) {
			for(LMState state_wl : wls) {
				state.addEdge(state_wl);
				state_wl.addEdge(bf);
			}
		}
		
		last_EP.addEdge(bs_ep);
		last_EP.addEdge(bf);
		bs_ep.addEdge(bf);		
		
		return coslm;
	}
	
	
	public static LM getCoS59860_Amalgation2() {
		LM coslm = new LM();
		
		LMState state_initial = new LMState(LABEL.iNULL,coslm);
		coslm.initial = state_initial;
		
		LMState state_iBS = new LMState(LABEL.BSPACE,coslm);
		LMState state_iTL = new LMState(LABEL.TLINE,coslm);
		
		state_initial.addEdge(state_iBS);
		state_initial.addEdge(state_iTL);
				
		List<LMState> plEdges = getPLEdges(coslm);
		LMState ep = new LMState(LABEL.EMPTYP,coslm);
		
		state_iBS.addEdges(plEdges);
		state_iBS.addEdge(ep);
		
		state_iTL.addEdges(plEdges);
		state_iTL.addEdge(ep);
		
		LMState last_EP = ep;
		List<LMState> last_PLs = plEdges; 
		
		// Loop 
		for(int i = 0; i < 5; i++) {	
			List<LMState> WLs = getWLEdge(coslm);		
			LMState next_EP = new LMState(LABEL.EMPTYP,coslm);
			List<LMState> next_PLs = getPLEdges(coslm);
			
			LMState bs_ep = new LMState(LABEL.BSPACE,coslm);
			LMState bs_wl = new LMState(LABEL.BSPACE,coslm);
			
			for(LMState state : last_PLs) {
				for(LMState state_wl : WLs) {
					state.addEdge(state_wl);
				}
			}
			
			for(LMState state_wl : WLs) {
				state_wl.addEdges(next_PLs);
				state_wl.addEdge(bs_wl);
			}
			
			bs_wl.addEdges(next_PLs);
			
			last_EP.addEdge(bs_ep);
			bs_ep.addEdge(next_EP);
			
			
			//
			last_EP = next_EP;
			last_PLs = next_PLs;					
		}
		
		LMState bf = new LMState(LABEL.BFINAL,coslm);
		
		List<LMState> wls = getWLEdge(coslm);
		LMState bs_ep = new LMState(LABEL.BSPACE, coslm);
		
		for(LMState state : last_PLs) {
			for(LMState state_wl : wls) {
				state.addEdge(state_wl);
				state_wl.addEdge(bf);
			}
		}
		
		last_EP.addEdge(bs_ep);
		last_EP.addEdge(bf);
		bs_ep.addEdge(bf);		
		
		return coslm;
	}
	
	public static LM getCoS59860_Amalgation12() {
		LM coslm = new LM();
		
		LMState state_initial = new LMState(LABEL.iNULL,coslm);
		coslm.initial = state_initial;
		
		LMState state_iBS = new LMState(LABEL.BSPACE,coslm);
		LMState state_iTL = new LMState(LABEL.TLINE,coslm);
		
		state_initial.addEdge(state_iBS);
		state_initial.addEdge(state_iTL);
				
		List<LMState> plEdges = getPLEdge(coslm);
		LMState ep = new LMState(LABEL.EMPTYP,coslm);
		
		state_iBS.addEdges(plEdges);
		state_iBS.addEdge(ep);
		
		state_iTL.addEdges(plEdges);
		state_iTL.addEdge(ep);
		
		LMState last_EP = ep;
		List<LMState> last_PLs = plEdges; 
		
		// Loop 
		for(int i = 0; i < 5; i++) {	
			List<LMState> WLs = getWLEdge(coslm);		
			LMState next_EP = new LMState(LABEL.EMPTYP,coslm);
			List<LMState> next_PLs = getPLEdge(coslm);
			
			LMState bs_ep = new LMState(LABEL.BSPACE,coslm);
			LMState bs_wl = new LMState(LABEL.BSPACE,coslm);
			
			for(LMState state : last_PLs) {
				for(LMState state_wl : WLs) {
					state.addEdge(state_wl);
				}
			}
			
			for(LMState state_wl : WLs) {
				state_wl.addEdges(next_PLs);
				state_wl.addEdge(bs_wl);
			}
			
			bs_wl.addEdges(next_PLs);
			
			last_EP.addEdge(bs_ep);
			bs_ep.addEdge(next_EP);
			
			
			//
			last_EP = next_EP;
			last_PLs = next_PLs;					
		}
		
		LMState bf = new LMState(LABEL.BFINAL,coslm);
		
		List<LMState> wls = getWLEdge(coslm);
		LMState bs_ep = new LMState(LABEL.BSPACE, coslm);
		
		for(LMState state : last_PLs) {
			for(LMState state_wl : wls) {
				state.addEdge(state_wl);
				state_wl.addEdge(bf);
			}
		}
		
		last_EP.addEdge(bs_ep);
		last_EP.addEdge(bf);
		bs_ep.addEdge(bf);		
		
		return coslm;
	}

	private static List<LMState> getWLEdges(LM lm) {
		return Arrays.asList(
			new LMState(LABEL.WORDLINE, lm),
			new LMState(LABEL.SHORTWL, lm)
		);
	}
	
	private static List<LMState> getPLEdges(LM lm) {
		return Arrays.asList(
			new LMState(LABEL.PLINE, lm),
			new LMState(LABEL.PLASC, lm),
			new LMState(LABEL.PLDESC, lm),
			new LMState(LABEL.PLDESCASC, lm)
		);
	}
	
	private static List<LMState> getWLEdge(LM lm) {
		return Arrays.asList(
			new LMState(LABEL.WORDLINE, lm)
		);
	}
	
	private static List<LMState> getPLEdge(LM lm) {
		return Arrays.asList(
			new LMState(LABEL.PLINE, lm)
		);
	}
}
