package ppitransformation.markovmodels;

import java.io.Serializable;
import java.util.Set;

public class HMM implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8998510578778539174L;
	SemanticModel semModel;
	LexicalizationModel lexModel;
	
	
	public HMM(SemanticModel semModel, LexicalizationModel lexModel) {
		super();
		this.semModel = semModel;
		this.lexModel = lexModel;
	}


	public SemanticModel getSemModel() {
		return semModel;
	}


	public LexicalizationModel getLexModel() {
		return lexModel;
	}
	
	public Set<String> getTags() {
		return semModel.getTags();
	}
	
	
	
}
