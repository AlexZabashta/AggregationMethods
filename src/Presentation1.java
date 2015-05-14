import java.util.ArrayList;
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
import rank.CopelandScore;
import rank.PickAPerm;
import rank.Stochastic;

public class Presentation1 {

	public static void main(String[] args) {

		int n = 5, m = 13;

		Random rng = new Random();
		PermutationGenerator rpg = new FisherYatesShuffle(rng);

		Metric mu = new CanberraDistance();

		Permutation[] data = new Permutation[n];
		for (int i = 0; i < n; i++) {
			data[i] = rpg.generate(m, 0.5);
		}

		Permutation ans = new Permutation(m);
		double sum = n * 2;

		// for (Permutation p : Permutation.all(m)) {
		// double cur = 0;
		// for (Permutation q : data) {
		// cur += mu.distance(p, q);
		// }
		// if (cur < sum) {
		// sum = cur;
		// ans = p;
		// }
		// }

		List<Aggregation> aggregations = new ArrayList<Aggregation>();
		{

			aggregations.add(new BordaCount());
			// aggregations.add(new PickAPerm(mu));
			aggregations.add(new CopelandScore());
			aggregations.add(new Stochastic());
		}

		for (Permutation p : data) {
			System.out.println(p);
		}

		for (Aggregation aggregation : aggregations) {
			Permutation p = aggregation.aggregate(data);

			double cur = 0;
			for (Permutation q : data) {
				cur += mu.distance(p, q);
			}
			if (cur < sum) {
				sum = cur;
				ans = p;
			}

			System.out.printf("%s %f%n", p.toString(), cur);
		}

		System.out.println();
		System.out.println(ans);

	}
}
