package perm;
public class CayleyDistance implements Metric {

	public double distance(Permutation a, Permutation b) {
		Permutation c = a.product(b.invert());
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
