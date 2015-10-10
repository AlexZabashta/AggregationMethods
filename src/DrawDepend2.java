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

public class DrawDepend2 {

	final static String res = "results" + File.separator + DrawDepend2.class.getSimpleName() + File.separator;

	public static void main(String[] args) throws Exception {
		Random rng = new Random();

		int wh = 1024;
		double dwh = wh - 1;

		int n = 3;

		int numberOfSets = 1024;
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

		for (int gid = 0; gid < n; gid++) {

			String outFileName = permGen[gid] + "_" + permInSet[gid] + "x" + permLength[gid] + "_" + xid + "x" + yid + "_line_color_bold";
			LineSigmaGenerator dsg = new LineSigmaGenerator(permGen[gid], rng);

			List<Aggregation> aggregations = new ArrayList<Aggregation>();
			{
				aggregations.add(new BordaCount());
				aggregations.add(new PickAPerm(mu));
				aggregations.add(new CopelandScore());
				// aggregations.add(new Stochastic());
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
				Permutation[] p = dsg.generate(permInSet[gid], permLength[gid]);

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
					System.out.println(minSize);
				}
			}

			double lx = Double.POSITIVE_INFINITY, rx = Double.NEGATIVE_INFINITY;
			double dy = Double.POSITIVE_INFINITY, uy = Double.NEGATIVE_INFINITY;

			int[] ox = { 0, 0, 0, 1, -1 };
			int[] oy = { -1, 0, 1, 0, 0 };

			for (List<double[]> fl : features) {
				for (double[] fv : fl) {
					lx = Math.min(lx, fv[xid]);
					rx = Math.max(rx, fv[xid]);

					dy = Math.min(dy, fv[yid]);
					uy = Math.max(uy, fv[yid]);
				}
			}

			BufferedImage canvas = new BufferedImage(wh, wh, BufferedImage.TYPE_INT_RGB);

			for (int i = 0; i < numberOfSets; i++) {
				for (int j = 0; j < m; j++) {

					List<double[]> fl = features[j];
					double[] fv = fl.get(i);

					int x = (int) (dwh * (fv[xid] - lx) / (rx - lx));
					int y = (int) (dwh * (fv[yid] - dy) / (uy - dy));

					for (int offset = 0; offset < ox.length; offset++) {
						int tx = x + ox[offset];
						int ty = y + oy[offset];
						if (0 <= tx && tx < wh && 0 <= ty && ty < wh) {
							canvas.setRGB(tx, wh - ty - 1, pal[j]);
						}
					}
				}
			}
			ImageIO.write(canvas, "png", new File(res + outFileName + ".png"));

		}
	}
}
