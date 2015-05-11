package gen;

import java.util.Random;

import perm.Permutation;

public class GaussGenerator implements PermutationGenerator {

	Random rng;

	@Override
	public String toString() {
		return "GaussGenerator";
	}

	double scale, offset;

	public GaussGenerator() {
		this(new Random());
	}

	public GaussGenerator(double scale, double offset) {
		this(scale, offset, new Random());
	}

	public GaussGenerator(Random rng) {
		this(1.0, 0.0, rng);
	}

	public GaussGenerator(double scale, double offset, Random rng) {
		this.rng = rng;
		this.scale = scale;
		this.offset = offset;
	}

	public Permutation generate(int length, double sigma) {
		sigma = scale * sigma + offset;
		int[] permutation = new int[length];
		for (int i = 0; i < length; i++) {
			permutation[i] = i;
			float scaledValue = (float) Math.abs(length * sigma * rng.nextGaussian());
			int j = Math.max(0, i - Math.round(scaledValue));
			Permutation.swap(permutation, i, j);
		}
		return new Permutation(permutation);
	}

}
