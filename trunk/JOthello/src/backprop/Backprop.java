package backprop;

import java.io.Serializable;
import java.util.Random;

public class Backprop implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1798604603698096051L;
	SigUnit[] hidden;
	SigUnit[] out;
	int numHidden, numInput;
	double ln;
	final int numOutput = 1;
	final double threshold = 0.25;
	final long maxTrainingTimeMillis = 10000;

	/** Creates a new instance of Backprop */
	public Backprop(int in, int h, double n) {
		// Initialize array of hidden sigUnits based on input h
		hidden = new SigUnit[h];
		// Initialize array of output sigUnits fixed at 1
		out = new SigUnit[numOutput];
		// Initialize the learning rate
		ln = n;

		// Initialize each hidden sigUnit with number of inputs in, plus one for
		// threshold weight
		for (int i = 0; i < h; i++) {
			hidden[i] = new SigUnit(in + 1);
		}

		// Initialize each output sigUnit with fixed number of outputs 1, plus
		// one for threshold weight
		for (int i = 0; i < numOutput; i++) {
			out[i] = new SigUnit(h + 1);
		}

		// Store the size variables
		numInput = in;
		numHidden = h;
	}

	public void init_weights() {
		Random gen;

		gen = new Random();
		// Give each weight for each hidden unit a random value
		int size = hidden[0].getSize();
		for (int i = 0; i < hidden.length; i++) {
			for (int j = 0; j < size; j++)
				hidden[i].weight[j] = gen.nextDouble() - 0.5;
		}

		// Give each weight for each output unit a random value
		size = out[0].getSize();
		for (int i = 0; i < out.length; i++) {
			for (int j = 0; j < size; j++)
				out[i].weight[j] = gen.nextDouble() - 0.5;
		}

	}

	/*
	 * public void update_weights(TrainingEx[] te) { int teSize = te.length;
	 * double net, sig; double avgError = 1; double[] hOutput = new
	 * double[numHidden]; double[] oOutput = new double[numOutput]; double[]
	 * hError = new double[numHidden]; double[] oError = new double[numOutput];
	 * 
	 * boolean done = false;
	 * 
	 * while(avgError > 1) { for(int i = 0; i < teSize; i++) {
	 * 
	 * //Calculate the output of each hidden unit for(int j = 0; j < numHidden;
	 * j++) { net = 0; for(int k = 0; k < te[i].getInputSize(); k++) { net +=
	 * hidden[j].weight[k]te[i].input[k]; } sig = 1/(1 +
	 * java.lang.Math.exp(net)); hOutput[j] = sig; }
	 * 
	 * //Calculate the output of each output unit for(int j = 0; j < numOutput;
	 * j++) { net = 0; for(int k = 0; k < te[i].getInputSize(); k++) { net +=
	 * out[j].weight[k]te[i].input[k]; } sig = 1/(1 + java.lang.Math.exp(net));
	 * oOutput[j] = sig; }
	 * 
	 * //Calculate the error for each output unit for(int k = 0; k < numOutput;
	 * k++) { oError[k] =
	 * oOutput[k](1-oOutput[k])(te[i].getOutput()-oOutput[k]); }
	 * 
	 * //Calculate the error for each hidden unit for(int h = 0; h < numHidden;
	 * h++) { double a;
	 * 
	 * a = 0; for(int k = 0; k < numOutput; k++) { a +=
	 * out[k].weight[h]oError[k]; }
	 * 
	 * hError[h] = hOutput[h](1-hOutput[h])a; }
	 * 
	 * double dw; //Update each hidden weight for(int m = 0; m < numHidden; m++)
	 * { for(int n = 0; n < hidden[m].getSize(); n++) { dw =
	 * lnhError[m]te[i].input[n]; hidden[m].weight[n] += dw; } }
	 * 
	 * //Update each output weight for(int m = 0; m < numOutput; m++) { for(int
	 * n = 0; n < out[m].getSize(); n++) { dw = lnoError[m]hOutput[n];
	 * out[m].weight[n] += dw; } } }
	 * 
	 * 
	 * }
	 * 
	 * }
	 */

	public void update_weights(TrainingEx[] te) throws Exception {
		update_weights(te, 0);
	}

	public void update_weights(TrainingEx[] te, double alpha) throws Exception {
		int teSize = te.length;
		double net, sig;
		double avgError;
		double[] hOutput = new double[numHidden];
		double[] oOutput = new double[numOutput];
		double[] hError = new double[numHidden];
		double[] oError = new double[numOutput];
		double[][] hDw = new double[numHidden][numInput + 1];
		double[][] oDw = new double[numOutput][numHidden + 1];
		
		if(te.length <= 0) {
			throw new Exception("Tried to create an ANN with using no weights");
		}

		for (int i = 0; i < numHidden; i++)
			for (int j = 0; j < numInput + 1; j++)
				hDw[i][j] = 0;
		for (int i = 0; i < numOutput; i++)
			for (int j = 0; j < numHidden + 1; j++)
				oDw[i][j] = 0;

		//boolean done = false;

		avgError = 10;
		long startTime = System.currentTimeMillis();
		while (avgError > threshold) {
			this.checkTime(startTime);
			for (int i = 0; i < teSize; i++) {

				// Calculate the output of each hidden unit
				for (int j = 0; j < numHidden; j++) {
					net = 0;
					for (int k = 0; k < te[i].getInputSize(); k++) {
						net += hidden[j].weight[k] * te[i].input[k];
					}
					sig = 1 / (1 + java.lang.Math.exp(-net));
					hOutput[j] = sig;
				}

				// Calculate the output of each output unit
				for (int j = 0; j < numOutput; j++) {
					net = 0;
					net += 1 * out[j].weight[0];
					for (int k = 0; k < numHidden; k++) {
						net += out[j].weight[k + 1] * hOutput[k];
					}
					sig = 1 / (1 + java.lang.Math.exp(-net));
					oOutput[j] = sig;
				}

				// Calculate the error for each output unit
				for (int k = 0; k < numOutput; k++) {
					oError[k] = oOutput[k] * (1 - oOutput[k])
							* (te[i].getOutput() - oOutput[k]);
				}

				// Calculate the error for each hidden unit
				for (int h = 0; h < numHidden; h++) {
					double a;

					a = 0;
					for (int k = 0; k < numOutput; k++) {
						a += out[k].weight[h] * oError[k];
					}

					hError[h] = hOutput[h] * (1 - hOutput[h]) * a;
				}

				// Update each hidden weight
				for (int m = 0; m < numHidden; m++) {
					for (int n = 0; n < hidden[m].getSize(); n++) {
						hDw[m][n] = ln * hError[m] * te[i].input[n] + alpha
								* hDw[m][n];
						hidden[m].weight[n] += hDw[m][n];
					}
				}

				// Update each output weight
				for (int m = 0; m < numOutput; m++) {
					oDw[m][0] = ln * oError[0] * 1 + alpha * oDw[m][0];
					out[m].weight[0] += oDw[m][0];
					for (int n = 1; n < out[m].getSize(); n++) {
						oDw[m][n] = ln * oError[m] * hOutput[n - 1] + alpha
								* oDw[m][n];
						out[m].weight[n] += oDw[m][n];
					}
				}
			}

			// Calculate the error
			avgError = calcError(te);
			System.out.println(avgError);
		}

	}

	public void update_weights(TrainingEx[] te, TrainingEx[] vs) throws Exception {
		update_weights(te, vs, 0);
	}

	public void update_weights(TrainingEx[] te, TrainingEx[] vs, double alpha) throws Exception {
		int teSize = te.length;
		double net, sig;
		double avgError;
		double[] hOutput = new double[numHidden];
		double[] oOutput = new double[numOutput];
		double[] hError = new double[numHidden];
		double[] oError = new double[numOutput];
		double[][] hDw = new double[numHidden][numInput + 1];
		double[][] oDw = new double[numOutput][numHidden + 1];
		
		if(te.length <= 0) {
			throw new Exception("Tried to create an ANN with using no weights");
		}

		for (int i = 0; i < numHidden; i++)
			for (int j = 0; j < numInput + 1; j++)
				hDw[i][j] = 0;
		for (int i = 0; i < numOutput; i++)
			for (int j = 0; j < numHidden + 1; j++)
				oDw[i][j] = 0;

		//boolean done = false;

		avgError = 10;
		long startTime = System.currentTimeMillis();
		while (avgError > threshold) {
			this.checkTime(startTime);
			for (int i = 0; i < teSize; i++) {

				// Calculate the output of each hidden unit
				for (int j = 0; j < numHidden; j++) {
					net = 0;
					for (int k = 0; k < te[i].getInputSize(); k++) {
						net += hidden[j].weight[k] * te[i].input[k];
					}
					sig = 1 / (1 + java.lang.Math.exp(-net));
					hOutput[j] = sig;
				}

				// Calculate the output of each output unit
				for (int j = 0; j < numOutput; j++) {
					net = 0;
					net += 1 * out[j].weight[0];
					for (int k = 0; k < numHidden; k++) {
						net += out[j].weight[k + 1] * hOutput[k];
					}
					sig = 1 / (1 + java.lang.Math.exp(-net));
					oOutput[j] = sig;
				}

				// Calculate the error for each output unit
				for (int k = 0; k < numOutput; k++) {
					oError[k] = oOutput[k] * (1 - oOutput[k])
							* (te[i].getOutput() - oOutput[k]);
				}

				// Calculate the error for each hidden unit
				for (int h = 0; h < numHidden; h++) {
					double a;

					a = 0;
					for (int k = 0; k < numOutput; k++) {
						a += out[k].weight[h] * oError[k];
					}

					hError[h] = hOutput[h] * (1 - hOutput[h]) * a;
				}

				// Update each hidden weight
				for (int m = 0; m < numHidden; m++) {
					for (int n = 0; n < hidden[m].getSize(); n++) {
						hDw[m][n] = ln * hError[m] * te[i].input[n] + alpha
								* hDw[m][n];
						hidden[m].weight[n] += hDw[m][n];
					}
				}

				// Update each output weight
				for (int m = 0; m < numOutput; m++) {
					oDw[m][0] = ln * oError[0] * 1 + alpha * oDw[m][0];
					out[m].weight[0] += oDw[m][0];
					for (int n = 1; n < out[m].getSize(); n++) {
						oDw[m][n] = ln * oError[m] * hOutput[n - 1] + alpha
								* oDw[m][n];
						out[m].weight[n] += oDw[m][n];
					}
				}
			}

			// Calculate the error
			avgError = calcError(vs);
			// System.out.println(avgError);
		}

	}

	public double[] calculateOutput(TrainingEx in) {
		double net, sig;
		double hOutput[] = new double[numHidden];
		double oOutput[] = new double[numOutput];

		// Calculate the outputs of the hiddenunits
		for (int j = 0; j < numHidden; j++) {
			net = 0;
			for (int k = 0; k < in.getInputSize(); k++) {
				net += hidden[j].weight[k] * in.input[k];
			}
			sig = 1 / (1 + java.lang.Math.exp(-net));
			hOutput[j] = sig;
		}

		// Calculate the output values
		for (int j = 0; j < numOutput; j++) {
			net = 0;
			net += 1 * out[j].weight[0];
			for (int k = 0; k < numHidden; k++) {
				net += out[j].weight[k + 1] * hOutput[k];
			}
			sig = 1 / (1 + java.lang.Math.exp(-net));
			oOutput[j] = sig;
		}

		return oOutput;
	}

	public double calcError(TrainingEx[] te) {
		double error = 0;
		double output, err;
		for (int i = 0; i < te.length; i++) {
			output = calculateOutput(te[i])[0];
			err = java.lang.Math.abs(output - te[i].getOutput());
			error += err;
			// System.out.println(te[i] + " Error: " + err + "Out: " + output);
		}
		error = error / te.length;
		return error;
	}

	public SigUnit[] get_hidden() {
		return hidden;
	}

	public SigUnit[] get_out() {
		return out;
	}
	
	public void checkTime(long startTime) throws Exception {
		long currentTime = System.currentTimeMillis();
		
		if(currentTime - startTime > this.maxTrainingTimeMillis) {
			throw new Exception("Ran out of time training the ANN, will stop here, Threshold: " + this.threshold);
		}
		
	}

}
