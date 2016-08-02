package ppitransformation.markovmodels;

import java.io.Serializable;

public class Transition implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3686838728310890521L;
	public State from;
	public State to;
	public int count;
	
	public Transition(State from, State to) {
		this.from = from;
		this.to = to;
		count = 0;
	}
	
	public State getOrigin() {
		return from;
	}
	
	public State getDestination() {
		return to;
	}

	public int getCount() {
		return count;
	}
	
	public void incrementCount() {
		count++;
	}

	public double probability() {
		return from.getOutgoingCount() > 0 ? count * 1.0 / from.getOutgoingCount() : 0.0;
	}
	
	public String toString() {
		return from + " - " + to + " (" + probToString() + ")";
	}
	
	private String probToString() {
		return String.valueOf(Math.round(probability() * 1000.0) / 1000.00);
	}
}
