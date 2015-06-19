import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import gen.FisherYatesShuffle;
import gen.PermutationGenerator;
import perm.CanberraDistance;
import perm.KendallTau;
import perm.LevenshteinDistance;
import perm.Metric;
import perm.Permutation;
import rank.Aggregation;
import rank.BordaCount;
import rank.BruteForceSearch;
import rank.CopelandScore;
import rank.PickAPerm;
import rank.Stochastic;

public class Presentation2 {

	public static void main(String[] args) {

		// Permutation[] p = { new Permutation(12, 2, 5, 1, 4, 3, 6, 7, 9, 8,
		// 10, 11, 0), new Permutation(0, 11, 2, 12, 4, 5, 1, 7, 8, 10, 9, 6,
		// 3), new Permutation(0, 1, 3, 7, 10, 4, 6, 11, 8, 9, 5, 2, 12), new
		// Permutation(0, 4, 10, 2, 1, 5, 6, 3, 8, 12, 7, 11, 9), new
		// Permutation(4, 11, 2, 3, 0, 5, 6, 10, 9, 7, 8, 1, 12) };

		int n = 9;
		Random rnd = new Random();

		Metric mu = new CanberraDistance();
		Aggregation bfs = new BruteForceSearch(mu);

		double min = 3;

		while (true) {
			Permutation a = Permutation.random(n, rnd);
			Permutation b = Permutation.random(n, rnd);
			Permutation c = Permutation.random(n, rnd);

			boolean nc = false;
			for (int i = 0; i < n; i++) {
				if (a.get(i) == b.get(i) || b.get(i) == c.get(i) || c.get(i) == a.get(i)) {
					nc = true;
					break;
				}
			}

			if (nc) {
				continue;
			}

			Permutation p = bfs.aggregate(a, b, c);

			double cur = 0;
			cur += mu.distance(a, p);
			cur += mu.distance(b, p);
			cur += mu.distance(c, p);

			if (cur < min) {
				min = cur;

				System.out.println(a);
				System.out.println(b);
				System.out.println(c);
				System.out.println(p);
				System.out.println();
			}
		}

	}
}
