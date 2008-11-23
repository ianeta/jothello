
package backprop;

import java.util.Random;

public class Main {
	
	public static final int inputSize = 64, trainingSetSize = 1000, verificationSetSize = 500;

	public static void main(String[] args) {
		Backprop bp = new Backprop(inputSize, inputSize/2, 0.05);
		bp.init_weights();

		TrainingEx[] te = new TrainingEx[trainingSetSize];
		TrainingEx[] vs = new TrainingEx[verificationSetSize];

		for (int i = 0; i < verificationSetSize; i ++) {
			int[] in2 = new int[inputSize];
			Random ran = new Random();
			for(int j = 0; j < inputSize; j++) {
				if(ran.nextBoolean()) {
					in2[j] = 1;
				}
				else {
					in2[j] = 0;
				}
			}
			vs[i] = new TrainingEx(in2, i%2);//(double) i * 4 * 0.01);
			System.out.println(vs[i]);
		}

		
		for (int i = 0; i < trainingSetSize; i ++) {
			int[] in2 = new int[inputSize];
			Random ran = new Random();
			for(int j = 0; j < inputSize; j++) {
				if(ran.nextBoolean()) {
					in2[j] = 1;
				}
				else {
					in2[j] = 0;
				}
			}
			te[i] = new TrainingEx(in2, i%2);//(double) i * 4 * 0.01);
			System.out.println(te[i]);

		}
		

		bp.update_weights(te);

		for (int i = 0; i < verificationSetSize; i++) {
			double[] a = bp.calculateOutput(vs[i]);
			System.out.println(vs[i] + " Calc: " + a[0]);
		}

		for (int i = 0; i < trainingSetSize; i++) {
			double[] a = bp.calculateOutput(te[i]);
			System.out.println(te[i] + " Calc: " + a[0]);
		}
	}

}
