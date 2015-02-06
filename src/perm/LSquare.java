package perm;
public class LSquare implements Metric {

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

		double size = n * (((double) n * n) - 1);

		double d = 0;

		for (int i = 0; i < n; i++) {
			double s = a.get(i) - b.get(i);
			d += s * s;
		}

		return 3 * d / size;
	}
}
