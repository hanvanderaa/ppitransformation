package ppitransformation.evaluation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ppitransformation.main.Config;
import ppitransformation.parsing.NLProcessor;
import ppitransformation.ppi.PPI;
import ppitransformation.ppi.PPICollection;
import ppitransformation.processmodel.PMCollection;
import ppitransformation.utils.ListUtils;

public class Evaluation {

	final Config _config;
	final NLProcessor _nlp;
	List<SingleRun> runResults;
	
	PPICollection _ppis;
	PMCollection _models;
	
	public Evaluation(Config config, NLProcessor nlp, PPICollection ppis, PMCollection models) {
		_config = config;
		_nlp = nlp;
		_ppis = ppis;
		_models = models;
		runResults = new ArrayList<SingleRun>();
	}
	
	public void runEvaluation(int runs, int folds) {
		for (int i = 0; i < runs; i++) {
			_ppis.partitionCollection(folds);
			SingleRun runEvaluation = new SingleRun(_config, _nlp, _ppis, _models);
			runEvaluation.runEvaluation();
			runResults.add(runEvaluation);
		}
	}
	
	public Map<String, Set<PPI>> wrongTypePPIs() {
		List<String> caseids = new ArrayList<String>(runResults.get(0).getCaseIDs());
		Collections.sort(caseids);
		Map<String, Set<PPI>> resultsMap = new LinkedHashMap<String, Set<PPI>>();
		for (String caseid : caseids) {
			resultsMap.put(caseid, new LinkedHashSet<PPI>());
		}
		for (SingleRun run : runResults) {
			for (String caseid : caseids) {
				resultsMap.get(caseid).addAll(run.getCaseResults(caseid).getWrongTypes());
			}	
		}
		return resultsMap;
	}
	
	public Map<String, Set<PPI>> wrongTagPPIs() {
		List<String> caseids = new ArrayList<String>(runResults.get(0).getCaseIDs());
		Collections.sort(caseids);
		Map<String, Set<PPI>> resultsMap = new HashMap<String, Set<PPI>>();
		for (String caseid : caseids) {
			resultsMap.put(caseid, new LinkedHashSet<PPI>());
		}
		for (SingleRun run : runResults) {
			for (String caseid : caseids) {
				resultsMap.get(caseid).addAll(run.getCaseResults(caseid).getWrongTags());
			}	
		}
		return resultsMap;
	}
	
	public Map<String, Set<PPI>> wrongChunksPPIs() {
		List<String> caseids = new ArrayList<String>(runResults.get(0).getCaseIDs());
		Collections.sort(caseids);
		Map<String, Set<PPI>> resultsMap = new HashMap<String, Set<PPI>>();
		for (String caseid : caseids) {
			resultsMap.put(caseid, new LinkedHashSet<PPI>());
		}
		for (SingleRun run : runResults) {
			for (String caseid : caseids) {
				resultsMap.get(caseid).addAll(run.getCaseResults(caseid).getWrongChunks());
			}	
		}
		return resultsMap;
	}
	
	public void printResults(boolean printTypeErrors, boolean printTagErrors, boolean printChunkErrors) {
		
		List<String> caseids = new ArrayList<String>(runResults.get(0).getCaseIDs());
		Collections.sort(caseids);
		
		Map<String, int[]> wrongTypesCase = new LinkedHashMap<String, int[]>();
		Map<String, int[]> wrongTagsCase = new LinkedHashMap<String, int[]>();
		Map<String, int[]> wrongChunksCase = new LinkedHashMap<String, int[]>();
		int[] wrongTypes = new int[runResults.size()];
		int[] wrongTags = new int[runResults.size()];
		int[] wrongChunks = new int[runResults.size()];
		Map<String, int[]> tpsCase = new LinkedHashMap<String, int[]>();
		Map<String, int[]> fpsCase = new LinkedHashMap<String, int[]>();
		Map<String, int[]> fnsCase = new LinkedHashMap<String, int[]>();
		int[] tps = new int[runResults.size()];
		int[] fps = new int[runResults.size()];
		int[] fns = new int[runResults.size()];
		double prec[] = new double[runResults.size()];
		double rec[] = new double[runResults.size()];
		
		for (int i = 0; i < _config.runs; i++) {
			SingleRun run = runResults.get(i);
			for (String caseid : caseids) {
				CaseResults caseResults = run.getCaseResults(caseid);
				if (!wrongTypesCase.containsKey(caseid)) {
					wrongTypesCase.put(caseid, new int[_config.runs]);
					wrongTagsCase.put(caseid, new int[_config.runs]);
					wrongChunksCase.put(caseid, new int[_config.runs]);
					tpsCase.put(caseid, new int[_config.runs]);
					fpsCase.put(caseid, new int[_config.runs]);
					fnsCase.put(caseid, new int[_config.runs]);
				}
				wrongTypesCase.get(caseid)[i] = caseResults.getWrongTypeCount();
				wrongTagsCase.get(caseid)[i] = caseResults.getWrongTagCount();
				wrongChunksCase.get(caseid)[i] = caseResults.getWrongChunkCount();
				tpsCase.get(caseid)[i] = caseResults.getTP(_config);
				fpsCase.get(caseid)[i] = caseResults.getFP(_config);
				fnsCase.get(caseid)[i] = caseResults.getFN(_config);
			}
			wrongTypes[i] = run.getAnnotationResultsArray()[0];
			wrongTags[i] = run.getAnnotationResultsArray()[1];
			wrongChunks[i] = run.getAnnotationResultsArray()[2];
			tps[i] = run.getFormalizationResultsArray()[0];
			fps[i] = run.getFormalizationResultsArray()[1];
			fns[i] = run.getFormalizationResultsArray()[2];
			
			prec[i] = ListUtils.precision(tps[i], fps[i]);
			rec[i] = ListUtils.recall(tps[i], fns[i]);
		}
		
		
		if (printTypeErrors) {
			System.out.println("\n\n");
			Map<String, Set<PPI>> wrongTypePPIs = wrongTypePPIs();
			for (String caseid : wrongTypePPIs.keySet()) {
				if (!wrongTypePPIs.get(caseid).isEmpty()) {
					System.out.println("wrong types for : " + caseid);
					for (PPI ppi : wrongTypePPIs.get(caseid)) {
						System.out.println(ppi);
						System.out.println("predicted type: " + ppi.getPredictedType() + " real type: " + ppi.getType());
						System.out.println("Predicted annotation: " + ppi.getPredictedAnnotation());
						System.out.println("GS annotation: " + ppi.getAnnotation());
					}
				}
			}
		}
		
		
		if (printTagErrors) {
			System.out.println("\n\n");
			Map<String, Set<PPI>> wrongTagPPIs = wrongTagPPIs();
			for (String caseid : wrongTagPPIs.keySet()) {
				if (!wrongTagPPIs.get(caseid).isEmpty()) {
					System.out.println("\nwrong tags for : " + caseid);
					for (PPI ppi : wrongTagPPIs.get(caseid)) {
						if (!printTypeErrors || !ppi.wrongType()) {
							System.out.println(ppi);
							System.out.println("predicted type: " + ppi.getPredictedType() + " real type: " + ppi.getType());
							System.out.println("Predicted annotation: " + ppi.getPredictedAnnotation());
							System.out.println("GS annotation: " + ppi.getAnnotation());
						}
					}
				}
			}
		}
		
		
		
		for (String caseid : wrongTypesCase.keySet()) {
			
//			System.out.println("case: " + caseid + 
//					" precision: " + ListUtils.precisionStr(tpsCase.get(caseid), fpsCase.get(caseid)) +
//					" recall: " + ListUtils.recallStr(tpsCase.get(caseid), fnsCase.get(caseid))
//					+ " tps: " + Arrays.toString((tpsCase.get(caseid))) +  " fps: " + Arrays.toString((fpsCase.get(caseid)))
//					);
//			
			for (PPI ppi : _ppis.getPPIsFromCase(caseid)) {
				if (ppi.getGoldStandard() == null) {
					System.out.println("NO GS FOUND");
				} else {
				ppi.printEvaluationResult(_config);
					}
				
			}
			
//			System.out.println("case: " + caseid + 
//					" wrong types: " + ListUtils.avgAndSTd(wrongTypesCase.get(caseid)) +
//					" wrong tags: " + ListUtils.avgAndSTd(wrongTagsCase.get(caseid)) +
//					" wrong chunks: " + ListUtils.avgAndSTd(wrongChunksCase.get(caseid)) +
//					" total PPIs: " + runResults.get(0).getCaseResults(caseid).getPPICount()
//					);
		
		}
//		System.out.println("total" + 
//				" wrong types: " + ListUtils.avgAndSTd(wrongTypes) +
//				" wrong tags: " + ListUtils.avgAndSTd(wrongTags) +
//				" wrong types: " + ListUtils.avgAndSTd(wrongChunks)
//				);
		
		System.out.println("Total Precision:" + ListUtils.precisionStr(tps, fps) + " recall: " + ListUtils.recallStr(tps, fns));
//		System.out.println("total precision: " + (tps[0] * 1.0 / (tps[0] + fps[0])) + " recall: " + (tps[0] * 1.0 / (tps[0] + fns[0])));
		System.out.println(Arrays.toString(tps) + " " + Arrays.toString(fps) + " " + Arrays.toString(fns));
		printAlignmentResults();
		printMeasureResults();
		printAggregatorResults();
		printGroupByResults();
		
		
		System.out.println("total prec:" + Arrays.toString(prec));
		System.out.println("total rec: " + Arrays.toString(rec));
	
	}
	
	
	private void printAlignmentResults() {
		int[] tpsm = new int[runResults.size()];
		int[] fpsm = new int[runResults.size()];
		int[] fnsm = new int[runResults.size()];
		
		double prec[] = new double[runResults.size()];
		double rec[] = new double[runResults.size()];
		
		for (int i = 0; i < runResults.size(); i++) {
			int[] runres = runResults.get(i).alignmentResultsArray();
			tpsm[i] = runres[0];
			fpsm[i] = runres[1];
			fnsm[i] = runres[2];
			
			prec[i] = ListUtils.precision(runres[0], runres[1]);
			rec[i] = ListUtils.recall(runres[0], runres[2]);
		}
		System.out.println("Alignment precision: " + ListUtils.precisionStr(tpsm, fpsm) + 
				" recall: " + ListUtils.recallStr(tpsm, fnsm)) ;
		System.out.println(Arrays.toString(tpsm) + " " + Arrays.toString(fpsm) + " " + Arrays.toString(fnsm));
		
		System.out.println("prec:" + Arrays.toString(prec));
		System.out.println("rec: " + Arrays.toString(rec));
	}
	
	private void printMeasureResults() {
		int[] tpsm = new int[runResults.size()];
		int[] fpsm = new int[runResults.size()];
		int[] fnsm = new int[runResults.size()];
		
		double prec[] = new double[runResults.size()];
		double rec[] = new double[runResults.size()];
		for (int i = 0; i < runResults.size(); i++) {
			int[] runres = runResults.get(i).measureTypeResultsArray();
			tpsm[i] = runres[0];
			fpsm[i] = runres[1];
			fnsm[i] = runres[2];
			
			prec[i] = ListUtils.precision(runres[0], runres[1]);
			rec[i] = ListUtils.recall(runres[0], runres[2]);
			
		}
		System.out.println("Measure type precision: " + ListUtils.precisionStr(tpsm, fpsm) + 
				" recall: " + ListUtils.recallStr(tpsm, fnsm)) ;
		System.out.println(Arrays.toString(tpsm) + " " + Arrays.toString(fpsm) + " " + Arrays.toString(fnsm));
		System.out.println("prec:" + Arrays.toString(prec));
		System.out.println("rec: " + Arrays.toString(rec));
	}
	
	private void printAggregatorResults() {
		int[] tpsm = new int[runResults.size()];
		int[] fpsm = new int[runResults.size()];
		int[] fnsm = new int[runResults.size()];
		for (int i = 0; i < runResults.size(); i++) {
			int[] runres = runResults.get(i).aggregatorResultsArray();
			tpsm[i] = runres[0];
			fpsm[i] = runres[1];
			fnsm[i] = runres[2];
		}
		System.out.println("Aggregator precision: " + ListUtils.precisionStr(tpsm, fpsm) + 
				" recall: " + ListUtils.recallStr(tpsm, fnsm)) ;
		System.out.println(Arrays.toString(tpsm) + " " + Arrays.toString(fpsm) + " " + Arrays.toString(fnsm));
	}
	
	private void printGroupByResults() {
		int[] tpsm = new int[runResults.size()];
		int[] fpsm = new int[runResults.size()];
		int[] fnsm = new int[runResults.size()];
		for (int i = 0; i < runResults.size(); i++) {
			int[] runres = runResults.get(i).groupbyResultsArray();
			tpsm[i] = runres[0];
			fpsm[i] = runres[1];
			fnsm[i] = runres[2];
		}
		System.out.println("Group by precision: " + ListUtils.precisionStr(tpsm, fpsm) + 
				" recall: " + ListUtils.recallStr(tpsm, fnsm)) ;
		System.out.println(Arrays.toString(tpsm) + " " + Arrays.toString(fpsm) + " " + Arrays.toString(fnsm));
	}
	
}
