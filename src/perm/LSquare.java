package perm;

public class LSquare extends Metric {

	public double distance(int i, int j) {
		return Math.abs(i - j);
	}

	@Override
	public double distanceToIdentity(Permutation permutation) {
		int n = permutation.length();

		if (n <= 1) {
			return 0.0;
		}

		double size = n * (((double) n * n) - 1);

		double d = 0;

		for (int i = 0; i < n; i++) {
			double s = distance(i, permutation.get(i));
			d += s * s;
		}

		return Math.sqrt(3 * d / size);
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}
}
