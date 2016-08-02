package ppitransformation.templatefilling;

import java.util.List;

import ppitransformation.decoding.Chunk;
import ppitransformation.decoding.PPIAnnotation;
import ppitransformation.main.Config;
import ppitransformation.parsing.NLProcessor;
import ppitransformation.parsing.Tags;
import ppitransformation.ppi.MeasureType;
import ppitransformation.ppi.templates.Aggregator;
import ppitransformation.ppi.templates.Correspondence;
import ppitransformation.ppi.templates.CountPPI;
import ppitransformation.ppi.templates.FractionPPI;
import ppitransformation.ppi.templates.PPITemplate;
import ppitransformation.ppi.templates.TimePPI;
import ppitransformation.processmodel.ElementType;
import ppitransformation.processmodel.ModelElement;
import ppitransformation.processmodel.ProcessModelWrapper;

public class Aligner {

	Config _config;
	NLProcessor _nlp;
	ProcessModelWrapper model;
	
	public Aligner(Config config, NLProcessor nlp) {
		_config = config;
		_nlp = nlp;
	}
	
	public PPITemplate formalizePPI(PPIAnnotation annotation, ProcessModelWrapper model) {
		this.model = model;
		SimilarityScores scores = computeSimilarityScores(annotation, model);	
		switch(annotation.getType()) {
		case COUNT: return alignCountPPI(scores, annotation);
		case TIME: return alignTemporalPPI(scores, annotation);
		case FRACTION: return alignFractionalPPI(scores, annotation);
		default:  return null;
		}
	}
	
	
	public CountPPI alignCountPPI(SimilarityScores scores, PPIAnnotation annotation) {
		CountPPI formalppi = new CountPPI(annotation);
		formalppi.setAggregator(findAggregator(scores, annotation));
		formalppi.setGrouping(findGroupBy(scores, annotation));
		String tag2 = "CA";
		if (annotation.hasTag(tag2)) {
			Correspondence best = scores.getHighestCorrespondence(annotation.getChunk(tag2));
			formalppi.addModelElement(best);
			if (best.getModelElement().getType() == ElementType.DATAOBJECT){
				formalppi.setType(MeasureType.DATA);
			}
		}
		return formalppi;
	}
	
	public FractionPPI alignFractionalPPI(SimilarityScores scores, PPIAnnotation annotation) {
		FractionPPI formalppi = new FractionPPI(annotation);
		
		formalppi.setGrouping(findGroupBy(scores, annotation));
		
		String tag1 = "FNU";
		String tag1b = "CA";
		Correspondence numerator;
		if (annotation.hasTag(tag1)) {
			numerator = scores.getHighestCorrespondence(annotation.getChunk(tag1));
		} else {
			numerator = scores.getHighestCorrespondence(annotation.getChunk(tag1b));
		}
		formalppi.setNumerator(numerator);
		String tag2 = "FDE";
		Correspondence denominator = null;
		if (annotation.hasTag(tag2)) {
			denominator = scores.getHighestCorrespondence(annotation.getChunk(tag2));
			formalppi.setDenominator(denominator);
		}
		if (denominator == null) {
			denominator = getParent(numerator);
			if (denominator != null) {
				formalppi.setDenominator(denominator);
			}
		}
		if (numerator.getModelElement().getType() == ElementType.DATAOBJECT ||
				(denominator != null && denominator.getModelElement().getType() == ElementType.DATAOBJECT)) {
			formalppi.setType(MeasureType.DATA);
		}
		return formalppi;
	}
	
	private Correspondence getParent(Correspondence numerator) {
		if (numerator.getModelElement().getType() == ElementType.DATAOBJECT) {
			String l1 = numerator.getModelElement().getLabel();
			for (ModelElement m : model.getDataObjects()) {
				if (numerator.getModelElement().getLabel().contains(m.getLabel()) && !m.getLabel().equals(l1)) {
					return new Correspondence(m, new Chunk(""), 0.0);
				}
			}
		}
		return null;
	}
	

	public TimePPI alignTemporalPPI(SimilarityScores scores, PPIAnnotation annotation) {
		TimePPI formalppi = new TimePPI(annotation);
		formalppi.setAggregator(findAggregator(scores, annotation));
		formalppi.setGrouping(findGroupBy(scores, annotation));
		String tag2 = "TFR";
		if (annotation.hasTag(tag2)) {
			Correspondence best = scores.getHighestCorrespondence(annotation.getChunk(tag2), ElementType.ACTIVITY);
			formalppi.setStart(best);
		}
		String tag3 = "TTO";
		if (annotation.hasTag(tag3)) {
			Correspondence best = scores.getHighestCorrespondence(annotation.getChunk(tag3), ElementType.ACTIVITY);
			formalppi.setEnd(best);
		}
		String tag4 = "TSE";
		if (annotation.hasTag(tag4)) {
			Correspondence best = scores.getHighestCorrespondence(annotation.getChunk(tag4), ElementType.ACTIVITY);
			formalppi.setStart(best);
			formalppi.setEnd(best);
		}
		if (formalppi.getEnd() == null) {
			formalppi.setEnd(formalppi.getStart());
		}
		return formalppi;
	}
	
	private Correspondence findAggregator(SimilarityScores scores, PPIAnnotation annotation) {
		if (annotation.hasTag(Tags.AGGR_TAG)) {
			Correspondence best = scores.getHighestCorrespondence(annotation.getChunk(Tags.AGGR_TAG));
			return best;
		}
		return null;
	}
	
	private Correspondence findGroupBy(SimilarityScores scores, PPIAnnotation annotation) {
		if (annotation.hasTag(Tags.GROUPBY_TAG)) {
			Correspondence best = new Correspondence(annotation.getChunk(Tags.GROUPBY_TAG).getWordString(), annotation.getChunk(Tags.GROUPBY_TAG));
			return best;
		}
		return null;
	}
	
	private SimilarityScores computeSimilarityScores(PPIAnnotation annotation, ProcessModelWrapper model) {
		SimilarityScores scores = new SimilarityScores();
		
		for (Chunk chunk : annotation.getChunks()) {
			List<String> words1 = _nlp.preprocessStringList(chunk.getWords());
			if (Tags.EVENT_TAGS.contains(chunk.getTag())) {
				for (ModelElement modelElement : model.getModelElements()) {
					if (!modelElement.isPreprocessed()) {
						modelElement.setTerms(_nlp.preprocessString(modelElement.getLabel()));
					}
					List<String> words2 = modelElement.getTerms();
					double score = _nlp.getScorer(annotation.getCaseid()).computeSimilarity(words1, words2);
					scores.addCorrespondence(new Correspondence(modelElement, chunk, score));
				}
			}
			if (Tags.AGGR_TAG.equals(chunk.getTag())) {
				for (Aggregator a : Aggregator.values()) {
					List<String> words2 = _nlp.preprocessString(a.toString());
					double score = _nlp.getScorer(annotation.getCaseid()).computeSimilarity(words1, words2);
					scores.addCorrespondence(new Correspondence(a, chunk,score));
				}
			}
		}
		return scores;
	}
	
}
