package ppitransformation.processmodel;

import java.util.ArrayList;
import java.util.List;

import org.jbpt.pm.Activity;
import org.jbpt.pm.DataNode;
import org.jbpt.pm.Event;
import org.jbpt.pm.ProcessModel;

import ppialignment.processmodel.DataObject;


public class ProcessModelWrapper {

	String id;
	ProcessModel jbpt;
	List<ModelElement> activities;
	List<ModelElement> dataObjects;
	List<ModelElement> events;
	
	
	public ProcessModelWrapper(String id, ProcessModel jbpt) {
		this.id = id;
		this.jbpt = jbpt;
		wrapModel();
	}
	
	public List<ModelElement> getModelElements() {
		List<ModelElement> result = new ArrayList<ModelElement>(activities);
		result.addAll(dataObjects);
		result.addAll(events);
		return result;
	}
	
	
	private void wrapModel() {
		activities = new ArrayList<ModelElement>();
		for (Activity activity : jbpt.getActivities()) {
			ModelElement e = new ModelElement(
					activity.getId(), activity.getName(), ElementType.ACTIVITY, activity);
			activities.add(e);
		}
		dataObjects = new ArrayList<ModelElement>();
		for (DataNode dataNode : jbpt.getDataNodes()) {
			ModelElement e = new ModelElement(
					dataNode.getId(), dataNode.getName(), ElementType.DATAOBJECT, dataNode);
			dataObjects.add(e);
		}
		events = new ArrayList<ModelElement>();
		for (Event event : jbpt.getEvents()) {
			if (!event.getName().isEmpty()) {
				ModelElement e = new ModelElement(
						event.getId(), event.getName(), ElementType.EVENT, event);
				events.add(e);
			}
		}
		
	}

	public String getId() {
		return id;
	}

	public ModelElement getElement(String id) {
		for (ModelElement me : getModelElements()) {
			if (me.getId().equals(id)) {
				return me;
			}
		}
		return null;
	}

	public List<ModelElement> getDataObjects() {
		return dataObjects;
	}
	
	
	
	
	
}
