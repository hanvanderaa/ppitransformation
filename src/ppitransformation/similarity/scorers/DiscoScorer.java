package ppitransformation.similarity.scorers;

import ppitransformation.main.Config;
import ppitransformation.similarity.SimilarityManager;
import ppitransformation.similarity.utils.CleanUpUtils;

public class DiscoScorer implements TermSimilarityScorer{

	private SimilarityManager similarityManager;
	
	public DiscoScorer(String name) {
		similarityManager = new SimilarityManager(name, Config.MODE_DISCO);
	}
	
	
	public double getSimilarity(String w1, String w2) {
		return similarityManager.getRelatedness(CleanUpUtils.removeCharacters(w1), CleanUpUtils.removeCharacters(w2));
	}

		
}
