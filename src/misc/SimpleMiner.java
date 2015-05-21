package misc;

import java.util.ArrayList;
import java.util.List;

import perm.CanberraDistance;
import perm.LAbs;
import perm.LMax;
import perm.LSquare;
import perm.Metric;
import perm.CayleyDistance;
import perm.KendallTau;
import perm.LevenshteinDistance;
import perm.Permutation;

public class SimpleMiner implements FeatureMiner {

	private static final int m = 5;

	public double[] mine(Permutation[] permutations) {
		double[] features = new double[m];
		int n = permutations.length;

		Permutation[] invper = new Permutation[n];
		for (int i = 0; i < n; i++) {
			invper[i] = permutations[i].invert();
		}

		StatisticalValue dist = new StatisticalValue();

		for (int i = 0; i < n; i++) {
			for (int j = i + 1; j < n; j++) {
				for (int k = 0; k < invper[i].length(); k++) {
					dist.add(Math.abs(invper[i].get(k) - invper[j].get(k)));
				}
			}
		}

		int fp = 0;

		features[fp++] = dist.getMax();
		// features[fp++] = dist.getMin();
		features[fp++] = dist.getMean();
		features[fp++] = dist.getSkewness();
		features[fp++] = dist.getKurtosis();
		features[fp++] = dist.getStandardDeviation();

		return features;
	}

	public int length() {
		return m;
	}

}
