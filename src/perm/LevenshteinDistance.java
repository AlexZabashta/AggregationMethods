package perm;

import java.util.Arrays;

public class LevenshteinDistance extends Metric  {
	public double distanceToIdentity(Permutation permutation) {
		int n = permutation.length();

		if (n <= 1) {
			return 0.0;
		}

		int[] d = new int[n + 1];

		Arrays.fill(d, n);
		d[0] = -1;

		int lis = 0;

		for (int i = 0; i < n; i++) {
			int v = permutation.get(i), j = ~Arrays.binarySearch(d, v);
			d[j] = Math.min(d[j], v);
			lis = Math.max(lis, j);
		}

		return (n - lis)/ (n - 1.0);
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}
}
