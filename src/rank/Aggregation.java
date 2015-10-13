package rank;

import java.util.Arrays;
import java.util.Comparator;

import perm.Disagreement;
import perm.Permutation;

public abstract class Aggregation {

	public static Permutation aggregateByWeights(final double[] weighs) {
		int n = weighs.length;

		Integer[] order = new Integer[n];
		for (int i = 0; i < n; i++) {
			order[i] = i;
		}
		Arrays.sort(order, new Comparator<Integer>() {
			@Override
			public int compare(Integer i, Integer j) {
				return Double.compare(weighs[j], weighs[i]);
			}
		});

		return new Permutation(order);

	}

	public abstract Permutation aggregate(Disagreement disagreement);

	public final Permutation aggregate(Permutation... permutations) {
		return aggregate(new Disagreement(permutations));
	}
}
