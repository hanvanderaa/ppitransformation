package ppitransformation.decoding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ppitransformation.parsing.Tags;
import ppitransformation.ppi.MeasureType;
import ppitransformation.ppi.PPI;

public class PPIAnnotation {

	String caseid;
	String ppiid;
	String original;
	List<Chunk> chunks;
	Chunk current;
	MeasureType type;
	
	public PPIAnnotation() {
		chunks = new ArrayList<Chunk>();
		current = null;
		type = MeasureType.UNDEFINED;
	}
	
	public void setParentInfo(PPI ppi) {
		this.caseid = ppi.getCaseID();
		this.ppiid = ppi.getID();
		this.original = ppi.getOriginal();
	}
	
	
	
	public String getCaseid() {
		return caseid;
	}

	public void setCaseid(String caseid) {
		this.caseid = caseid;
	}

	public String getPpiid() {
		return ppiid;
	}

	public void setPpiid(String ppiid) {
		this.ppiid = ppiid;
	}

	public String getOriginal() {
		return original;
	}

	public void setOriginal(String original) {
		this.original = original;
	}

	public void addWordAnnotation(String word, String tag) {
		if (!tag.equals("I")) {
			current = new Chunk(tag);
			chunks.add(current);
		}
		if (!word.isEmpty()) {
			current.addWord(word);
		}
	}
	
	public List<String> getTagSequence() {
		List<String> tags = new ArrayList<String>();
		for (Chunk chunk : chunks) {
			tags.add(chunk.getTag());
		}
		return tags;
	}
	
	public String getLastTag() {
		return chunks.get(chunks.size() - 1).getTag();
	}

	public List<Chunk> getChunks() {
		return chunks;
	}
	
	public boolean hasTag(String tag) {
		return getChunk(tag) != null;
	}
	
	public Chunk getChunk(String tag) {
		for (Chunk chunk : chunks) {
			if (chunk.getTag().equals(tag)) {
				return chunk;
			}
		}
		return null;
	}
	
	public String getWordString() {
		StringBuilder sb = new StringBuilder();
		for (Chunk chunk : chunks) {
			sb.append(chunk.getWordString() + " ");
		}
		return sb.toString().trim();
	}
	
	public boolean hasSameTagSequence(PPIAnnotation annotation) {
		List<String> tags1 = this.getTagSequence();
		List<String> tags2 = annotation.getTagSequence();
		
		tags1.removeAll(Collections.singleton(Tags.DUMMY_TAG));
		tags2.removeAll(Collections.singleton(Tags.DUMMY_TAG));
		
		return tags1.equals(tags2);
	}
	
	public boolean hasSameAnnotation(PPIAnnotation annotation) {
		return annotation.getChunks().equals(this.getChunks());
	}
	
	public String toString() {
		return chunks.toString();
	}
	
	public void setType(MeasureType type) {
		this.type = type;
	}
	
	public MeasureType getType() {
		List<String> tags = getTagSequence();
		for (String tag : tags) {
			if (Tags.TIME_TAGS.contains(tag)) {
				return MeasureType.TIME;
			}
			if (Tags.FRACTION_TAGS.contains(tag)) {
				return MeasureType.FRACTION;
			}
		}
		for (String tag : tags) {
			if (Tags.COUNT_TAGS.contains(tag)) {
				return MeasureType.COUNT;
			}
		}

		return MeasureType.UNDEFINED;
	}
	
}
