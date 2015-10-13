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

public class RealDataExp1 {

	final static String res = "results" + File.separator + RealDataExp1.class.getSimpleName() + File.separator;

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
								//p.add(Double.NaN);
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

		int permInSet = 21;
		int permLength = 40;

		add(new File(dataPath), data, permInSet, permLength);

		double[] gaw = new double[permLength];
		int[] gmarks = new int[permLength];

		for (double[][] al : data) {
			for (double[] a : al) {
				for (int i = 0; i < permLength; i++) {
					if (!Double.isNaN(a[i])) {
						++gmarks[i];
						gaw[i] += a[i];
					}
				}
			}
		}
		for (int i = 0; i < permLength; i++) {
			gaw[i] /= gmarks[i];
		}

		List<Permutation[]> dataSet = new ArrayList<>();

		Aggregation abw = new BordaCount();

		for (double[][] al : data) {
			double[] aw = new double[permLength];
			int[] marks = new int[permLength];

			for (double[] a : al) {
				for (int i = 0; i < permLength; i++) {
					if (!Double.isNaN(a[i])) {
						++marks[i];
						aw[i] += a[i];
					}
				}
			}

			for (int i = 0; i < permLength; i++) {
				if (marks[i] > 0) {
					aw[i] /= marks[i];
				} else {
					aw[i] = gaw[i];
				}
			}

			Permutation[] p = new Permutation[permInSet];

			for (int j = 0; j < permInSet; j++) {
				double[] w = al[j].clone();

				for (int i = 0; i < permLength; i++) {
					if (Double.isNaN(w[i])) {
						w[i] = aw[i];
					}
				}

				p[j] = abw.aggregateByWeights(w);
			}

			dataSet.add(p);
		}

		List<Metric> metrList = new ArrayList<Metric>();
		{
			metrList.add(new CanberraDistance());
			metrList.add(new KendallTau());
			metrList.add(new LevenshteinDistance());
			metrList.add(new CayleyDistance());
			metrList.add(new LSquare());
		}

		int mls = metrList.size();
		int m = 3;

		int dss = dataSet.size();

		Permutation[][] x = new Permutation[dss][];
		Permutation[][][] y = new Permutation[mls][dss][m];

		for (int i = 0; i < dss; i++) {
			x[i] = dataSet.get(i);
		}

		Aggregation[][] a = new Aggregation[mls][m];
		Painter[] painter = new Painter[mls];

		List<Aggregation> fake = new ArrayList<>();
		for (int i = 0; i < m; i++) {
			fake.add(new BordaCount());
		}

		for (int i = 0; i < mls; i++) {
			painter[i] = new Painter(fake, metrList.get(i));

			a[i][0] = new BordaCount();
			a[i][1] = new CopelandScore();
			a[i][2] = new Stochastic();
			//a[i][0] = new PickAPerm(metrList.get(i));
		}

		for (int k = 0; k < dss; k++) {
			for (int j = 0; j < m; j++) {
				Permutation r = a[0][j].aggregate(x[k]);
				for (int i = 0; i < mls; i++) {
					y[i][k][j] = r;
				}
			}
			for (int i = 0; i < mls; i++) {
				//y[i][k][0] = a[i][0].aggregate(x[k]);
			}

			if (k % 100 == 0) {
				System.out.println(k);
			}
		}

		for (int i = 0; i < mls; i++) {
			int[] distr = new int[m];
			for (int k = 0; k < dss; k++) {
				int color = painter[i].getColor(x[k], y[i][k], 0.0001);
				if (color != -1) {
					++distr[color];
				}
			}

			System.out.println(metrList.get(i));
			System.out.println(Arrays.toString(distr));
			System.out.println();
		}

	}
}
