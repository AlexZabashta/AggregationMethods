import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import misc.ClassifierCollection;
import misc.FeatureMiner;
import misc.SimpleMiner;
import perm.CanberraDistance;
import perm.KendallTau;
import perm.LevenshteinDistance;
import perm.Metric;
import perm.Permutation;
import rank.Aggregation;
import rank.BordaCount;
import rank.CopelandScore;
import rank.CopelandScore;
import rank.PickAPerm;
import rank.Stochastic;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

public class Experiment2 {
	public static void main(String[] args) throws Exception {
		List<Classifier> classifiers = ClassifierCollection.getClassifies();

		int permutationLength = 100;
		int numberOfSets = 1 << 13;
		int permutationsInSet = 10;

		Metric metric = new CanberraDistance();
		FeatureMiner miner = new SimpleMiner();

		List<Aggregation> aggregations = new ArrayList<Aggregation>();

		aggregations.add(new CopelandScore());
		aggregations.add(new CopelandScore());

		int n = miner.length();
		int m = aggregations.size();

		java.util.Random rng = new java.util.Random();
		perm.Random rpg = new perm.Random(rng);

		double[][] feature = new double[numberOfSets][];
		int[] color = new int[numberOfSets];

		int x = 0, y = 0;

		for (int i = 0; i < numberOfSets; i++) {
			double sigma = rng.nextDouble() * 50;
			Permutation[] p = new Permutation[permutationsInSet];
			for (int j = 0; j < p.length; j++) {
				p[j] = rpg.nextGaussian(permutationLength, sigma);
				// p[j] = rpg.next(permutationLength);
			}

			double min = permutationsInSet;

			for (int j = 0; j < m; j++) {
				double cur = 0;
				Permutation q = aggregations.get(j).aggregate(p);

				for (Permutation permutation : p) {
					cur += metric.distance(permutation, q);
				}

				if (cur < min) {
					min = cur;
					color[i] = j;
				}
			}

			if (color[i] == 0) {
				++x;
			} else {
				++y;
			}

			feature[i] = miner.mine(p);
		}
		System.out.println(x + " " + y);
		x = y = Math.min(x, y);

		String[] cn = new String[m];
		for (int i = 0; i < m; i++) {
			cn[i] = aggregations.get(i).getClass().getSimpleName() + i;
		}
		FastVector fvWekaAttributes = new FastVector(n + 1);

		for (int i = 0; i < n; i++) {
			fvWekaAttributes.addElement(new Attribute("atr" + i));
		}

		FastVector fvClassVal = new FastVector(m);
		for (int i = 0; i < m; i++) {
			fvClassVal.addElement(cn[i]);
		}
		fvWekaAttributes.addElement(new Attribute("class_v", fvClassVal));

		PrintWriter out = new PrintWriter(new File("expr2.txt"));
		out.println(x + " " + y);

		Instances trainSet = new Instances("R", fvWekaAttributes, 1 << 9);
		Instances testSet = new Instances("R", fvWekaAttributes, 1 << 9);

		trainSet.setClassIndex(n);
		testSet.setClassIndex(n);

		for (int i = 0; i < numberOfSets; i++) {
			if (color[i] == 0) {
				if (x == 0) {
					continue;
				}
				--x;
			} else {
				if (y == 0) {
					continue;
				}
				--y;
			}

			Instances instances = i % 5 == 0 ? testSet : trainSet;
			Instance instance = new Instance(n + 1);

			for (int j = 0; j < n; j++) {
				instance.setValue((Attribute) fvWekaAttributes.elementAt(j), feature[i][j]);
			}
			instance.setValue((Attribute) fvWekaAttributes.elementAt(n), cn[color[i]]);

			instances.add(instance);

		}

		for (Classifier classifier : classifiers) {
			Evaluation eTest = new Evaluation(trainSet);
			classifier.buildClassifier(trainSet);
			eTest.evaluateModel(classifier, testSet);

			out.println(classifier.getClass().getSimpleName() + ':');
			double[][] cm = eTest.confusionMatrix();
			out.println("Confusion matrix:");
			for (double[] d : cm) {
				for (double val : d) {
					out.print((int) val);
					out.print(' ');
				}
				out.println();
			}
			out.println(eTest.toSummaryString());
		}

		out.close();

	}
}
