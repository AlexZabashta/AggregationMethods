import gen.FisherYatesShuffle;
import gen.GaussGenerator;
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

		int wh = 256;
		double dwh = wh - 1;
		int rep = 10;

		int permInSet = 15;
		int permLength = 25;

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

			String outFileName = permGen + "_20_50.png";
			LineSigmaGenerator dsg = new LineSigmaGenerator(permGen, rng);

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
				for (int y = 0; y < wh; y++) {
					double alpha = x / dwh, beta = y / dwh;
					double v = 0;
					int[] d = new int[m];
					for (int r = 0; r < rep; r++) {
						Permutation[] p = dsg.generate(permInSet, permLength, alpha, beta);

						int c = painter.getColor(p, 0.001);

						if (c == -1) {
							continue;
						}
						v = Math.max(v, ++d[c]);
					}

					int color = 0;

					for (int i = 0; i < m; i++) {
						int cur = (int) (255 * d[i] / v);

						color |= cur << (i * 8);
					}

					canvas.setRGB(x, y, color);

				}
				System.out.println(x);
			}

			ImageIO.write(canvas, "png", new File(res + outFileName));

		}
	}
}
