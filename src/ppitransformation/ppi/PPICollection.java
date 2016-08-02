package ppitransformation.ppi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;

public class PPICollection {

	
	private List<PPI> ppis;
	private List<List<PPI>> partitions;
	
	public PPICollection() {
		ppis = new ArrayList<PPI>();
	}
	
	public PPICollection(PPICollection foldedCollection) {
		this.ppis = foldedCollection.ppis;
		this.partitions = new ArrayList<List<PPI>>(foldedCollection.partitions);
	}

	public int size() {
		return ppis.size();
	}
	
	public void add(PPI ppi) {
		ppis.add(ppi);
	}
	
	public List<PPI> getPPIs() {
		return ppis;
	}
	
	public PPI getPPI(String caseid, String ppiid) {
		for (PPI ppi : ppis) {
			if (ppi.getCaseID().equals(caseid) && ppi.getID().equals(ppiid)) {
				return ppi;
			}
		}
		return null;
	}
	
	public int folds() {
		return partitions.size();
	}
	
	public void partitionCollection(int folds) {
		long seed = System.nanoTime();
		Collections.shuffle(ppis, new Random(seed));
		int partsize = (int) Math.ceil(size() * 1.0 / folds);
		partitions =  Lists.partition(ppis, partsize);
	}
	
	private List<PPI> getPartition(int index) {
		List<String> ids = new ArrayList<String>();
		for (PPI ppi : partitions.get(index)) {
			ids.add(ppi.getID());
		}
		return partitions.get(index);
	}
	
	public List<PPI> getFilteredPartition(int index) {
		List<PPI> result = new ArrayList<PPI>();
		for (PPI ppi : getPartition(index)) {
			if (!ppi.getCaseID().equals("00") && !ppi.getCaseID().equals("0")) {
				result.add(ppi);
			}
		}
		return result;
	}
	
	public List<PPI> getRemainder(int index) {
		List<PPI> result = new ArrayList<PPI>(ppis);
		for (PPI ppi : getPartition(index)) {
			result.remove(ppi);
		}
		return result;
	}

	
	public List<PPI> getPPIsFromCase(String caseid) {
		List<PPI> result = new ArrayList<PPI>();
		for (PPI ppi : ppis) {
			if (ppi.getCaseID().equals(caseid)) {
				result.add(ppi);
			}
		}
		return result;
	}
	
	public List<PPI> getPPIsOutsideOfCase(String caseid) {
		List<PPI> result = new ArrayList<PPI>();
		for (PPI ppi : ppis) {
			if (!ppi.getCaseID().equals(caseid) && !ppi.getCaseID().equals("00")) {
				result.add(ppi);
			}
		}
		return result;
	}

	public String toString() {
		return ppis.toString();
	}

}
