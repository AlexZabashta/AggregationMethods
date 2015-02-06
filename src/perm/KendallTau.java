package perm;
public class KendallTau implements Metric {

	public double distance(Permutation a, Permutation b) {
		Permutation c = a.product(b.invert());
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
