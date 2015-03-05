package misc;

import perm.Permutation;

public interface FeatureMiner {
	public int length();

	public double[] mine(Permutation[] permutations);
}
