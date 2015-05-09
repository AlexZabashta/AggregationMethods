package gen;

import perm.Permutation;

public interface DataSetsGenerator {
	Permutation[] generate(int permutationsInSet, int permutationLength);
}
