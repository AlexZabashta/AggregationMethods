package misc;

import java.util.ArrayList;
import java.util.List;

import perm.CanberraDistance;
import perm.LAbs;
import perm.LMax;
import perm.LSquare;
import perm.Metric;
import perm.RightInvariantMetric;
import perm.CayleyDistance;
import perm.KendallTau;
import perm.LevenshteinDistance;
import perm.Permutation;

public class SimpleMiner implements FeatureMiner {

	private static final int m = 48;

	Metric[] metrics;

	public SimpleMiner() {

		List<Metric> metricsList = new ArrayList<Metric>();

		metricsList.add(new CanberraDistance());

		metricsList.add(new KendallTau());
		metricsList.add(new LevenshteinDistance());
		metricsList.add(new CayleyDistance());

		metricsList.add(new LSquare());
		metricsList.add(new LMax());
		metricsList.add(new LAbs());

		metrics = metricsList.toArray(new Metric[0]);
	}

	public double[] mine(Permutation[] permutations) {
		double[] features = new double[m];
		int n = permutations.length;

		Permutation[] invper = new Permutation[n];
		for (int i = 0; i < n; i++) {
			invper[i] = permutations[i].invert();
		}

		int s = metrics.length;

		StatisticalValue[] values = new StatisticalValue[s + 1];
		for (int i = 0; i < values.length; i++) {
			values[i] = new StatisticalValue();
		}

		for (int i = 0; i < n; i++) {
			for (int j = i + 1; j < n; j++) {
				for (int k = 0; k < s; k++) {
					values[k].add(metrics[k].distance(permutations[i], permutations[j]));
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
