import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import misc.ClassifierCollection;
import misc.FeatureMiner;
import misc.IOUtils;
import misc.SimpleMiner;
import misc.Table;
import perm.CanberraDistance;
import perm.CayleyDistance;
import perm.KendallTau;
import perm.LSquare;
import perm.LevenshteinDistance;
import perm.Metric;
import perm.Permutation;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

public class Experiment11 {

	final static String res = "results" + File.separator + Experiment11.class.getSimpleName() + File.separator;

	public static void main(String[] args) throws Exception {
		List<Classifier> classifiers = ClassifierCollection.getClassifies();
		int numberOfClassifiers = classifiers.size();

		String dataName = "SameSigmaGenerator09";

		String dataPath = "data" + File.separator + dataName + ".obj";
		String outFileName = res + dataName + ".txt";

		List<Permutation[]>[] data = (List<Permutation[]>[]) IOUtils.readObjectFromFile(dataPath);

		int m = data.length;
		int tttr = 2;
		int numberOfTest = 10;

		int maxSets = 1024;

		FeatureMiner miner = new SimpleMiner();

		Random rng = new Random();

		List<Metric> metrList = new ArrayList<Metric>();
		metrList.add(new CanberraDistance());
		metrList.add(new KendallTau());
		metrList.add(new LevenshteinDistance());
		metrList.add(new CayleyDistance());
		metrList.add(new LSquare());

		int n = miner.length();
		List<double[]>[] features = new List[m];

		for (int i = 0; i < m; i++) {
			List<Permutation[]> apl = data[i];
			int apls = apl.size();
			features[i] = new ArrayList<double[]>(apls + 3);

			for (int j = 0; j < apls; j++) {
				features[i].add(miner.mine(apl.get(j)));
			}

		}

		Table table = new Table(numberOfClassifiers + 1, 1 + numberOfTest + 1 + 1);
		table.setDelimiterAfterRow(0, true);

		for (int j = numberOfTest + 1; j >= 0; j--) {
			table.setDelimiterAfterColumn(j, true);
		}

		table.set(0, 0, "Classifier");

		table.set(0, numberOfTest + 1, "Correct");

		table.set(0, numberOfTest + 2, "Kappa");

		for (int i = 1; i <= numberOfTest; i++) {
			table.set(0, i, "#" + i);
		}

		for (int cid = 0; cid < numberOfClassifiers; cid++) {
			double prSum = 0, kappaSum = 0;

			Classifier classifier = classifiers.get(cid);

			String clName = classifier.getClass().getSimpleName().replace("ClassificationVia", "");

			table.set(cid + 1, 0, clName);
			System.out.print(clName + ":");

			for (int test = 1; test <= numberOfTest; test++) {
				for (List<double[]> fl : features) {
					Collections.shuffle(fl, rng);
				}

				String[] cn = new String[m];
				for (int i = 0; i < m; i++) {
					cn[i] = "aggregations" + i;
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

				Instances trainSet = new Instances("R", fvWekaAttributes, maxSets);
				Instances testSet = new Instances("R", fvWekaAttributes, maxSets);

				trainSet.setClassIndex(n);
				testSet.setClassIndex(n);

				for (int i = 0; i < m; i++) {
					int numberOfSets = data[i].size();
					for (int j = 0; j < numberOfSets; j++) {
						Instances instances = (j % tttr == 0) ? testSet : trainSet;
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

				double correct = eTest.correct();
				double incorrect = eTest.incorrect();

				double prCur = 100 * correct / (correct + incorrect);
				double kappaCur = eTest.kappa();

				table.set(cid + 1, test, String.format("%4.2f", prCur));

				prSum += prCur;
				kappaSum += kappaCur;

				System.out.print(' ');
				System.out.print(test);
			}
			System.out.println();

			table.set(cid + 1, numberOfTest + 1, String.format("%4.2f", prSum / numberOfTest));
			table.set(cid + 1, numberOfTest + 2, String.format("%5.3f", kappaSum / numberOfTest));
		}

		try (PrintWriter out = new PrintWriter(new File(outFileName))) {
			table.print(out);
		}

	}
}
