package ppitransformation.utils;

import java.util.Arrays;
import java.util.List;

public class ListUtils {
	
	public static int countSequenceStartOccurrences(String[] seq, String[] subseq) {
		if (containsSequenceOccurrence(seq, subseq, 0)) {
			return 1;
		}
		return 0;
	}
	
	public static boolean equalSequences(String[] seq1, String[] seq2) {
		if (seq1.length != seq2.length) {
			return false;
		}
		for (int i = 0; i < seq1.length; i++) {
			if (!seq1[i].equals(seq2[i])) {
				return false;
			}
		}
		return true;
	}
	
	public static int countSequenceOccurrences(String[] seq, String[] subseq) {
		int count = 0;
		for (int i = 0; i <= seq.length - subseq.length; i++) {
			if (containsSequenceOccurrence(seq, subseq, i)) {
				count++;
			}
		}
		return count;
	}
	
	private static boolean containsSequenceOccurrence(String[] seq, String[] subseq, int start) {
		for (int i = 0; i < subseq.length; i++) {
			if ((start + i >= seq.length) || (!seq[start + i].equals(subseq[i]))) {
				return false;
			}
		}
		return true;
	}
	
	public static List<String> splitStringToList(String s) {
		return Arrays.asList(splitStringToArray(s));
	}
	
	public static String[] splitStringToArray(String s) {
		s = s.replace("\\", " or ");
		s = s.replace("/", " or ");
		s = s.replace(",", "");
//		s = s.replaceAll("[^A-Za-z0-9]", " ");
		s = s.trim();
		return s.split(" ");
	}
	
	public static double round(double val, int dec) {
		return Math.round(val * Math.pow(10.0, dec)) / Math.pow(10.0, dec);
	}
	
	public static double stdev(int[] numbers) {
		double avg = avg(numbers);
		double sd = 0.0;
		for (int i=0; i < numbers.length;i++) {
		    sd = sd + Math.pow(numbers[i] - avg, 2);
		}
		sd = sd / numbers.length;
		sd = Math.sqrt(sd);
		return sd;
	}
	
	public static double stdev(double[] numbers) {
		double avg = avg(numbers);
		double sd = 0.0;
		for (int i= 0; i < numbers.length;i++) {
		    sd = sd + Math.pow(numbers[i] - avg, 2);
		}
		sd = sd / numbers.length;
		sd = Math.sqrt(sd);
		return sd;
	}
	
	public static double avg(double[] numbers) {
		double sum = 0.0;
		for (int i = 0; i < numbers.length; i++) {
			sum += numbers[i];
		}
		return sum  / numbers.length;
	}
	
	public static double max(double[] numbers) {
		double max = 0.0;
		for (int i = 0; i < numbers.length; i++) {
			if (numbers[i] > max) {
				max = numbers[i];
			}
		}
		return max;
	}

	public static double avg(int[] numbers) {
		int sum = 0;
		for (int i = 0; i < numbers.length; i++) {
			sum += numbers[i];
		}
		return sum * 1.0 / numbers.length;
	}
	
	public static int max(int[] numbers) {
		int max = 0;
		for (int i = 0; i < numbers.length; i++) {
			if (numbers[i] > max) {
				max = numbers[i];
			}
		}
		return max;
	}
	
	public static String avgAndSTd(int[] numbers) {
		return round(avg(numbers), 2) + " (" + round(stdev(numbers),4) + ", max: " + max(numbers) + ")";
	}
	
	public static String precisionStr(int[] tps, int[] fps) {
		double[] result = new double[tps.length];
		for (int i = 0; i < tps.length; i++) {
			result[i] = precision(tps[i], fps[i]);
		}
		return round(avg(result), 2) + " (std: " + round(stdev(result),4) + ", max: " + round(max(result), 2) + ")";
	}
	
	public static String recallStr(int[] tps, int[] fns) {
		double[] result = new double[tps.length];
		for (int i = 0; i < tps.length; i++) {
			result[i] = recall(tps[i], fns[i]);
		}
		return round(avg(result), 2) + " (std: " + round(stdev(result),4) + ", max: " + round(max(result), 2) + ")";
	}
	
	public static double precision(int tp, int fp) {
		return tp * 1.0 / (tp + fp);
	}
	
	public static double recall(int tp, int fn) {
		return tp * 1.0 / (tp + fn);
	}
}
