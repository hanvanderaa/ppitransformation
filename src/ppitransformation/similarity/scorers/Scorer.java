package ppitransformation.similarity.scorers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ppitransformation.main.Config;
import ppitransformation.similarity.termdictionary.TermDictionary;
import ppitransformation.similarity.utils.SimilarityHelper;



public class Scorer {

	public static final String DISCO_CACHE_PATH = "cache/disco/";
	
	private String cache_path;
	private Config config;
	private TermSimilarityScorer termScorer;
	private TermDictionary dictionary;
	private HashMap<String, Double> similarityMap;


		
	public Scorer(Config config, TermDictionary dictionary) {
			this.config = config;
			termScorer = loadTermScorer();
			this.dictionary = dictionary;
	}

	public double computeSimilarity(List<String> lemmas1, List<String> lemmas2) {
		if (config.termSimMode == Config.TFIDF) {
			return bagOfWords(lemmas1, lemmas2);
		}
		return mihalceaSimilarity(lemmas1, lemmas2);
//		return bagOfWords(lemmas1, lemmas2);
	}
	
	private double bagOfWords(List<String> lemmas1, List<String> lemmas2) {
		double sim = 0.0;
		// Copy lists
    	ArrayList<String> lemmas1Copy = new ArrayList<String>(lemmas1);
		ArrayList<String> lemmas2Copy = new ArrayList<String>(lemmas2);
				
		// Determine best string match constellation
		while (lemmas1Copy.size() > 0 && lemmas2Copy.size() > 0) {
			sim = sim + computeBestMatch(lemmas1Copy, lemmas2Copy);
		}
		
//		double score = (sim / Math.max(lemmas1.size(), lemmas2.size()));
		return (sim / Math.max(lemmas1.size(), lemmas2.size()));
	}
	
	
	private double computeBestMatch(ArrayList<String> comps1Copy,ArrayList<String> comps2Copy) {
		int max_i = 0;
		int max_j = 0;
		double max_sim = 0;
		double sim = 0;
		for (int i = 0; i<comps1Copy.size();i++) {
			String best = findBestMatch(comps1Copy.get(i), comps2Copy);
			sim = termScorer.getSimilarity(comps1Copy.get(i), best);
			if (sim > max_sim) {
				max_sim = sim;
				max_i = i;
				max_j = comps2Copy.indexOf(best);
			}
		}
		comps1Copy.remove(max_i);
		if (max_j != -1) {
			comps2Copy.remove(max_j);
		}
		return max_sim;
	}
	
	private String findBestMatch(String lemma1, List<String> lemmas2) {
		double max_sim = 0;
		double sim = 0;
		String best = "";
		for (String lemma2 : lemmas2) {
			sim = getSimilarity(lemma1, lemma2);
			if (sim > max_sim) {
				max_sim = sim;
				best = lemma2;
			}
		}
		return best;
	}
	
	public double getSimilarity(String w1, String w2) {
		if (config.termSimMode == Config.MODE_DISCO) {
			String key1 = w1 + "-" + w2;
			String key2 = w2 + "-" + w1;
			if (SimilarityHelper.isCorrected(key1) || SimilarityHelper.isCorrected(key2)) {
				return 1.0;
			}
			if (similarityMap.containsKey(key1)) {
				return similarityMap.get(key1);
			}
			if (similarityMap.containsKey(key2)) {
				return similarityMap.get(key2);
			}
			double sim = termScorer.getSimilarity(w1, w2);
			try {
				File cache = new File(cache_path);
				if (cache.exists()) {
					FileWriter fw = new FileWriter(cache, true);
					fw.append(key1 + "\t" + sim + "\n");
					fw.close();
				} else {
					FileWriter fw = new FileWriter(cache);
					fw.write(key1 + "\t" + sim + "\n");
					fw.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return sim;
		}
		return termScorer.getSimilarity(w1, w2);
	}
	
	private double mihalceaSimilarity(List<String> lemmas1, List<String> lemmas2) {
		if (lemmas1.isEmpty() || lemmas2.isEmpty()) {
			return 0.0;
		}
		List<String> lemmas1copy = new ArrayList<String>(lemmas1);
		List<String> lemmas2copy = new ArrayList<String>(lemmas2);
		double leftnum = 0.0;
		double leftdenom = 0.0;
		for (String lemma1 : lemmas1) {
			if (!lemmas2copy.isEmpty()) {
				String best2 = findBestMatch(lemma1, lemmas2copy);
				double idf = dictionary.getIDF(lemma1);
				leftnum += getSimilarity(lemma1, best2) * idf;
				leftdenom += idf;
			}
		}
		double rightnum = 0.0;
		double rightdenom = 0.0;
		for (String lemma2 : lemmas2) {
			if (!lemmas1copy.isEmpty()) {
				String best1 = findBestMatch(lemma2, lemmas1copy);
				double idf = dictionary.getIDF(lemma2);
				double score = getSimilarity(lemma2, best1);
				rightnum += getSimilarity(lemma2, best1) * idf;
				rightdenom += idf;
			}
		}
		if (leftdenom == 0.0 || rightdenom == 0.0) {
			return 0.0;
		}
		double score = 0.5 * ((leftnum / leftdenom) + (rightnum / rightdenom)); 
		return score;
	}
	
	
	private TermSimilarityScorer loadTermScorer() {
	if (config.termSimMode == Config.TFIDF) {
		return new TFIDFScorer();
	}
	if (config.termSimMode == Config.MODE_DISCO) {
		cache_path = DISCO_CACHE_PATH + "simscores";
		similarityMap = new HashMap<String, Double>();
		if (new File(cache_path).exists()) {
			loadCache();
		}
		return new DiscoScorer("scorer");
		}
	return null;
	}
	
	private void loadCache() {
		try {
			BufferedReader input = new BufferedReader(new FileReader(new File(cache_path)));
			String line = "";
			while ((line = input.readLine()) != null) {
				if (!line.isEmpty()) {
					String[] split = line.split("\t");
					similarityMap.put(split[0], Double.valueOf(split[1]));
				}
			}
			input.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	
	
}
