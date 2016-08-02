package ppitransformation.processmodel;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class PMCollection {

	Map<String, ProcessModelWrapper> modelMap;
	
	public PMCollection() {
		modelMap = new LinkedHashMap<String, ProcessModelWrapper>();
	}
	
	public void add(ProcessModelWrapper model) {
		modelMap.put(model.getId(), model);
	}
	
	public ProcessModelWrapper getModel(String caseid) {
		return modelMap.get(caseid);
	}
	
	public Set<String> getCaseIDs() {
		return modelMap.keySet();
	}
}
