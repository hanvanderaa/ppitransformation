package ppitransformation.ppi.templates;

import ppitransformation.decoding.Chunk;
import ppitransformation.processmodel.ModelElement;

public class Correspondence {

	ModelElement modelElement;
	Aggregator aggregator;
	String groupByString;
	Chunk chunk;
	double simscore;
	
	public Correspondence(ModelElement modelElement, Chunk chunk) {
		super();
		this.modelElement = modelElement;
		this.chunk = chunk;
	}
	
	public Correspondence(Aggregator aggregator, Chunk chunk) {
		super();
		this.aggregator = aggregator;
		this.chunk = chunk;
	}
	
	public Correspondence(String groupByString, Chunk chunk) {
		super();
		this.groupByString = groupByString;
		this.chunk = chunk;
	}
	
	public Correspondence(ModelElement modelElement, Chunk chunk, double simscore) {
		super();
		this.modelElement = modelElement;
		this.chunk = chunk;
		this.simscore = simscore;
	}
	
	public Correspondence(Aggregator aggregator, Chunk chunk, double simscore) {
		super();
		this.aggregator = aggregator;
		this.chunk = chunk;
		this.simscore = simscore;
	}

	public Chunk getChunk() {
		return chunk;
	}

	public ModelElement getModelElement() {
		return modelElement;
	}
	
	public String getGroupByString() {
		return groupByString;
	}

	public double getSimscore() {
		return simscore;
	}
	
	public String getCorrespondentID() {
		if (modelElement != null) {
			return modelElement.getId();
		}
		return aggregator.toString();
	}
	
	public String toString() {
		if (modelElement != null) {
			return chunk + " - " + modelElement + " " + simscore;
		}
		return chunk + " - " + aggregator + " " + simscore;
	}
	

}
