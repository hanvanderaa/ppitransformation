package ppitransformation.ppi.templates;

import ppitransformation.decoding.PPIAnnotation;
import ppitransformation.ppi.MeasureType;

public class CountPPI extends PPITemplate {

	public CountPPI(PPIAnnotation annotation) {
		super(annotation);
		setType(MeasureType.COUNT);
	}
	
	
	public String toString() {
		String end = this.elements.toString();
		if (hasGrouping()) {
			end = end + " group by: " + grouping.getGroupByString();
		}
		if (aggregator == null) {
			return super.toString() + "\ncount: " + end;
		}
		return super.toString() + "\ncount: " + aggregator + " " + end;
	}
}
