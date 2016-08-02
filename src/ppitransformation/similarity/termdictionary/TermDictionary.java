package ppitransformation.similarity.termdictionary;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import ppitransformation.processmodel.ModelElement;
import ppitransformation.processmodel.ProcessModelWrapper;


public class TermDictionary {

	String _caseid;
	ProcessModelWrapper _model;
	private Map<String, Integer> termMap;
	private int documentCount;
	
	
	public TermDictionary(ProcessModelWrapper model) {
		_caseid = model.getId();
		_model = model;
		initialize();
	}
	
	private void initialize() {
		termMap = new HashMap<String, Integer>();
		for (ModelElement e : _model.getModelElements()) {
			addTerms(e.getTerms());
		}
		documentCount = _model.getModelElements().size();
	}
	
	private void addTerms(Collection<String> lemmas) {
		if (lemmas != null) {
			for (String lemma : new HashSet<String>(lemmas)) {
				addTerm(lemma);
			}
		}
	}
		
	private void addTerm(String lemma) {
		int count = 0;
		if (termMap.containsKey(lemma)) {
			count = termMap.get(lemma);
		}
		count += 1;
		termMap.put(lemma, count);
	}

	public int getTermFrequency(String lemma) {
		return termMap.containsKey(lemma) ? termMap.get(lemma) : 0;
	}
	
	public double getIDF(String lemma) {
		int tf = getTermFrequency(lemma) + 1;
		if (tf == 0) {
			return 0;
		}
		double idf = Math.log10((documentCount + 1) * 1.0 / tf);
		return Math.log10((documentCount + 1) * 1.0 / tf);
	}
	
	

	
	
	
}
