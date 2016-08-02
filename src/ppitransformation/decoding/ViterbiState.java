package ppitransformation.decoding;

import java.util.ArrayList;

import ppitransformation.parsing.Tags;
import ppitransformation.ppi.MeasureType;

public class ViterbiState extends PPIAnnotation {

	double probability;
	int cwk;
	
	public ViterbiState() {
		probability = 0.0;
	}
	
	public ViterbiState(Chunk firstChunk, int cwk, double initialProb) {
		chunks = new ArrayList<Chunk>();
		chunks.add(new Chunk(Tags.START_TAG));
		chunks.add(firstChunk);
		probability = initialProb;
		this.cwk = cwk;
	}
	
	public ViterbiState(ViterbiState preceding, Chunk newChunk, int cwk, double transitionProb) {
		chunks = new ArrayList<Chunk>(preceding.getChunks());
		chunks.add(newChunk);
		probability = preceding.getProbability() * transitionProb;
		this.cwk = cwk;
	}
	
	public int getCwk() {
		return cwk;
	}
	
	public double getProbability() {
		return probability;
	}

	public Chunk getLastChunk() {
		return chunks.get(chunks.size() - 1);
	}
	
	public String toString() {
		return super.toString() + " prob: " + probability;
	}
	
	public void removeLastChunk() {
		chunks.remove(chunks.size() - 1);
	}
	
	
	public void closeAnnotation() {
		chunks.add(new Chunk(Tags.END_TAG));
	}

	public boolean isEmpty() {
		return this.getType() == MeasureType.UNDEFINED;
	}
	
	
}
