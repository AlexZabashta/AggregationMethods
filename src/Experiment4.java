import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
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

public class Experiment4 {
	public static void main(String[] args) throws Exception {
		List<Classifier> classifiers = ClassifierCollection.getClassifies();

		int permutationLength = 100;
		int numberOfSets = 1 << 10;
		int permutationsInSet = 10;

		Metric metric = new CanberraDistance();
		FeatureMiner miner = new SimpleMiner();

		List<Aggregation> aggregations = new ArrayList<Aggregation>();

		aggregations.add(new BordaCount());
		aggregations.add(new PickAPerm(metric));
		aggregations.add(new CopelandScore());
		//aggregations.add(new Stochastic());

		int n = miner.length();
		int m = aggregations.size();

		List<double[]>[] features = new List[m];

		for (int i = 0; i < m; i++) {
			features[i] = new ArrayList<double[]>();
		}

		java.util.Random rng = new java.util.Random();
		perm.Random rpg = new perm.Random(rng);

		int minSize = 0;

		while (minSize < numberOfSets) {
			Permutation[] p = new Permutation[permutationsInSet];

			if ( rng.nextBoolean()) {
				double sigma = rng.nextDouble() * permutationLength;
				for (int j = 0; j < p.length; j++) {
					p[j] = rpg.nextGaussian(permutationLength, sigma);
				}
			} else {
				int numberOfSwaps = rng.nextInt(permutationLength);
				for (int j = 0; j < p.length; j++) {
					p[j] = rpg.nextGaussian(permutationLength, numberOfSwaps);
				}
			}

			int best = 0;
			double min = permutationsInSet * 2;

			for (int j = 0; j < m; j++) {
				double cur = 0;
				Permutation q = aggregations.get(j).aggregate(p);

				for (Permutation permutation : p) {
					cur += metric.distance(permutation, q);
				}

				if (cur < min) {
					min = cur;
					best = j;
				}
			}

			features[best].add(miner.mine(p));

			minSize = numberOfSets;
			for (List<double[]> fl : features) {
				minSize = Math.min(minSize, fl.size());
			}
		}

		for (List<double[]> fl : features) {
			Collections.shuffle(fl, rng);
		}

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

		try (PrintWriter out = new PrintWriter(new File("expr4.3.txt"))) {

			Instances trainSet = new Instances("R", fvWekaAttributes, 1 << 9);
			Instances testSet = new Instances("R", fvWekaAttributes, 1 << 9);

			trainSet.setClassIndex(n);
			testSet.setClassIndex(n);

			for (int i = 0; i < m; i++) {
				for (int j = 0; j < numberOfSets; j++) {
					Instances instances = j % 5 == 0 ? testSet : trainSet;
					Instance instance = new Instance(n + 1);

					for (int k = 0; k < n; k++) {
						instance.setValue((Attribute) fvWekaAttributes.elementAt(k), features[i].get(j)[k]);
					}
					instance.setValue((Attribute) fvWekaAttributes.elementAt(n), cn[i]);

					instances.add(instance);
				}
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
		}
	}
}
