package ppitransformation.ppi.templates;

import ppitransformation.decoding.PPIAnnotation;
import ppitransformation.ppi.MeasureType;

public class TimePPI extends PPITemplate {

	Correspondence start;
	Correspondence end;
	
	public TimePPI(PPIAnnotation annotation) {
		super(annotation);
		setType(MeasureType.TIME);
	}

	public Correspondence getStart() {
		return start;
	}
	
	public Correspondence getEnd() {
		return end;
	}
	
	public void setStart(Correspondence e) {
		this.start = e;
		addModelElement(e);
	}
	
	public void setEnd(Correspondence e) {
		this.end = e;
		addModelElement(e);
	}
	
	public String toString() {
		if (aggregator == null) {
			return super.toString() + "\nstart: " + start + "\nend: " + end;
		}
		return super.toString() + "\n" + aggregator + "\nstart: " + start + "\nend: " + end;
	}
	
}
