package es.grfia.hmm.wg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class WGReader {
	public static void main(String [] args) throws IOException {
		WordGraph wg = WGReader.readLattices("/home/jcalvo/Escritorio/Prueba/12611-A_2.lat");
		
		System.out.println(new Viterbi().decoding(wg));
		
	}
	
	public static WordGraph readLattices(String latticesFile) throws IOException {
		
		BufferedReader br = null;
		
		br = new BufferedReader(new FileReader(new File(latticesFile)));
		
		br.readLine(); // VERSION=1.0
		br.readLine(); // UTTERANCE
		br.readLine(); // lmname=all.wnet
		br.readLine(); // lmscale=50.00  wdpenalty=17.00 
		br.readLine(); // acscale=1.00  
		br.readLine(); // vocab=all.dic
		
		String [] wgParams = br.readLine().split(" ");
		int N = Integer.parseInt(wgParams[0].split("=")[1]);
		int L = Integer.parseInt(wgParams[1].split("=")[1]);
		
		WordGraph wg = new WordGraph(N);
		
		// Read states
		for(int i = 0; i < N; i++) {
			String [] stateParams = br.readLine().split(" +");
			int idx = Integer.parseInt(stateParams[0].split("=")[1]);
			String label = stateParams[1].split("=")[1];
			
			wg.addState(idx, label);
		}
		
		// Read edges
		for(int i = 0; i < L; i++) {
			// J=669   S=80   E=209  W=WMINIMAp1           v=1  a=1905.40   l=0.000 
			String [] stateParams = br.readLine().split(" +");
			int idx = Integer.parseInt(stateParams[0].split("=")[1]);
			int start = Integer.parseInt(stateParams[1].split("=")[1]);
			int end = Integer.parseInt(stateParams[2].split("=")[1]);
			String word = stateParams[3].split("=")[1];
			int explanation = Integer.parseInt(stateParams[4].split("=")[1]);
			double logacoustic = Double.parseDouble(stateParams[5].split("=")[1]);
			double loglanguage = Double.parseDouble(stateParams[6].split("=")[1]);
			
			wg.addEdge(idx,start,end,word,explanation,logacoustic,loglanguage);
		}
		
		br.close();		

		return wg;		
	}
}
