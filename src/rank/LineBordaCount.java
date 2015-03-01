package rank;

public class LineBordaCount extends Aggregation {

	double[] getWeigh(Vote[] votes) {
		int n = votes[0].permutation.length();
		double[] w = new double[n];

		for (Vote v : votes) {
			for (int i = 0; i < n; i++) {
				int j = v.permutation.get(i);
				w[j] += (double) (n - i) * v.numberOfVoters;
			}
		}

		return w;
	}

}
