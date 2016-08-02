package ppitransformation.decoding;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class Chunk {

	List<String> words;
	String tag;
	
	public Chunk(String tag) {
		this.tag = tag;
		words = new ArrayList<String>();
	}
	
	public Chunk(String tag, String firstWord) {
		this(tag);
		words.add(firstWord);
	}
	
	public Chunk(Chunk old, String nextWord) {
		this.tag = old.getTag();
		words = new ArrayList<String>(old.getWords());
		words.add(nextWord);
	}
	
	public void addWord(String w) {
		words.add(w);
	}
	
	public String getTag() {
		return tag;
	}
	
	public List<String> getWords() {
		return words;
	}
	
	public String[] getWordSequence() {
		String[] seq = new String[words.size()];
		return words.toArray(seq);
	}
	
	public int countSequenceOccurrences(List<String> sequence) {
		int n = 0;
		for (int i = 0; i <= words.size() - sequence.size(); i++) {
			if (containsSequence(sequence, i)) {
				n++;
			}
		}
		return n;
	}
	
	private boolean containsSequence(List<String> sequence, int startindex) {
		for (int i = 0; i < sequence.size(); i++) {
			if (!words.get(startindex + i).equals(sequence.get(i))) {
				return false;
			}
		}
		return true;
	}
	
	public String getWordString() {
		return StringUtils.join(words, " ");
	}
	
	public String toString() {
		return getWordString() + "\\" + tag;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((tag == null) ? 0 : tag.hashCode());
		result = prime * result + ((words == null) ? 0 : words.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Chunk other = (Chunk) obj;
		if (tag == null) {
			if (other.tag != null)
				return false;
		} else if (!tag.equals(other.tag))
			return false;
		if (words == null) {
			if (other.words != null)
				return false;
		} else if (!words.equals(other.words))
			return false;
		return true;
	}
	
	
}
