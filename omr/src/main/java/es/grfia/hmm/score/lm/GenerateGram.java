package es.grfia.hmm.score.lm;

import java.util.List;

public class GenerateGram {
	public static void main(String [] args) {
		List<String> words = WordList.getList();
		
		for(String str : words) {
			System.out.println(str);
		}				
	}
	
	
}
