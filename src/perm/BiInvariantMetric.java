package perm;

public abstract class BiInvariantMetric implements Metric {

	public abstract double distanceToIdentity(Permutation c);

	public double distance(Permutation a, Permutation b) {
		return distanceToIdentity(a.product(b.invert()));
	}

}
