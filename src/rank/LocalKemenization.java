package rank;

import java.util.Arrays;

import perm.Disagreement;
import perm.Permutation;

public class LocalKemenization extends Aggregation {

	public boolean cmp(int i, int j, Permutation[] invper) {
		int d = 0;

		for (Permutation p : invper) {
			if (p.get(i) < p.get(j)) {
				++d;
			}
			if (p.get(i) > p.get(j)) {
				--d;
			}
		}

		return d >= 0;
	}

	void merge(int l, int r, int[] p, Permutation[] invper) {
		if (r <= l) {
			return;
		}

		int len = r - l + 1;
		int m = (r + l) / 2;

		merge(l, m, p, invper);
		merge(m + 1, r, p, invper);

		int[] q = new int[len];

		for (int i = 0, lp = l, rp = m + 1; i < len; i++) {
			if ((r < rp) || ((lp <= m) && (cmp(p[lp], p[rp], invper)))) {
				q[i] = p[lp++];
			} else {
				q[i] = p[rp++];
			}
		}

		for (int i = 0; i < len; i++) {
			p[i + l] = q[i];
		}

	}

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

		int[] p = new int[n];

		for (int i = 0; i < n; i++) {
			p[i] = i;
		}

		merge(0, n - 1, p, invper);

		return new Permutation(p);

	}

	@Override
	public String toString() {
		return "LocalKemenization";
	}
}
