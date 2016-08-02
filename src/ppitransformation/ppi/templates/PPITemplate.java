package ppitransformation.ppi.templates;

import java.util.ArrayList;
import java.util.List;

import ppitransformation.decoding.PPIAnnotation;
import ppitransformation.ppi.MeasureType;

public class PPITemplate {

	PPIAnnotation annotation;
	MeasureType type;
	Correspondence aggregator;
	Correspondence grouping;
	List<Correspondence> elements;

	
	public PPITemplate(PPIAnnotation annotation) {
		this.annotation = annotation;
		elements = new ArrayList<Correspondence>();
	}
	
	public void setType(MeasureType type) {
		this.type = type;
	}
	
	public void setAggregator(Correspondence aggregator) {
		this.aggregator = aggregator;
	}
	
	public void setGrouping(Correspondence grouping) {
		this.grouping = grouping;
	}
	
	public String getGroupByString() {
		if (grouping == null) {
			return "";
		}
		return grouping.getGroupByString();
	}
	
	public boolean hasGrouping() {
		return (grouping != null);
	}
	
	public boolean hasAggregator() {
		return (aggregator != null);
	}
	
	public void addModelElement(Correspondence e) {
		elements.add(e);
	}
	
	public List<Correspondence> getCorrespondences() {
		return elements;
	}
	
	public String toString() {
		return type + " PPI - " + annotation.getCaseid() + "-" + annotation.getPpiid() + " " + annotation.getOriginal();
	}

	public String getAggregator() {
		if (aggregator == null) {
			return "";
		}
		return aggregator.getCorrespondentID();
	}

	public MeasureType getType() {
		return type;
	}

	public int alignmentSize(boolean justModelElements) {
		// +1 accounts for type measure
		int size = elements.size() + 1;
		if (!justModelElements && hasAggregator()) {
			size++;
		}
		if (!justModelElements && hasGrouping()) {
			size++;
		}
		return size;
	}
}
