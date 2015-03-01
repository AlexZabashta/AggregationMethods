package rank;

import java.util.Arrays;

import perm.Permutation;

public class Stochastic extends Aggregation {

	double[] getWeigh(Vote[] votes) {
		int n = votes[0].permutation.length();
		int m = votes.length;
		double[] w = new double[n];

		Permutation[] invper = new Permutation[m];
		for (int i = 0; i < m; i++) {
			invper[i] = votes[i].permutation.invert();
		}

		double[][] markovChain = new double[n][n];

		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				for (int k = 0; k < m; k++) {
					if (invper[k].get(i) < invper[k].get(j)) {
						markovChain[i][j] += votes[k].numberOfVoters;
					}
				}

			}
		}

		double q = 0.5;

		for (int i = 0; i < n; i++) {
			double sum = 0;
			for (int j = 0; j < n; j++) {
				sum += markovChain[i][j];
			}

			if (sum == 0.0) {
				Arrays.fill(markovChain[i], 1.0);
				sum = n - 1;
			}

			sum /= q;
			for (int j = 0; j < n; j++) {
				markovChain[i][j] /= sum;
			}
			markovChain[i][i] = q;

		}

		markovChain = pow(markovChain, n);

		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				w[j] -= markovChain[i][j];
			}
		}

		return w;
	}

	double[][] pow(double[][] a, int m) {
		int n = a.length;
		double[][] b = new double[n][n];

		for (int i = 0; i < n; i++) {
			b[i][i] = 1;
		}

		while (m > 0) {
			if (m % 2 == 1) {
				b = mul(b, a);
			}
			a = mul(a, a);
			m /= 2;
		}

		return b;
	}

	double[][] mul(double[][] a, double[][] b) {
		int n = a.length;
		double[][] c = new double[n][n];

		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				for (int k = 0; k < n; k++) {
					c[i][j] += a[i][k] * b[k][j];
				}
			}
		}

		return c;
	}
}
