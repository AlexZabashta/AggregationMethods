package gen;

import java.util.Random;

import perm.Permutation;

public class LineSigmaGenerator implements DataSetsGenerator {

	public String toString() {
		return "SameSigmaGenerator(" + rpg + ")";
	}

	PermutationGenerator rpg;
	Random rng;

	public LineSigmaGenerator(PermutationGenerator rpg) {
		this(rpg, new Random());
	}

	public LineSigmaGenerator(PermutationGenerator rpg, Random rng) {
		this.rpg = rpg;
		this.rng = rng;
	}

	@Override
	public Permutation[] generate(int permutationsInSet, int permutationLength) {
		return generate(permutationsInSet, permutationLength, rng.nextDouble(), rng.nextDouble());
	}

	public Permutation[] generate(int permutationsInSet, int permutationLength, double alpha, double beta) {

		double delta = (beta - alpha) / (permutationsInSet - 1);

		Permutation[] dataSet = new Permutation[permutationsInSet];
		for (int i = 0; i < permutationsInSet; i++) {
			dataSet[i] = rpg.generate(permutationLength, alpha + i * delta);
		}

		return dataSet;
	}

}
