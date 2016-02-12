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

public class AllMinersExperement {

	protected static Instances useFilter(Instances data) {
		AttributeSelection filter = new AttributeSelection();
		ASEvaluation eval = new CfsSubsetEval();

		GreedyStepwise search = new GreedyStepwise();
		search.setSearchBackwards(true);

		filter.setEvaluator(eval);
		filter.setSearch(search);

		try {
			filter.setInputFormat(data);
			return Filter.useFilter(data, filter);
		} catch (Exception e) {
			return data;
		}
	}

	public static void main(String[] args) throws IOException {

		int n = 20, k = 1000;
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

				for (int as = 0; as < 2; as++) {
					if (as == 1) {
						instances = useFilter(instances);
						try (PrintWriter out = new PrintWriter(new File("results/fsel/" + name + "a.txt"))) {
							int na = instances.numAttributes();
							for (int aid = 0; aid < na; aid++) {
								out.println(instances.attribute(aid).name());
							}
						}
					}

					try (PrintWriter out = new PrintWriter(new File("results/fsel/" + name + as + ".txt"))) {
						for (Classifier classifier : getClassifies()) {
							String curName = classifier.getClass().getSimpleName();
							out.println(curName);
							System.out.println("     " + curName);
							try {

								Evaluation evaluation = new Evaluation(instances);
								evaluation.crossValidateModel(classifier, instances, 10, random);
								double[][] cm = evaluation.confusionMatrix();
								for (double[] da : cm) {
									for (double val : da) {
										out.printf("%5.0f ", val);
									}
									out.println();
								}

								double arec = 0, apre = 0;
								for (int i = 0; i < s; i++) {
									double srec = 0, spre = 0;

									for (int j = 0; j < s; j++) {
										srec += cm[i][j];
										spre += cm[j][i];
									}

									if (cm[i][i] == 0.0) {
										srec += 1;
										spre += 1;
									}
									double rec = cm[i][i] / srec;
									double pre = cm[i][i] / spre;

									arec += rec / s;
									apre += pre / s;
								}

								double f = 2 * arec * apre / (arec + apre);

								out.printf(Locale.ENGLISH, "%.3f%n", f);
								out.printf(Locale.ENGLISH, "%.4f%n%.2f%%%n%n", evaluation.kappa(), (1 - evaluation.errorRate()) * 100);

							} catch (Exception e) {
								System.out.println(e.getMessage());
							}
						}
					}
				}
			}
		}
	}
}
