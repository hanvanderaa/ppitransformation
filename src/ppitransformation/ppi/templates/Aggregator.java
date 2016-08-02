package ppitransformation.ppi.templates;

public enum Aggregator {

	MINIMUM, MAXIMUM, AVERAGE, SUM;
	
	public static Aggregator getAggregator(String s) {
		for (Aggregator a : Aggregator.values()) {
			if (a.toString().equalsIgnoreCase(s)) {
				return a;
			}
		}
		return null;
	}
	
}
