import java.util.ArrayList;
import java.util.List;

import perm.CanberraDistance;
import perm.CayleyDistance;
import perm.KendallTau;
import perm.LAbs;
import perm.LSquare;
import perm.LevenshteinDistance;
import perm.Metric;
import perm.Permutation;
import rank.Aggregation;
import rank.BordaCount;
import rank.CopelandScore;
import rank.PickAPerm;
import rank.Stochastic;

public class Experiment3 {
	public static void main(String[] args) throws Exception {
		List<Metric> metrics = new ArrayList<Metric>();

		metrics.add(new CanberraDistance());
		metrics.add(new KendallTau());
		metrics.add(new LevenshteinDistance());
		metrics.add(new CayleyDistance());
		metrics.add(new LAbs());
		metrics.add(new LSquare());

		for (Metric metric : metrics) {
			System.out.println(metric.getClass().getSimpleName());

			List<Aggregation> aggregations = new ArrayList<Aggregation>();

			aggregations.add(new BordaCount());

			aggregations.add(new CopelandScore());

			aggregations.add(new PickAPerm(metric));
			aggregations.add(new Stochastic());

			int permutationLength = 25;
			int numberOfSets = 1 << 10;
			int permutationsInSet = 25;

			int n = aggregations.size();

			java.util.Random rng = new java.util.Random();
			perm.Random rpg = new perm.Random(rng);

			int[] color = new int[n];

			for (int i = 0; i < numberOfSets; i++) {
				Permutation[] p = new Permutation[permutationsInSet];

				if (rng.nextBoolean()) {
					double sigma = rng.nextDouble() * permutationLength;
					for (int j = 0; j < p.length; j++) {
						p[j] = rpg.nextGaussian(permutationLength, sigma);
					}
				} else {
					int numberOfSwaps = rng.nextInt(permutationLength);
					for (int j = 0; j < p.length; j++) {
						p[j] = rpg.nextGaussian(permutationLength, numberOfSwaps);
					}
				}

				double min = permutationsInSet * 2;
				int best = 0;

				for (int j = 0; j < n; j++) {
					double cur = 0;
					Permutation q = aggregations.get(j).aggregate(p);

					for (Permutation permutation : p) {
						cur += metric.distance(permutation, q);
					}

					if (cur < min) {
						min = cur;
						best = j;
					}
				}
				++color[best];
			}

			for (int i = 0; i < n; i++) {
				System.out.println("   " + aggregations.get(i).getClass().getSimpleName() + " " + color[i]);
			}
			System.out.println();
		}
	}
}
