package gen;

import java.util.Locale;
import java.util.Random;

import perm.Permutation;

public class GaussGenerator implements PermutationGenerator {

	private static final double sqrt3 = Math.sqrt(3);

	Random rng;

	double scale, offset;
	public GaussGenerator() {
		this(new Random());
	}

	public GaussGenerator(double scale, double offset) {
		this(scale, offset, new Random());
	}

	public GaussGenerator(double scale, double offset, Random rng) {
		this.rng = rng;
		this.scale = scale;
		this.offset = offset;
	}

	public GaussGenerator(Random rng) {
		this(1.0, 0.0, rng);
	}

	public Permutation generate(int length, double sigma) {
		sigma = (scale * sigma + offset) / sqrt3;
		int[] permutation = new int[length];
		for (int i = 0; i < length; i++) {
			permutation[i] = i;
			float scaledValue = (float) Math.abs(length * sigma * rng.nextGaussian());
			int j = Math.max(0, i - Math.round(scaledValue));
			Permutation.swap(permutation, i, j);
		}
		return new Permutation(permutation);
	}

	public double[] hidenValues() {
		return new double[] { 2, scale, offset };
	}

	@Override
	public String toString() {
		return String.format(Locale.ENGLISH, "GaussGenerator(%.2f, %.2f)", scale, offset);
	}

}
