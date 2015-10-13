import gen.FisherYatesShuffle;
import gen.GaussGenerator;
import gen.HyperSigmaGenerator;
import gen.LineSigmaGenerator;
import gen.PermutationGenerator;
import gen.SameSigmaGenerator;
import gen.SeveralSwapsGenerator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import misc.BordaMiner;
import misc.ClassifierCollection;
import misc.FeatureMiner;
import misc.IOUtils;
import misc.KnnClassifierCollection;
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

public class RealDataExp2 {

	final static String res = "results" + File.separator + RealDataExp2.class.getSimpleName() + File.separator;

	static double[][] transpose(double[][] a) {
		int n = a.length, m = a[0].length;
		double[][] b = new double[m][n];

		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {
				b[j][i] = a[i][j];
			}
		}

		return b;

	}

	public static void add(File f, List<double[][]> data, int permInSet, int permLength) throws IOException {
		if (f.isDirectory()) {
			for (File file : f.listFiles()) {
				add(file, data, permInSet, permLength);
			}
		} else {
			System.out.println(f);
			try (BufferedReader file = new BufferedReader(new FileReader(f))) {
				String line = null;

				List<double[]> cur = new ArrayList<>();

				while ((line = file.readLine()) != null) {
					String[] sl = line.split(" ");
					List<Double> p = new ArrayList<>();
					for (String s : sl) {
						int d = s.indexOf(":") + 1;
						if (0 < d) {
							String val = s.substring(d);
							if (val.equals("NULL")) {
								// p.add(Double.NaN);
								p.add(0.0);
							} else {
								p.add(Double.parseDouble(val));
							}
						}
					}

					int n = p.size(), i = 0;
					double[] temp = new double[n];
					for (Double val : p) {
						temp[i++] = val;
					}
					cur.add(temp);
				}

				int n = cur.size();
				for (int i = 0, j = 0; i < n; i = j) {
					while (j < n && cur.get(i)[0] == cur.get(j)[0]) {
						++j;
					}

					int m = j - i;
					double[][] q = new double[m][];

					for (int k = 0; k < m; k++) {
						double[] t = cur.get(i + k);
						q[k] = Arrays.copyOfRange(t, 1, t.length);
					}

					if (q.length == permLength && q[0].length == permInSet) {
						data.add(transpose(q));
					}
				}

			}
		}
	}

	public static void main(String[] args) throws Exception {
		String dataPath = "data\\agg.txt";
		List<double[][]> data = new ArrayList<>();
		Random rng = new Random();
		int permInSet = 21;
		int permLength = 40;

		add(new File(dataPath), data, permInSet, permLength);

		List<Classifier> classifiers = new ArrayList<Classifier>();

		classifiers.addAll(ClassifierCollection.getClassifies());

		int numberOfTest = 10;
		int tttr = 5;

		Metric mu = new CanberraDistance();
		List<Aggregation> aggregations = new ArrayList<Aggregation>();
		{
			//aggregations.add(new PickAPerm(mu));
			aggregations.add(new BordaCount());
			aggregations.add(new CopelandScore());
			aggregations.add(new Stochastic());
		}
		int m = aggregations.size();
		int maxSets = 1234;

		FeatureMiner[] miner = new FeatureMiner[4];

		miner[0] = new SimpleMiner();
		miner[1] = new NormalMiner();
		miner[2] = new BordaMiner();
		miner[3] = new AllMiner();

		String outFileName = "part_unorm_2.txt";
		Painter painter = new Painter(aggregations, mu);

		int[] fsize = new int[miner.length];
		for (int mid = 0; mid < miner.length; mid++) {
			fsize[mid] = miner[mid].length();
		}

		List<double[][]>[] features = new List[m];

		for (int i = 0; i < m; i++) {
			features[i] = new ArrayList<double[][]>();
		}

		Aggregation abw = new BordaCount();

		int iter = 0;

		for (double[][] al : data) {
			Permutation[] p = new Permutation[permInSet];
			for (int j = 0; j < permInSet; j++) {
				p[j] = abw.aggregateByWeights(al[j]);
			}

			int color = painter.getColor(p, 0.0001);

			if (color == -1) {
				continue;
			}

			double[][] f = new double[miner.length][];
			for (int mid = 0; mid < miner.length; mid++) {
				f[mid] = miner[mid].mine(p);
			}
			features[color].add(f);

			if (++iter % 100 == 0) {
				System.out.println(iter / 100);
			}

		}

		int numberOfSets = 1000000;

		for (List<double[][]> fl : features) {
			numberOfSets = Math.min(numberOfSets, fl.size());
		}

		System.out.println(numberOfSets);

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
					double[][] cm = new double[m][m];
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

						try {
							Evaluation eTest = new Evaluation(trainSet);
							classifier.buildClassifier(trainSet);
							eTest.evaluateModel(classifier, testSet);

							double[][] ccm = eTest.confusionMatrix();
							for (int x = 0; x < m; x++) {
								for (int y = 0; y < m; y++) {
									cm[x][y] += ccm[x][y];
								}
							}
							double correct = eTest.correct();
							double incorrect = eTest.incorrect();

							double prCur = 100 * correct / (correct + incorrect);
							double kappaCur = eTest.kappa();

							table.set(cid + 1, test, String.format("%4.2f", prCur));

							prSum += prCur;
							kappaSum += kappaCur;

							System.out.print(' ');
							System.out.print(test);
						} catch (Exception err) {
							System.out.println(err);
						}
					}
					System.out.println();

					out.println(clName + " " + Arrays.deepToString(cm));

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
