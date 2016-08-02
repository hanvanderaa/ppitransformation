package ppitransformation.main;

public class Config {

	public static int TFIDF = 0;
	public static int MODE_DISCO = 1;

	public int termSimMode;
	public int runs;
	public int folds;
	public boolean justmodelelements;
	
	public Config(int termSimMode, int runs, int folds, boolean justmodelelements) {
		this.termSimMode = termSimMode;
		this.runs = runs;
		this.folds = folds;
		this.justmodelelements = justmodelelements;
	}
	
}
