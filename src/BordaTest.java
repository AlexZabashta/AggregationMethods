import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import javax.imageio.ImageIO;

import gen.DisagreementsGenerator;
import gen.FisherYatesShuffle;
import gen.GaussGenerator;
import gen.LineSigmaGenerator;
import gen.PermutationGenerator;
import gen.SeveralSwapsGenerator;
import miner.ClassMiner;
import perm.CanberraDistance;
import perm.Disagreement;
import perm.Metric;
import perm.Permutation;
import rank.Aggregation;
import rank.AverageLoss;
import rank.BordaCount;
import rank.LossFunction;
import rank.OneMax;

public class BordaTest {

	public static void main(String[] args) throws IOException {

		Random rng = new Random();

		List<PermutationGenerator> pgl = new ArrayList<>();
		pgl.add(new FisherYatesShuffle(rng));
		pgl.add(new GaussGenerator(rng));
		pgl.add(new SeveralSwapsGenerator(rng));

		Metric mu = new CanberraDistance();
		LossFunction lossf = new AverageLoss(mu);

		int n = 17;
		int m = 73;

		for (PermutationGenerator pg : pgl) {
			LineSigmaGenerator dg = new LineSigmaGenerator(pg, rng);

			int k = 256;
			double[] x = new double[k];
			double[] y = new double[k];

			for (int i = 0; i < k; i++) {
				double sigma = rng.nextDouble() * 0.9;

				double p = 0;

				int rep = 50;

				for (int cai = 0; cai < rep; cai++) {

					Disagreement d = dg.generate(n, m, 0.05, sigma);

					double l = 0.2;
					double r = 2.0;

					for (int rbp = 0; rbp < 64; rbp++) {
						double delta = (r - l) / 3;
						double ll = l + delta;
						double rr = r - delta;

						BordaCount lb = new BordaCount(ll);
						BordaCount rb = new BordaCount(rr);

						Permutation lp = lb.aggregate(d);
						Permutation rp = rb.aggregate(d);

						double lv = lossf.getLoss(lp, d);
						double rv = lossf.getLoss(rp, d);

						if (lv < rv) {
							r = rr;
						} else {
							l = ll;
						}
					}

					p += (l + r) / 2;
				}
				x[i] = sigma;
				y[i] = p / rep;

				System.out.printf(Locale.ENGLISH, "%40s %4d %.3f %.3f%n", pg.toString(), i, x[i], y[i]);
			}

			int w = 1600;
			int h = 1200;

			BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_BGR);
			double l = Double.POSITIVE_INFINITY, r = Double.NEGATIVE_INFINITY;
			double d = Double.POSITIVE_INFINITY, u = Double.NEGATIVE_INFINITY;

			for (int i = 0; i < k; i++) {
				l = Math.min(l, x[i]);
				d = Math.min(d, y[i]);
				r = Math.max(r, x[i]);
				u = Math.max(u, y[i]);
			}

			for (int i = 0; i < w; i++) {
				for (int j = 0; j < h; j++) {
					image.setRGB(i, h - j - 1, 0xFFFFFF);
				}
			}
			int rad = 3;
			for (int i = 0; i < k; i++) {
				int px = (int) Math.floor(((x[i] - l) / (r - l)) * w);
				int py = (int) Math.floor(((y[i] - d) / (u - d)) * h);

				for (int dx = -rad; dx <= rad; dx++) {
					for (int dy = -rad; dy <= rad; dy++) {
						int ds = Math.abs(dx) + Math.abs(dy);
						if (ds <= rad) {
							int tx = px + dx;
							int ty = py + dy;
							if (0 <= tx && tx < w && 0 <= ty && ty < h) {
								image.setRGB(tx, h - ty - 1, 0);
							}

						}

					}
				}

			}

			String imgName = pg + ".png";
			String className = BordaTest.class.getSimpleName();

			ImageIO.write(image, "png", new File("results" + File.separatorChar + className + File.separatorChar + imgName));

		}
	}

}
