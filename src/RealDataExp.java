import gen.FisherYatesShuffle;
import gen.GaussGenerator;
import gen.HyperSigmaGenerator;
import gen.LineSigmaGenerator;
import gen.PermutationGenerator;
import gen.SameSigmaGenerator;
import gen.SeveralSwapsGenerator;
import miner.AttributeMiner;
import miner.ClassMiner;
import miner.FastMiner;
import miner.LMiner;
import miner.SimpleMiner;

import static misc.ClassifierCollection.getClassifies;
import static misc.MetricsCollection.getMetrics;

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
import java.util.Locale;
import java.util.Random;

import misc.ClassifierCollection;
import misc.IOUtils;
import misc.KnnClassifierCollection;
import misc.Table;
import perm.CanberraDistance;
import perm.CayleyDistance;
import perm.Disagreement;
import perm.KendallTau;
import perm.LSquare;
import perm.LevenshteinDistance;
import perm.Metric;
import perm.Permutation;
import rank.Aggregation;
import rank.AverageLoss;
import rank.BordaCount;
import rank.CopelandScore;
import rank.LocalKemenization;
import rank.LossFunction;
import rank.MarkovChain;
import rank.MetaBordaCount;
import rank.MetaFastAlgos;
import rank.MetaMarkovChain;
import rank.MetaSlowAlgos;
import rank.PickAPerm;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public class RealDataExp {

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
								p.add(Double.NaN);
								// p.add(0.0);
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
		String dataPath = "data/agg.txt";
		List<double[][]> data = new ArrayList<>();
		int permInSet = 21;
		int permLength = 40;

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
			aggregations.add(new BordaCount(new BordaCount.DecreasingFunction() {
				@Override
				public double calculate(int n) {
					return 1.0 / (n + 50);
				}
			}));

			aggregations.add(new CopelandScore());
			aggregations.add(new LocalKemenization());
			aggregations.add(new PickAPerm(lossFunction));

			aggregations.add(new MarkovChain(0));
			aggregations.add(new MarkovChain(1));
			aggregations.add(new MarkovChain(2));
		}
		int fd = 600;
		int s = aggregations.size();

		AttributeMiner fminer = new FastMiner(metrics);
		AttributeMiner sminer = new SimpleMiner(metrics);
		AttributeMiner lminer = new LMiner();
		ClassMiner cminer = new ClassMiner(aggregations, lossFunction);

		List<List<AttributeMiner>> minersList = new ArrayList<List<AttributeMiner>>();
		{
			List<AttributeMiner> miners = new ArrayList<AttributeMiner>();
			{
				miners.add(lminer);
			}
			minersList.add(miners);
		}
		{
			List<AttributeMiner> miners = new ArrayList<AttributeMiner>();
			{
				miners.add(sminer);
			}
			minersList.add(miners);
		}
		{
			List<AttributeMiner> miners = new ArrayList<AttributeMiner>();
			{
				miners.add(fminer);
			}
			minersList.add(miners);
		}
		add(new File(dataPath), data, permInSet, permLength);

		if (fd == 0) {
			int[] distr = new int[s];

			for (double[][] vals : data) {

				for (int j = 0; j < permLength; j++) {
					double minVal = Double.POSITIVE_INFINITY;
					for (int i = 0; i < permInSet; i++) {
						if (Double.isNaN(vals[i][j])) {
							continue;
						}
						minVal = Math.min(minVal, vals[i][j]);
					}

					if (Double.isInfinite(minVal)) {
						minVal = 1;
					}

					for (int i = 0; i < permInSet; i++) {
						if (Double.isNaN(vals[i][j])) {
							vals[i][j] = j * minVal / permLength;
						}
					}
				}

				Permutation[] p = new Permutation[permInSet];

				for (int i = 0; i < permInSet; i++) {
					p[i] = Aggregation.aggregateByWeights(vals[i]);
				}

				Disagreement d = new Disagreement(p);

				int c = cminer.getClassIndex(d);
				++distr[c];

			}

			System.out.println(Arrays.toString(distr));

		} else {

			for (List<AttributeMiner> miners : minersList) {
				boolean ns = false;
				String name = "";
				for (AttributeMiner miner : miners) {
					if (ns) {
						name += " ";
					} else {
						ns = true;
					}
					name += miner.getClass().getSimpleName();
				}
				System.out.println(name);

				try (PrintWriter out = new PrintWriter(new File("results/RealWorld/" + name + ".txt"))) {
					ArrayList<Attribute> attributes = new ArrayList<>();
					attributes.add(cminer.getClassAttributes());
					for (AttributeMiner miner : miners) {
						attributes.addAll(miner.getAttributes());
					}

					Instances instances = new Instances("testset", attributes, 20000);
					instances.setClass(cminer.getClassAttributes());

					int[] distr = new int[s];

					for (double[][] vals : data) {

						for (int j = 0; j < permLength; j++) {
							double minVal = Double.POSITIVE_INFINITY;
							for (int i = 0; i < permInSet; i++) {
								if (Double.isNaN(vals[i][j])) {
									continue;
								}
								minVal = Math.min(minVal, vals[i][j]);
							}

							if (Double.isInfinite(minVal)) {
								minVal = 1;
							}

							for (int i = 0; i < permInSet; i++) {
								if (Double.isNaN(vals[i][j])) {
									vals[i][j] = random.nextDouble() * minVal;
								}
							}
						}

						Permutation[] p = new Permutation[permInSet];

						for (int i = 0; i < permInSet; i++) {
							p[i] = Aggregation.aggregateByWeights(vals[i]);
						}

						Disagreement d = new Disagreement(p);

						int c = cminer.getClassIndex(d);
						if (distr[c] == fd) {
							continue;
						}
						++distr[c];

						Instance instance = new DenseInstance(attributes.size());
						instance.setDataset(instances);

						for (AttributeMiner miner : miners) {
							miner.mine(instance, d);
						}
						instance.setClassValue(cminer.getClassName(c));

						instances.add(instance);

					}

					for (Classifier classifier : getClassifies()) {
						String curName = classifier.getClass().getSimpleName();
						out.println(curName);
						System.out.println("     " + curName);
						try {

							Evaluation evaluation = new Evaluation(instances);
							evaluation.crossValidateModel(classifier, instances, 10, random);
							double[][] cm = evaluation.confusionMatrix();
							for (double[] da : cm) {
								for (double val : da) {
									out.printf("%5.0f ", val);
								}
								out.println();
							}
							double arec = 0, apre = 0;
							for (int i = 0; i < s; i++) {
								double srec = 0, spre = 0;

								for (int j = 0; j < s; j++) {
									srec += cm[i][j];
									spre += cm[j][i];
								}

								if (cm[i][i] == 0.0) {
									srec += 1;
									spre += 1;
								}
								double rec = cm[i][i] / srec;
								double pre = cm[i][i] / spre;

								arec += rec / s;
								apre += pre / s;
							}

							double f = 2 * arec * apre / (arec + apre);

							out.printf(Locale.ENGLISH, "%.3f%n", f);
							out.printf(Locale.ENGLISH, "%.4f%n%.2f%%%n%n", evaluation.kappa(), (1 - evaluation.errorRate()) * 100);

						} catch (Exception e) {
							System.out.println(e.getMessage());
						}
					}
				}
			}
		}
	}
}