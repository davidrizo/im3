package es.grfia.hmm.score.lm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class WordList {	
	public enum Symbols {		
		// Common
		WBCOLOUREDFUSA,
		WCOLOUREDFUSA,
		WFUSA,
		WCOLOUREDSEMIMINIMA,
		WBCOLOUREDSEMIMINIMA,
		WSEMIMINIMA,
		WMINIMA,
		WCOLOUREDMINIMA,
		WSEMIBREVIS,
		WCOLOUREDSEMIBREVIS,
		WBREVIS,
		WCOLOUREDBREVIS,
		
		// Accidentals
		WSHARP,
		WFLAT,
		
		// Clefs
		WGCLEF,
		WCCLEF,
		WFCLEF,
		
		// Rest
		WLONGAREST,
		WBREVISREST,
		WSEMIBREVISREST,
		WMINIMAREST,
		WSEMIMINIMAREST,
		
		// Others
		WCUSTOS,
		WDOT,
		WBARLINE,
		WVOID,
		WSMUDGE			
	}
	
	private static HashMap<Symbols,List<String>> pitches;
	
	static {
		pitches = new HashMap<Symbols,List<String>>();
		
		// All pitches considered
		List<String> allPitches = new ArrayList<String>();
		for(int i = -2; i <= 5; i++) {
			allPitches.add(""+int2pos(i)+"");
			allPitches.add(""+int2pos(i)+""+int2pos(i+1)+"");
		}
		
		// Common		
		pitches.put(Symbols.WBCOLOUREDFUSA,allPitches);
		pitches.put(Symbols.WCOLOUREDFUSA,allPitches);
		pitches.put(Symbols.WFUSA,allPitches);
		pitches.put(Symbols.WCOLOUREDSEMIMINIMA,allPitches);
		pitches.put(Symbols.WBCOLOUREDSEMIMINIMA,allPitches);
		pitches.put(Symbols.WSEMIMINIMA,allPitches);
		pitches.put(Symbols.WMINIMA,allPitches);
		pitches.put(Symbols.WCOLOUREDMINIMA,allPitches);
		pitches.put(Symbols.WSEMIBREVIS,allPitches);
		pitches.put(Symbols.WCOLOUREDSEMIBREVIS,allPitches);
		pitches.put(Symbols.WBREVIS,allPitches);
		pitches.put(Symbols.WCOLOUREDBREVIS,allPitches);
		
		// Accidentals		
		pitches.put(Symbols.WSHARP,allPitches);
		pitches.put(Symbols.WFLAT,allPitches);
		
		// Clefs
		pitches.put(Symbols.WGCLEF, Arrays.asList("5") );
		pitches.put(Symbols.WCCLEF, Arrays.asList("3") );
		pitches.put(Symbols.WCCLEF, Arrays.asList("2") );
		pitches.put(Symbols.WFCLEF, Arrays.asList("2") );
		
	}

	private static String int2pos(int i) {
		return (i < 0 ? "m" : "") + Math.abs(i);
	}
	
	public static List<String> getList() {
		List<String> words = new ArrayList<String>();
		
		for(Symbols symbol : pitches.keySet()) {
			for(String pitch : pitches.get(symbol)) {
				
				if(pitch.equals("")) {
					words.add( symbol.toString() );
				} 
				else {
					words.add( symbol.toString() + "p" + pitch );
				}
			}
		}
		
		return words;
	}
	
}
