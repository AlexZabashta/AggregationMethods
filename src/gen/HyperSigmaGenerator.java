package gen;

import java.util.Random;

import perm.Permutation;

public class HyperSigmaGenerator implements DataSetsGenerator {

	public String toString() {
		return "SameSigmaGenerator(" + rpg + ")";
	}

	PermutationGenerator rpg;
	Random rng;

	public HyperSigmaGenerator(PermutationGenerator rpg) {
		this(rpg, new Random());
	}

	public HyperSigmaGenerator(PermutationGenerator rpg, Random rng) {
		this.rpg = rpg;
		this.rng = rng;
	}

	@Override
	public Permutation[] generate(int permutationsInSet, int permutationLength) {
		return generate(permutationsInSet, permutationLength, rng.nextDouble(), rng.nextDouble());
	}

	public Permutation[] generate(int permutationsInSet, int permutationLength, double alpha, double beta) {

		double delta = (beta - alpha);

		Permutation[] dataSet = new Permutation[permutationsInSet];
		for (int i = 0; i < permutationsInSet; i++) {
			dataSet[i] = rpg.generate(permutationLength, alpha + delta / (i + 1));
		}

		return dataSet;
	}

}
