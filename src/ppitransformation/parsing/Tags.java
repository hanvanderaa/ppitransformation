package ppitransformation.parsing;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tags {

	public static final String START_WORD = "<s>";
	public static final String START_TAG = "START";
	
	public static final String END_WORD = "</s>";
	public static final String END_TAG = "END";
	
	public static final String DUMMY_TAG = "DUMMY";
	public static final String AGGR_TAG = "AGR";
	public static final String GROUPBY_TAG = "GBC";
	
	
	public static final List<String> EVENT_TAGS = Arrays.asList("CE", "FNE", "FDE", "TSE", "TEE", "TBE", "GBC");
	public static final List<String> DIVIDER_TAGS = Arrays.asList("FDI", "TEI");
	public static final List<String> TYPE_TAGS = Arrays.asList("FMI", "TMI", "CMI");
	
	public static final List<String> TIME_TAGS = Arrays.asList("TMI", "TSI", "TSI", "TSE", "TEI", "TEE", "TBE");
	public static final List<String> COUNT_TAGS = Arrays.asList("CMI", "CE");
	public static final List<String> FRACTION_TAGS = Arrays.asList("FMI", "FNE", "FDI", "FDE");
	
	public static final Map<String, String> TYPE_TO_WORD_MAP;
    static
    {
    	TYPE_TO_WORD_MAP = new HashMap<String, String>();
    	TYPE_TO_WORD_MAP.put("TMI", "time");
    	TYPE_TO_WORD_MAP.put("FMI", "percentage");
    	TYPE_TO_WORD_MAP.put("CMI", "number");
    }
	

}
