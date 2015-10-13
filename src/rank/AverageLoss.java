package rank;

import perm.Disagreement;
import perm.Metric;
import perm.Permutation;

public class AverageLoss implements LossFunction {

	final Metric metric;

	public AverageLoss(Metric metric) {
		this.metric = metric;
	}

	public double getLoss(Permutation p, Disagreement d) {
		Permutation q = p.invert();

		if (d.size == 0) {
			return 0;
		}
		double sum = 0;

		for (Permutation x : d) {
			sum += metric.distanceToIdentity(x.product(q));
		}

		return sum / d.size;
	}

	@Override
	public String toString() {
		return "AverageLoss(" + metric + ")";
	}

}
