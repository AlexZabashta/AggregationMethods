package perm;

public class KendallTau extends BiInvariantMetric {

	public double distanceToIdentity(Permutation c) {
		int n = c.length();

		if (n <= 1) {
			return 0.0;
		}

		double size = n * (n - 1.0);

		long inversions = 0;
		for (int d : c.toInversions()) {
			inversions += d;
		}

		return 2 * inversions / size;
	}
}
