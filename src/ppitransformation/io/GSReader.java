package ppitransformation.io;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.opencsv.CSVReader;

import ppitransformation.ppi.templates.GSTemplate;
import ppitransformation.utils.IOUtils;

public class GSReader {

	public static Map<String, Map<String, GSTemplate>> importGSFiles(String folder, String[] ignoreCases) throws IOException {
		Map<String, Map<String, GSTemplate>> result = new HashMap<String, Map<String, GSTemplate>>();
		for (File file : IOUtils.getFilesWithExtension(new File(folder), ".csv")) {
			String caseid = IOUtils.getFileName(file).split(" ")[0];
			if (!Arrays.asList(ignoreCases).contains(caseid)) {
				result.put(caseid, importGSFile(file.getAbsolutePath()));
			}
		}
		return result;
	}
	
	public static Map<String, GSTemplate> importGSFile(String filepath) throws IOException {
		System.out.println("loading: " + filepath);
		Map<String, GSTemplate> resultMap = new HashMap<String, GSTemplate>();
		
		 CSVReader reader = new CSVReader(new FileReader(filepath), ';');
	     String [] nextLine;
	     
	     // skip header
	     reader.readNext();
	     
	     // read identifier map
	     Map<String, String> idMap = new HashMap<String, String>();
	     while ((nextLine = reader.readNext()) != null && !nextLine[0].equals("id1")) {
	     }
	     while ((nextLine = reader.readNext()) != null) {
	    	 idMap.put(nextLine[0], nextLine[1]);
	     }
	     
	     reader = new CSVReader(new FileReader(filepath), ';');
	     
	     // skip header
	     reader.readNext();
	     
	     // read gold standard
	     while ((nextLine = reader.readNext()) != null && nextLine.length > 1 && !nextLine[1].isEmpty()) {
	    	 GSTemplate gs = new GSTemplate();
	    	 gs.setId(nextLine[1]);
	    	 gs.setOriginal(nextLine[2]);
	    	 gs.setTypeString(nextLine[3]);
	    	 if (nextLine.length > 4 && nextLine[4] != null && !nextLine[4].isEmpty()) {
	    		 gs.setAggrString(nextLine[4]);
	    	 }
	    	 
	    	 if (nextLine.length > 5 && nextLine[5] != null && !nextLine[5].isEmpty()) {
	    		 gs.setMatchIDs1(parseGSCell(idMap, nextLine[5]));
	    	 }
	    	 if (nextLine.length > 6 && nextLine[6] != null && !nextLine[6].isEmpty()) {
	    		 gs.setMatchIDs2(parseGSCell(idMap, nextLine[6]));
	    	 }	 
	    	 if (nextLine.length > 7 && nextLine[7] != null && !nextLine[7].isEmpty()) {
	    		 gs.setGroupByString(nextLine[7]);
	    	 }
	    	 
	    	 resultMap.put(nextLine[1], gs);
	     }
	     reader.close();
	     return resultMap;
	}
	
	private static List<Set<String>> parseGSCell(Map<String,String> idMap, String cellValue) {
		List<Set<String>> result = new ArrayList<Set<String>>();
		String[] split = cellValue.split("\\.");
		for (String s : split) {
			result.add(toIDSet(idMap, s));
		}
		return result;
	}
	
	private static Set<String> toIDSet(Map<String,String> idMap, String s) {
		Set<String> set = new HashSet<String>();
		if (!s.contains("{")) {
			set.add(idMap.get(s));
			return set;
		}
		s = s.substring(1, s.length() - 1);
		String[] split = s.split(",");
		for (String s2 : split) {
			s2 = s2.trim();
			set.add(idMap.get(s2));
		}
		return set;
	}

}
