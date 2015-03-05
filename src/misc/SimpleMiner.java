package misc;

import perm.BiInvariantMetric;
import perm.CayleyDistance;
import perm.KendallTau;
import perm.LevenshteinDistance;
import perm.Permutation;

public class SimpleMiner implements FeatureMiner {

	private static final int m = 10;

	public double[] mine(Permutation[] permutations) {
		double[] feature = new double[m];
		int n = permutations.length;

		Permutation[] invper = new Permutation[n];
		for (int i = 1; i < n; i++) {
			invper[i] = permutations[i].invert();
		}

		BiInvariantMetric[] bim = new BiInvariantMetric[] { new KendallTau(), new CayleyDistance(), new LevenshteinDistance() };

		StatisticalValue[] values = new StatisticalValue[bim.length];
		for (int i = 0; i < values.length; i++) {
			values[i] = new StatisticalValue();
		}

		for (int i = 0; i < n; i++) {
			for (int j = i + 1; j < n; j++) {
				Permutation p = permutations[i].product(invper[j]);
				for (int k = 0; k < bim.length; k++) {
					values[k].add(bim[k].distanceToIdentity(p));
				}
			}
		}

		return null;
	}

	public int length() {
		return m;
	}

}
