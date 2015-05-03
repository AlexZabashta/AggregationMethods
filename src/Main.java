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

import misc.ClusterGenerator;
import misc.FeatureMiner;
import misc.PermutationSetsGenerator;
import misc.SimpleMiner;
import perm.CanberraDistance;
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
import rank.BordaCount;
import rank.BordaCount.DecreasingFunction;
import rank.CopelandScore;
import rank.CopelandScore;
import rank.Stochastic;
import rank.Vote;

public class Main {

	public static void main(String[] args) throws IOException {

		for (int n = 3; n < 50; n++) {
			PermutationSetsGenerator psg = new ClusterGenerator(10, 25, new KendallTau(), n);
			long time = System.currentTimeMillis();
			Permutation[] q = psg.generate();

			System.out.println(n + " " + (System.currentTimeMillis() - time) + " " + q[0]);

		}

		// for (int r = 0; r < 10; r++) {
		// Permutation[] q = psg.generate();
		//
		// for (Permutation p : q) {
		// System.out.println(p);
		// }
		//
		// System.out.println();
		//
		// }

	}
}
