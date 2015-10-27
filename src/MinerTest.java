import gen.DisagreementsGenerator;
import gen.FisherYatesShuffle;
import gen.LineSigmaGenerator;
import gen.PermutationGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import misc.ClassMiner;
import misc.SimpleMiner;

import perm.CanberraDistance;
import perm.CayleyDistance;
import perm.Disagreement;
import perm.KendallTau;
import perm.LAbs;
import perm.LMax;
import perm.LSquare;
import perm.LevenshteinDistance;
import perm.Metric;
import perm.Permutation;
import rank.Aggregation;
import rank.AverageLoss;
import rank.LossFunction;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public class MinerTest {

	public static void main(String[] args) {
		List<Metric> metrics = new ArrayList<>();

		metrics.add(new KendallTau());
		metrics.add(new CayleyDistance());
		metrics.add(new LevenshteinDistance());

		metrics.add(new LMax());
		metrics.add(new LAbs());
		metrics.add(new LSquare());

		metrics.add(new CanberraDistance());

		SimpleMiner miner = new SimpleMiner(metrics);

		int n = 10, m = 20;

		Random random = new Random(4815162342L);
		PermutationGenerator pg = new FisherYatesShuffle(random);
		DisagreementsGenerator dg = new LineSigmaGenerator(pg, random);

		Disagreement d = dg.generate(n, m);

		for (Permutation p : d) {
			System.out.println(p);
		}
		List<Aggregation> aggregations = new ArrayList<>();

		// aggregations.add(arg0)
		LossFunction lossFunction = new AverageLoss(new KendallTau());

		ClassMiner classMiner = new ClassMiner(aggregations, lossFunction);

		ArrayList<Attribute> attributes = new ArrayList<>();
		attributes.addAll(miner.getAttributes());

		Instance instance = new DenseInstance(attributes.size());
		Instances instances = new Instances("testset", attributes, 123);

		instance.setDataset(instances);
		instances.add(instance);

		// classMiner.g

		System.out.println();
		miner.mine(instance, d);
		System.out.println(instance);

	}
}
