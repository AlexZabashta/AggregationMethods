package perm;
public class LAbs implements Metric {

	public double distance(Permutation a, Permutation b) {

		if (a.length() != b.length()) {
			throw new IllegalArgumentException("Permutations has different size.");
		}
		a = a.invert();
		b = b.invert();

		int n = a.length();

		if (n <= 1) {
			return 0.0;
		}

		double size = ((double) n * n) - (n % 2);

		long d = 0;

		for (int i = 0; i < n; i++) {
			d += Math.abs(a.get(i) - b.get(i));
		}

		return 2 * d / size;
	}
}
