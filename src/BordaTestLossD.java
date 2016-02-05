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
import perm.KendallTau;
import perm.Metric;
import perm.Permutation;
import rank.Aggregation;
import rank.AverageLoss;
import rank.BordaCount;
import rank.LossFunction;
import rank.OneMax;

public class BordaTestLossD {

	public static void main(String[] args) throws IOException {

		Random rng = new Random();

		List<PermutationGenerator> pgl = new ArrayList<>();
		pgl.add(new FisherYatesShuffle(rng));
		pgl.add(new GaussGenerator(rng));
		pgl.add(new SeveralSwapsGenerator(rng));

		Metric mu = new KendallTau();
		LossFunction lossf = new AverageLoss(mu);

		for (int rep = 0; rep < 10; rep++) {

			int n = rng.nextInt(64) + 8;
			int m = rng.nextInt(256) + 32;

			for (PermutationGenerator pg : pgl) {
				LineSigmaGenerator dg = new LineSigmaGenerator(pg, rng);

				int k = 256;
				double[] x = new double[k];
				double[] y = new double[k];

				double lowP = 0.05, higP = 2;

				Disagreement dis = dg.generate(n, m, 0.07, 0.61);

				for (int i = 0; i < k; i++) {
					double delta = higP - lowP;
					double p = (i * delta / k) + lowP;

					BordaCount bc = new BordaCount(p);
					double lv = lossf.getLoss(bc.aggregate(dis), dis);

					x[i] = p;
					y[i] = lv;
					if (i % 10 == 0) {
						System.out.printf(Locale.ENGLISH, "%40s %4d %.3f %.3f%n", pg.toString(), i, x[i], y[i]);
					}
				}

				double fm;
				{
					double l = lowP;
					double r = higP;

					for (int rbp = 0; rbp < 64; rbp++) {
						double delta = (r - l) / 6;
						double ll = l + delta;
						double rr = r - delta;

						BordaCount lb = new BordaCount(ll);
						BordaCount rb = new BordaCount(rr);

						Permutation lp = lb.aggregate(dis);
						Permutation rp = rb.aggregate(dis);

						double lv = lossf.getLoss(lp, dis);
						double rv = lossf.getLoss(rp, dis);

						if (lv < rv) {
							r = rr;
						} else {
							l = ll;
						}
					}

					fm = (l + r) / 2;
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

				int fmx = (int) Math.floor(((fm - l) / (r - l)) * w);

				for (int i = 0; i < w; i++) {
					for (int j = 0; j < h; j++) {
						if (i == fmx) {
							image.setRGB(i, h - j - 1, 0);
						} else {
							image.setRGB(i, h - j - 1, 0xFFFFFF);
						}
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

				String imgName = n + "x" + m + "_" + pg + ".png";
				String className = BordaTestLossD.class.getSimpleName();

				ImageIO.write(image, "png", new File("results" + File.separatorChar + className + File.separatorChar + imgName));
			}
		}
	}

}
