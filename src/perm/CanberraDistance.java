package perm;

public class CanberraDistance implements Metric {
	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}

	public double distance(int i, int j) {
		return Math.abs(i - j) / (i + j + 1.0);
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
		int offset = (n + 1) / 2;
		double d = 0, size = 0;

		for (int i = 0; i < n; i++) {
			d += distance(a.get(i), b.get(i));
			size += distance(i, (offset + i) % n);
		}

		return d / size;
	}
}
