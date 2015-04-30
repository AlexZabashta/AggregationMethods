package rank;

import perm.Metric;
import perm.Permutation;

public class PickAPerm extends Aggregation {

	private Metric metric;

	public PickAPerm(Metric metric) {
		this.metric = metric;
	}

	@Override
	public Permutation aggregate(Permutation[] permutations) {
		int n = chekSizes(permutations);
		int m = permutations.length;

		if (n < 2) {
			return new Permutation(n);
		}
		double[] w = new double[n];

		Permutation ans = null;
		double aDist = 2 * m;

		for (Permutation p : permutations) {
			double pDist = 0;
			for (Permutation q : permutations) {
				pDist += metric.distance(p, q);
			}
			if (ans == null || pDist < aDist) {
				aDist = pDist;
				ans = p;
			}
		}

		return ans;
	}

}
