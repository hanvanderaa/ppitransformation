package ppitransformation.similarity;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import de.linguatools.disco.Compositionality;
import de.linguatools.disco.CorruptConfigFileException;
import de.linguatools.disco.DISCO;
import de.linguatools.disco.WrongWordspaceTypeException;
import ppitransformation.main.Config;
import ppitransformation.main.Main;
import ppitransformation.similarity.utils.Stemmer;
import uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein;

public class SimilarityManager {
	
	public static final String DISCO_CACHE_PATH = "cache/disco/";
	public static final String LIN_CACHE_PATH = "cache/lin/";

	
	private String source;
	private HashMap<String, Double> similarityMap;

	// DISCO
	private DISCO disco;
	private Compositionality discoComp;
	
	private int mode;
	private String cache_path = null;
	private Levenshtein ls;
	

	public SimilarityManager(String source, int mode) {
		ls = new Levenshtein();
		this.source = source;
		this.mode = mode;
		similarityMap = new HashMap<String, Double>();
		
		if (mode == Config.MODE_DISCO) {
			cache_path = DISCO_CACHE_PATH;
		}	

		if (mode == Config.MODE_DISCO) {
			try {
				disco = new DISCO(Main.DISCO_PATH, false);
				discoComp = new Compositionality();
			} catch (IOException | CorruptConfigFileException e) {
				e.printStackTrace();
			}
		
		}
	}

	public double getRelatedness(String w1, String w2) {
		if (w1.isEmpty() || w2.isEmpty()) {
			return 0;
		}
		String key1 = w1 + "-" + w2;
		String key2 = w2 + "-" + w1;
		if (similarityMap.containsKey(key1)) {
			return similarityMap.get(key1);
		}
		if (similarityMap.containsKey(key2)) {
			return similarityMap.get(key2);
		}

	
		
		double sim = 0;

		if (mode == Config.MODE_DISCO) {
			try {
				if (!w1.contains(" ") && !w2.contains(" ")) {
					sim = disco.secondOrderSimilarity(w1, w2);
					if (sim > 1 && sim < 1.01) {
						sim = 1;
					}
					if (sim > 1.01) {
						sim = 0.5;
					}
					if (stem(w1).equals(stem(w2))) {
						sim = 1.0;
					}
					
				} else {
					sim = discoComp.compositionalSemanticSimilarity(w1, w2, 
							Compositionality.VectorCompositionMethod.MULTIPLICATION,
							DISCO.SimilarityMeasure.KOLB, disco, null, null, null, null);
					if (sim > 1 && sim < 1.01) {
						sim = 1;
					}
					if (sim > 1.01) {
						sim = 0.5;
					}
					if (stem(w1).equals(stem(w2))) {
						sim = 1.0;
					}
				}
			} catch (IOException | WrongWordspaceTypeException e) {
				e.printStackTrace();
			}
			if (sim < 0) {
				sim = ls.getSimilarity(w1, w2);
			}
		}
		
		try {
			File cache = new File(cache_path + source);
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
	
	private String stem(String s) {
		Stemmer stemmer = new Stemmer();
		String stem = stemmer.stem(s);
		return stem;
	}
}
