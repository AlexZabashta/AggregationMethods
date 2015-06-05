package perm;

public class KendallTau extends Metric {
	public double distanceToIdentity(Permutation permutation) {
		int n = permutation.length();

		if (n <= 1) {
			return 0.0;
		}

		double size = n * (n - 1.0);

		long inversions = 0;
		for (int d : permutation.toInversions()) {
			inversions += d;
		}

		// return 2 * inversions / size;
		return inversions;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}
}
