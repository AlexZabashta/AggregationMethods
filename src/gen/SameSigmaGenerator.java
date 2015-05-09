package gen;

import java.util.Random;

import perm.Permutation;

public class SameSigmaGenerator implements DataSetsGenerator {

	PermutationGenerator rpg;
	Random rng;

	public SameSigmaGenerator(PermutationGenerator rpg) {
		this(rpg, new Random());
	}

	public SameSigmaGenerator(PermutationGenerator rpg, Random rng) {
		this.rpg = rpg;
		this.rng = rng;
	}

	@Override
	public Permutation[] generate(int permutationsInSet, int permutationLength) {
		double sigma = rng.nextDouble();

		Permutation[] dataSet = new Permutation[permutationsInSet];
		for (int i = 0; i < permutationsInSet; i++) {
			dataSet[i] = rpg.generate(permutationLength, sigma);
		}

		return dataSet;

	}

}
