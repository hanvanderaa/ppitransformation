package ppitransformation.ppi;

public enum MeasureType {
	TIME, COUNT, CONDITION, DATA, AGGREGATED, DERIVED, FRACTION, UNDEFINED;
	
	
	public static MeasureType getType(String string) {
		for (MeasureType type : MeasureType.values()) {
			if (type.toString().equalsIgnoreCase(string)) {
				return type;
			}
		}
		if (string.equalsIgnoreCase("DATAF") || string.equalsIgnoreCase("DATAN")) {
			return DATA;
		}
		return UNDEFINED;
	}
	
	public static String[] stringValues() {
		String[] result = new String[MeasureType.values().length];
		int i = 0;
		for (MeasureType type : MeasureType.values()) {
			result[i] = type.toString();
			i++;
		}
		return result;
	}
}

