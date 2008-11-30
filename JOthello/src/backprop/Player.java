package backprop;

public enum Player {
	WHITE(0), BLACK(1), EMPTY(2);

	private int code;

	private Player(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

}
