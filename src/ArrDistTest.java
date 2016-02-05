import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

import gen.DisagreementsGenerator;
import gen.FisherYatesShuffle;
import gen.LineSigmaGenerator;
import gen.PermutationGenerator;
import miner.ArrClassMiner;
import miner.ClassMiner;
import perm.CanberraDistance;
import perm.Disagreement;
import perm.Metric;
import rank.Aggregation;
import rank.AverageLoss;
import rank.BordaCount;
import rank.CopelandScore;
import rank.LossFunction;
import rank.MarkovChain;
import rank.PickAPerm;
import rank.Stochastic;

public class ArrDistTest {
	public static void main(String[] args) throws IOException {
		Random rng = new Random();

		PermutationGenerator pg = new FisherYatesShuffle(rng);

		Metric mu = new CanberraDistance();
		LossFunction lossFunction = new AverageLoss(mu);

		List<Aggregation> aggregations = new ArrayList<>();
		{
			 aggregations.add(new BordaCount(0.43));
			// aggregations.add(new CopelandScore());
			// aggregations.add(new PickAPerm(lossFunction));

			aggregations.add(new MarkovChain(0));
			aggregations.add(new MarkovChain(1));
			aggregations.add(new MarkovChain(2));
		}

		ArrClassMiner miner = new ArrClassMiner(1, aggregations, lossFunction);
		DisagreementsGenerator dg = new LineSigmaGenerator(pg, rng);

		int n = 17, m = 31;

		for (double k = 0; k < 1; k += 0.1) {
			int[] d = new int[4];

			for (int i = 0; i < 100; i++) {
				Disagreement disagreement = dg.generate(n, m);
				int c = miner.getClassIndex(disagreement, k / 10);
				++d[c];
			}

			System.out.println(Arrays.toString(d));

		}

	}
}
