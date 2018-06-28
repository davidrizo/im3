package es.grfia.hmm.score.lm;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NGramDecoupledModel implements LanguageModel {
	private String kInternalSeparator = ",";
	private String kExternalSeparator = " ";
	private String kCoupleSeparator = "\\.";
	private int n = 0;
	
	private HashMap<String,Integer> counts;	
	private HashMap<String,Double> probs;
	private List<NGramDecoupledModel> auxiliar;
	
	private HashMap<String,HashSet<String>> prefixes;
	
	private void init(int n) {
		this.n = n;
		this.prefixes = new HashMap<String,HashSet<String>>();
	}
	
	public NGramDecoupledModel(int n) {
		init(n);
	}

	public NGramDecoupledModel(int n, List<NGramDecoupledModel> auxiliar) {
		init(n);
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
				probs.put(str, 1.0/3000);
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
					String [] word = w.split(kCoupleSeparator);
					
					counts.put(word[0],1);
					
					HashSet<String> set = prefixes.get(word[0]);
					
					if(set == null) {
						set = new HashSet<String>();						
						prefixes.put(word[0],set);
					}
					
					if(word.length > 1) {
						set.add(word[1]);
					} else {
						set.add("");
					}
				}					
			} 
			else if (n == 1) {
				for(String w : sequence) {
					String [] word = w.split(kCoupleSeparator);
					
					Integer c = counts.get(word[0]);
					if(c == null) {
						counts.put(word[0],1);
					} else {
						counts.put(word[0], c+1);
					}
					
					HashSet<String> set = prefixes.get(word[0]);
					
					if(set == null) {
						set = new HashSet<String>();						
						prefixes.put(word[0],set);
					}
					
					if(word.length > 1) {
						set.add(word[1]);
					} else {
						set.add("");
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
					
					String [] w = sequence[i].split(kCoupleSeparator);
					
					word += w[0];
					
					HashSet<String> set = prefixes.get(w[0]);
					
					if(set == null) {
						set = new HashSet<String>();						
						prefixes.put(w[0],set);
					}
					
					if(w.length > 1) {
						set.add(w[1]);
					} else {
						set.add("");
					}
					
					Integer c = counts.get(word); 
					if(c == null) {
						counts.put(word,1);
					} else {
						counts.put(word,c+1);
					}
					
					for(int k = 0; k < prev.length-1; k++) {
						prev[k] = prev[k+1];
					}
					prev[n-2] = w[0];
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
				String [] word = w.split(kCoupleSeparator);
				
				accumulated += Math.log( getProfOf(word[0],n) );				
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

				String [] w = sequence[i].split(kCoupleSeparator);
				
				word += w[0];
				
				accumulated += Math.log( getProfOf(word,n) );
			
				
				for(int k = 0; k < prev.length-1; k++) {
					prev[k] = prev[k+1];
				}
				prev[n-2] = w[0];
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
				String [] w = word.split(kInternalSeparator);
				
				return p / (prefixes.get(w[w.length-1])).size();
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
			for(NGramDecoupledModel aux : auxiliar) {
				if(aux.getN() == level) {
					p = aux.probs.get(nword);
					break;
				}
			}
			
			if(p == null) {
				return getProfOf(nword,level-1);
			} else {
				return p / (prefixes.get(w[w.length-1])).size();				
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
