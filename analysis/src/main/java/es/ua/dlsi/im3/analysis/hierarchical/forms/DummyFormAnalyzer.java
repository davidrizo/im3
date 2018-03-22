package es.ua.dlsi.im3.analysis.hierarchical.forms;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.adt.tree.TreeException;
import es.ua.dlsi.im3.core.score.Measure;
import es.ua.dlsi.im3.core.score.ScoreSong;

import java.util.ArrayList;

/**
 * It just splits the work in sections of the same length in number of bars. Those sections are provided by the user. Only returns one analysis 
 * @author drizo
 *
 */
public class DummyFormAnalyzer implements IFormAnalyzer {
	private String[] sectionNames;

	public DummyFormAnalyzer(String [] sectionNames) {
		this.sectionNames = sectionNames;
	}
	
	@Override
	public FormAnalysis analyze(ScoreSong song) throws IM3Exception {
		FormAnalysis fa = new FormAnalysis(song);

		ArrayList<Measure> bars = song.getMeasuresSortedAsArray();
		int sectionLength = bars.size() / sectionNames.length;
		
		try {
			int i=0;
			for (String sn : sectionNames) {
				fa.addSection(sn, bars.get(i).getTime(), "Description of " + sn, "FF0000");
				i+=sectionLength;
			}
		} catch (TreeException e) {
			throw new IM3Exception(e);
		}
		return fa;
	}

}
