package rank;

import java.util.Arrays;

import perm.Permutation;

public class Stochastic extends Aggregation {
	private double p;

	public Stochastic() {
		this(8.3);
	}

	public Stochastic(double p) {
		this.p = p;
	}

	public int testPow = 2;

	public Permutation aggregate(Permutation[] permutations) {
		int n = chekSizes(permutations);
		int m = permutations.length;

		if (n < 2) {
			return new Permutation(n);
		}

		Permutation[] invper = new Permutation[m];
		for (int i = 0; i < m; i++) {
			invper[i] = permutations[i].invert();
		}

		double[][] markovChain = new double[n][n];

		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				for (int k = 0; k < m; k++) {
					if (invper[k].get(i) < invper[k].get(j)) {
						++markovChain[i][j];
					}
				}

			}
		}

		for (int i = 0; i < n; i++) {
			double sum = 0;
			for (int j = 0; j < n; j++) {
				markovChain[i][j] += p * m;
				sum += markovChain[i][j];
			}

			for (int j = 0; j < n; j++) {
				markovChain[i][j] /= sum;
			}

		}

		markovChain = superPow(markovChain, testPow);
		// markovChain = pow(markovChain, 32);
		double[] weigh = new double[n];

		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				weigh[j] -= markovChain[i][j];
			}
		}

		return aggregateByW(weigh);
	}

	double[][] superPow(double[][] a, int m) {
		int n = a.length;

		double[][] b = new double[n][n], c;

		while (--m >= 0) {
			fillBySquare(b, a);
			c = b;
			b = a;
			a = c;
		}

		return a;

	}

	void fillBySquare(double[][] d, double[][] s) {
		int n = d.length;

		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				d[i][j] = 0.0;
				for (int k = 0; k < n; k++) {
					d[i][j] += s[i][k] * s[k][j];
				}
			}
		}

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
}
