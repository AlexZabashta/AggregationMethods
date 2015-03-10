package perm;

public class Random {
	java.util.Random rng;

	public Random(long seed) {
		this(new java.util.Random(seed));
	}

	public Random() {
		this(new java.util.Random());
	}

	public Random(java.util.Random rng) {
		this.rng = rng;
	}

	public Permutation next(int length) {
		int[] permutation = new int[length];
		for (int i = 0; i < length; i++) {
			permutation[i] = i;
			int j = rng.nextInt(i + 1);
			Permutation.swap(permutation, i, j);
		}
		return new Permutation(permutation);
	}

	public Permutation nextGaussian(int length, double sigma) {
		int[] permutation = new int[length];
		for (int i = 0; i < length; i++) {
			permutation[i] = i;

			float scaledValue = (float) Math.abs(sigma * rng.nextGaussian());
			int j = Math.max(0, i - Math.round(scaledValue));
			Permutation.swap(permutation, i, j);
		}
		return new Permutation(permutation);
	}
}
