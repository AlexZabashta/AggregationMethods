package rank;

import perm.Disagreement;
import perm.Metric;
import perm.Permutation;

public class OneMax implements LossFunction {

	final Metric metric;

	public OneMax(Metric metric) {
		this.metric = metric;
	}

	public double getLoss(Permutation p, Disagreement d) {
		Permutation q = p.invert();

		if (d.size == 0) {
			return 0;
		}
		double max = 0;

		for (Permutation x : d) {
			double dist = metric.distanceToIdentity(x.product(q));
			max = Math.max(max, dist);
		}

		return max;
	}

	@Override
	public String toString() {
		return "OneMax(" + metric + ")";
	}

}
