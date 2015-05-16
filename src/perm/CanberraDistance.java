package perm;

public class CanberraDistance extends Metric {
	public double distance(int i, int j) {
		return Math.abs(i - j) / (i + j + 1.0);
	}

	@Override
	public double distanceToIdentity(Permutation permutation) {

		int n = permutation.length();

		if (n <= 1) {
			return 0.0;
		}
		int offset = (n + 1) / 2;
		double d = 0, size = 0;

		for (int i = 0; i < n; i++) {
			d += distance(i, permutation.get(i));
			size += distance(i, (offset + i) % n);
		}

		return d / size;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}
}
