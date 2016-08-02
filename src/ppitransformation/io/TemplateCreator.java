package ppitransformation.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import com.opencsv.CSVWriter;

import ppitransformation.parsing.Tags;
import ppitransformation.ppi.MeasureType;
import ppitransformation.ppi.PPI;
import ppitransformation.ppi.PPICollection;
import ppitransformation.processmodel.ModelElement;
import ppitransformation.processmodel.PMCollection;
import ppitransformation.utils.ListUtils;

public class TemplateCreator {

	public static final String OLD_PPI_DIR = "input/caiseppis/";
	public static final String PPI_FILE = "41 incidentresolution.csv";
	public static final boolean SINGLE_FILE = true;
	
//	public static final String OUT_FILE = "output/templates/timetemplate.csv";
	public static final String OUT_DIR = "output/templates/";

	
	public void createGSTemplates(PMCollection models, PPICollection ppis) throws IOException {

		for (String caseid : models.getCaseIDs()) {

			String outFile = OUT_DIR +  caseid + "t.csv"; 
			CSVWriter writer = new CSVWriter(new FileWriter(new File(outFile), false), ';');
			String[] header = new String[]{"", "id", "original", "type", "aggregator", "elements1", "elements2", "scope"};
			writer.writeNext(header);
			for (PPI ppi : ppis.getPPIsFromCase(caseid)) {
				String[] ppiinfo = new String[]{"", ppi.getID(), ppi.getOriginal(), String.valueOf(ppi.getType())};
				writer.writeNext(ppiinfo);
			}
			writer.writeNext(new String[]{});
			writer.writeNext(new String[]{});
			header = new String[]{"id1", "id2", "label", "type"};
			writer.writeNext(header);
			int i = 1;
			for (ModelElement modelElement : models.getModel(caseid).getModelElements()) {
				String[] meinfo = new String[]{String.valueOf(i), modelElement.getId(), 
						modelElement.getLabel(), String.valueOf(modelElement.getType())};
				writer.writeNext(meinfo);
				i++;
			}
			writer.close();
		}

	}
	

	
	
	
	public void createPPIAnnotationTemplates() {
		try {

			if (SINGLE_FILE) {
				PPICollection ppis = PPILoader.importPPICollectionsFromCSV(OLD_PPI_DIR + PPI_FILE);
				
				String outFile = OUT_DIR +  PPI_FILE + ".template.csv"; 
				CSVWriter writer = new CSVWriter(new FileWriter(new File(outFile), false), ';');


				for (PPI ppi : ppis.getPPIs()) {
					writer.writeNext(new String[]{Tags.START_WORD, Tags.START_TAG, ppi.caseid(), ppi.getID(), String.valueOf(ppi.getType())});
					for (String s : ListUtils.splitStringToList(ppi.getOriginal())) {
						s = s.trim();
						if (!s.isEmpty()) {
							writer.writeNext(new String[]{s, ""});
						}
					}
					writer.writeNext(new String[]{Tags.END_WORD, Tags.END_TAG});
				}
				writer.close();

			} else {
				Map<String, PPICollection> ppiCollections = PPILoader.importPPICollectionsFromCSVs(OLD_PPI_DIR);
				
				for (MeasureType type : MeasureType.values()) {
					PPICollection ppis = new PPICollection();
					for (PPICollection ppiCollection : ppiCollections.values()) {
						for (PPI ppi : ppiCollection.getPPIs()) {
							if (ppi.getType() == type) {
								ppis.add(ppi);
							}
						}
					}

					if (!ppis.getPPIs().isEmpty()) {
						String outFile = OUT_DIR + String.valueOf(type) + "template.csv"; 
						CSVWriter writer = new CSVWriter(new FileWriter(new File(outFile), false), ';');


						for (PPI ppi : ppis.getPPIs()) {
							writer.writeNext(new String[]{Tags.START_WORD, Tags.START_TAG, ppi.caseid(), ppi.getID(), String.valueOf(ppi.getType())});
							for (String s : ListUtils.splitStringToList(ppi.getOriginal())) {
								s = s.trim();
								if (!s.isEmpty()) {
									writer.writeNext(new String[]{s, ""});
								}
							}
							writer.writeNext(new String[]{Tags.END_WORD, Tags.END_TAG});
						}
						writer.close();
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
