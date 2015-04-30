package misc;

import java.util.Collection;
import java.util.List;
import java.util.Random;

import perm.Permutation;

public class SeveralSwapsGenerator implements PermutationSetsGenerator {

	private double scale;
	private perm.Random rpg;
	private java.util.Random rng;
	private int permutationsInSet, permutationLength;

	public SeveralSwapsGenerator(int permutationsInSet, int permutationLength, Random random) {
		this(permutationsInSet, permutationLength, 0.1, new Random());
	}

	public SeveralSwapsGenerator(int permutationsInSet, int permutationLength) {
		this(permutationsInSet, permutationLength, new Random());
	}

	public SeveralSwapsGenerator(int permutationsInSet, int permutationLength, double scale) {
		this(permutationsInSet, permutationLength, scale, new Random());
	}

	public SeveralSwapsGenerator(int permutationsInSet, int permutationLength, double scale, Random random) {
		this.rng = random;
		this.rpg = new perm.Random(random);
		this.scale = scale;

		this.permutationsInSet = permutationsInSet;
		this.permutationLength = permutationLength;
	}

	public Permutation[] generate() {
		int numberOfSwaps = Math.round((float) (rng.nextDouble() * scale * permutationLength));
		return generate(numberOfSwaps);
	}

	public Permutation[] generate(int numberOfSwaps) {
		Permutation[] p = new Permutation[permutationsInSet];
		for (int i = 0; i < permutationsInSet; i++) {
			p[i] = rpg.next(permutationLength, numberOfSwaps);
		}

		return p;
	}

}
