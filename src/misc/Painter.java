package misc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import perm.Metric;
import perm.Permutation;

import rank.Aggregation;

public class Painter {

	List<Aggregation> aggregations = new ArrayList<Aggregation>();
	Metric metric;
	int n;

	public Painter(List<Aggregation> aggregations, Metric metric) {
		this.aggregations.addAll(aggregations);
		this.metric = metric;
		this.n = aggregations.size();
	}

	public int getColor(Permutation[] p) {
		return getColor(p, -1);
	}

	public int getColor(Permutation[] p, double eps) {
		int color = 0;
		double min1 = Double.POSITIVE_INFINITY, min2 = min1;

		if (n < 2) {
			return 0;
		}

		for (int i = 0; i < n; i++) {
			double cur = 0;
			Permutation q = aggregations.get(i).aggregate(p);

			for (Permutation permutation : p) {
				cur += metric.distance(permutation, q);
			}

			if (cur < min1) {
				min2 = min1;
				min1 = cur;
				color = i;
				continue;
			}

			if (cur < min2) {
				min2 = cur;
			}

		}

		if (min2 - min1 < eps * p.length) {
			return -1;
		}

		return color;
	}

	public int[] getColorDistribution(PermutationSetsGenerator psg) {
		return getColorDistribution(psg, 32 * n);
	}

	public int[] getColorDistribution(PermutationSetsGenerator psg, int numberOfSets) {
		int[] distribution = new int[n];

		while (--numberOfSets >= 0) {
			int color = getColor(psg.generate());
			if (color == -1) {
				continue;
			}
			++distribution[color];
		}

		return distribution;
	}
}
