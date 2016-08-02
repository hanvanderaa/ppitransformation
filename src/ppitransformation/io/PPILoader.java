package ppitransformation.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.opencsv.CSVReader;

import ppitransformation.decoding.PPIAnnotation;
import ppitransformation.parsing.ParsingUtils;
import ppitransformation.parsing.Tags;
import ppitransformation.ppi.MeasureType;
import ppitransformation.ppi.PPI;
import ppitransformation.ppi.PPICollection;
import ppitransformation.utils.IOUtils;

public class PPILoader {

	
	
	public static PPICollection loadAnnotatedPPIs(String folder, String[] ignoreCases) throws IOException {
		PPICollection collection = new PPICollection();
		
		CSVReader reader = null;
		
		for (File file : IOUtils.getFilesWithExtension(new File(folder), ".csv")) {
			
			reader = new CSVReader(new FileReader(file.getAbsolutePath()), ';');
			String[] nextLine;
			PPI ppi = null;
			PPIAnnotation annotation = null;
			
			while ((nextLine = reader.readNext()) != null) {
				String word = nextLine[0].toLowerCase();
				String tag = nextLine[1];
				if (ParsingUtils.isAbbreviation(word)) {
					word = ParsingUtils.resolveAbbreviation(word);
				}
				if (word.equals(Tags.START_WORD) || tag.equals(Tags.START_TAG)) {
					String caseid = nextLine[2];
					String ppiid = nextLine[3];
					String typestring = nextLine[4];
					MeasureType type = MeasureType.getType(typestring);
					ppi = new PPI(caseid, ppiid, type);
					annotation = new PPIAnnotation();
					annotation.addWordAnnotation("", Tags.START_TAG);
				}
				else if (word.equals(Tags.END_WORD) || tag.equals(Tags.END_TAG)) {
					annotation.addWordAnnotation("", Tags.END_TAG);
					ppi.setAnnotation(annotation);
					if (!Arrays.asList(ignoreCases).contains(ppi.getCaseID())) {
						collection.add(ppi);
					}
				} else {
					word = word.replace(",", "");
					annotation.addWordAnnotation(word, tag);
				}
			}
		}
		reader.close();
		return collection;
	}
	
	
	
	// method for loading caise files
	public static Map<String, PPICollection> importPPICollectionsFromCSVs(String folder) throws IOException {
		Map<String, PPICollection> map = new HashMap<String, PPICollection>();
		for (File file : IOUtils.getFilesWithExtension(new File(folder), ".csv")) {
			map.put(IOUtils.getFileName(file), importPPICSVFile(file.getAbsolutePath()));
		}
		return map;
	}

	
	public static PPICollection importPPICollectionsFromCSV(String filepath) throws IOException {
		return importPPICSVFile(filepath);
	}
	
	public static PPICollection importPPICSVFile(String filepath) throws IOException {
		System.out.println("loading: " + filepath);
		PPICollection collection = new PPICollection();
		
		 CSVReader reader = new CSVReader(new FileReader(filepath), ';');
	     String [] nextLine;
	     
	     // skip header
	     reader.readNext();
	     
	     // read activities
	     while ((nextLine = reader.readNext()) != null) {
	    	 int caseID = Integer.parseInt(("" + IOUtils.getFileName(new File(filepath)).substring(0, 2)).trim());
	    	 int id = Integer.parseInt(nextLine[0]);
	    	 String description = nextLine[1];
	    	 String typeString = nextLine[2];
	    	 MeasureType type = MeasureType.getType(typeString);
	    	 PPI ppi = new PPI(caseID, id, description, type);
	    	 collection.add(ppi);
	    	 

	    	 
	     }
	     reader.close();
	     return collection;
	}



	public static PPICollection loadTextualDescriptions(int modelid, String filepath) throws IOException {
		System.out.println("loading: " + filepath);
		PPICollection collection = new PPICollection();
		
		FileInputStream fis = new FileInputStream(new File(filepath));
		 
		//Construct BufferedReader from InputStreamReader
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
	 
		String line = null;
		int id = 1; 
		while ((line = br.readLine()) != null) {
			PPI ppi = new PPI(modelid, id, line.toLowerCase(), MeasureType.UNDEFINED);
			collection.add(ppi);
			id++;
		}
	 
		br.close();
		return collection;
	}
	
	

}
