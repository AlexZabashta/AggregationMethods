package perm;

public class CayleyDistance extends RightInvariantMetric {
	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}

	public double distanceToIdentity(Permutation c) {
		int n = c.length();

		if (n <= 1) {
			return 0.0;
		}

		double size = n - 1.0;

		long inversions = 0;
		for (int[] loop : c.toCycles()) {
			inversions += loop.length - 1;
		}

		return inversions / size;
	}
}
