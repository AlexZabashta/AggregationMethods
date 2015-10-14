import gen.ClusterGenerator;
import gen.DisagreementsGenerator;
import gen.FisherYatesShuffle;
import gen.GaussGenerator;
import gen.HyperSigmaGenerator;
import gen.LinerGenerator;
import gen.LineSigmaGenerator;
import gen.PermutationGenerator;
import gen.SameSigmaGenerator;
import gen.SeveralSwapsGenerator;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import misc.ClassifierCollection;
import misc.FeatureMiner;
import misc.Painter;
import misc.SimpleMiner;
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
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

public class FindOptDistr {

	final static String res = "results" + File.separator + FindOptDistr.class.getSimpleName() + File.separator;

	public static void main(String[] args) throws Exception {

		int rep = 128;
		Metric mu = new CanberraDistance();

		Random rng = new Random();

		int curMaxPermInSet = 20;
		int curMaxPermLength = 50;
		double curMaxSigma = 1.0;

		int curMinPermInSet = 20;
		int curMinPermLength = 50;
		double curMinSigma = 1.0;

		double curMax = 0, curMin = rep;

		List<Aggregation> aggregations = new ArrayList<Aggregation>();
		{
			aggregations.add(new BordaCount());
			aggregations.add(new PickAPerm(mu));
			aggregations.add(new CopelandScore());
			aggregations.add(new Stochastic());
		}

		int m = aggregations.size();

		Painter painter = new Painter(aggregations, mu);

		while (true) {

			int tmpPermInSet = rng.nextInt(25) + 5;
			int tmpPermLength = rng.nextInt(75) + 15;
			double tmpSigma = 0.99;
			// double tmpSigma = rng.nextDouble() * 0.2 + 0.1;
			double tmpMax = rep, tmpMin = 0, sum = 0;

			PermutationGenerator permGen = new FisherYatesShuffle(tmpSigma, 0.01, rng);
			LineSigmaGenerator sigmGen = new LineSigmaGenerator(permGen, rng);

			int[] distr = new int[m];

			for (int r = 0; r < rep; r++) {
				Permutation[] p = sigmGen.generate(tmpPermInSet, tmpPermLength);
				int c = painter.getColor(p, 0.0023);
				if (c != -1) {
					++distr[c];
					++sum;
				}
			}

			for (int val : distr) {
				tmpMax = Math.min(tmpMax, val / sum);
				tmpMin = Math.max(tmpMin, val / sum);
			}

			if (curMax < tmpMax) {

				curMax = tmpMax;
				curMaxPermInSet = tmpPermInSet;
				curMaxPermLength = tmpPermLength;
				curMaxSigma = tmpSigma;

				System.out.print("maxmin ");
				System.out.printf("%.3f in %16s", curMax, Arrays.toString(distr));
				System.out.printf(" at %2dx%2d", curMaxPermInSet, curMaxPermLength);
				// System.out.println(" " + curMaxSigma);
				System.out.println();
			}
			if (curMin > tmpMin) {

				curMin = tmpMin;
				curMinPermInSet = tmpPermInSet;
				curMinPermLength = tmpPermLength;
				curMinSigma = tmpSigma;

				System.out.print("minmax ");
				System.out.printf("%.3f in %16s", curMin, Arrays.toString(distr));
				System.out.printf(" at %2dx%2d", curMinPermInSet, curMinPermLength);
				// System.out.println(" " + curMinSigma);
				System.out.println();
			}

		}

	}
}
