package rank;

import java.util.ArrayList;
import java.util.List;

import perm.Disagreement;
import perm.Permutation;

public class MetaBordaCount extends Aggregation {

	private final LossFunction lossFunction;
	private final List<Aggregation> aggregations = new ArrayList<>();

	public MetaBordaCount(LossFunction lossFunction) {
		this.lossFunction = lossFunction;
		aggregations.add(new BordaCount(0.43));
		aggregations.add(new BordaCount(new BordaCount.DecreasingFunction() {
			@Override
			public double calculate(int n) {
				return -Math.log(n + 1);
			}
		}));
		aggregations.add(new BordaCount(new BordaCount.DecreasingFunction() {
			@Override
			public double calculate(int n) {
				return 1.0 / (n + 50);
			}
		}));
	}

	@Override
	public Permutation aggregate(Disagreement disagreement) {
		int n = disagreement.permutationLength;

		if (disagreement.size == 0 || n < 2) {
			return new Permutation(n);
		}

		Permutation ans = null;
		double cur = 1;

		for (Aggregation a : aggregations) {
			Permutation p = a.aggregate(disagreement);
			double tmp = lossFunction.getLoss(p, disagreement);

			if (ans == null || tmp < cur) {
				cur = tmp;
				ans = p;
			}
		}

		return ans;

	}

	@Override
	public String toString() {
		return "MetaBordaCount";
	}
}
