package perm;

public abstract class Metric {

	public double distance(Permutation a, Permutation b) {
		return distanceToIdentity(a.product(b.invert()));
	}

	public abstract double distanceToIdentity(Permutation permutation);

}