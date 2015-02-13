package aggr;

import perm.Permutation;

public class Vote {
	final Permutation permutation;
	final int numberOfVoters;

	public Vote(Permutation permutation, int numberOfVoters) {
		this.permutation = permutation;
		this.numberOfVoters = numberOfVoters;
	}

	@Override
	public String toString() {
		return permutation + " (" + numberOfVoters + ")";
	}

}
