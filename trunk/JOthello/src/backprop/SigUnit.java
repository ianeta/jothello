package backprop;

import java.io.Serializable;

public class SigUnit implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1359505127206610061L;
	double weight[];

	/** Creates a new instance of SigUnit */
	public SigUnit(int i) {
		weight = new double[i];
	}

	public int getSize() {
		return weight.length;
	}

}
