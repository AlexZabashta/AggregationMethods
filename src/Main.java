import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import perm.CayleyDistance;
import perm.KendallTau;
import perm.LAbs;
import perm.LMax;
import perm.LSquare;
import perm.LevenshteinDistance;
import perm.Metric;
import perm.Permutation;
import plot.ImageViewer;
import plot.PlotBuilder;
import rank.Aggregation;
import rank.HyperbolicBordaCount;
import rank.LineBordaCount;
import rank.Stochastic;
import rank.Vote;

public class Main {

	public static void main(String[] args) throws IOException {

		int n = 10, m = 20;
		Permutation p = new Permutation(n);
		Random rnd = new Random();

		Permutation[] permutations = new Permutation[m];
		for (int i = 0; i < m; i++) {
			p = permutations[i] = p.swap(rnd.nextInt(n), rnd.nextInt(n));
			System.out.println(p);
		}

		System.out.println();

		Aggregation[] a = new Aggregation[] { new HyperbolicBordaCount(), new LineBordaCount(), new Stochastic() };

		for (Aggregation aggregation : a) {
			System.out.println(aggregation.aggregate(permutations));
		}

		// List<Metric> metrics = new ArrayList<Metric>();
		// metrics.add(new CayleyDistance());
		// metrics.add(new KendallTau());
		// metrics.add(new LAbs());
		// metrics.add(new LMax());
		// metrics.add(new LSquare());
		// metrics.add(new LevenshteinDistance());
		//
		//
		// int n = 100, w = 1280;
		//
		// Permutation p = new Permutation(n);

		// for (Metric m : metrics) {
		// double[] d = new double[w];
		// for (int i = 0; i < w; i++) {
		// d[i] = m.distance(p, new Permutation(rnd, n));
		// }
		//
		// Arrays.sort(d);
		//
		// BufferedImage image = PlotBuilder.simpleSplot(d, 2520, 300);
		// ImageIO.write(image, "png", new File(m.getClass().getSimpleName() +
		// ".png"));
		// }

		// {
		// double[] d = new double[w];
		// for (int i = 0; i < w; i++) {
		// d[i] = rnd.nextDouble();
		// }
		//
		// Arrays.sort(d);
		//
		// BufferedImage image = PlotBuilder.simpleSplot(d, 2520, 300);
		// ImageIO.write(image, "png", new File("random.png"));
		// }

		// BufferedImage image = PlotBuilder.simpleSplot(d, 640, 480);
		// JFrame jf = new ImageViewer(image);
		// jf.setVisible(true);
		// jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

	}
}
