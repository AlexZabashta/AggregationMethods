package rank;

import perm.Permutation;

public class HyperbolicBordaCount extends Aggregation {

	@Override
	public Permutation aggregate(Permutation[] permutations) {
		int n = chekSizes(permutations);
		int m = permutations.length;

		if (n < 2) {
			return new Permutation(n);
		}
		double[] w = new double[n];

		for (Permutation p : permutations) {
			for (int i = 0; i < n; i++) {
				int j = p.get(i);
				w[j] += 1.0 / (1 + i);
			}
		}

		return aggregateByW(w);
	}

}
