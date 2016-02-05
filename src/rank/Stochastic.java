package rank;

import java.util.Locale;

import perm.Disagreement;
import perm.Permutation;

@Deprecated
public class Stochastic extends Aggregation {
	public int kpow = 32;

	public double p = 0.001;

	@Override
	public Permutation aggregate(Disagreement disagreement) {
		int n = disagreement.permutationLength;
		int m = disagreement.size;

		if (disagreement.size == 0 || n < 2) {
			return new Permutation(n);
		}

		Permutation[] invper = new Permutation[m];
		for (int i = 0; i < m; i++) {
			invper[i] = disagreement.get(i).invert();
		}

		double[][] markovChain = new double[n][n];

		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				for (int k = 0; k < m; k++) {
					if (invper[k].get(i) >= invper[k].get(j)) {
						++markovChain[i][j];
					}
				}
			}
		}

		for (int i = 0; i < n; i++) {
			double cur = 0, sum = (1 + p) * n * m;
			for (int j = 0; j < n; j++) {
				if (i != j) {
					markovChain[i][j] += p * m;
					markovChain[i][j] /= sum;
					cur += markovChain[i][j];
				}
			}
			markovChain[i][i] = 1 - cur;
		}

		print(markovChain);

		markovChain = superPow(markovChain, kpow);
		double[] weigh = new double[n];

		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				weigh[j] += markovChain[i][j];
			}
		}

		return aggregateByWeights(weigh);
	}

	void print(double[][] f) {
		for (double[] r : f) {
			double s = 0;
			for (double v : r) {
				s += v;
				System.out.printf(Locale.ENGLISH, "% .2f ", v);
			}
			System.out.printf(Locale.ENGLISH, "       % .3f%n", s);
		}
		System.out.println();
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

	@Override
	public String toString() {
		return "Stochastic";
	}
}
