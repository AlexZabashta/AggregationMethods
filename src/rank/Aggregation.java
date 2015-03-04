package rank;

import java.util.Arrays;
import java.util.Comparator;

import perm.Permutation;

public abstract class Aggregation {

	public abstract Permutation aggregate(Permutation[] permutation);

	public int chekSizes(Permutation[] permutation) {
		if (permutation.length == 0) {
			throw new IllegalArgumentException("Array of permutations is empty.");
		}

		int n = permutation[0].length();
		for (int i = 1; i < permutation.length; i++) {
			if (permutation[i].length() != n) {
				throw new IllegalArgumentException("Perementation[" + i + "] lenght != " + n);
			}
		}
		return n;
	}

	public Permutation aggregateByW(final double[] weigh) {
		int n = weigh.length;

		Integer[] order = new Integer[n];
		for (int i = 0; i < n; i++) {
			order[i] = i;
		}
		Arrays.sort(order, new Comparator<Integer>() {
			@Override
			public int compare(Integer i, Integer j) {
				return Double.compare(weigh[j], weigh[i]);
			}
		});

		return new Permutation(order);

	}

}
