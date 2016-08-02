package ppitransformation.markovmodels;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import ppitransformation.utils.ListUtils;

public class State implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7049724274771154317L;
	String label;
	String[] labelseq;
	boolean accepting;
	Map<String, Transition> transitions;
	
	
	
	public State() {
		this("");
	}
	
	public State(String label) {
		this.label = label;
		labelseq = ListUtils.splitStringToArray(label);
		transitions = new HashMap<String, Transition>();
		accepting = false;
	}
	
	public String getLabel() {
		return label;
	}
	
	public String[] getWordSequence() {
		return labelseq;
	}
	
	public boolean hasTransition(String destLabel) {
		return transitions.containsKey(destLabel);
	}
	
	public boolean hasTransition(State dest) {
		return hasTransition(dest.getLabel());
	}
	

	public Transition getTransition(String destLabel) {
		return transitions.get(destLabel);
	}
	
	public Collection<Transition> getTransitions() {
		return transitions.values();
	}

	public void addTransition(Transition t) {
		transitions.put(t.getDestination().getLabel(), t);
	}
	
	public boolean isAccepting() {
		return accepting;
	}
	
	public void setAccepting() {
		accepting = true;
	}
	
	public int getOutgoingCount() {
		int total = 0;
		for (Transition t : transitions.values()) {
			total += t.getCount();
		}
		return total;
	}
	
	public String toString() {
		return label;
	}


}
