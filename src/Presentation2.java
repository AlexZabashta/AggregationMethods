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

		Metric metric = new LevenshteinDistance();

		// Aggregation aggregation = new CopelandScore();

		// System.out.println(aggregation.aggregate(p));

		perm.Random rpg = new perm.Random();

		Permutation c = new Permutation(3, 1, 2, 4, 0);

		Permutation a = new Permutation(2, 4, 0, 1, 3).product(c);
		Permutation b = c;

		// System.out.println(c);
		System.out.println(a);
		System.out.println(b);
		System.out.println();

		Permutation t = a.product(b.invert());

		System.out.println(t);
		System.out.println(metric.distance(a, b));
		System.out.println(Arrays.toString(t.toInversions()));

	}
}
