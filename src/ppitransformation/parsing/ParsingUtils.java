package ppitransformation.parsing;

import java.util.HashMap;
import java.util.Map;

public class ParsingUtils {

	private static final Map<String, String> ABBREVIATION_MAP;
    static
    {
    	ABBREVIATION_MAP = new HashMap<String, String>();
    	ABBREVIATION_MAP.put("%", "percentage");
    	ABBREVIATION_MAP.put("#", "number");
    }
    
    public static boolean isAbbreviation(String word) {
    	return ABBREVIATION_MAP.containsKey(word);
    }
    
    public static String resolveAbbreviation(String word) {
    	if (isAbbreviation(word)) {
    		return ABBREVIATION_MAP.get(word);
    	}
    	return word;
    }
}
