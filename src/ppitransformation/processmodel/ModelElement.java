package ppitransformation.processmodel;

import java.util.List;

public class ModelElement {

	String id;
	String label;
	Object jbptElement;
	ElementType type;
	List<String> terms;
	
	public ModelElement(String id, String label, ElementType type, Object jbptElement) {
		super();
		this.id = id;
		this.label = label;
		this.type = type;
		this.jbptElement = jbptElement;
	}
	
	public void setTerms(List<String> terms) {
		this.terms = terms;
	}
	
	public List<String> getTerms() {
		return terms;
	}
	
	public boolean isPreprocessed() {
		return terms != null;
	}
	
	public String getId() {
		return id;
	}

	public String getLabel() {
		return label;
	}
	
	public String toString() {
		return type + " " + label;
	}

	public ElementType getType() {
		return type;
	}
}
