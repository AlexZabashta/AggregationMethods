package rank;

import java.util.Arrays;

import perm.Permutation;

public class CopelandScore extends Aggregation {
	public Permutation aggregate(Permutation[] permutations) {
		int n = chekSizes(permutations);
		int m = permutations.length;

		if (n < 2) {
			return new Permutation(n);
		}

		Permutation[] invper = new Permutation[m];
		for (int i = 0; i < m; i++) {
			invper[i] = permutations[i].invert();
		}

		int[][] v = new int[n][n], l = new int[n][n];

		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				for (Permutation p : invper) {
					if (p.get(i) < p.get(j)) {
						++l[i][j];
					} else {
						++v[i][j];
					}
				}
			}
		}

		double[] weigh = new double[n];

		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if (i != j) {
					if (v[i][j] > l[i][j]) {
						weigh[i] -= 1.0;
					}
					if (v[i][j] == l[i][j]) {
						weigh[i] -= 0.5;
					}
				}
			}
		}

		return aggregateByW(weigh);
	}
}
