import gen.DisagreementsGenerator;
import gen.FisherYatesShuffle;
import gen.LinerGenerator;
import gen.PermutationGenerator;

import java.security.AllPermission;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import misc.Painter;
import perm.CanberraDistance;
import perm.CayleyDistance;
import perm.KendallTau;
import perm.LSquare;
import perm.LevenshteinDistance;
import perm.Metric;
import perm.Permutation;
import rank.Aggregation;
import rank.BordaCount;
import rank.BruteForceSearch;
import rank.CopelandScore;
import rank.PickAPerm;
import rank.Stochastic;

public class Example1 {

	public static void main(String[] args) {

		int n = 3, m = 5;

		Metric mu = new KendallTau();

		List<Aggregation> aggregations = new ArrayList<Aggregation>();
		{

			aggregations.add(new BordaCount());
			aggregations.add(new PickAPerm(mu));
			aggregations.add(new CopelandScore());
			aggregations.add(new Stochastic());
		}

		aggregations.add(new BruteForceSearch(mu));
		int k = aggregations.size();

		Permutation[][] best = new Permutation[k][];
		double[] diff = new double[k];

		boolean smgo = true;

		for (Permutation a = new Permutation(m); smgo && a != null; a = a.next()) {
			for (Permutation b = a; smgo && b != null; b = b.next()) {
				for (Permutation c = b; smgo && c != null; c = c.next()) {
					Permutation[] data = { a, b, c };
					Permutation[] agr = new Permutation[k];
					double[] err = new double[k];

					for (int i = 0; i < k; i++) {
						agr[i] = aggregations.get(i).aggregate(data);
						err[i] += mu.distance(agr[i], a);
						err[i] += mu.distance(agr[i], b);
						err[i] += mu.distance(agr[i], c);
					}

					double min1 = Double.POSITIVE_INFINITY, min2 = min1;

					int color = 0;

					for (int i = 0; i < k; i++) {
						if (err[i] < min1) {
							min2 = min1;
							min1 = err[i];
							color = i;
							continue;
						}

						if (err[i] < min2) {
							min2 = err[i];
						}
					}

					if (min2 - min1 > diff[color]) {
						diff[color] = min2 - min1;
						best[color] = data;
						if (color == k - 1) {
							smgo = false;
						}
						System.out.println(color + " " + diff[color]);
					}

				}
			}
		}

		for (int i = 0; i < k; i++) {
			if (best[i] == null) {
				System.out.println("null");
			} else {
				for (Permutation p : best[i]) {
					System.out.println(inc(p));
				}
				for (int j = 0; j < k; j++) {
					double esum = 0;
					String err = "";
					Permutation p = aggregations.get(j).aggregate(best[i]);

					for (int u = 0; u < n; u++) {
						double d = mu.distance(p, best[i][u]);
						esum += d;
						err += d + " + ";
					}

					System.out.println(inc(p) + " " + err + " " + esum);
				}

			}

			System.out.println();
		}

	}

	static String inc(Permutation p) {
		String ans = p.toString();
		ans = ans.replace('[', '(');
		ans = ans.replace(']', ')');

		for (int i = p.length(); i >= 1; i--) {
			ans = ans.replace((char) ('0' + i - 1), (char) ('0' + i));
		}
		return ans;
	}
}
