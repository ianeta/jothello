package backprop;

public enum TrainingValues {
	WINNER(1), LOSER(0), EMPTY(0.5);
	
	private double code;

	private TrainingValues(double code) {
		this.code = code;
	}

	public double getCode() {
		return code;
	}
}
