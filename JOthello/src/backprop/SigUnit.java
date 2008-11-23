package backprop;

public class SigUnit {
	double weight[];

	/** Creates a new instance of SigUnit */
	public SigUnit(int i) {
		weight = new double[i];
	}

	public int getSize() {
		return weight.length;
	}

}
