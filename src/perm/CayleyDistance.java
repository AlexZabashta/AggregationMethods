package perm;

public class CayleyDistance extends Metric  {
	public double distanceToIdentity(Permutation permutation) {
		int n = permutation.length();

		if (n <= 1) {
			return 0.0;
		}

		double size = n - 1.0;

		long inversions = 0;
		for (int[] loop : permutation.toCycles()) {
			inversions += loop.length - 1;
		}

		return inversions / size;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}
}
