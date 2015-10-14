package gen;

import perm.Disagreement;

public interface DisagreementsGenerator {
	Disagreement generate(int permutationsInSet, int permutationLength);
}
