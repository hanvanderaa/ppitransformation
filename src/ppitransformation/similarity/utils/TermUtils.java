package ppitransformation.similarity.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import edu.stanford.nlp.util.StringUtils;

public class TermUtils {
	
	public static String[] DETERMINERS =
			new String[]{"the", "a", "an", "this", "that", "and", "or"};

	
	public static String[] SPECIFIER_PREPOSITIONS = 
			new String[]{"with", "per", "for", "to", "from", "of", "in", "by",
						 "within", "as", "at", "where", "who", "if", "under"};
	
	public static String[] FRACTION_SPECIFIERS = 
			new String[]{"from"};
	
	public static String[] ACTOR_PREPOSITIONS = 
			new String[]{"by"};
	
	public static String[] STATE_PREPOSITIONS = 
			new String[]{"due to", "under", "in"};

	
	public static String[] FUNCTION_OPERATIONS = 
			new String[]{"total", "maximum", "max.", "minimum", "min.", "average", "all", "any", "divided"};


	public static String[] OTHER_PREPOSITIONS = 
			new String[]{};
	
	public static String[] PREPOSITIONS() {
		String[] result = ArrayUtils.addAll(ACTOR_PREPOSITIONS, STATE_PREPOSITIONS);
		result = ArrayUtils.addAll(result, SPECIFIER_PREPOSITIONS);
		result = ArrayUtils.addAll(result, OTHER_PREPOSITIONS);
		return result;
	}
	
	
	public static Boolean inArray(String[] array, String s) {
		return Arrays.asList(array).contains(s.toLowerCase());
	}
	
	
	public static Boolean isPreposition(String s) {
		return inArray(PREPOSITIONS(), s);
	}
	
	public static boolean isOperator(String s) {
		return inArray(FUNCTION_OPERATIONS, s);
	}
	
	public static Boolean isSpecifier(String s) {
		return inArray(SPECIFIER_PREPOSITIONS, s);
	}
	
	public static Boolean isDeterminer(String s) {
		return inArray(DETERMINERS, s);
	}
	
	public static int MAX_PREP_SIZE() {
		int max = 0;
		for (int i = 0; i < PREPOSITIONS().length; i++) {
			int s = PREPOSITIONS()[i].split(" ").length;
			if (s > max) {
				max = s;
			}
		}
		return max;
	}
	
	public static List<String> filterStopwords(List<String> lemmas) {
		List<String> filtered = new ArrayList<String>();
		for (String lemma : lemmas) {
			if (!isDeterminer(lemma) && StringUtils.isAlpha(lemma)) {
				filtered.add(lemma);
			}
		}
		return filtered;
	}
	

	
	
}
