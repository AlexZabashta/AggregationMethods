package misc;

import perm.RightInvariantMetric;
import perm.CayleyDistance;
import perm.KendallTau;
import perm.LevenshteinDistance;
import perm.Permutation;

public class SimpleMiner implements FeatureMiner {

	private static final int m = 24;

	public double[] mine(Permutation[] permutations) {
		double[] features = new double[m];
		int n = permutations.length;

		Permutation[] invper = new Permutation[n];
		for (int i = 0; i < n; i++) {
			invper[i] = permutations[i].invert();
		}

		RightInvariantMetric[] rim = new RightInvariantMetric[] { new KendallTau(), new CayleyDistance(), new LevenshteinDistance() };

		int s = rim.length;

		StatisticalValue[] values = new StatisticalValue[s + 1];
		for (int i = 0; i < values.length; i++) {
			values[i] = new StatisticalValue();
		}

		for (int i = 0; i < n; i++) {
			for (int j = i + 1; j < n; j++) {
				Permutation p = permutations[i].product(invper[j]);
				for (int k = 0; k < s; k++) {
					values[k].add(rim[k].distanceToIdentity(p));
				}

				for (int k = 0; k < invper[i].length(); k++) {
					values[s].add(Math.abs(invper[i].get(k) - invper[j].get(k)));
				}

			}
		}

		for (int i = 0, j = 0; i <= s; i++) {
			features[j++] = values[i].getMax();
			features[j++] = values[i].getMin();
			features[j++] = values[i].getMean();
			features[j++] = values[i].getSkewness();
			features[j++] = values[i].getKurtosis();
			features[j++] = values[i].getStandardDeviation();
		}

		return features;
	}

	public int length() {
		return m;
	}

}
