import gen.DisagreementsGenerator;
import gen.FisherYatesShuffle;
import gen.LineSigmaGenerator;
import gen.PermutationGenerator;

import static misc.MetricsCollection.getMetrics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import perm.CanberraDistance;
import perm.Disagreement;
import perm.Metric;
import miner.AttributeMiner;
import miner.ClassMiner;
import miner.DimensionalMiner;
import miner.FastMiner;
import miner.HidenValuesMiner;
import rank.Aggregation;
import rank.AverageLoss;
import rank.BordaCount;
import rank.CopelandScore;
import rank.LocalKemenization;
import rank.LossFunction;
import rank.PickAPerm;
import rank.Stochastic;
import weka.core.Attribute;
import weka.core.Instances;
import rank.MarkovChain;
import rank.MetaBordaCount;
import rank.MetaMarkovChain;

public class FindOptDistribution {
	public static void main(String[] args) {

		Random random = new Random();
		int maxMin = 0;
		int k = 1000;

		List<Metric> metrics = getMetrics();

		LossFunction lossFunction = new AverageLoss(metrics.get(0));

		List<Aggregation> aggregations = new ArrayList<>();
		{
			aggregations.add(new BordaCount(0.43));
//			aggregations.add(new BordaCount(new BordaCount.DecreasingFunction() {
//				@Override
//				public double calculate(int n) {
//					return -Math.log(n + 1);
//				}
//			}));
//			aggregations.add(new BordaCount(new BordaCount.DecreasingFunction() {
//				@Override
//				public double calculate(int n) {
//					return 1.0 / (n + 50);
//				}
//			}));
//			aggregations.add(new LocalKemenization());
//			aggregations.add(new CopelandScore());
//			aggregations.add(new PickAPerm(lossFunction));
//			aggregations.add(new MarkovChain(0));
//			aggregations.add(new MarkovChain(1));
			aggregations.add(new MarkovChain(2));
//			
			
			aggregations.add(new CopelandScore());
//			aggregations.add(new LocalKemenization());
			aggregations.add(new PickAPerm(lossFunction));
		}

		ClassMiner cminer = new ClassMiner(aggregations, lossFunction);

		while (true) {

			double sigma = random.nextDouble() * 0.8 + 0.1;
			PermutationGenerator pg = new FisherYatesShuffle(sigma, 0.01, random);
			DisagreementsGenerator dg = new LineSigmaGenerator(pg, random);

			int[] distr = new int[aggregations.size()];
			int n = random.nextInt(33) + 7;
			int m = random.nextInt(73) + 17;
			for (int i = 0; i < k; i++) {
				int permutationsInSet = n;
				int permutationLength = m;

				Disagreement d = dg.generate(permutationsInSet, permutationLength);
				int c = cminer.getClassIndex(d);
				++distr[c];
			}

			int curMin = k;

			for (int v : distr) {
				curMin = Math.min(curMin, v);
			}

			if (curMin > maxMin) {
				maxMin = curMin;
				System.out.println(sigma + " " + n + " " + m + " " + Arrays.toString(distr));
			}
		}
	}
}
