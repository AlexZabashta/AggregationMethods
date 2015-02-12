package aggr;

import java.util.Arrays;
import java.util.Comparator;

import perm.Permutation;

public abstract class Aggregation {

	abstract double[] getWeigh(Vote[] votes);

	public Permutation aggregate(Vote[] votes) {
		if (votes.length == 0) {
			return null;
		}

		int m = votes.length;
		int n = votes[0].permutation.length();
		for (int i = 1; i < m; i++) {
			if (votes[i].permutation.length() != n) {
				throw new IllegalArgumentException("All permutations does not has the same size.");
			}
		}

		final double[] w = getWeigh(votes);
		Integer[] order = new Integer[n];
		for (int i = 0; i < n; i++) {
			order[i] = i;
		}
		Arrays.sort(order, new Comparator<Integer>() {
			@Override
			public int compare(Integer i, Integer j) {
				return Double.compare(w[j], w[i]);
			}
		});

		return new Permutation(order);

	}

}
