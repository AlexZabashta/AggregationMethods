import java.util.Random;

import gen.FisherYatesShuffle;
import gen.PermutationGenerator;
import perm.CanberraDistance;
import perm.KendallTau;
import perm.LevenshteinDistance;
import perm.Metric;
import perm.Permutation;

public class Test {

	public static void main(String[] args) {

		int n = 5, m = 13;

		Random rng = new Random();
		PermutationGenerator rpg = new FisherYatesShuffle(rng);

		Metric mu = new CanberraDistance();

		Permutation[] data = new Permutation[n];
		for (int i = 0; i < n; i++) {
			data[i] = rpg.generate(m, 0.3);
		}

		Permutation ans = new Permutation(m);

		for (Permutation p : data) {
			System.out.println(p);
		}

	}
}
