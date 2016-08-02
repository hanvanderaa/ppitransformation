package ppitransformation.parsing;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import ppitransformation.main.Config;
import ppitransformation.similarity.scorers.Scorer;
import ppitransformation.similarity.utils.Lemmatizer;
import ppitransformation.similarity.utils.Stemmer;

public class NLProcessor {

	Config config;
	private Scorer emptyScorer;
	private Map<String, Scorer> scorers;
	private Lemmatizer lemmatizer;
	private Stemmer stemmer;
	private StanfordCoreNLP pipeline;
	
	public NLProcessor(Config config) {
		this.config = config;
		emptyScorer = new Scorer(config, null);
		scorers = new HashMap<String, Scorer>();
		pipeline = loadPipeline();
		lemmatizer = new Lemmatizer(pipeline);
		stemmer = new Stemmer();
	}
	
	public Scorer scorer() {
		return emptyScorer;
	}
	
	public Scorer getScorer(String caseid) {
		return scorers.get(caseid);
	}

	public void addScorer(String caseid, Scorer scorer) {
		scorers.put(caseid, scorer);
	}


	public Lemmatizer lemmatizer() {
		return lemmatizer;
	}

	public Stemmer stemmer() {
		return stemmer;
	}

	private StanfordCoreNLP loadPipeline() {
		Properties props = new Properties();
		props.put("annotators", "tokenize, ssplit, pos, lemma, parse");
		return  new StanfordCoreNLP(props);
	}
	
	public StanfordCoreNLP getPipeline() {
		return pipeline;
	}
	
	public List<String> preprocessString(String s) {
		return lemmatizer().lemmatize(s);
	}
	
	public List<String> preprocessStringList(List<String> l) {
		return preprocessString(String.join(" ", l));
	}
	
	

	
}
