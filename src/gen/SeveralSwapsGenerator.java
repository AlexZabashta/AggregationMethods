package gen;

import java.util.Random;
import perm.Permutation;

public class SeveralSwapsGenerator implements PermutationGenerator {

	Random rng;
	double scale, offset;

	public SeveralSwapsGenerator() {
		this(new Random());
	}

	public SeveralSwapsGenerator(double scale, double offset) {
		this(scale, offset, new Random());
	}

	public SeveralSwapsGenerator(Random rng) {
		this(1.0, 0.0, rng);
	}

	public SeveralSwapsGenerator(double scale, double offset, Random rng) {
		this.rng = rng;
		this.scale = scale;
		this.offset = offset;
	}

	public Permutation generate(int length, double sigma) {
		int numberOfSwaps = (int) (length * (scale * sigma + offset));

		int[] permutation = new int[length];
		for (int i = 0; i < length; i++) {
			permutation[i] = i;
		}

		while (--numberOfSwaps >= 0) {
			Permutation.swap(permutation, rng.nextInt(length), rng.nextInt(length));
		}
		return new Permutation(permutation);
	}
}
