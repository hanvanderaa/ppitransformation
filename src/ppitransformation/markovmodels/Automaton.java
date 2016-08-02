package ppitransformation.markovmodels;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class Automaton implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 197948949376581351L;
	State initial;
	Map<String, State> states;
	
	public Automaton() {
		states = new LinkedHashMap<String, State>();
	}
	
	public State getInitial() {
		return initial;
	}
	
	public void setInitial(String label) {
		initial = states.get(label);
	}
	
	public boolean hasState(String label) {
		return states.containsKey(label);
	}
	
	public void addState(String label) {
		states.put(label, new State(label));
	}
	
	public void incrementPath(String originLabel, String destLabel) {
		if (!states.containsKey(originLabel)) {
			states.put(originLabel, new State(originLabel));
		}
		if (!states.containsKey(destLabel)) {
			states.put(destLabel, new State(destLabel));
		}
		State origin = states.get(originLabel);
		State dest = states.get(destLabel);
		if (!origin.hasTransition(dest)) {
			origin.addTransition(new Transition(origin, dest));
		}
		origin.getTransition(destLabel).incrementCount();
	}
	
	public double getProbability(String originLabel, String destLabel) {
		if (states.get(originLabel).hasTransition(destLabel)) {
			return states.get(originLabel).getTransition(destLabel).probability();
		}
		return 0.0;
	}
	
	public Collection<Transition> getTransitionsFrom(String originLabel) {
		return states.get(originLabel).getTransitions();
	}
	
	public int outgoingTransitionCount(String stateLabel) {
		return states.get(stateLabel).getOutgoingCount();
	}
	
	public void setStates(Collection<String> labels) {
		initial = new State("<s>");
		states.put(initial.getLabel(), initial);
		State finalState = new State("</s>");
		finalState.setAccepting();
		states.put(finalState.getLabel(), finalState);
		for (String label : labels) {
			states.put(label, new State(label));
		}
		for (State origin : states.values()) {
			for (State dest : states.values()) {
				origin.addTransition(new Transition(origin, dest));
			}
		}
	}

	
	public void print() {
		for (State s : states.values()) {
			for (Transition t : s.getTransitions()) {
				System.out.println(t);
			}
		}
		
//		Set<State> visited = new HashSet<State>();
//		Stack<State> stack = new Stack<State>();
//		stack.push(initial);
//		while (!stack.isEmpty()) {
//			State current = stack.pop();
//			if (!visited.contains(current)) {
////				System.out.println("State: " + current);
//				for (Transition t : current.getAllTransitions()) {
//					System.out.println(t);
//					if (!visited.contains(t.getDestination())) {
//						stack.push(t.getDestination());
//					}
//				}
//				visited.add(current);
//			}
//		}
	}
}
