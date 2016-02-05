import gen.*;
import rank.*;
import perm.*;
import miner.*;
import java.util.*;
import weka.core.*;
import weka.classifiers.*;
import static misc.MetricsCollection.getMetrics;
import static misc.ClassifierCollection.getClassifies;

public class TestExperement {

	public static void main(String[] args) {

		int n = 28, m = 82, k = 100000;
		Random random = new Random();

		List<Metric> metrics = getMetrics();

		LossFunction lossFunction = new AverageLoss(metrics.get(0));

		List<Aggregation> aggregations = new ArrayList<>();
		{
			aggregations.add(new BordaCount(0.43));
			aggregations.add(new BordaCount(new BordaCount.DecreasingFunction() {
				@Override
				public double calculate(int n) {
					return -Math.log(n + 1);
				}
			}));
			aggregations.add(new CopelandScore());
			aggregations.add(new PickAPerm(lossFunction));
			aggregations.add(new MarkovChain(0));
			aggregations.add(new MarkovChain(1));
			aggregations.add(new MarkovChain(2));
		}

		// AttributeMiner fminer = new HidenValuesMiner(6);
		AttributeMiner fminer = new FastMiner(metrics);
		ClassMiner cminer = new ClassMiner(aggregations, lossFunction);

		ArrayList<Attribute> attributes = new ArrayList<>();

		attributes.addAll(fminer.getAttributes());
		attributes.add(cminer.getClassAttributes());

		PermutationGenerator pg = new FisherYatesShuffle(random);
		DisagreementsGenerator dg = new LineSigmaGenerator(pg, random);

		Instances instances = new Instances("testset", attributes, k);
		instances.setClass(cminer.getClassAttributes());

		int[] distr = new int[aggregations.size()];

		for (int i = 0; i < k; i++) {
			Disagreement d = dg.generate(n, m);
			int c = cminer.getClassIndex(d);
			if (distr[c] > k / 150) {
				continue;
			}
			++distr[c];

			Instance instance = new DenseInstance(attributes.size());

			instance.setDataset(instances);

			fminer.mine(instance, d);
			instance.setClassValue(cminer.getClassName(c));
			// cminer.mine(instance, d);

			instances.add(instance);
		}

		for (Classifier classifier : getClassifies()) {
			System.out.println(classifier.getClass().getSimpleName());
			try {

				Evaluation evaluation = new Evaluation(instances);
				evaluation.crossValidateModel(classifier, instances, 10, random);
				double[][] cm = evaluation.confusionMatrix();
				for (double[] da : cm) {
					for (double val : da) {
						System.out.printf("%5.0f ", val);
					}
					System.out.println();
				}
				System.out.printf("%.4f%n%.2f%%%n%n", evaluation.kappa(), (1 - evaluation.errorRate()) * 100);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}

		}

		System.out.println(Arrays.toString(distr));

	}

}
