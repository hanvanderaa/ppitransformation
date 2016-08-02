package ppitransformation.similarity.utils;

import java.util.Arrays;

public class SimilarityHelper {

	public static  String[] corrected = new String[]{
		"receive-receipt", "packaging-pack", "verification-verify", "production-produce", "submission-submit", "closed-closure", "total-sum", "completion-completed", "received-receipt"
	};
	
	
	public static boolean isCorrected(String key) {
		return Arrays.asList(corrected).contains(key);
	}
}
