package es.grfia.hmm.score.lm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NGrams {
	public static void main(String [] args) {
		List<String> sequences = readSequences("/home/jcalvo/Escritorio/Local/HMR/PoC/LM/Transcripts.txt");
		
		System.out.println(sequences.size() + " sequences read.");
		
		List<String> trainSequences = new ArrayList<String>();
		List<String> testSequences = new ArrayList<String>();
		
		for(String str : sequences) {
			if(Math.random() < 0.7) {
				trainSequences.add(str);
			} else {
				testSequences.add(str);
			}
		}
				
		// Learning		
		int N = 2;
		List<NGramDecoupledModel> models = new ArrayList<NGramDecoupledModel>(N);
		
		for(int i = 0; i <= N; i++) {
			NGramDecoupledModel ngram = learnDecoupledGram(trainSequences,i,models);
									
			models.add(ngram);
			
			System.out.println(i+"-gram mean perplexity: " + measurePerplexity(testSequences,ngram));					
		}
		
	}

	private static NGramDecoupledModel learnDecoupledGram(
			List<String> sequences, int n, List<NGramDecoupledModel> auxiliar) {
		NGramDecoupledModel ngram = new NGramDecoupledModel(n, auxiliar);		
		ngram.learn(sequences);
		return ngram;	
	}

	private static double measurePerplexity(List<String> sequences,	LanguageModel ngram) {				
		double accumulated = 0;
		int N = sequences.size();
		
		for(String sequence : sequences) {
			accumulated += ngram.perplexityOf(sequence);
		}
		
		return accumulated/N;
	}

	private static NGramModel learnGram(List<String> sequences, int n, List<NGramModel> auxiliar) {
		NGramModel ngram = new NGramModel(n, auxiliar);		
		ngram.learn(sequences);
		return ngram;		
	}

	private static List<String> readSequences(String filename) {
		List<String> sequences = new ArrayList<String>();
		
		try {			
			BufferedReader br = new BufferedReader(new FileReader(new File(filename)));
			
			String line = null;
			
			while( (line = br.readLine()) != null) {				
				sequences.add(line);				
			}			
		
			br.close();
		
		} catch (Exception e) {
			System.err.println("Error reading " + filename + " : " + e.getMessage());
		}
		
		return sequences;
	}
}
