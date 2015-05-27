import gen.FisherYatesShuffle;
import gen.GaussGenerator;
import gen.HyperSigmaGenerator;
import gen.LineSigmaGenerator;
import gen.PermutationGenerator;
import gen.SeveralSwapsGenerator;

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

public class DrawDepend1 {

	final static String res = "results" + File.separator + DrawDepend1.class.getSimpleName() + File.separator;

	public static void main(String[] args) throws Exception {
		Random rng = new Random();

		int wh = 64;
		double dwh = wh - 1;
		int rep = 10;

		int n = 3;

		int[] permInSet = new int[n];
		int[] permLength = new int[n];
		PermutationGenerator[] permGen = new PermutationGenerator[n];
		{
			int i = 0;
			{
				permInSet[i] = 22; // 18x15
				permLength[i] = 33; // 22x33
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
				permInSet[i] = 28; // 25x17
				permLength[i] = 20; // 28x20
				permGen[i] = new SeveralSwapsGenerator(0.99, 0.01, rng);
			}
		}

		// Metric mu = new CanberraDistance();
		Metric mu = new LAbs();

		List<Metric> metrList = new ArrayList<Metric>();
		metrList.add(new CanberraDistance());
		metrList.add(new KendallTau());
		metrList.add(new LevenshteinDistance());
		metrList.add(new CayleyDistance());
		metrList.add(new LSquare());

		for (int i = 0; i < n; i++) {

			String outFileName = permGen[i] + "_" + permInSet[i] + "_" + permLength[i] + "line.png";
			LineSigmaGenerator dsg = new LineSigmaGenerator(permGen[i], rng);

			List<Aggregation> aggregations = new ArrayList<Aggregation>();
			{

				aggregations.add(new BordaCount());
				aggregations.add(new PickAPerm(mu));
				aggregations.add(new CopelandScore());
				// aggregations.add(new Stochastic());
			}

			int m = aggregations.size();

			Painter painter = new Painter(aggregations, mu);

			BufferedImage canvas = new BufferedImage(wh, wh, BufferedImage.TYPE_INT_RGB);

			for (int x = 0; x < wh; x++) {
				for (int y = 0; y <= x; y++) {
					double alpha = x / dwh, beta = y / dwh;
					double v = 1;
					int[] d = new int[m];
					for (int r = 0; r < rep; r++) {
						Permutation[] p = dsg.generate(permInSet[i], permLength[i], alpha, beta);

						int c = painter.getColor(p, 0.001);

						if (c == -1) {
							continue;
						}
						v = Math.max(v, ++d[c]);
					}

					int color = 0;

					for (int j = 0; j < m; j++) {
						int cur = (int) (255 * d[j] / v);
						if (cur > 255) {
							cur = 255;
						}
						if (cur < 0) {
							cur = 0;
						}
						color |= cur << (j * 8);
					}

					canvas.setRGB(x, wh - y - 1, color);
					canvas.setRGB(y, wh - x - 1, color);

				}
				System.out.println(x);
			}

			ImageIO.write(canvas, "png", new File(res + outFileName));

		}
	}
}
