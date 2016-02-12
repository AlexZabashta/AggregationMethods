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
import rank.LocalKemenization;
import rank.LossFunction;
import rank.PickAPerm;
import rank.Stochastic;
import rank.MarkovChain;
import rank.MetaBordaCount;
import rank.MetaMarkovChain;

public class TestDistribution {
	public static void main(String[] args) {

		LossFunction lossFunction = new AverageLoss(new CanberraDistance());

		int n = 7, m = 17;
		Random random = new Random();

		PermutationGenerator pg = new FisherYatesShuffle(random);
		DisagreementsGenerator dg = new LineSigmaGenerator(pg, random);

		List<Aggregation> aggregations = new ArrayList<>();

		{

			// aggregations.add(new BordaCount(0.43));
			// aggregations.add(new BordaCount(new
			// BordaCount.DecreasingFunction() {
			// @Override
			// public double calculate(int n) {
			// return -Math.log(n + 1);
			// }
			// }));
			// aggregations.add(new LocalKemenization());
			// aggregations.add(new PickAPerm(lossFunction));
			// aggregations.add(new MarkovChain(0));
			// aggregations.add(new MarkovChain(1));
			// aggregations.add(new MarkovChain(2));

			aggregations.add(new LocalKemenization());
			aggregations.add(new PickAPerm(lossFunction));
			aggregations.add(new MetaBordaCount(lossFunction));
			aggregations.add(new MetaMarkovChain(lossFunction));

		}

		int k = aggregations.size();

		ClassMiner cminer = new ClassMiner(aggregations, lossFunction);

		int[] distribution = new int[k + 1];

		for (int i = 0; i < 2048; i++) {
			Disagreement d = dg.generate(n, m);
			int c = cminer.getClassIndex(d);
			++distribution[c + 1];

		}

		System.out.println(Arrays.toString(distribution));

	}
}
