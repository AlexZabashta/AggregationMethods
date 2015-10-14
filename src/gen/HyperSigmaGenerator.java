package gen;

import java.util.Arrays;
import java.util.Random;

import perm.Disagreement;
import perm.Permutation;

public class HyperSigmaGenerator implements DisagreementsGenerator {

	private final double[] hidenValues;

	Random rng;
	PermutationGenerator rpg;
	public HyperSigmaGenerator(PermutationGenerator rpg) {
		this(rpg, new Random());
	}

	public HyperSigmaGenerator(PermutationGenerator rpg, Random rng) {
		this.rpg = rpg;
		this.rng = rng;
		hidenValues = rpg.hidenValues();
	}

	@Override
	public Disagreement generate(int permutationsInSet, int permutationLength) {
		return generate(permutationsInSet, permutationLength, rng.nextDouble(), rng.nextDouble());
	}

	public Disagreement generate(int permutationsInSet, int permutationLength, double alpha, double beta) {

		double delta = (beta - alpha);

		Permutation[] permutations = new Permutation[permutationsInSet];
		for (int i = 0; i < permutationsInSet; i++) {
			permutations[i] = rpg.generate(permutationLength, alpha + delta / (i + 1));
		}

		double[] hv = Arrays.copyOf(hidenValues, hidenValues.length + 3);
		hv[hv.length - 3] = 2;
		hv[hv.length - 2] = alpha;
		hv[hv.length - 1] = beta;

		return new Disagreement(hv, permutations);
	}

	public String toString() {
		return "SameSigmaGenerator(" + rpg + ")";
	}

}
