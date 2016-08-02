package ppitransformation.evaluation;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ppitransformation.decoding.PPIAnnotation;
import ppitransformation.decoding.ViterbiDecoder;
import ppitransformation.main.Config;
import ppitransformation.markovmodels.HMM;
import ppitransformation.parsing.NLProcessor;
import ppitransformation.ppi.PPI;
import ppitransformation.ppi.PPICollection;
import ppitransformation.ppi.templates.PPITemplate;
import ppitransformation.processmodel.PMCollection;
import ppitransformation.templatefilling.Aligner;
import ppitransformation.training.ModelTrainer;

public class SingleRun {


	public final Config _config;
	public final NLProcessor _nlp;
	public final PPICollection _ppis;
	public final PMCollection _models;
	
	public Map<String, CaseResults> caseResults;
	
	int[] annotationresults;
	int[] formalizationresults;
	int[] alignmentresults;
	int[] aggregatorresults;
	int[] measuretyperesults;
	int[] groupbyresults;
			
	
	public SingleRun(Config config, NLProcessor nlp, PPICollection ppis, PMCollection models) {
		_config = config;
		_nlp = nlp;
		_ppis = new PPICollection(ppis);
		_models = models;
		caseResults = new LinkedHashMap<String, CaseResults>();
	}
	
	
	public void runEvaluation(){
			
		int ppisn = 0;
		for (int i = 0; i < _ppis.folds(); i++) {
		
				List<PPI> casePPIs = _ppis.getFilteredPartition(i);
				List<PPI> trainingPPIs = _ppis.getRemainder(i);
				
				ModelTrainer trainer = new ModelTrainer();
				HMM hmm = new HMM(trainer.trainSemanticModel(trainingPPIs), trainer.trainLexModel(trainingPPIs));
				
				ViterbiDecoder decoder = new ViterbiDecoder(_nlp, hmm);
				
				
				for (PPI ppi : casePPIs) {
				
					if (!ppi.hasGoldStandard()) {
						continue;
					}
					String caseid = ppi.getCaseID();
					
					// ANNOTATE PPI
					PPIAnnotation predictedAnnotation = decoder.createPrediction(ppi.getID(), ppi.getOriginal());
					predictedAnnotation.setParentInfo(ppi);
					ppi.setPredictedAnnotation(predictedAnnotation);
					
					// FILL TEMPLATE
					Aligner aligner = new Aligner(_config, _nlp);
					PPITemplate filledTemplate = null;
					if (_models.getModel(caseid) != null) {
						filledTemplate = aligner.formalizePPI(predictedAnnotation, _models.getModel(caseid));
					}
//					ppicopy.addFormalization(_config, filledTemplate);
					ppi.addFormalization(_config, filledTemplate);
					if (filledTemplate != null) {
						ppi.setPredictedType(filledTemplate.getType());
					}
					
					ppisn++;
					
					// STORE RESULTS
					if (!caseResults.containsKey(caseid)) {
						caseResults.put(caseid, new CaseResults(caseid));						
					}
//					caseResults.get(caseid).addPPI(ppicopy);
//					caseResults.get(caseid).getFormalizationsResultsArray(_config, ppicopy);
					caseResults.get(caseid).addPPI(ppi);
					caseResults.get(caseid).getFormalizationsResultsArray(_config, ppi);
					setFormalizationResultsArray();
					setAlignmentResultsArray();
					setMeasureTypeResultsArray();
					setAggregatorResultsArray();
					setGroupbyResultsArray();
					setAnnotationResultsArray();
					
					
					
				}
				
			}
//		for (String caseid : caseResults.keySet()) {
//			System.out.println(caseid + "; " + caseResults.get(caseid).totalGSAlignments());
//		}
		System.out.println("total ppis aligned: " + ppisn);
		
	}
	
	public Collection<CaseResults> getCaseResults() {
		return caseResults.values();
	}
	
	public Collection<String> getCaseIDs() {
		return caseResults.keySet();
	}
	
	public int[] getAnnotationResultsArray() {
		return annotationresults;
	}
	
	private void setAnnotationResultsArray() {
		annotationresults = new int[4];
		for (CaseResults cr : caseResults.values()) {
			int[] crint = cr.getAnnotationResultsArray();
			for (int i = 0; i < annotationresults.length; i++) {
				annotationresults[i] += crint[i];
			}
		}
	}
	
	public int[] getFormalizationResultsArray() {
		return formalizationresults;
	}
	
	public void setFormalizationResultsArray() {
		formalizationresults = new int[3];
		for (CaseResults cr : caseResults.values()) {
			int[] crint = cr.getFormalizationResultsArray(_config);
			for (int i = 0; i < formalizationresults.length; i++) {
				formalizationresults[i] += crint[i];
			}
		}
	}
	
	public int[] alignmentResultsArray() {
		return alignmentresults;
	}
	
	private void setAlignmentResultsArray() {
		alignmentresults = new int[3];
		for (CaseResults cr : caseResults.values()) {
			int[] crint = cr.getAlignmentResultsArray(_config);
			for (int i = 0; i < alignmentresults.length; i++) {
				alignmentresults[i] += crint[i];
			}
		}
	}
	
	public int[] measureTypeResultsArray() {
		return measuretyperesults;
	}
	
	private void setMeasureTypeResultsArray() {
		measuretyperesults = new int[3];
		for (CaseResults cr : caseResults.values()) {
			int[] crint = cr.getMeasureTypeResultsArray(_config);
			for (int i = 0; i < measuretyperesults.length; i++) {
				measuretyperesults[i] += crint[i];
			}
		} 
	}
	
	public int[] aggregatorResultsArray() {
		return aggregatorresults;
	}
	
	private void setAggregatorResultsArray() {
		aggregatorresults= new int[3];
		for (CaseResults cr : caseResults.values()) {
			int[] crint = cr.getAggregatorResultsArray(_config);
			for (int i = 0; i < aggregatorresults.length; i++) {
				aggregatorresults[i] += crint[i];
			}
		}
	}
	
	public int[] groupbyResultsArray() {
		return groupbyresults;
	}
	
	private void setGroupbyResultsArray() {
		groupbyresults = new int[3];
		for (CaseResults cr : caseResults.values()) {
			int[] crint = cr.getGroupByResultsArray(_config);
			for (int i = 0; i < groupbyresults.length; i++) {
				groupbyresults[i] += crint[i];
			}
		}
	}


	public CaseResults getCaseResults(String caseid) {
		return caseResults.get(caseid);
	}
	
	
}
