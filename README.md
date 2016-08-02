# ppitransformation
# ppitransformation
—INTRODUCTION

This document provides instructions on how to use the ppitransformation prototype for the automated transformation of natural language descriptions of Process Performance Indicators (PPIs) into a structured notation. The details of the approach that this prototype implements are described in the article “Probabilistic Transformation from Unstructured Natural Language to Measurable Process Performance Indicators” by Van der Aa et al. 

—SETUP

In order to run the prototype, two things must be downloaded and configured in the project: The Stanford CoreNLP Library and a corpus for the DISCO semantic similarity computation. These are not included in the project due to their file size. 

The Stanford CoreNLP library can be downloaded from: http://stanfordnlp.github.io/CoreNLP/. 
Add the \stanford-corenlp-version-javadoc.jar, -models.jar, -sources.jar, and stanford-corenlp-version.jar to the lib folder in the project. The project is developed based on version 3.6.0 of the library. In case you obtain a different version, the build path of the project must be updated accordingly. Furthermore, we cannot guarantee compatibility with other versions.

A corpus for the DISCO similarity library can be obtained from:  http://www.linguatools.de/disco/disco-download\_en.html. In the evaluation of the paper, we made use of the “enwiki-20130403-word2vec-lm-mwl-lc-sim” corpus. To point the project to the right file, update the variable “DISCO_PATH” in the Main class.

—INPUT

The project makes use of four types of input. As reference material, the project distribution comes with example files for all the types in the appropriate folders.

1. Annotated PPIs: .csv files with annotated PPI descriptions, used to train the HMM. Location: input/annotatedppis
2. Goldstandard definitions: .csv files with the goldstandard definitions of how PPIs should be formalized. Templates for this can be generated using ppitransformation.io.TemplateCreator. Note that for proper cross-validation, the IDs used in these files should correspond to the IDs used in the Annoted PPI files. Location: input/goldstandard
3. Process models: .json files containing process model definitions. These files can be created using, among others, Signavio. Location: input/models
4. PPI descriptions: .txt file containing ppi descriptions to be transformed by the approach (without evaluating their results). Location: input/ppidescriptions

—CONFIGURATION

NOTE: All parameter names used here refer to global parameters in the main class: ppitransformation.main.Main 
The project can be used for two types of runs: non-evaluated and evaluated PPI transformation.

Non-evaluated PPI transformation can be used to transform natural language PPI descriptions (from input #4) into a structured notation. This option is chosen by setting CROSS_VALIDATION = false. This type of run comes with the following parameters to be configured:
- PPI_DESCRIPTIONS_FILE: location of .txt file containing the PPI descriptions to be transformed (see input #4 described earlier).
- MODEL_ID_FOR_DESCRIPTIONS: id of the process model associated with the PPI descriptions.
- USE_SERIALIZED_HMM: boolean, if true, it uses a serialized HMM instead of training an HMM on the set of annotated PPIS.
- HMM_SER:  location of serialized HMM that can be used.  The project distribution comes with a serialized HMM trained on the entire data collection used in the paper.

Evaluated PPI transformation can be used to evaluate the performance of the prototype against a goldstandard. This option is chosen by setting CROSS_VALIDATION = true. This type of run comes with the following parameters to be configured:
- EVAL_FOLDS: number of folds used in k-fold cross validation.
- EVAL_RUNS: number of times to run the k-fold cross validation.
- JUST_MODEL_ELEMENTS, PRINT_TYPE_ERRORS, PRINT_TAG_ERRORS, and PRINT_CHUNK_ERRORS can be used to alter the representation of evaluation results.


