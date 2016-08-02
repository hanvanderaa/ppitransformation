package ppitransformation.ppi.templates;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ppitransformation.ppi.MeasureType;
import ppitransformation.processmodel.ModelElement;
import ppitransformation.processmodel.ProcessModelWrapper;

public class GSTemplate{

	String id;
	String original;
	List<Set<String>> ids1;
	List<Set<String>> ids2;
	List<Set<ModelElement>> elements1;
	List<Set<ModelElement>> elements2;
	String aggrString;
	String scopeString;
	String typeString;
	MeasureType type;
	Aggregator aggregator;
	String groupByString;
	
	public GSTemplate() {
		ids1 = new ArrayList<Set<String>>();
		ids2 = new ArrayList<Set<String>>();
		elements1 = new ArrayList<Set<ModelElement>>();
		elements2 = new ArrayList<Set<ModelElement>>();
	}
	
	public List<Set<ModelElement>> getElementSets() {
		List<Set<ModelElement>> result = new ArrayList<Set<ModelElement>>(elements1);
		result.addAll(elements2);
		return result;		
	}
	
	
	
	public void setId(String id) {
		this.id = id;
	}

	public void setOriginal(String original) {
		this.original = original;
	}

	public List<Set<ModelElement>> getMatches1() {
		return elements1;
	}
	
	public List<Set<ModelElement>> getMatches2() {
		return elements2;
	}
	
	public void setMatchIDs1(List<Set<String>> ids) {
		this.ids1 = ids;
	}
	
	public void setMatchIDs2(List<Set<String>> ids) {
		this.ids2 = ids;
	}
	
	public void setAggrString(String aggrString) {
		this.aggrString = aggrString;
	}

	public void setScopeString(String scopeString) {
		this.scopeString = scopeString;
	}

	public void setTypeString(String typeString) {
		this.typeString = typeString;
	}
	
	public int size() {
		return getElementSets().size();
	}
	
	public void resolveAbstraction(ProcessModelWrapper processModel) {
		type = MeasureType.getType(typeString);
		for (Set<String> idSet : ids1) {
				elements1.add(resolveAbstraction(processModel, idSet));
		}
		for (Set<String> idSet : ids2) {
				elements2.add(resolveAbstraction(processModel, idSet));
		}
		if (aggrString != null && !aggrString.isEmpty()) {
			aggregator = Aggregator.getAggregator(aggrString);
		}
		
	}
	
	private Set<ModelElement> resolveAbstraction(ProcessModelWrapper processModel, Set<String> idSet) {
		Set<ModelElement> elements = new HashSet<ModelElement>();
		for (String id : idSet) {
			elements.add(processModel.getElement(id));
		}
		return elements;
	}
	
	public String toString() {
		String result = id + " " + type + " " + original;
		for (Set<ModelElement> element : elements1) {
			result += "\n" + element;
		}
		if (!elements2.isEmpty()) {
			for (Set<ModelElement> element : elements2) {
				result += "\n" + element;
			}
		}
		return result;
	}

	public boolean containsCorrespondent(String id) {
		for (Set<ModelElement> set : getElementSets()) {
			for (ModelElement e : set) {
				if (e.getId().equals(id)) {
					return true;
				}
			}
		}
		if (aggrString != null && aggrString.equals(id)) {
			return true;
		}
		return false;
	}

	public int alignmentSize(boolean justModelElements) {
		// +1 for measure type
		int size = elements1.size() + elements2.size() + 1;
		if (!justModelElements && !getAggregatorString().isEmpty()) {
			size++;
		}
		if (!justModelElements && !getGroupByString().isEmpty()) {
			size++;
		}
		return size;
	}


	public String getAggregatorString() {
		if (aggregator == null) {
			return "";
		}
		return aggregator.toString();
	}
	
	public String getGroupByString() {
		if (groupByString == null) {
			return "";
		}
		return groupByString;
	}
	
	public List<String> correctCorrespondences(List<Correspondence> correspondences) { 
		List<String> result = new ArrayList<String>();
		for (Correspondence c : correspondences) {
			if (containsCorrespondent(c.getCorrespondentID())) {
				result.add(c.toString());
			}
		}
		return result;
	}
	
	public List<String> falseCorrespondences(List<Correspondence> correspondences) {
		List<String> result = new ArrayList<String>();
		for (Correspondence c : correspondences) {
			if (!containsCorrespondent(c.getCorrespondentID())) {
				result.add(c.toString());
			}
		}
		return result;
	}
	
	public List<String> missedCorrespondences(List<Correspondence> correspondences) { 
		List<String> result = new ArrayList<String>();
		for (Set<ModelElement> set : getElementSets()) {
			if (!containsElementFromSet(set, correspondences)) {
				result.add(String.valueOf(set));
			}
		}
		return result;
	}
	
	private boolean containsElementFromSet(Set<ModelElement> set, List<Correspondence> correspondences) {
		for (Correspondence c : correspondences) {
			for (ModelElement e : set) {
				if (e.getId().equals(c.getModelElement().getId())) {
					return true;
				}
			}
		}
		return false;
	}

	
	public MeasureType getType() {
		return type;
	}

	public void setGroupByString(String groupByString) {
		this.groupByString = groupByString;
	}
	
//	public boolean containsElement(ModelElement e) {
//		for (Set<ModelElement> set : getElementSets()) {
//			if (set.contains(e)) {
//				return true;
//			}
//		}
////		if (ppi.getType() == MeasureType.FRACTION && elements1.equals(elements2) && e.getId() == 0) {
////			return true;
////		}
//		return false;
//	}



}
