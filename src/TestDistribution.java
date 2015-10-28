import gen.DisagreementsGenerator;
import gen.FisherYatesShuffle;
import gen.LineSigmaGenerator;
import gen.PermutationGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import perm.CanberraDistance;
import perm.Disagreement;

import miner.AttributeMiner;
import miner.ClassMiner;
import miner.HidenValuesMiner;
import rank.Aggregation;
import rank.AverageLoss;
import rank.BordaCount;
import rank.CopelandScore;
import rank.LossFunction;
import rank.PickAPerm;
import rank.Stochastic;

public class TestDistribution {
	public static void main(String[] args) {

		LossFunction lossFunction = new AverageLoss(new CanberraDistance());

		int n = 10, m = 20;
		Random random = new Random();

		PermutationGenerator pg = new FisherYatesShuffle(random);
		DisagreementsGenerator dg = new LineSigmaGenerator(pg, random);

		List<Aggregation> aggregations = new ArrayList<>();

		{
			// aggregations.add(new BordaCount(new
			// BordaCount.DecreasingFunction() {
			// @Override
			// public double calculate(int n) {
			// return -Math.sqrt(1 + n);
			// }
			// }));

			{
				Stochastic s = new Stochastic();
				s.p = 0.61;
				s.kpow = 3;
				aggregations.add(s);
			}
			{
				Stochastic s = new Stochastic();
				s.p = 0.61;
				s.kpow = 9;
				aggregations.add(s);
			}
			{
				Stochastic s = new Stochastic();
				s.p = 0.61;
				s.kpow = 27;
				aggregations.add(s);
			}
			

			// aggregations.add(new CopelandScore());
			// aggregations.add(new PickAPerm(lossFunction));
		}

		int k = aggregations.size();

		ClassMiner cminer = new ClassMiner(aggregations, lossFunction);

		int[] distribution = new int[k + 1];

		for (int i = 0; i < 2048; i++) {
			Disagreement d = dg.generate(n, m);
			int c = cminer.getClassIndex(d, 0.002);
			++distribution[c + 1];

		}

		System.out.println(Arrays.toString(distribution));

	}
}
