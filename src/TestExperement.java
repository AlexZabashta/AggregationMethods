import gen.DisagreementsGenerator;
import gen.FisherYatesShuffle;
import gen.LineSigmaGenerator;
import gen.PermutationGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import misc.AttributeMiner;
import misc.ClassMiner;
import misc.ClassifierCollection;
import misc.FastMiner;
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
import rank.Aggregation;
import rank.AverageLoss;
import rank.BordaCount;
import rank.CopelandScore;
import rank.LossFunction;
import rank.PickAPerm;
import rank.Stochastic;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.SMO;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public class TestExperement {

	public static void main(String[] args) {

		int n = 10, m = 20, k = 4096;
		Random random = new Random();

		List<Metric> metrics = new ArrayList<>();
		{
			metrics.add(new CanberraDistance());

			metrics.add(new KendallTau());
			metrics.add(new CayleyDistance());
			metrics.add(new LevenshteinDistance());

			metrics.add(new LMax());
			metrics.add(new LAbs());
			metrics.add(new LSquare());
		}

		LossFunction lossFunction = new AverageLoss(metrics.get(0));

		List<Aggregation> aggregations = new ArrayList<>();
		{
			aggregations.add(new BordaCount());
			aggregations.add(new Stochastic());
			aggregations.add(new CopelandScore());
			aggregations.add(new PickAPerm(lossFunction));
		}

		AttributeMiner fminer = new FastMiner(metrics);
		ClassMiner cminer = new ClassMiner(aggregations, lossFunction);

		ArrayList<Attribute> attributes = new ArrayList<>();

		attributes.addAll(fminer.getAttributes());
		attributes.add(cminer.getClassAttributes());

		PermutationGenerator pg = new FisherYatesShuffle(random);
		DisagreementsGenerator dg = new LineSigmaGenerator(pg, random);

		Instances instances = new Instances("testset", attributes, k);
		instances.setClass(cminer.getClassAttributes());

		for (int i = 0; i < k; i++) {
			Disagreement d = dg.generate(n, m);
			Instance instance = new DenseInstance(attributes.size());

			instance.setDataset(instances);

			fminer.mine(instance, d);
			cminer.mine(instance, d);

			instances.add(instance);
		}

		for (Classifier classifier : ClassifierCollection.getClassifies()) {
			System.out.println(classifier.getClass().getSimpleName());
			try {

				Evaluation evaluation = new Evaluation(instances);
				evaluation.crossValidateModel(classifier, instances, 5, random);
				double[][] cm = evaluation.confusionMatrix();
				for (double[] da : cm) {
					for (double val : da) {
						System.out.printf("%5.0f ", val);
					}
					System.out.println();
				}
				System.out.println(evaluation.kappa());
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}

		}

	}

}
