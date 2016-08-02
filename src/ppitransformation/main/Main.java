package ppitransformation.main;

import java.io.IOException;
import java.util.Map;

import ppitransformation.decoding.PPIAnnotation;
import ppitransformation.decoding.ViterbiDecoder;
import ppitransformation.evaluation.Evaluation;
import ppitransformation.io.GSReader;
import ppitransformation.io.HMMSerialization;
import ppitransformation.io.PPILoader;
import ppitransformation.io.ProcessModelLoader;
import ppitransformation.markovmodels.HMM;
import ppitransformation.parsing.NLProcessor;
import ppitransformation.ppi.PPI;
import ppitransformation.ppi.PPICollection;
import ppitransformation.ppi.templates.GSTemplate;
import ppitransformation.ppi.templates.PPITemplate;
import ppitransformation.processmodel.PMCollection;
import ppitransformation.similarity.scorers.Scorer;
import ppitransformation.similarity.termdictionary.TermDictionary;
import ppitransformation.templatefilling.Aligner;
import ppitransformation.training.ModelTrainer;

public class Main {
	
	public static final String DISCO_PATH = "/Users/han/Documents/enwiki-20130403-word2vec-lm-mwl-lc-sim";
	
	public static final boolean CROSS_VALIDATION = false;
	public static final boolean USE_SERIALIZED_HMM = false;
	
	public static final int EVAL_RUNS = 30;
	public static final int EVAL_FOLDS = 10;
	public static final boolean JUST_MODEL_ELEMENTS = true;
	
	public static boolean PRINT_TYPE_ERRORS = false;
	public static boolean PRINT_TAG_ERRORS = false;
	public static boolean PRINT_CHUNK_ERRORS = false;
	

	public static final String ANNOTATED_PPI_DIR =  "input/annotatedppis";
	public static final String MODEL_DIR = "input/models";
	public static final String GS_DIR = "input/goldstandard";
	public static final String HMM_SER = "cache/ser/hmm.ser";
	public static final String PPI_DESCRIPTIONS_FILE = "input/ppidescriptions/descriptions.txt";
	public static final int MODEL_ID_FOR_DESCRIPTIONS = 1;
	

	
	public static final String[] IGNORE_CASES = new String[]{};
	
	NLProcessor nlp;
	PPICollection ppis;
	PPICollection training;
	PMCollection models;
	HMM hmm;
	
	Config config;
	
	public static void main(String[] args) throws IOException {
		Main main = new Main();
		main.loadstuff();
		main.run();
	}
	

	public void run() throws IOException {
		
		if (!CROSS_VALIDATION) {
			nonCrossValidation();
		}
		
		
		
		if (CROSS_VALIDATION) {
			Evaluation evaluation = new Evaluation(config, nlp, ppis, models);
			evaluation.runEvaluation(config.runs, config.folds);
			evaluation.printResults(PRINT_TYPE_ERRORS, PRINT_TAG_ERRORS, PRINT_CHUNK_ERRORS);
		}
	}
	
	private void loadstuff() {

		
		config = new Config(Config.MODE_DISCO, EVAL_RUNS, EVAL_FOLDS, JUST_MODEL_ELEMENTS);
		
		try {
			models = ProcessModelLoader.importJSONModels(MODEL_DIR, IGNORE_CASES);
			nlp = new NLProcessor(config);
			for (String caseid : models.getCaseIDs()) {
					TermDictionary dictionary = new TermDictionary(models.getModel(caseid));
					nlp.addScorer(caseid, new Scorer(config, dictionary));
			}
			
			if (CROSS_VALIDATION) {
			ppis = PPILoader.loadAnnotatedPPIs(ANNOTATED_PPI_DIR, IGNORE_CASES);
			Map<String, Map<String, GSTemplate>> gs = GSReader.importGSFiles(GS_DIR, IGNORE_CASES);
			for (String caseid : gs.keySet()) {
				for (String ppiid : gs.get(caseid).keySet()) {
					GSTemplate gss = gs.get(caseid).get(ppiid);
					gss.resolveAbstraction(models.getModel(caseid));
					PPI ppi = ppis.getPPI(caseid, ppiid);
					ppi.setGoldStandard(gss);
				}
			}
			} else {
				ppis = PPILoader.loadTextualDescriptions(MODEL_ID_FOR_DESCRIPTIONS, PPI_DESCRIPTIONS_FILE);
				training = PPILoader.loadAnnotatedPPIs(ANNOTATED_PPI_DIR, IGNORE_CASES);
				if (!USE_SERIALIZED_HMM) {
					ModelTrainer trainer2 = new ModelTrainer();
					hmm = new HMM(trainer2.trainSemanticModel(training.getPPIs()), trainer2.trainLexModel(training.getPPIs()));
					HMMSerialization.serializeHMM(hmm, HMM_SER);
				} else {
					hmm = HMMSerialization.deserializeHMM(HMM_SER);
				}
			}
			
//			TemplateCreator inputCreator = new TemplateCreator();
//			inputCreator.createPPIAnnotationTemplates();
//			inputCreator.createGSTemplates(models, ppis);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	
	private void nonCrossValidation() {
		hmm.getLexModel().print();
		hmm.getSemModel().print();
		ViterbiDecoder decoder = new ViterbiDecoder(nlp, hmm);
		for (PPI ppi : ppis.getPPIs()) {
			System.out.println("\nAnnotating ppi: " + ppi);
			// ANNOTATE PPI
			PPIAnnotation predictedAnnotation = decoder.createPrediction(ppi.getID(), ppi.getOriginal());
			predictedAnnotation.setParentInfo(ppi);
			ppi.setPredictedAnnotation(predictedAnnotation);
			System.out.println("Generated annotation:");
			System.out.println(predictedAnnotation);

			// FILL TEMPLATE
			Aligner aligner = new Aligner(config, nlp);
			PPITemplate filledTemplate = null;
			if (models.getModel(ppi.getCaseID()) != null) {
				filledTemplate = aligner.formalizePPI(predictedAnnotation, models.getModel(ppi.getCaseID()));
			}
			ppi.addFormalization(config, filledTemplate);
			if (filledTemplate != null) {
				ppi.setPredictedType(filledTemplate.getType());
			}
			System.out.println("Generated template:");
			System.out.println(filledTemplate);
		}
	}
	
}
