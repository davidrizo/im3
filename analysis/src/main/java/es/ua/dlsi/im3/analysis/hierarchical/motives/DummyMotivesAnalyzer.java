package es.ua.dlsi.im3.analysis.hierarchical.motives;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.AtomFigure;
import es.ua.dlsi.im3.core.score.ScoreSong;

import java.util.ArrayList;
import java.util.List;

/**
 * It generates 3 motives of some fixed elements in the score
 * @author drizo
 *
 */
public class DummyMotivesAnalyzer implements IMotivesAnalizer {

	@Override
	public MelodicMotivesAnalysis analyze(ScoreSong song) throws IM3Exception {
		MelodicMotivesAnalysis result = new MelodicMotivesAnalysis(song);

        List<AtomFigure> sds = song.getParts().get(0).getAtomFigures();
		if (sds.size() < 10) {
			throw new IM3Exception("Cannot generate motive for a part with less than 10 notes");
		}
		
		MelodicMotive motive1 = new MelodicMotive();
		motive1.setName("A");
		motive1.setDescription("Exposition");
		result.addMotive(motive1);
		ArrayList<AtomFigure> motive1SSD = new ArrayList<>();
		for (int i=0; i<4; i++) {
			motive1SSD.add(sds.get(i));
		}
		motive1.setAtomFigures(motive1SSD);
		
		MelodicMotive motive2 = new MelodicMotive();
		motive1.setName("A'");
		motive1.setDescription("Variation of A");		
		result.addMotive(motive2);		
		ArrayList<AtomFigure> motive2SSD = new ArrayList<>();
		for (int i=sds.size()-5; i<sds.size(); i++) {
			motive2SSD.add(sds.get(i));
		}
		motive2.setAtomFigures(motive2SSD);
		
		result.connect(motive1, motive2, "Variation");
		return result;
	}

}
