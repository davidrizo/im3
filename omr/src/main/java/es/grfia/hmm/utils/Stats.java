package es.grfia.hmm.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Stats {
	public int count = 0;
	public int notFound = 0;
	public class GramSample {
		public HashSet<String> set;

		public GramSample() {
			set = new HashSet<String>();
		}
		
		public void add(String str) {
			set.add(str);
		}
		
	}

	public static void main(String [] args) throws IOException {
		String file = "/home/jcalvo/Escritorio/HMMOMR/PoC_3grad/Rests/Text_Lines";
		
		Stats stats = new Stats();
		stats.run(file);
	}
	
	public void run(String text) throws IOException {
		List<GramSample> gramSample = new ArrayList<GramSample>();
		HashSet<String> vocabulary = new HashSet<String>();
		
		BufferedReader br = new BufferedReader(new FileReader(new File(text)));
		
		String line = null;
		while( (line = br.readLine()) != null) {
			
			List<String> lines = new ArrayList<String>();
			lines.add(line);
			for(int i = 1; i <= 5; i++) {
				lines.add(br.readLine());
			}
			
			gramSample.add(parsePage(lines));			
		}
		
		br.close();
		
		// ** //
		
		int samples = gramSample.size();
		
		for(int i = 0; i < samples; i++) {
			GramSample sample = gramSample.remove(0);
			
			check(sample,gramSample);
			
			gramSample.add(sample);
		}
		

		System.out.println("Size: " + count + " - Not found: " + notFound);
		
	}

	private void check(GramSample test, List<GramSample> gramSample) {
		for(String strTest : test.set) {
			boolean found = false;
			for(GramSample trainSample : gramSample) {
				for(String strTrain : trainSample.set) {
					if(strTrain.equals(strTest)) {
						found = true;
						break;
					}
				}
				
				if(found) {
					break;
				}
			}
			
			if(!found) {
				notFound++;
			}
		}
	}

	private GramSample parsePage(List<String> lines) {
		GramSample sample = new GramSample();
		
		for(String line : lines) {
			String [] symbols = line.split("\\s+");
			
			for(int i = 3; i < symbols.length; i++) {
				sample.add(symbols[i-3] + " " + symbols[i-2] + " " + symbols[i-1] + " " + symbols[i]);
				count ++ ;
			}
		}
		
		return sample;
	}
}
