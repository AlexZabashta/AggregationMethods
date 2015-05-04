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
		int color = 0;
		double min = Double.POSITIVE_INFINITY;

		for (int j = 0; j < n; j++) {
			double cur = 0;
			Permutation q = aggregations.get(j).aggregate(p);

			for (Permutation permutation : p) {
				cur += metric.distance(permutation, q);
			}

			if (cur < min) {
				min = cur;
				color = j;
			}
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
