package rank;

import perm.Disagreement;
import perm.Permutation;

public interface LossFunction {
	double getLoss(Permutation p, Disagreement d);
}
