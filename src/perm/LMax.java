package perm;

public class LMax implements Metric {
	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}

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

		int d = 0;

		for (int i = 0; i < n; i++) {
			d = Math.max(d, Math.abs(a.get(i) - b.get(i)));
		}

		return d / (n - 1.0);
	}
}
