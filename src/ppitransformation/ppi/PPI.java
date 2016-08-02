package ppitransformation.ppi;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ppitransformation.decoding.Chunk;
import ppitransformation.decoding.PPIAnnotation;
import ppitransformation.main.Config;
import ppitransformation.ppi.templates.Correspondence;
import ppitransformation.ppi.templates.GSTemplate;
import ppitransformation.ppi.templates.PPITemplate;

public class PPI {

	String id;
	String caseID;
	String original;
	MeasureType type;
	MeasureType predictedType;
	PPIAnnotation annotation;
	PPIAnnotation predictedAnnotation;
	Map<Config, PPITemplate> formalizations;
	GSTemplate gs;

	
	public PPI(String caseID, String ppiID, MeasureType type) {
		this.caseID = caseID;
		this.id = ppiID; 
		this.type = type;
		formalizations = new LinkedHashMap<Config, PPITemplate>();
		
		original = "";
	}
	
	
	// constructor for loading from CAiSE files
	public PPI(int caseid, int ppiID, String original, MeasureType type) {
		super();
		this.caseID = String.valueOf(caseid);
		this.id = caseID + "--" + String.valueOf(ppiID);
		this.original = original;
		this.type = type;
		formalizations = new LinkedHashMap<Config, PPITemplate>();
	}
	
	
	
	public String getID() {
		return id;
	}
	
	public String getCaseID() {
		return caseID;
	}

	public String getOriginal() {
		return original;
	}

	public void setMeasureType(MeasureType type) {
		this.type = type;
	}
	

	public MeasureType getType() {
		return type;
	}
	
	
	public List<String> getTagSequence() {
		return annotation.getTagSequence();
	}
	
	public List<Chunk> getChunks() {
		return annotation.getChunks();
	}
	
	public void setPredictedType(MeasureType predictedType) {
		this.predictedType = predictedType;
	}
	
	public MeasureType getPredictedType() {
		return predictedType;
	}
	
	public void setPredictedAnnotation(PPIAnnotation annotation) {
		this.predictedAnnotation = annotation;
	}
	
	public PPIAnnotation getPredictedAnnotation() {
		return predictedAnnotation;
	}
	
	public void setAnnotation(PPIAnnotation annotation) {
		this.annotation = annotation;
		original = annotation.getWordString();
	}
	

	public String caseid() {
		return caseID;
	}
	
	public String toString() {
		return id  + " " + getOriginal();
	}

	public void setGoldStandard(GSTemplate gs) {
		this.gs = gs;
		this.type = gs.getType();
	}

	public PPIAnnotation getAnnotation() {
		return annotation;
	}
	
	public PPI copy() {
		PPI copy = new PPI(this.caseID, this.id, this.type);
		copy.setAnnotation(this.getAnnotation());
		copy.setGoldStandard(this.getGoldStandard());
		return copy;
	}

	public boolean wrongType() {
		return this.type != this.predictedType;
	}
	
	public boolean wrongTags() {
		return !predictedAnnotation.hasSameTagSequence(annotation);
	}
	
	public boolean wrongChunks() {
		return !predictedAnnotation.hasSameAnnotation(annotation);
	}

	public void addFormalization(Config config, PPITemplate template) {
		formalizations.put(config, template);
	}
	
	public PPITemplate getFormalization(Config config) {
		return formalizations.get(config);
	}

	public GSTemplate getGoldStandard() {
		return gs;
	}
	
	public int getTP(Config config) {
		if (this.getGoldStandard() != null && this.getFormalization(config) != null) {
			int tp = 0;
			for (Correspondence c : this.getFormalization(config).getCorrespondences()) {
				if (this.getGoldStandard().containsCorrespondent(c.getCorrespondentID())) {
					tp++;
				}
			}
			if (this.type == this.predictedType) {
				tp++;
			}
			if (!config.justmodelelements) {
				if (hasAggregator() && aggregatorOK(config)) {
					tp++;
				}
				if (hasGrouping() && groupByOK(config)) {
					tp++;
				}
			}
			return tp;
		}
		return 0;
	}
	
	public int getFP(Config config) {
		if (this.getGoldStandard() != null && this.getFormalization(config) != null) {
			return this.getFormalization(config).alignmentSize(config.justmodelelements) - getTP(config);
		}
		return 0;
	}
	
	public int getFN(Config config) {
		if (this.getGoldStandard() != null) {
			return this.getGoldStandard().alignmentSize(config.justmodelelements) - getTP(config);
		}
		return 0;
	}
	
	public boolean hasAggregator() {
		return !this.getGoldStandard().getAggregatorString().isEmpty();
	}
	
	public boolean hasGrouping() {
		return !this.getGoldStandard().getGroupByString().isEmpty();
	}
	
	public boolean aggregatorOK(Config config) {
		if (this.getFormalization(config) == null) {
			return !hasAggregator();
		}
		String predAggr = this.getFormalization(config).getAggregator();
		String gsAggr = this.getGoldStandard().getAggregatorString();
		if (predAggr.equalsIgnoreCase(gsAggr)) {
			return true;
		}
		if (this.getType() == MeasureType.COUNT && ((gsAggr.isEmpty() && predAggr.equals("SUM")) || predAggr.isEmpty() && gsAggr.equals("SUM"))) {
			return true;
		}
		return false;
	}
	
	public boolean groupByOK(Config config) {
		if (this.getFormalization(config) == null) {
			return !hasGrouping();
		}
		String groupByString = this.getFormalization(config).getGroupByString();
		if (groupByString.equalsIgnoreCase(this.getGoldStandard().getGroupByString())) {
			return true;
		}
		return false;
	}
	
	
	public void printEvaluationResult(Config config) {
		System.out.println(toString());
		System.out.println("tp: " + getTP(config) + " fp: " + getFP(config) + " fn: " + getFN(config));
		if (getPredictedAnnotation() == null) {
			System.err.println("NO PPI ANNOTATION");
			return;
		}
		if (getFormalization(config) == null) {
			System.err.println("NO FORMALIZATION");
			return;
		}
		System.out.println("Predicted Annotation:");
		System.out.println(getPredictedAnnotation());
		if (predictedType == type) {
			System.out.println("Type OK");
		} else {
			System.out.println("Type NOK. Predicted:" + predictedType + " GS: " + type);
		}
		if (hasAggregator() && aggregatorOK(config)) {
			System.out.println("Aggregator OK");
		} 
		if (!aggregatorOK(config)) {
			System.out.println("Aggregator NOK. Predicted:" + getFormalization(config).getAggregator() + " GS: " + gs.getAggregatorString());
		}
		if (hasGrouping() && groupByOK(config)) {
			System.out.println("Group by OK");
		}
		if (!groupByOK(config)) {
			System.out.println("Grouping NOK. Predicted:" + getFormalization(config).getGroupByString() + " GS: " + gs.getGroupByString());
		}
		System.out.println("Correct correspondences");
		for (String s : gs.correctCorrespondences(getFormalization(config).getCorrespondences())) {
			System.out.println(s);
		}
		if (getFP(config) > 0) {
			System.out.println("Incorrect correspondences");
			for (String s : gs.falseCorrespondences(getFormalization(config).getCorrespondences())) {
				System.out.println(s);
			}
		}
		if (getFN(config) > 0) {
			System.out.println("Missed correspondences");
			for (String s : gs.missedCorrespondences(getFormalization(config).getCorrespondences())) {
				System.out.println(s);
			}
		}
		System.out.println("");
//		System.out.println(ppi.getFormalization(_config));
//		System.out.println(ppi.getGoldStandard() + "\n");
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((caseID == null) ? 0 : caseID.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((original == null) ? 0 : original.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PPI other = (PPI) obj;
		if (caseID == null) {
			if (other.caseID != null)
				return false;
		} else if (!caseID.equals(other.caseID))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (original == null) {
			if (other.original != null)
				return false;
		} else if (!original.equals(other.original))
			return false;
		if (type != other.type)
			return false;
		return true;
	}


	public boolean hasGoldStandard() {
		return gs != null;
	}
	
	

}
