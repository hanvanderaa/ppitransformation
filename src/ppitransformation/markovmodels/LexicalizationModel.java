package ppitransformation.markovmodels;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

import ppitransformation.decoding.Chunk;
import ppitransformation.decoding.PPIAnnotation;
import ppitransformation.parsing.Tags;
import ppitransformation.utils.ListUtils;

public class LexicalizationModel extends Automaton implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7987642499614871284L;
	Set<String> tags;
	
	public LexicalizationModel() {
		super();
		tags = new LinkedHashSet<String>();
	}
	
	public void addAnnotation(PPIAnnotation annotation) {
		for (Chunk chunk : annotation.getChunks()) {
			tags.add(chunk.getTag());
			incrementPath(chunk.getTag(), chunk.getWordString());
		}
	}
	

	public void addTagState(String tag) {
		states.put(tag, new State(tag));
		tags.add(tag);
	}
	
	public int getSubStringOccurrences(String word, String tag) {
		return getSubStringOccurrences(new String[]{word}, tag);
	}
	
	public int getSubStringOccurrences(String[] subsequence, String tag) {
		int n = 0;
		for (Transition t : getTransitionsFrom(tag)) {
			String[] sequence = t.getDestination().getWordSequence();
			if (Tags.TYPE_TAGS.contains(tag) || Tags.DIVIDER_TAGS.contains(tag)) {
				n += ListUtils.countSequenceStartOccurrences(sequence, subsequence) * t.getCount();
			}
			else {
				n += ListUtils.countSequenceOccurrences(sequence, subsequence) * t.getCount();
			}
		}
		return n;
	}
	
	public boolean sequenceEqualsReachableState(String[] sequence, String tag) {
		for (Transition t : getTransitionsFrom(tag)) {
			String[] sequence2 = t.getDestination().getWordSequence();
			if (ListUtils.equalSequences(sequence, sequence2)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean containedInModel(String word) {
		for (State s : states.values()) {
			if (s.label.contains(word)) {
				return true;
			}
		}
		return false;
	}
	
	
	
}
