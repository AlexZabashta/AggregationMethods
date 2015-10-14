package gen;

import java.util.Arrays;
import java.util.Random;

import perm.Disagreement;
import perm.Permutation;

public class LineSigmaGenerator implements DisagreementsGenerator {

	private final double[] hidenValues;

	Random rng;
	PermutationGenerator rpg;
	public LineSigmaGenerator(PermutationGenerator rpg) {
		this(rpg, new Random());
	}

	public LineSigmaGenerator(PermutationGenerator rpg, Random rng) {
		this.rpg = rpg;
		this.rng = rng;
		hidenValues = rpg.hidenValues();
	}

	@Override
	public Disagreement generate(int permutationsInSet, int permutationLength) {
		return generate(permutationsInSet, permutationLength, rng.nextDouble(), rng.nextDouble());
	}

	public Disagreement generate(int permutationsInSet, int permutationLength, double alpha, double beta) {

		double delta = (beta - alpha) / (permutationsInSet - 1);

		Permutation[] permutations = new Permutation[permutationsInSet];
		for (int i = 0; i < permutationsInSet; i++) {
			permutations[i] = rpg.generate(permutationLength, alpha + i * delta);
		}

		double[] hv = Arrays.copyOf(hidenValues, hidenValues.length + 3);
		hv[hv.length - 3] = 1;
		hv[hv.length - 2] = alpha;
		hv[hv.length - 1] = beta;

		return new Disagreement(hv, permutations);
	}

	public String toString() {
		return "SameSigmaGenerator(" + rpg + ")";
	}

}
