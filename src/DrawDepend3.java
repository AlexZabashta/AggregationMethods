import gen.FisherYatesShuffle;
import gen.GaussGenerator;
import gen.HyperSigmaGenerator;
import gen.LineSigmaGenerator;
import gen.PermutationGenerator;
import gen.SeveralSwapsGenerator;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import misc.AllMiner;
import misc.FeatureMiner;
import misc.NormalMiner;
import misc.Painter;
import misc.SimpleMiner;
import perm.CanberraDistance;
import perm.CayleyDistance;
import perm.KendallTau;
import perm.LAbs;
import perm.LSquare;
import perm.LevenshteinDistance;
import perm.Metric;
import perm.Permutation;
import rank.Aggregation;
import rank.BordaCount;
import rank.CopelandScore;
import rank.PickAPerm;
import rank.Stochastic;

public class DrawDepend3 {

	final static String res = "results" + File.separator + DrawDepend3.class.getSimpleName() + File.separator;

	public static void main(String[] args) throws Exception {
		Random rng = new Random();

		int wh = 1024;
		double dwh = wh - 1;

		int n = 3;

		int numberOfSets = 4096;
		int xid = 0, yid = 3;

		int[] permInSet = new int[n];
		int[] permLength = new int[n];
		PermutationGenerator[] permGen = new PermutationGenerator[n];
		{
			int i = 0;
			{
				permInSet[i] = 18; // 18x15
				permLength[i] = 15; // 22x33
				permGen[i] = new FisherYatesShuffle(0.99, 0.01, rng);
			}
			++i;
			{
				permInSet[i] = 25; // 28x77
				permLength[i] = 36; // 25x36
				permGen[i] = new GaussGenerator(0.43, 0.01, rng);
			}
			++i;
			{
				permInSet[i] = 25; // 25x17
				permLength[i] = 17; // 28x20
				permGen[i] = new SeveralSwapsGenerator(0.99, 0.01, rng);
			}
		}

		Metric mu = new CanberraDistance();
		// Metric mu = new KendallTau();

		List<Metric> metrList = new ArrayList<Metric>();
		metrList.add(new CanberraDistance());
		metrList.add(new KendallTau());
		metrList.add(new LevenshteinDistance());
		metrList.add(new CayleyDistance());
		metrList.add(new LSquare());

		for (int i = 2; i < n; i++) {

			String outFileName = permGen[i] + "_" + permInSet[i] + "x" + permLength[i] + "_" + xid + "x" + yid + "_line";
			LineSigmaGenerator dsg = new LineSigmaGenerator(permGen[i], rng);

			List<Aggregation> aggregations = new ArrayList<Aggregation>();
			{
				aggregations.add(new BordaCount());
				aggregations.add(new PickAPerm(mu));
				aggregations.add(new CopelandScore());
				aggregations.add(new Stochastic());
			}

			int m = aggregations.size();

			int[] pal = new int[m];

			for (int j = 0; j < m; j++) {
				pal[j] = Color.HSBtoRGB(0.66f * j / (m - 1), 1.0f, 1.0f);
			}

			// double[] fx = new double[k], fy = new double[k];
			// int[] color = new int[k];
			Painter painter = new Painter(aggregations, mu);
			FeatureMiner miner = new SimpleMiner();
			// for (int cur = 0; cur < k;) {
			// Permutation[] p = dsg.generate(permInSet[i], permLength[i]);
			// int c = painter.getColor(p, 0.001);
			// if (c == -1) {
			// continue;
			// }
			//
			// double[] f = miner.mine(p);
			//
			// fx[cur] = f[3];
			// fy[cur] = f[4];
			//
			// color[cur] = c;
			// if (cur % 100 == 0) {
			// System.out.println(cur);
			// }
			// ++cur;
			// }

			List<double[]>[] features = new List[m];
			for (int j = 0; j < m; j++) {
				features[j] = new ArrayList<double[]>();
			}

			for (int last = 0, minSize = 0; minSize < numberOfSets; last = minSize) {
				Permutation[] p = dsg.generate(permInSet[i], permLength[i]);

				int c = painter.getColor(p, 0.001);

				if (c == -1) {
					continue;
				}

				if (features[c].size() < numberOfSets) {
					features[c].add(miner.mine(p));
				} else {
					continue;
				}
				minSize = numberOfSets;
				for (List<double[]> fl : features) {
					minSize = Math.min(minSize, fl.size());
				}

				if (last < minSize) {
					System.out.println("D" + i + " " + minSize);
				}
			}

			double lx = Double.POSITIVE_INFINITY, rx = Double.NEGATIVE_INFINITY;
			double dy = Double.POSITIVE_INFINITY, uy = Double.NEGATIVE_INFINITY;

			for (List<double[]> fl : features) {
				for (double[] fv : fl) {
					lx = Math.min(lx, fv[xid]);
					rx = Math.max(rx, fv[xid]);

					dy = Math.min(dy, fv[yid]);
					uy = Math.max(uy, fv[yid]);
				}
			}

			for (int j = 0; j < m; j++) {
				BufferedImage canvas = new BufferedImage(wh, wh, BufferedImage.TYPE_INT_RGB);
				for (int x = 0; x < wh; x++) {
					for (int y = 0; y < wh; y++) {
						canvas.setRGB(x, y, -1);
					}
				}
				List<double[]> fl = features[j];
				for (double[] fv : fl) {
					int x = (int) (dwh * (fv[xid] - lx) / (rx - lx));
					int y = (int) (dwh * (fv[yid] - dy) / (uy - dy));
					if (0 <= x && x < wh && 0 <= y && y < wh) {
						canvas.setRGB(x, wh - y - 1, 0);
					}
				}
				ImageIO.write(canvas, "png", new File(res + outFileName + "_" + aggregations.get(j) + ".png"));
			}

		}
	}
}
