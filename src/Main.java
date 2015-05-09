import gen.ClusterGenerator;
import gen.DataSetsGenerator;
import gen.GaussGenerator;
import gen.LineGenerator;
import gen.PermutationGenerator;

import java.io.IOException;

import perm.LevenshteinDistance;
import perm.Permutation;

public class Main {

	public static void main(String[] args) throws IOException {

		PermutationGenerator rpg = new GaussGenerator(1.0 / 10, 0.0);
		int n = 32;
		DataSetsGenerator dsg = new ClusterGenerator(new LevenshteinDistance(), rpg, n);

		for (int i = 0; i < n; i++) {
			System.out.println();
			for (Permutation p : dsg.generate(5, 30)) {
				System.out.println(p);
			}

		}

	}
}
