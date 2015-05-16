import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import misc.Painter;
import perm.CanberraDistance;
import perm.CayleyDistance;
import perm.KendallTau;
import perm.LSquare;
import perm.LevenshteinDistance;
import perm.Metric;
import rank.Aggregation;
import rank.BordaCount;
import rank.CopelandScore;
import rank.PickAPerm;
import rank.Stochastic;

public class Experiment5 {

	final static String res = "results" + File.separator + Experiment5.class.getSimpleName() + File.separator;

	public static void main(String[] args) throws Exception {
		java.util.Random rng = new java.util.Random();

		List<Metric> metrics = new ArrayList<Metric>();
		metrics.add(new CanberraDistance());
		metrics.add(new KendallTau());
		metrics.add(new LevenshteinDistance());
		metrics.add(new CayleyDistance());
		metrics.add(new LSquare());

		for (Metric metric : metrics) {
			String meticName = metric.getClass().getSimpleName();

			List<Aggregation> aggregations = new ArrayList<Aggregation>();
			aggregations.add(new BordaCount());
			aggregations.add(new PickAPerm(metric));
			aggregations.add(new CopelandScore());
			// aggregations.add(new Stochastic());

			Painter painter = new Painter(aggregations, metric);

			try (PrintWriter out = new PrintWriter(new File(res + meticName + ".txt"))) {

				out.print("         ");

				for (double scale = 0.1; scale <= 1.0; scale += 0.1) {
					out.printf("|    %3.1f   ", scale);
				}

				out.println();
				out.print("---------");

				for (double scale = 0.1; scale <= 1.0; scale += 0.1) {
					out.print("+----------");
				}
				out.println();

				for (int permutationsInSet = 5; permutationsInSet <= 25; permutationsInSet += 5) {
					for (int permutationLength = 10; permutationLength <= 100; permutationLength += 10) {
						out.printf("%2d x %3d ", permutationsInSet, permutationLength);
						for (double scale = 0.1; scale <= 1.0; scale += 0.1) {
							out.print("| ");

							
							
						}
						out.println();
					}
					System.out.println(permutationsInSet);
				}
			}

		}
	}
}
