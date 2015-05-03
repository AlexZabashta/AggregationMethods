import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import misc.ClassifierCollection;
import misc.ClusterGenerator;
import misc.FeatureMiner;
import misc.GaussGenerator;
import misc.Painter;
import misc.PermutationSetsGenerator;
import misc.SeveralSwapsGenerator;
import misc.SimpleMiner;
import perm.CanberraDistance;
import perm.KendallTau;
import perm.LevenshteinDistance;
import perm.Metric;
import perm.Permutation;
import rank.Aggregation;
import rank.BordaCount;
import rank.CopelandScore;
import rank.PickAPerm;
import rank.Stochastic;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

public class Experiment9 {

	final static String res = "results" + File.separator + Experiment9.class.getSimpleName() + File.separator;

	public static void main(String[] args) throws Exception {
		List<Classifier> classifiers = ClassifierCollection.getClassifies();

		int tttRatio = 5;

		java.util.Random rng = new java.util.Random();

		int permInSet = 10;
		int permLength = 20;
		int numberOfSets = 1 << 8;
		int bs = 128;

		PermutationSetsGenerator psg = new ClusterGenerator(permInSet, permLength, new CanberraDistance(), bs, rng);
		FeatureMiner miner = new SimpleMiner();

		Metric metric = new CanberraDistance();
		List<Aggregation> aggregations = new ArrayList<Aggregation>();

		aggregations.add(new BordaCount());
		aggregations.add(new PickAPerm(metric));
		aggregations.add(new CopelandScore());
		aggregations.add(new Stochastic());

		int n = miner.length();
		int m = aggregations.size();

		Painter painter = new Painter(aggregations, metric);

		System.out.println(Arrays.toString(painter.getColorDistribution(psg, 99)));

		List<double[]>[] features = new List[m];

		for (int i = 0; i < m; i++) {
			features[i] = new ArrayList<double[]>();
		}

		for (int last = 0, minSize = 0; minSize < numberOfSets; last = minSize) {
			Permutation[] p = psg.generate();

			int color = painter.getColor(p);

			if (features[color].size() < numberOfSets) {
				features[color].add(miner.mine(p));
			} else {
				continue;
			}
			minSize = numberOfSets;
			for (List<double[]> fl : features) {
				minSize = Math.min(minSize, fl.size());
			}

			if (last < minSize) {
				System.out.println(minSize);
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
