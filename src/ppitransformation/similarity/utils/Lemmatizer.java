package ppitransformation.similarity.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

public class Lemmatizer {

	StanfordCoreNLP pipeline;
	
	public Lemmatizer(StanfordCoreNLP pipeline) {
		this.pipeline = pipeline;
	}
		
	
	protected String lemmatizeSingleTerm2(String lemma) {
		if (lemma == null) {
			return "";
		}
		if (!isComposite(lemma)) {
			return lemma.toLowerCase();
		}
		return lemmatizeComposite(lemma.toLowerCase());
	}
	
	private Boolean isComposite(String word) {
		if (word != null) {
			return word.contains("-");
		}
		return false;
	}
	
	private String lemmatizeComposite(String compositeTerm) {
		String[] parts = compositeTerm.split("-");
		String splitForm = "";
		String lemma = "";
		for (String part : parts) {
			splitForm += " " + part; 
		}
		Annotation annotation = new Annotation(splitForm);
		pipeline.annotate(annotation);
		for (CoreLabel token : annotation.get(TokensAnnotation.class)) {
			if (!isSpecialCharacter(token.lemma())) {
				lemma += token.lemma().toLowerCase();
			}
		}
		return lemma;
	}
	
	
	protected Boolean isSpecialCharacter(String lemma) {
		//TODO: add more special chars
//		return Arrays.asList(new String[]{"-lrb-", "-rrb-", "''"}).contains(lemma);
		return !StringUtils.isAlphanumeric(lemma);
	}
	
	public String lemmatizeSingleTerm(String original) {
		Annotation annotation = new Annotation(original);
		pipeline.annotate(annotation);
		for (CoreLabel coreLabel : annotation.get(TokensAnnotation.class)) {
			return lemmatizeSingleTerm2(coreLabel.lemma()); 
		}
		return "";
	}
	
	public List<String> lemmatize(String original) {
		Annotation annotation = new Annotation(original);
		pipeline.annotate(annotation);
		List<String> lemmas = new ArrayList<String>();
		for (CoreLabel coreLabel : annotation.get(TokensAnnotation.class)) {
			if (!isSpecialCharacter(coreLabel.lemma()) && 
					!TermUtils.isDeterminer(coreLabel.lemma()) &&
					!TermUtils.isPreposition(coreLabel.lemma())) {
				lemmas.add(lemmatizeSingleTerm2(coreLabel.lemma()));
			} 
		}
		return lemmas;
	}
	
	protected List<IndexedWord> indexLabel(String label) {
		Annotation annotation = new Annotation(label);
		pipeline.annotate(annotation);
		List<IndexedWord> result = new ArrayList<IndexedWord>();
		for (CoreLabel coreLabel : annotation.get(TokensAnnotation.class)) {
				result.add(new IndexedWord(coreLabel));
		}
		return result;
	}
	
	
}
