package es.ua.dlsi.im3.analysis.hierarchical.forms;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.ScoreSong;

public interface IFormAnalyzer {
	FormAnalysis analyze(ScoreSong song) throws IM3Exception;
}
