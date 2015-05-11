import gen.ClusterGenerator;
import gen.DataSetsGenerator;
import gen.FisherYatesShuffle;
import gen.GaussGenerator;
import gen.LineGenerator;
import gen.PermutationGenerator;
import gen.SameSigmaGenerator;
import gen.SeveralSwapsGenerator;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import misc.ClassifierCollection;
import misc.FeatureMiner;
import misc.Painter;
import misc.SimpleMiner;
import perm.CanberraDistance;
import perm.CayleyDistance;
import perm.KendallTau;
import perm.LSquare;
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

public class Experiment10 {

	final static String res = "results" + File.separator + Experiment10.class.getSimpleName() + File.separator;

	public static void main(String[] args) throws Exception {
		List<Classifier> classifiers = ClassifierCollection.getClassifies();

		for (Classifier cl : classifiers) {
			System.out.println(cl.getClass().getSimpleName());
		}
		if (!classifiers.isEmpty()) {
			return;
		}

		int trainSetSzie = 32, testSetSize = 64;
		int numberOfTest = 5;

		int permInSet = 10;
		int permLength = 100;

		int maxIter = 1000000;

		int numberOfSets = trainSetSzie + testSetSize;

		FeatureMiner miner = new SimpleMiner();

		Metric mu = new CanberraDistance();

		Random rng = new Random();

		List<Metric> metrList = new ArrayList<Metric>();
		metrList.add(new CanberraDistance());
		metrList.add(new KendallTau());
		metrList.add(new LevenshteinDistance());
		metrList.add(new CayleyDistance());
		metrList.add(new LSquare());

		List<PermutationGenerator> permGenList = new ArrayList<PermutationGenerator>();
		{
			// permGenList.add(new FisherYatesShuffle(1, 0.05, rng));
			permGenList.add(new GaussGenerator(0.4, 0.05, rng));
			// permGenList.add(new SeveralSwapsGenerator(1, 0.05, rng));
		}

		List<DataSetsGenerator> dataGenList = new ArrayList<DataSetsGenerator>();
		{

			for (PermutationGenerator permGen : permGenList) {
				dataGenList.add(new SameSigmaGenerator(permGen, rng));

				// for (Metric metric : metrList) {
				// dataGenList.add(new LineGenerator(metric, permGen, 2048));
				// dataGenList.add(new ClusterGenerator(metric, permGen, 64));
				// }
			}
		}

		List<Aggregation> aggregations = new ArrayList<Aggregation>();
		{

			aggregations.add(new BordaCount());
			aggregations.add(new PickAPerm(mu));
			aggregations.add(new CopelandScore());
			aggregations.add(new Stochastic());
		}

		int n = miner.length();
		int m = aggregations.size();

		Painter painter = new Painter(aggregations, mu);

		List<double[]>[] features = new List[m];

		for (int i = 0; i < m; i++) {
			features[i] = new ArrayList<double[]>();
		}

		Permutation[][] debug = new Permutation[m][];

		int[] colorSize = new int[m];

		for (int curIter = 0, last = 0, minSize = 0; curIter < maxIter && minSize < numberOfSets; last = minSize, curIter++) {
			for (DataSetsGenerator dsg : dataGenList) {
				Permutation[] p = dsg.generate(permInSet, permLength);

				int color = painter.getColor(p);

				if (color == -1) {
					continue;
				}

				debug[color] = p;

				++colorSize[color];

				if (features[color].size() < numberOfSets) {
					features[color].add(miner.mine(p));
				} else {
					continue;
				}
			}
			minSize = numberOfSets;
			for (List<double[]> fl : features) {
				minSize = Math.min(minSize, fl.size());
			}

			if (last < minSize) {
				System.out.println(minSize);
			}
		}

		boolean empt = false;
		for (List<double[]> fl : features) {
			if (fl.size() < numberOfSets) {
				empt = true;
			}
		}

		// for (Permutation[] d : debug) {
		// for (Permutation p : d) {
		// System.out.println(p);
		// }
		// System.out.println();
		// }
		//
		// if (!empt) {
		// return;
		// }

		System.out.println(Arrays.toString(colorSize));

		if (empt) {
			System.out.println("empt");
			return;
		}

		for (Classifier classifier : classifiers) {
			try (PrintWriter out = new PrintWriter(new File(res + classifier.getClass().getSimpleName() + ".txt"))) {

				for (int test = 1; test <= numberOfTest; test++) {
					out.println("test #" + test + ":");
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

					Instances trainSet = new Instances("R", fvWekaAttributes, trainSetSzie);
					Instances testSet = new Instances("R", fvWekaAttributes, testSetSize);

					trainSet.setClassIndex(n);
					testSet.setClassIndex(n);

					for (int i = 0; i < m; i++) {
						for (int j = 0; j < numberOfSets; j++) {
							Instances instances = (j < testSetSize) ? testSet : trainSet;
							Instance instance = new Instance(n + 1);

							for (int k = 0; k < n; k++) {
								instance.setValue((Attribute) fvWekaAttributes.elementAt(k), features[i].get(j)[k]);
							}
							instance.setValue((Attribute) fvWekaAttributes.elementAt(n), cn[i]);

							instances.add(instance);
						}
					}

					Evaluation eTest = new Evaluation(trainSet);
					classifier.buildClassifier(trainSet);
					eTest.evaluateModel(classifier, testSet);
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
}
