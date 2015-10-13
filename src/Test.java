import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import misc.IOUtils;
import perm.CanberraDistance;
import perm.Metric;
import perm.Permutation;
import perm.Disagreement;
import rank.Aggregation;

public class Test {

	public static void main(String[] args) throws IOException, ClassNotFoundException {

		Random rng = new Random();
		int n = 4, m = 7;

		Permutation[] p = new Permutation[n];

		for (int i = 0; i < n; i++) {
			p[i] = Permutation.random(m, rng);
		}

		System.out.println(Aggregation.aggregateByWeights(new double[] { 1, 2, 3, 4 }));

	}
}
