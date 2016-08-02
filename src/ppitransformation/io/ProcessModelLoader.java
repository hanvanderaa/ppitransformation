package ppitransformation.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

import org.jbpt.pm.ProcessModel;
import org.json.JSONException;

import bpmnimporter.importer.BPMNImporter;
import ppitransformation.processmodel.PMCollection;
import ppitransformation.processmodel.ProcessModelWrapper;

public class ProcessModelLoader {
	
	
	
	public static PMCollection importJSONModels(String folder, String[] ignoreCases) {
		PMCollection models = new PMCollection();
		try {
			List<ProcessModel> jbpts = BPMNImporter.importModels(new File(folder));
			
			for (ProcessModel jbpt : jbpts) {
				if (!Arrays.asList(ignoreCases).contains(jbpt.getName())) {
					models.add(new ProcessModelWrapper(jbpt.getName(), jbpt));
				}
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return models;
	}
}
