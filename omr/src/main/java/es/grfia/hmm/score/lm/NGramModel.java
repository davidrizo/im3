package es.grfia.hmm.score.lm;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class NGramModel implements LanguageModel {
	private String kInternalSeparator = ",";
	private String kExternalSeparator = " ";
	private int n = 0;
	
	private HashMap<String,Integer> counts;	
	private HashMap<String,Double> probs;
	private List<NGramModel> auxiliar;
	
	public NGramModel(int n) {
		this.n = n;
	}

	public NGramModel(int n, List<NGramModel> auxiliar) {
		this.n = n;
		this.auxiliar = auxiliar;
	}
	
	public int getN() {
		return n;
	}
	
	public void learn(List<String> samples) {
		this.counts = new HashMap<String,Integer>();
		this.probs = new HashMap<String,Double>();
		
		count(samples);		
		normalize();				
	}

	private void normalize() {
		if(n == 0) {
			Set<String> keySet = counts.keySet();
			for(String str : keySet) {
				probs.put(str, 1.0/keySet.size());
			}
		} 
		else if(n == 1) {
			Set<String> keySet = counts.keySet();
			double totalCount = keySet.size();
			
			for(String str : keySet) {
				totalCount += counts.get(str);
			}
			
			for(String str : keySet) {
				probs.put(str, counts.get(str)/totalCount );
			}
			
		} else {
			Set<String> keySet = counts.keySet();
			
			for(String str : keySet) {
				double countPrefix = 0;
				
				for(String str2 : keySet) {
					if(isPrefixOf(str,str2)) {
						countPrefix += counts.get(str2);
					}
				}
				
				probs.put(str, counts.get(str)/countPrefix);
			}			
		}
		
	}

	private boolean isPrefixOf(String str, String prefix) {
		String [] strA = str.split(kInternalSeparator);
		String [] strB = prefix.split(kInternalSeparator);
		
		
		for(int k = 0; k < strA.length-1; k++) {
			if(!strA[k].equals(strB[k]))
				return false;			
		}
		return true;
	}

	private void count(List<String> samples) {
		for(String str : samples) {
			String [] sequence = str.split(kExternalSeparator);
			
			if(n == 0) {
				for(String w : sequence) {					
					counts.put(w,1);					
				}					
			} 
			else if (n == 1) {
				for(String w : sequence) {
					Integer c = counts.get(w);
					if(c == null) {
						counts.put(w,1);
					} else {
						counts.put(w, c+1);
					}
				}					
			} else {
				String [] prev = new String[n-1];
				Arrays.fill(prev, "#");
				
				for(int i = 0; i < sequence.length; i++) {
					String word = "";
					for(int k = 0; k < prev.length; k++) {
						word += prev[k] + kInternalSeparator;
					}
					word += sequence[i];
					
					Integer c = counts.get(word); 
					if(c == null) {
						counts.put(word,1);
					} else {
						counts.put(word,c+1);
					}
					
					for(int k = 0; k < prev.length-1; k++) {
						prev[k] = prev[k+1];
					}
					prev[n-2] = sequence[i];
				}
			}
		}
	}

	public double perplexityOf(String sample) {
		String [] sequence = sample.split(kExternalSeparator);
		
		if(n == 0 || n == 1) {
			double accumulated = 0;			
			int N = sequence.length;
						
			for(String w : sequence) {		
				accumulated += Math.log( getProfOf(w,n) );				
			}
			
			return Math.exp( -1.0/N * accumulated );
			
		} else {
			double accumulated = 0;			
			int N = sequence.length;
			
			String [] prev = new String[n-1];
			Arrays.fill(prev, "#");
			
			for(int i = 0; i < sequence.length; i++) {
				String word = "";
				for(int k = 0; k < prev.length; k++) {
					word += prev[k] + kInternalSeparator;
				}
				word += sequence[i];
				
				accumulated += Math.log( getProfOf(word,n) );
			
				
				for(int k = 0; k < prev.length-1; k++) {
					prev[k] = prev[k+1];
				}
				prev[n-2] = sequence[i];
			}
			
			return Math.exp( -1.0/N * accumulated );
		}
	}

	private double getProfOf(String word, int level) {
		if(level == 0) {
			return 1.0/300;
		} else if(level == n) {
			Double p = probs.get(word);
			if(p == null) {
				return getProfOf(word,level-1);
			} else {
				return p;
			}			
		} else {
			String [] w = word.split(kInternalSeparator);
			
			String nword = null;
			
			for(int k = 1; k < w.length; k++) {
				if(nword == null) {
					nword = w[k];
				} else {
					nword += kInternalSeparator + w[k];
				}
			}
			
			Double p = null;
			for(NGramModel aux : auxiliar) {
				if(aux.getN() == level) {
					p = aux.probs.get(nword);
					break;
				}
			}
			
			if(p == null) {
				return getProfOf(nword,level-1);
			} else {
				return p;
			}
		}
	}

	public void dumpProbs() {
		for(String w : probs.keySet()) { 
			System.out.println(w + "\t" + probs.get(w));
		}
	}

	public void dumpCounts() {
		for(String w : counts.keySet()) { 
			System.out.println(w + "\t" + counts.get(w));
		}
	}

}
