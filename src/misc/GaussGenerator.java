package misc;

import java.util.Collection;
import java.util.List;
import java.util.Random;

import perm.Permutation;

public class GaussGenerator implements PermutationSetsGenerator {

	private double scale;
	private perm.Random rpg;
	private java.util.Random rng;
	private int permutationsInSet, permutationLength;

	public GaussGenerator(int permutationsInSet, int permutationLength, Random random) {
		this(permutationsInSet, permutationLength, 0.1, new Random());
	}
	
	public GaussGenerator(int permutationsInSet, int permutationLength) {
		this(permutationsInSet, permutationLength, new Random());
	}

	public GaussGenerator(int permutationsInSet, int permutationLength, double scale) {
		this(permutationsInSet, permutationLength, scale, new Random());
	}


	public GaussGenerator(int permutationsInSet, int permutationLength, double scale, Random random) {
		this.rng = random;
		this.rpg = new perm.Random(random);
		this.scale = scale;

		this.permutationsInSet = permutationsInSet;
		this.permutationLength = permutationLength;
	}

	public Permutation[] generate() {
		double sigma = scale * rng.nextDouble();
		return generate(sigma);
	}

	public Permutation[] generate(double sigma) {
		Permutation[] p = new Permutation[permutationsInSet];

		for (int i = 0; i < permutationsInSet; i++) {
			p[i] = rpg.nextGaussian(permutationLength, sigma);
		}

		return p;
	}

}
