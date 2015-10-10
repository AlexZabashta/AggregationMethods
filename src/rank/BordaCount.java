package rank;

import perm.Permutation;

public class BordaCount extends Aggregation {
	public interface DecreasingFunction {
		public double calculate(int n);
	}

	final DecreasingFunction weigher;

	public BordaCount() {
		this(new DecreasingFunction() {
			@Override
			public double calculate(int n) {
				return -n;
			}
		});
	}

	@Override
	public String toString() {
		return "BordaCount";
	}

	public BordaCount(DecreasingFunction weigher) {
		this.weigher = weigher;
	}

	@Override
	public Permutation aggregate(Permutation... permutations) {
		int n = chekSizes(permutations);
		int m = permutations.length;

		if (n < 2) {
			return new Permutation(n);
		}
		double[] w = new double[n];

		for (Permutation p : permutations) {
			for (int i = 0; i < n; i++) {
				w[p.get(i)] += weigher.calculate(i);
			}
		}

		return aggregateByW(w);
	}

}
