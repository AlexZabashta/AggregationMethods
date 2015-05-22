import gen.FisherYatesShuffle;
import gen.GaussGenerator;
import gen.HyperSigmaGenerator;
import gen.LineSigmaGenerator;
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
import misc.IOUtils;
import misc.NormalMiner;
import misc.Painter;
import misc.SimpleMiner;
import misc.Table;
import misc.AllMiner;
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

public class Experiment12 {

	final static String res = "results" + File.separator + Experiment12.class.getSimpleName() + File.separator;

	public static void main(String[] args) throws Exception {
		List<Classifier> classifiers = ClassifierCollection.getClassifies();

		int numberOfTest = 10;

		int permInSet = 20;
		int permLength = 50;
		int tttr = 5;

		int maxIter = 1000000;
		int maxSets = 2048;

		int numberOfSets = 512;

		FeatureMiner simminer = new SimpleMiner();
		FeatureMiner badMiner = new AllMiner();
		FeatureMiner norMiner = new NormalMiner();

		Metric mu = new CanberraDistance();

		Random rng = new Random();

		List<Metric> metrList = new ArrayList<Metric>();
		metrList.add(new CanberraDistance());
		metrList.add(new KendallTau());
		metrList.add(new LevenshteinDistance());
		metrList.add(new CayleyDistance());
		metrList.add(new LSquare());

		List<PermutationGenerator> pgl = new ArrayList<PermutationGenerator>();
		{
			pgl.add(new GaussGenerator(0.4, 0.05, rng));
			pgl.add(new FisherYatesShuffle(0.9, 0.05, rng));
			pgl.add(new SeveralSwapsGenerator(0.9, 0.05, rng));
		}

		for (PermutationGenerator permGen : pgl) {

			String outFileName = permGen.getClass().getSimpleName() + "_20_50.txt";
			LineSigmaGenerator dsg = new LineSigmaGenerator(permGen, rng);

			List<Aggregation> aggregations = new ArrayList<Aggregation>();
			{

				aggregations.add(new BordaCount());
				aggregations.add(new PickAPerm(mu));
				aggregations.add(new CopelandScore());
				aggregations.add(new Stochastic());
			}

			int[] fsize = new int[] { simminer.length(), norMiner.length(), badMiner.length() };

			int m = aggregations.size();

			Painter painter = new Painter(aggregations, mu);

			List<double[][]>[] features = new List[m];

			for (int i = 0; i < m; i++) {
				features[i] = new ArrayList<double[][]>();
			}

			int[] colorSize = new int[m];

			for (int curIter = 0, last = 0, minSize = 0; curIter < maxIter && minSize < numberOfSets; last = minSize, curIter++) {
				double alpha = 0.3, beta = 0.7;
				Permutation[] p = dsg.generate(permInSet, permLength, alpha, beta);

				int color = painter.getColor(p, 0.0023);

				if (color == -1) {
					continue;
				}

				++colorSize[color];

				if (features[color].size() < numberOfSets) {
					features[color].add(new double[][] { simminer.mine(p), norMiner.mine(p), badMiner.mine(p) });
				} else {
					continue;
				}
				minSize = numberOfSets;
				for (List<double[][]> fl : features) {
					minSize = Math.min(minSize, fl.size());
				}

				if (last < minSize) {
					System.out.println(minSize);
				}
			}

			boolean empt = false;
			for (List<double[][]> fl : features) {
				if (fl.size() < numberOfSets) {
					empt = true;
				}
			}

			System.out.println(Arrays.toString(colorSize));

			if (empt) {
				System.out.println("empt");
				return;
			}
			System.out.println();
			try (PrintWriter out = new PrintWriter(new File(res + outFileName))) {
				int numberOfClassifiers = classifiers.size();
				Table total = new Table(numberOfClassifiers + 1, 1 + fsize.length);

				total.setDelimiterAfterRow(0, true);

				total.set(0, 0, "Classifier");

				for (int fid = 0; fid < fsize.length; fid++) {
					int n = fsize[fid];

					total.setDelimiterAfterColumn(fid, true);

					Table table = new Table(numberOfClassifiers + 1, 1 + numberOfTest + 1 + 1);
					table.setDelimiterAfterRow(0, true);

					for (int j = numberOfTest + 1; j >= 0; j--) {
						table.setDelimiterAfterColumn(j, true);
					}

					table.set(0, 0, "Classifier");

					table.set(0, numberOfTest + 1, "Correct");

					table.set(0, numberOfTest + 2, "Kappa");

					for (int i = 1; i <= numberOfTest; i++) {
						table.set(0, i, "Test #" + i);
					}

					total.set(0, fid + 1, "Correct #" + fid);

					for (int cid = 0; cid < numberOfClassifiers; cid++) {
						double prSum = 0, kappaSum = 0;

						Classifier classifier = classifiers.get(cid);

						String clName = classifier.getClass().getSimpleName().replace("ClassificationVia", "");

						table.set(cid + 1, 0, clName);
						total.set(cid + 1, 0, clName);
						System.out.print(clName + ":");

						for (int test = 1; test <= numberOfTest; test++) {
							for (List<double[][]> fl : features) {
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
								for (int j = 0; j < features[i].size(); j++) {
									Instances instances = (j % tttr == 0) ? testSet : trainSet;
									Instance instance = new Instance(n + 1);

									for (int k = 0; k < n; k++) {
										instance.setValue((Attribute) fvWekaAttributes.elementAt(k), features[i].get(j)[fid][k]);
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

						total.set(cid + 1, fid + 1, String.format("%4.2f", prSum / numberOfTest));
					}

					table.print(out);
					System.out.println();
					out.println();
					out.println();
				}
				total.print(out);

			}
		}
	}
}
