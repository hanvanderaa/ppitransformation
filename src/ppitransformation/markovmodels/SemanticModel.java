package ppitransformation.markovmodels;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import ppitransformation.decoding.ViterbiState;
import ppitransformation.parsing.Tags;

public class SemanticModel extends Automaton implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6273149423033094638L;

	public SemanticModel() {
		super();
	}
	
	public Set<String> getTags() {
		return states.keySet();
	}
	
	public boolean isAccepting(String tag) {
		return states.get(tag).hasTransition(Tags.END_TAG);
	}
	
	public double probability(ViterbiState state) {
		double p = 1.0;
		List<String> tags = state.getTagSequence();
		for (int i = 1; i < tags.size(); i++) {
			p = p * this.getProbability(tags.get(i-1), tags.get(i));
		}
		return p;
	}
}
