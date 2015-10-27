package rank;

import perm.Disagreement;
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

			@Override
			public String toString() {
				return "liner";
			}
		});

	}

	public BordaCount(DecreasingFunction weigher) {
		this.weigher = weigher;
	}

	@Override
	public Permutation aggregate(Disagreement disagreement) {
		int n = disagreement.permutationLength;

		if (disagreement.size == 0 || n < 2) {
			return new Permutation(n);
		}

		double[] w = new double[n];

		for (Permutation p : disagreement) {
			for (int i = 0; i < n; i++) {
				w[p.get(i)] += weigher.calculate(i);
			}
		}

		return aggregateByWeights(w);
	}

	@Override
	public String toString() {
		return "BordaCount(" + weigher.getClass().getSimpleName() + ")";
	}

}
