import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.xml.crypto.dsig.keyinfo.PGPData;

import misc.ClassifierCollection;
import misc.FeatureMiner;
import misc.GaussGenerator;
import misc.Painter;
import misc.PermutationSetsGenerator;
import misc.SeveralSwapsGenerator;
import misc.SimpleMiner;
import perm.CanberraDistance;
import perm.CayleyDistance;
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

public class Experiment6 {

	final static String res = "results" + File.separator + Experiment6.class.getSimpleName() + File.separator;

	public static void main(String[] args) throws Exception {
		List<Classifier> classifiers = ClassifierCollection.getClassifies();

		int tttRatio = 5;

		java.util.Random rng = new java.util.Random();

		int permutationsInSet = 15;
		int permutationLength = 20;
		int numberOfSets = 1 << 6;

		GaussGenerator psg = new GaussGenerator(permutationsInSet, permutationLength, 1.0, rng);
		SeveralSwapsGenerator ssg = new SeveralSwapsGenerator(permutationsInSet, permutationLength, 1.0, rng);
		Metric metric = new KendallTau();
		List<Aggregation> aggregations = new ArrayList<Aggregation>();

		aggregations.add(new BordaCount());
		 aggregations.add(new PickAPerm(metric));
		aggregations.add(new CopelandScore());
		aggregations.add(new Stochastic());

		int n = 1;
		int m = aggregations.size();

		Painter painter = new Painter(aggregations, metric);

		System.out.println(Arrays.toString(painter.getColorDistribution(psg, 99)));

		List<double[]>[] features = new List[m];

		for (int i = 0; i < m; i++) {
			features[i] = new ArrayList<double[]>();
		}

		for (int minSize = 0; minSize < numberOfSets;) {

			// int numberOfSwaps = rng.nextInt(permutationLength + 3);
			// Permutation[] p = ssg.generate(numberOfSwaps);

			double sigma = rng.nextDouble();
			Permutation[] p = psg.generate(sigma);

			int color = painter.getColor(p);

			features[color].add(new double[] { sigma });

			minSize = numberOfSets;
			for (List<double[]> fl : features) {
				minSize = Math.min(minSize, fl.size());
			}
		}

		for (List<double[]> fl : features) {
			Collections.shuffle(fl, rng);
		}

		if (n > 0) {

			for (List<double[]> fl : features) {

				for (int i = 0; i < numberOfSets; i++) {
					System.out.printf("%4.2f ", fl.get(i)[0]);

				}
				System.out.println();
			}

			return;
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

		String meticName = metric.getClass().getSimpleName();
		try (PrintWriter out = new PrintWriter(new File(res + meticName + ".txt"))) {

			Instances trainSet = new Instances("R", fvWekaAttributes, numberOfSets);
			Instances testSet = new Instances("R", fvWekaAttributes, numberOfSets / tttRatio);

			trainSet.setClassIndex(n);
			testSet.setClassIndex(n);

			for (int i = 0; i < m; i++) {
				for (int j = 0; j < numberOfSets; j++) {
					Instances instances = j % tttRatio == 0 ? testSet : trainSet;
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
