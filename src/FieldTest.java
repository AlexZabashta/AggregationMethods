import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import perm.CayleyDistance;
import perm.KendallTau;
import perm.LevenshteinDistance;
import perm.Metric;
import perm.Permutation;

public class FieldTest {

	static List<Permutation> plus(List<Permutation> a, List<Permutation> b, int n, Metric mu) {
		double m = 2;

		List<Permutation> ans = new ArrayList<Permutation>();

		for (Permutation p = new Permutation(n); p != null; p = p.next()) {
			for (Permutation x : a) {
				for (Permutation y : b) {
					double d = mu.distance(p, x) + mu.distance(p, y);
					if (d < m) {
						m = d;
						ans.clear();
					}
					if (d == m) {
						ans.add(p);
					}
				}
			}
		}
		return ans;
	}

	static void print(Iterable<Permutation> list) {
		for (Permutation p : list) {
			System.out.println(p);
		}
		System.out.println();

	}

	public static void main(String[] args) {

		Metric mu = new CayleyDistance();

		Random rnd = new Random();
		int n = 5;
		List<Permutation> a = new ArrayList<>(), b = new ArrayList<>();

		a.add(Permutation.random(n, rnd));
		print(a);
		b.add(Permutation.random(n, rnd));
		print(b);

		print(plus(a, b, n, mu));

	}

}
