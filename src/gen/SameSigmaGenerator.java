package gen;

import java.util.Arrays;
import java.util.Random;

import perm.Disagreement;
import perm.Permutation;

public class SameSigmaGenerator implements DisagreementsGenerator {

	private final double[] hidenValues;

	Random rng;
	PermutationGenerator rpg;

	public SameSigmaGenerator(PermutationGenerator rpg) {
		this(rpg, new Random());
	}

	public SameSigmaGenerator(PermutationGenerator rpg, Random rng) {
		this.rpg = rpg;
		this.rng = rng;
		hidenValues = rpg.hidenValues();
	}

	@Override
	public Disagreement generate(int permutationsInSet, int permutationLength) {
		return generate(permutationsInSet, permutationLength, rng.nextDouble());
	}

	public Disagreement generate(int permutationsInSet, int permutationLength, double sigma) {

		Permutation[] permutations = new Permutation[permutationsInSet];
		for (int i = 0; i < permutationsInSet; i++) {
			permutations[i] = rpg.generate(permutationLength, sigma);
		}

		double[] hv = Arrays.copyOf(hidenValues, hidenValues.length + 3);
		hv[hv.length - 3] = 0.0;
		hv[hv.length - 2] = sigma;
		hv[hv.length - 1] = sigma;

		return new Disagreement(hv, permutations);
	}

	public String toString() {
		return "SameSigmaGenerator(" + rpg + ")";
	}

}
