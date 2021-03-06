package perm;

public class LMax extends Metric {
	public double distance(int i, int j) {
		return Math.abs(i - j);
	}

	@Override
	public double distanceToIdentity(Permutation permutation) {
		int n = permutation.length();

		if (n <= 1) {
			return 0.0;
		}

		double size = n - 1.0;

		double d = 0;

		for (int i = 0; i < n; i++) {
			d = Math.max(d, distance(i, permutation.get(i)));
		}

		return d / size;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}

}
