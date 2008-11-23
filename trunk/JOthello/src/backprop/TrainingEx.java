package backprop;

public class TrainingEx {
	int[] input;
	int inputSize;
	double output;

	/** Creates a new instance of TrainingEx */
	public TrainingEx(int[] in, double o) {
		inputSize = in.length + 1;
		output = o;

		input = new int[inputSize];
		input[0] = 1;
		for (int i = 1; i < inputSize; i++)
			input[i] = in[i - 1];
	}

	public double getOutput() {
		return output;
	}

	public int[] getInput() {
		return input;
	}

	public int getInputSize() {
		return inputSize;
	}

	public String toString() {
		String out = "TrainingEx:";
		for (int i = 0; i < input.length; i++)
			out += " " + input[i];
		out += " Output: " + output;
		return out;
	}

}
