package rank;

import perm.Disagreement;
import perm.Permutation;

public class PickAPerm extends Aggregation {

	private final LossFunction lossFunction;

	public PickAPerm(LossFunction lossFunction) {
		this.lossFunction = lossFunction;
	}

	@Override
	public Permutation aggregate(Disagreement disagreement) {
		int n = disagreement.permutationLength;

		if (disagreement.size == 0 || n < 2) {
			return new Permutation(n);
		}

		Permutation ans = null;
		double cur = 1;

		for (Permutation p : disagreement) {
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
		return "PickAPerm (" + lossFunction + ")";
	}
}
