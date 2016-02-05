package rank;

import java.util.Arrays;
import java.util.Locale;

import perm.Disagreement;
import perm.Permutation;

public class MarkovChain extends Aggregation {
	public final double alpha;
	public final int type;

	public MarkovChain() {
		this(0);
	}

	public MarkovChain(int type) {
		this(type, 0.05);
	}

	public MarkovChain(double alpha) {
		this(0, alpha);
	}

	public MarkovChain(int type, double alpha) {
		if (type < 0 || 2 < type) {
			throw new IllegalArgumentException("type = " + type + " not in in range [0; 2]");
		}
		this.alpha = alpha;
		this.type = type;
	}

	@Override
	public Permutation aggregate(Disagreement disagreement) {
		int n = disagreement.permutationLength;
		int m = disagreement.size;

		if (disagreement.size == 0 || n < 2) {
			return new Permutation(n);
		}

		double subtrahend = (type < 2) ? (1.0 / n) : (1.0 / (m * n));
		double medium = m / 2.0;

		Permutation[] invper = new Permutation[m];
		for (int i = 0; i < m; i++) {
			invper[i] = disagreement.get(i).invert();
		}

		double[][] markovChain = new double[n][n];

		for (int i = 0; i < n; i++) {
			double sum = 0;
			for (int j = 0; j < n; j++) {

				double c = 0;

				for (int k = 0; k < m; k++) {
					if (invper[k].get(i) > invper[k].get(j)) {
						c += 1;
					}
				}

				if (type == 0 && c > 0) { // MC1
					markovChain[i][j] = subtrahend;
				}

				if (type == 1 && c >= medium) { // MC2
					markovChain[i][j] = subtrahend;
				}

				if (type == 2) {
					markovChain[i][j] = c * subtrahend; // MC3
				}

				sum += markovChain[i][j];
			}
			markovChain[i][i] = 1 - sum;
		}

		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				markovChain[i][j] = (1 - alpha) * markovChain[i][j] + alpha / n;
			}
		}

		// print(markovChain);

		double[][] a = new double[n][n];
		double[] b = new double[n];

		for (int i = 0; i < n; i++) {
			if (i == 0) {
				for (int j = 0; j < n; j++) {
					a[i][j] = 1;
				}
				b[i] = 1;
			} else {
				for (int j = 0; j < n; j++) {
					if (i == j) {
						a[i][j] = markovChain[j][i] - 1;
					} else {
						a[i][j] = markovChain[j][i];
					}
				}

			}
		}

		solve(n, a, b);

		for (double p : b) {
			// System.out.println(p);
		}

		return aggregateByWeights(b);
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

	void solve(int s, double[][] f, double[] g) {
		for (int r = 0; r < s; r++) {
			double val = 0;
			{
				int best = r;

				for (int i = r; i < s; i++) {
					double absVal = Math.abs(f[i][r]);

					if (absVal > val) {
						val = absVal;
						best = i;
					}
				}
				{
					double[] temp = f[best];
					f[best] = f[r];
					f[r] = temp;
				}
				{
					double temp = g[best];
					g[best] = g[r];
					g[r] = temp;
				}
			}

			val = f[r][r];

			for (int i = r + 1; i < s; i++) {
				double k = f[i][r] / val;
				g[i] -= k * g[r];

				for (int j = 0; j < s; j++) {
					f[i][j] -= k * f[r][j];
				}
			}

		}

		for (int r = s - 1; r >= 0; r--) {
			double val = f[r][r];
			for (int i = r - 1; i >= 0; i--) {
				double k = f[i][r] / val;
				g[i] -= k * g[r];
				for (int j = 0; j < s; j++) {
					f[i][j] -= k * f[r][j];
				}
			}
		}

		for (int i = 0; i < s; i++) {
			g[i] /= f[i][i];
		}
	}

	@Override
	public String toString() {
		return "StochasticSystem";
	}
}
