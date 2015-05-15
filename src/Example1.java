import gen.DataSetsGenerator;
import gen.FisherYatesShuffle;
import gen.LineGenerator;
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
import rank.CopelandScore;
import rank.PickAPerm;
import rank.Stochastic;

public class Example1 {

	public static void main(String[] args) {

		int n = 3, m = 5;

		// for (Permutation a = new Permutation(m); a != null; a = a.next()) {
		// for (Permutation b = a; b != null; b = b.next()) {
		// for (Permutation c = b; c != null; c = c.next()) {
		// Permutation[] data = { a, b, c };
		//
		// int[] sum = new int[m];
		//
		// for (int i = 0; i < m; i++) {
		// // sum[]
		// }
		//
		// }
		// }
		// }

		Permutation x = new Permutation(1, 0, 2, 3);
		Permutation y = new Permutation(0, 3, 2, 1);
		Permutation z = new Permutation(1, 0, 3, 2);

		Stochastic st = new Stochastic();

		Permutation[] data = { x, y, z };
		
		System.out.println(st.aggregate(data));

	}
}
