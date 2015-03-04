package rank;

import perm.Permutation;

public class HyperbolicBordaCount extends Aggregation {

	@Override
	public Permutation aggregate(Permutation[] permutation) {
		int n = chekSizes(permutation);
		int m = permutation.length;

		if (n < 2) {
			return new Permutation(n);
		}
		double[] w = new double[n];

		for (Permutation p : permutation) {
			for (int i = 0; i < n; i++) {
				int j = p.get(i);
				w[j] += 1.0 / (1 + i);
			}
		}

		return aggregateByW(w);
	}

}
