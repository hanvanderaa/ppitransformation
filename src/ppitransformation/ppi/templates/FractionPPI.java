package ppitransformation.ppi.templates;

import ppitransformation.decoding.PPIAnnotation;
import ppitransformation.ppi.MeasureType;

public class FractionPPI extends PPITemplate {

	PPIAnnotation annotation;
	Correspondence numerator;
	Correspondence denominator;
	
	public FractionPPI(PPIAnnotation annotation) {
		super(annotation);
		setType(MeasureType.FRACTION);
	}

	public void setNumerator(Correspondence e) {
		this.numerator = e;
		addModelElement(e);
	}
	
	public void setDenominator(Correspondence e) {
		this.denominator = e;
		addModelElement(e);
	}
	
	
	public String toString() {
		if (hasGrouping()) {
			return super.toString() +
					"\nnum: " + numerator + "\ndenom: " + denominator + "\ngroup by: " + grouping.getGroupByString();
		}
		return super.toString() +
				"\nnum: " + numerator + "\ndenom: " + denominator;
	}
}
