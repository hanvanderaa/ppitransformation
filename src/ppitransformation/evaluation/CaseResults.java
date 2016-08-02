package ppitransformation.evaluation;

import java.util.ArrayList;
import java.util.List;

import ppitransformation.main.Config;
import ppitransformation.ppi.PPI;
import ppitransformation.ppi.templates.Correspondence;

public class CaseResults {

	public String _caseid;
	public List<PPI> ppis;
	
	
	public CaseResults(String caseid) {
		_caseid = caseid;
		ppis = new ArrayList<PPI>();
		
	}
	
	public void addPPI(PPI ppi) {
		ppis.add(ppi);
	}
	
	public String getCaseID() {
		return _caseid;
	}
	
	public List<PPI> getWrongTypes() {
		List<PPI> result = new ArrayList<PPI>();
		for (PPI ppi : ppis) {
			if (ppi.wrongType()) {
				result.add(ppi);
			}
		}
		return result;
	}
	
	public List<PPI> getWrongTags() {
		List<PPI> result = new ArrayList<PPI>();
		for (PPI ppi : ppis) {
			if (ppi.wrongTags()) {
				result.add(ppi);
			}
		}
		return result;
	}
	
	public List<PPI> getWrongChunks() {
		List<PPI> result = new ArrayList<PPI>();
		for (PPI ppi : ppis) {
			if (ppi.wrongChunks()) {
				result.add(ppi);
			}
		}
		return result;
	}
	
	public int getWrongTypeCount() {
		int count = 0;
		for (PPI ppi : ppis) {
			if (ppi.wrongType()) {
				count++;
			}
		}
		return count;
	}

	public int getWrongTagCount() {
		int count = 0;
		for (PPI ppi : ppis) {
			if (ppi.wrongTags()) {
				count++;
			}
		}
		return count;
	}

	public int getWrongChunkCount() {
		int count = 0;
		for (PPI ppi : ppis) {
			if (ppi.wrongChunks()) {
				count++;
			}
		}
		return count;
	}
	
	public int getTP(Config config) {
		int result = 0;
		for (PPI ppi : ppis) {
			result += ppi.getTP(config);
		}
		return result;
	}
	
	public int getFP(Config config) {
		int result = 0;
		for (PPI ppi : ppis) {
			result += ppi.getFP(config);
		}
		return result;
	}
	
	public int getFN(Config config) {
		int result = 0;
		for (PPI ppi : ppis) {
			result += ppi.getFN(config);
		}
		return result;
	}
	
	
	
	public int getPPICount() {
		return ppis.size();
	}
	
	public int[] getAnnotationResultsArray() {
		return new int[]{getWrongTypeCount(), getWrongTagCount(), getWrongChunkCount(), getPPICount()};
	}
	
	public int[] getFormalizationResultsArray(Config config) {
		return new int[]{getTP(config), getFP(config), getFN(config)};
	}
	
	public int[] getFormalizationsResultsArray(Config config, PPI ppi) {
		int[] r = new int[]{ppi.getTP(config), ppi.getFP(config), ppi.getFN(config)};
		return r;
	}
	
	public String toString() {
		return "case: " + _caseid  + " " + getAnnotationResultsArray();
	}
	
	
	public int totalGSAlignments() {
		int total = 0;
		for (PPI ppi : ppis) {
			total += ppi.getGoldStandard().size();
		}
		return total;
	}

	public int[] getMeasureTypeResultsArray(Config _config) {
		int tp = 0;
		int fp = 0;
		int fn = 0;
		for (PPI ppi : ppis) {
			if (ppi.hasGoldStandard()) {
				if (ppi.getPredictedType() == ppi.getType()) {
					tp++;
				} else {
					fp++;
					fn++;
				}
			}
		}
		return new int[]{tp, fp, fn};
	}
	
	public int[] getAggregatorResultsArray(Config _config) {
		int tp = 0;
		int fp = 0;
		int fn = 0;
		for (PPI ppi : ppis) {
			if (ppi.hasGoldStandard()) {
				if (ppi.hasAggregator() && ppi.aggregatorOK(_config)) {
					tp++;
				}
				if (ppi.hasAggregator() && !ppi.aggregatorOK(_config)) {
					fn++;
					fp++;
				}
				if (!ppi.hasAggregator() && !ppi.aggregatorOK(_config)) {
					fp++;
				}
			}
		}
		return new int[]{tp, fp, fn};
	}

	public int[] getGroupByResultsArray(Config _config) {
		int tp = 0;
		int fp = 0;
		int fn = 0;
		for (PPI ppi : ppis) {
			if (ppi.hasGoldStandard()) {
				if (ppi.hasGrouping() && ppi.groupByOK(_config)) {
					tp++;
				}
				if (ppi.hasGrouping() && !ppi.groupByOK(_config)) {
					fn++;
					fp++;
				}
				if (!ppi.hasGrouping() && !ppi.groupByOK(_config)) {
					fp++;
				}
			}
		}
		return new int[]{tp, fp, fn};
	}

	public int[] getAlignmentResultsArray(Config _config) {
		int[] raw = getFormalizationResultsArray(_config);
		for (int i = 0; i < raw.length; i++) {
			raw[i] = raw[i] - getMeasureTypeResultsArray(_config)[i];
			if (!_config.justmodelelements) {
				raw[i] = raw[i] - getAggregatorResultsArray(_config)[i];
				raw[i] = raw[i] - getGroupByResultsArray(_config)[i];
			}
		}
		
		
		return raw;
	}
}
