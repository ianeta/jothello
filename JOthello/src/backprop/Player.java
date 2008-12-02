package backprop;

public enum Player {
	WHITE(0), BLACK(1), EMPTY(0.5);

	private double code;

	private Player(double code) {
		this.code = code;
	}

	public double getCode() {
		return code;
	}

}
