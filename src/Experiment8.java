import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import misc.ClusterGenerator;
import misc.FirstMinGenerator;
import misc.GaussGenerator;
import misc.Painter;
import misc.PermutationSetsGenerator;
import misc.SeveralSwapsGenerator;
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

public class Experiment8 {

	final static String res = "results" + File.separator + Experiment8.class.getSimpleName() + File.separator;

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
			aggregations.add(new Stochastic());

			Painter painter = new Painter(aggregations, metric);

			try (PrintWriter out = new PrintWriter(new File(res + meticName + ".txt"))) {

				for (Metric cm : metrics) {
					out.println(cm.getClass().getSimpleName());
					System.out.println(cm.getClass().getSimpleName());
					for (int bufferSize = 10; bufferSize <= 500; bufferSize += 37) {
						out.print("    " + bufferSize + ":");

						PermutationSetsGenerator psg = new FirstMinGenerator(10, 25, cm, bufferSize);

						int[] d = painter.getColorDistribution(psg, 100);

						for (int val : d) {
							out.printf(" %2d", val);
						}
						out.println();
					}
				}
			}
		}
	}
}
