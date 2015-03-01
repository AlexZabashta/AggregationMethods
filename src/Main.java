import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import perm.Permutation;
import plot.ImageViewer;
import rank.Aggregation;
import rank.HyperbolicBordaCount;
import rank.LineBordaCount;
import rank.Stochastic;
import rank.Vote;

public class Main {

	public static void main(String[] args) {

		// Random rnd = new Random();
		//
		// int n = 100, m = 5, k = 1;
		// Permutation p = new Permutation(rnd, n);
		// Permutation[] q = new Permutation[m];
		//
		// Vote[] v = new Vote[m];
		//
		// for (int i = 0; i < m; i++) {
		// q[i] = p;
		// for (int j = 0; j < k; j++) {
		// q[i] = q[i].swap(rnd.nextInt(n), rnd.nextInt(n));
		// }
		// v[i] = new Vote(q[i], rnd.nextInt(m) + 1);
		// System.out.println(v[i]);
		// }
		//
		// Aggregation lbc = new LineBordaCount();
		// Aggregation hbc = new HyperbolicBordaCount();
		// Aggregation smc = new Stochastic();
		//
		// System.out.println(lbc.aggregate(v));
		// // System.out.println(hbc.aggregate(v));
		// System.out.println(smc.aggregate(v));

		BufferedImage canvas = new BufferedImage(320, 240, BufferedImage.TYPE_INT_RGB);
		for (int i = 0; i < 320; i++) {
			for (int j = 0; j < 240; j++) {
				if (i == 0 || j == 0 || i == 319 || j == 239) {
					canvas.setRGB(i, j, 0);
				} else {
					canvas.setRGB(i, j, (i + j + i * j) | 0x333333);
				}
			}
		}
		JFrame jf = new ImageViewer(canvas);
		jf.setVisible(true);
		jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

	}

}
