import perm.KendallTau;
import perm.LevenshteinDistance;
import perm.Metric;
import perm.Permutation;

public class Test {

	public static void main(String[] args) {
		int n = 3;
		Metric kd = new KendallTau();
		Metric ld = new LevenshteinDistance();
		Permutation p = new Permutation(n);
		for (Permutation q : Permutation.all(n)) {
			System.out.println(q);

			System.out.println(kd.distance(p, q));
			System.out.println(ld.distance(p, q));
		}

	}
}
