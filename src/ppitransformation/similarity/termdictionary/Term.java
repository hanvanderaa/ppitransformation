package ppitransformation.similarity.termdictionary;

import java.util.Set;

public class Term {

	private String lemma;
	private int count;
	private double termIDF;
	
	public Term(String lemma) {
		super();
		this.lemma = lemma;
		count = 1;
	}
	
	public String getLemma() {
		return lemma;
	}
	
	public void incrementCount() {
		count++;
	}
	
	public int getCount() {
		return count;
	}
	
	public Boolean matchesTerm(String lemma) {
		return this.lemma.equals(lemma);
	}
	
	public double getTermIDF() {
		return termIDF;
	}

	public void setTermIDF(double termIDF) {
		this.termIDF = termIDF;
	}

	
	
}
