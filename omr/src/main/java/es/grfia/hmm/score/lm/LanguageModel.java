package es.grfia.hmm.score.lm;

import java.util.List;

public interface LanguageModel {
	public void learn(List<String> samples);
	public double perplexityOf(String sample);
}
