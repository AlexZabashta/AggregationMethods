import java.io.IOException;
import java.util.Random;

import misc.IOUtils;
import perm.CanberraDistance;
import perm.Metric;
import perm.Permutation;

public class Test {

	public static void main(String[] args) throws IOException, ClassNotFoundException {

		Random rng = new Random();
		int n = 13, m = 17;

		Permutation[] p = new Permutation[n];

		for (int i = 0; i < n; i++) {
			p[i] = Permutation.random(m, rng);
		}

		String file = "sertest.obj";

		// IOUtils.writeObjectToFile(file, p);

		Permutation[] q = (Permutation[]) IOUtils.readObjectFromFile(file);

		Metric metric = new CanberraDistance();

		for (int i = 0; i < n; i++) {
			System.out.println(metric.distance(q[i], q[(i + 1) % n]));
		}

	}
}
