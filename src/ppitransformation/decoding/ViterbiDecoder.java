package ppitransformation.decoding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import ppitransformation.markovmodels.HMM;
import ppitransformation.markovmodels.LexicalizationModel;
import ppitransformation.markovmodels.SemanticModel;
import ppitransformation.markovmodels.Transition;
import ppitransformation.parsing.NLProcessor;
import ppitransformation.parsing.Tags;
import ppitransformation.utils.ListUtils;

public class ViterbiDecoder {

	final SemanticModel _semModel;
	final LexicalizationModel _lexModel;
	final NLProcessor _nlp;
	List<ViterbiState> paths;
	
	
	public ViterbiDecoder(NLProcessor nlp, HMM hmm) {
		_nlp = nlp;
		this._semModel = hmm.getSemModel();
		this._lexModel = hmm.getLexModel();
	}

	public ViterbiState createPrediction(String ppiid, String s) {
		List<String> words = ListUtils.splitStringToList(s);
		paths = computeInitialPaths(words.get(0));
	
		for (int i = 1; i < words.size(); i++) {
			String word = words.get(i);
			List<ViterbiState> newPaths = new ArrayList<ViterbiState>();
			for (ViterbiState path : paths) {
				newPaths.addAll(computeNextPaths(path, word));
			}
			if (newPaths.isEmpty()) {
				for (ViterbiState path : paths) {
					newPaths.addAll(computeSkipPaths(path, word));
				}
			}
			if (newPaths.isEmpty()) {
				for (ViterbiState path : paths) {
					newPaths.addAll(computeSemanticPaths(path, word));
				}
			}
			paths = newPaths;
		}
		Iterator<ViterbiState> iter = paths.iterator();
		while (iter.hasNext()) {
			if (_semModel.probability(iter.next()) == 0.0 ) {
				iter.remove();
			}
		}
		
		if (paths.isEmpty()) {
			return createLessRestrictedPrediction(ppiid, s);
		}
		return maxProbability();
	}
	
	private ViterbiState maxProbability() {
		ViterbiState best = new ViterbiState();
		for (ViterbiState path : paths) {
			if (_semModel.isAccepting(path.getLastTag()) && path.getProbability() > best.getProbability()) {
				best = path;
			}
		}
		if (best.isEmpty()) {
			for (ViterbiState path : paths) {
				if (path.getProbability() >= best.getProbability()) {
					best = path;
				}
			}
		}
		best.closeAnnotation();
		return best;
	}
	
	
	private List<ViterbiState> computeInitialPaths(String firstWord) {
		List<ViterbiState> initPaths = new ArrayList<ViterbiState>();
		
		for (Transition t : _semModel.getTransitionsFrom(Tags.START_TAG)) {
			String tag = t.getDestination().getLabel();
			int outgoing = _lexModel.outgoingTransitionCount(tag);
			if (outgoing > 0) { 
				double p1 = _semModel.getProbability(Tags.START_TAG, tag);
				int cwk = _lexModel.getSubStringOccurrences(firstWord, tag);
				Chunk chunk = new Chunk(tag, firstWord);
				double prob = p1 * (cwk) / (outgoing) ;
				if (prob > 0.0) {
					initPaths.add(new ViterbiState(chunk, cwk, prob));
				}
			}
		}
		if (initPaths.isEmpty()) {
			return createSemanticInitialPaths(firstWord);
		}
		return initPaths;
		}
	
	
	private List<ViterbiState> computeNextPaths(ViterbiState state, String word) {
		List<ViterbiState> newPaths = new ArrayList<ViterbiState>();
		boolean generateNextTags = true;
		// compute states for transitions to next tag
		if (Tags.DIVIDER_TAGS.contains(state.getLastTag())) {
			generateNextTags = _lexModel.sequenceEqualsReachableState(state.getLastChunk().getWordSequence(), state.getLastTag());
		}
		if (generateNextTags) {
			for (Transition t: _semModel.getTransitionsFrom(state.getLastTag())) {
				String tag = t.getDestination().getLabel();
				double p1 = t.probability();
				int cwk = _lexModel.getSubStringOccurrences(word, tag);
				int outgoing = _lexModel.outgoingTransitionCount(tag);
				Chunk newChunk = new Chunk(tag, word);
				double prob = p1 * cwk / outgoing;
				if (prob > 0.0) {
					newPaths.add(new ViterbiState(state, newChunk, cwk, prob));
				}
			}
		}
		// add state for within tag continuation
		Chunk newChunk = new Chunk(state.getLastChunk(), word);
		int cwk = _lexModel.getSubStringOccurrences(newChunk.getWordSequence(), state.getLastTag());
		double prob = cwk * 1.0 / state.getCwk();
		if (prob > 0.0) {
			state.removeLastChunk();
			newPaths.add(new ViterbiState(state, newChunk, cwk, prob));
		} 
//		else if (Tags.EVENT_TAGS.contains(state.getLastTag())) {
//			newPaths.add(new ViterbiState(state, newChunk, cwk, 1.0 / Integer.MAX_VALUE));
//		}
		return newPaths;
	}
	
	private List<ViterbiState> computeSkipPaths(ViterbiState state, String word) {
		List<ViterbiState> newPaths = new ArrayList<ViterbiState>();
		boolean generateNextTags = true;
		// compute states for transitions to next tag
		if (Tags.DIVIDER_TAGS.contains(state.getLastTag())) {
			generateNextTags = _lexModel.sequenceEqualsReachableState(state.getLastChunk().getWordSequence(), state.getLastTag());
		}
		if (generateNextTags) {
			for (Transition t: _semModel.getTransitionsFrom(state.getLastTag())) {
				String tag = t.getDestination().getLabel();
				if (Tags.EVENT_TAGS.contains(tag)) {
					double p1 = t.probability();
					//			int cwk = lexModel.getSubStringOccurrences(word, tag);
					int outgoing = _lexModel.outgoingTransitionCount(tag);
					Chunk newChunk = new Chunk(tag, word);
					double prob = p1 * 1 / outgoing;
					if (prob > 0.0 && !tag.equals(Tags.END_TAG)) {
						newPaths.add(new ViterbiState(state, newChunk, 1, prob));
					}
				}
			}
		}
		// add state for within tag continuation
		// we only do this for Non-indicator tags
		if (Tags.EVENT_TAGS.contains(state.getLastTag())) {
			Chunk newChunk = new Chunk(state.getLastChunk(), word);
			//		int cwk = lexModel.getSubStringOccurrences(newChunk.getWordString(), state.getLastTag());
			int cwk = 1;
			double prob = cwk * 1.0 / state.getCwk();
			if (prob > 0.0 ) {
				state.removeLastChunk();
				newPaths.add(new ViterbiState(state, newChunk, cwk, prob));
			}
		}
		return newPaths;
	}
	
	private List<ViterbiState> computeSemanticPaths(ViterbiState state, String word) {
		List<ViterbiState> newPaths = new ArrayList<ViterbiState>();
		for (String typeTag : Tags.TYPE_TO_WORD_MAP.keySet()) {
			double simscore = _nlp.scorer().getSimilarity(word, Tags.TYPE_TO_WORD_MAP.get(typeTag));
			//			int cwk = lexModel.getSubStringOccurrences(word, tag);
			int outgoing = _lexModel.outgoingTransitionCount(typeTag);
			Chunk newChunk = new Chunk(typeTag, word);
			double prob = simscore * 1 / outgoing;
			if (prob > 0.0) {
				newPaths.add(new ViterbiState(state, newChunk, 1, prob));
			}
		}
		return newPaths;
	}
	
	private List<ViterbiState> createSemanticInitialPaths(String firstWord) {
		List<ViterbiState> initPaths = new ArrayList<ViterbiState>();
		ViterbiState best = null;
		
		firstWord = _nlp.lemmatizer().lemmatizeSingleTerm(firstWord);
		for (String typeTag : Tags.TYPE_TO_WORD_MAP.keySet()) {
			double simscore = _nlp.scorer().getSimilarity(firstWord, Tags.TYPE_TO_WORD_MAP.get(typeTag));
			if (simscore > 0.0) {
				Chunk chunk = new Chunk(typeTag, firstWord);
				ViterbiState newPath = new ViterbiState(chunk, 1, simscore); 
				initPaths.add(newPath);
				if (best == null || simscore > best.getProbability()) {
					best = newPath;
				}
			}
		}
		return Collections.singletonList(best);
//		return initPaths;
	}
	
	private ViterbiState createLessRestrictedPrediction(String ppiid, String s) {
		List<String> words = ListUtils.splitStringToList(s);
		paths = computeInitialPaths(words.get(0));
	
		for (int i = 1; i < words.size(); i++) {
			String word = words.get(i);
			List<ViterbiState> newPaths = new ArrayList<ViterbiState>();
			for (ViterbiState path : paths) {
				newPaths.addAll(computeNextPathsLessRestricted(path, word));
			}
			if (newPaths.isEmpty()) {
				for (ViterbiState path : paths) {
					newPaths.addAll(computeSkipPaths(path, word));
				}
			}
			if (newPaths.isEmpty()) {
				for (ViterbiState path : paths) {
					newPaths.addAll(computeSemanticPaths(path, word));
				}
			}
			paths = newPaths;
		}
		Iterator<ViterbiState> iter = paths.iterator();
		while (iter.hasNext()) {
			if (_semModel.probability(iter.next()) == 0.0 ) {
				iter.remove();
			}
		}
		return maxProbability();
	}
	
	private List<ViterbiState> computeNextPathsLessRestricted(ViterbiState state, String word) {
		List<ViterbiState> newPaths = new ArrayList<ViterbiState>();
		boolean generateNextTags = true;
		// compute states for transitions to next tag
		if (Tags.DIVIDER_TAGS.contains(state.getLastTag())) {
			generateNextTags = _lexModel.sequenceEqualsReachableState(state.getLastChunk().getWordSequence(), state.getLastTag());
		}
		if (generateNextTags) {
			for (Transition t: _semModel.getTransitionsFrom(state.getLastTag())) {
				String tag = t.getDestination().getLabel();
				double p1 = t.probability();
				int cwk = _lexModel.getSubStringOccurrences(word, tag);
				int outgoing = _lexModel.outgoingTransitionCount(tag);
				Chunk newChunk = new Chunk(tag, word);
				double prob = p1 * cwk / outgoing;
				if (prob > 0.0) {
					newPaths.add(new ViterbiState(state, newChunk, cwk, prob));
				}
			}
		}
		// add state for within tag continuation
		Chunk newChunk = new Chunk(state.getLastChunk(), word);
		int cwk = _lexModel.getSubStringOccurrences(newChunk.getWordSequence(), state.getLastTag());
		double prob = cwk * 1.0 / state.getCwk();
		if (prob > 0.0) {
			state.removeLastChunk();
			newPaths.add(new ViterbiState(state, newChunk, cwk, prob));
		} 
		else if (Tags.EVENT_TAGS.contains(state.getLastTag())) {
			state.removeLastChunk();
			newPaths.add(new ViterbiState(state, newChunk, cwk, 1.0 / Integer.MAX_VALUE));
		}
		
		return newPaths;
	}
	
}
