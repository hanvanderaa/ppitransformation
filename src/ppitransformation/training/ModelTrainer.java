package ppitransformation.training;

import java.util.Collection;
import java.util.List;

import ppitransformation.markovmodels.LexicalizationModel;
import ppitransformation.markovmodels.SemanticModel;
import ppitransformation.parsing.Tags;
import ppitransformation.ppi.PPI;

public class ModelTrainer {

	
	public SemanticModel trainSemanticModel(Collection<PPI> ppis) {
		SemanticModel m = new SemanticModel();
		for (PPI ppi : ppis) {
			List<String> tags = ppi.getTagSequence();
			m.setInitial(Tags.START_TAG);
			for (int i = 0; i < tags.size() - 1; i++) {
				String tag1 = tags.get(i);
				String tag2 = tags.get(i + 1);
				m.incrementPath(tag1, tag2);
			}
		}
		return m;
	}
	
	public LexicalizationModel trainLexModel(Collection<PPI> ppis) {
		LexicalizationModel m = new LexicalizationModel();
		m.addTagState(Tags.START_TAG);
		m.setInitial(Tags.START_TAG);
		for (PPI ppi : ppis) {
			m.incrementPath(Tags.START_TAG, ppi.getTagSequence().get(1));
			m.addAnnotation(ppi.getAnnotation());
		}
		
		return m;
	}
}
