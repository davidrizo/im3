package es.ua.dlsi.im3.analysis.hierarchical.motives;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.ScoreSong;

public interface IMotivesAnalizer {
	MelodicMotivesAnalysis analyze(ScoreSong song) throws IM3Exception;
}
