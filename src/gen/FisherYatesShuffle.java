package gen;

import java.util.Locale;
import java.util.Random;

import perm.Permutation;

public class FisherYatesShuffle implements PermutationGenerator {

	Random rng;

	double scale, offset;
	public FisherYatesShuffle() {
		this(new Random());
	}

	public FisherYatesShuffle(double scale, double offset) {
		this(scale, offset, new Random());
	}

	public FisherYatesShuffle(double scale, double offset, Random rng) {
		this.rng = rng;
		this.scale = scale;
		this.offset = offset;
	}

	public FisherYatesShuffle(Random rng) {
		this(1.0, 0.0, rng);
	}

	public Permutation generate(int length, double sigma) {
		double m = length * (scale * sigma + offset);
		int[] permutation = new int[length];
		for (int i = 0; i < length; i++) {
			permutation[i] = i;

			if ((length - i) * rng.nextDouble() < m) {
				int j = rng.nextInt(i + 1);
				Permutation.swap(permutation, i, j);
				--m;
			}

		}
		return new Permutation(permutation);
	}

	public double[] hidenValues() {
		return new double[] { 0, scale, offset };
	}

	@Override
	public String toString() {
		return String.format(Locale.ENGLISH, "FisherYatesShuffle(%.2f, %.2f)", scale, offset);
	}
}
