import gen.*;
import rank.*;
import perm.*;
import miner.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import weka.core.*;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;
import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.ASSearch;
import weka.attributeSelection.CfsSubsetEval;
import weka.attributeSelection.GreedyStepwise;
import weka.classifiers.*;
import static misc.MetricsCollection.getMetrics;
import static misc.ClassifierCollection.getClassifies;

public class DataSetGenerator {
	public static void main(String[] args) throws IOException {

		int n = 20, k = 30;
		Random random = new Random();

		List<Metric> metrics = getMetrics();

		LossFunction lossFunction = new AverageLoss(metrics.get(0));

		List<Aggregation> aggregations = new ArrayList<>();
		{
			aggregations.add(new BordaCount(0.43));
			// aggregations.add(new BordaCount(new
			// BordaCount.DecreasingFunction() {
			// @Override
			// public double calculate(int n) {
			// return -Math.log(n + 1);
			// }
			// }));
			// aggregations.add(new BordaCount(new
			// BordaCount.DecreasingFunction() {
			// @Override
			// public double calculate(int n) {
			// return 1.0 / (n + 50);
			// }
			// }));

			aggregations.add(new MarkovChain(0));
			aggregations.add(new MarkovChain(1));
			// aggregations.add(new MarkovChain(2));
			//

			aggregations.add(new CopelandScore());
			aggregations.add(new LocalKemenization());
			aggregations.add(new PickAPerm(lossFunction));
		}

		int s = aggregations.size();

		// AttributeMiner dminer = new DimensionalMiner();
		AttributeMiner hminer = new HidenValuesMiner(6);
		AttributeMiner fminer = new FastMiner(metrics);
		AttributeMiner lminer = new LMiner();

		AttributeMiner sminer = new SimpleMiner(metrics);

		ClassMiner cminer = new ClassMiner(aggregations, lossFunction);

		List<List<AttributeMiner>> minersList = new ArrayList<List<AttributeMiner>>();
		{
			List<AttributeMiner> miners = new ArrayList<AttributeMiner>();
			{
				miners.add(lminer);
			}
			minersList.add(miners);
		}
		{
			List<AttributeMiner> miners = new ArrayList<AttributeMiner>();
			{
				miners.add(hminer);
			}
			minersList.add(miners);
		}
		{
			List<AttributeMiner> miners = new ArrayList<AttributeMiner>();
			{
				miners.add(sminer);
			}
			minersList.add(miners);
		}
		{
			List<AttributeMiner> miners = new ArrayList<AttributeMiner>();
			{
				miners.add(fminer);
			}
			minersList.add(miners);
		}

		int[] permLen = { 80, 80, 50 };

		PermutationGenerator[] generators = { new FisherYatesShuffle(0.87, 0.01, random), new GaussGenerator(0.87, 0.01, random), new SeveralSwapsGenerator(0.87, 0.01, random) };

		for (int gid = 0; gid < generators.length; gid++) {
			PermutationGenerator pg = generators[gid];

			DisagreementsGenerator dg = new LineSigmaGenerator(pg, random);

			Set<Disagreement>[] data = new Set[s];
			for (int i = 0; i < s; i++) {
				data[i] = new HashSet<Disagreement>();
			}

			int lastMin = 0;

			while (true) {
				int permutationsInSet = n;
				int permutationLength = permLen[gid];

				// if (System.currentTimeMillis() % 300 == 0) {
				// for (Set<Disagreement> clazz : data) {
				// System.out.print(clazz.size() + " ");
				// }
				// System.out.println();
				// }

				Disagreement d = dg.generate(permutationsInSet, permutationLength);
				int c = cminer.getClassIndex(d);

				if (data[c].size() == k) {
					continue;
				}

				data[c].add(d);

				int minSize = k;

				for (Set<Disagreement> clazz : data) {
					minSize = Math.min(minSize, clazz.size());
				}

				if (minSize == k) {
					break;
				}

				if (minSize != lastMin) {
					lastMin = minSize;

					if (minSize % 10 == 0) {
						System.out.println(minSize);
					}
				}
			}

			for (List<AttributeMiner> miners : minersList) {
				String name = pg.getClass().getSimpleName();
				for (AttributeMiner miner : miners) {
					name += " " + miner.getClass().getSimpleName();
				}
				System.out.println(name);

				ArrayList<Attribute> attributes = new ArrayList<>();
				attributes.add(cminer.getClassAttributes());
				for (AttributeMiner miner : miners) {
					attributes.addAll(miner.getAttributes());
				}

				Instances instances = new Instances("testset", attributes, k);
				instances.setClass(cminer.getClassAttributes());

				for (int c = 0; c < s; c++) {
					for (Disagreement d : data[c]) {
						Instance instance = new DenseInstance(attributes.size());
						instance.setDataset(instances);

						for (AttributeMiner miner : miners) {
							miner.mine(instance, d);
						}
						instance.setClassValue(cminer.getClassName(c));

						instances.add(instance);
					}
				}

				try (PrintWriter out = new PrintWriter(new File("data/gen/" + name + ".arff"))) {
					out.println(instances);
				}
			}
		}
	}
}
