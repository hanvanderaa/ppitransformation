package ppitransformation.templatefilling;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ppitransformation.decoding.Chunk;
import ppitransformation.ppi.templates.Correspondence;
import ppitransformation.processmodel.ElementType;

public class SimilarityScores {

	Map<Chunk, List<Correspondence>> alignmentMap;
	
	public SimilarityScores() {
		alignmentMap = new HashMap<Chunk, List<Correspondence>>();
	}
	
	public void addCorrespondence(Correspondence c) {
		List<Correspondence> correspondences;
		if (alignmentMap.containsKey(c.getChunk())) {
			correspondences = alignmentMap.get(c.getChunk());
		} else {
			correspondences = new ArrayList<Correspondence>();
		}
		correspondences.add(c);
		alignmentMap.put(c.getChunk(), correspondences);
	}
	
	public Correspondence getHighestCorrespondence(Chunk chunk) {
		Correspondence best = null;
		for (Correspondence c : alignmentMap.get(chunk)) {
			if (best == null || c.getSimscore() > best.getSimscore()) {
				best = c;
			}
		}
		return best;
	}
	
	public Correspondence getHighestCorrespondence(Chunk chunk, ElementType type) {
		Correspondence best = null;
		for (Correspondence c : alignmentMap.get(chunk)) {
			if ( (c.getModelElement().getType() == type || 
				(c.getModelElement().getType() == ElementType.EVENT && type == ElementType.ACTIVITY))
					&& 
					(best == null || c.getSimscore() > best.getSimscore())) {
				best = c;
			}
		}
		return best;
	}
	
}
