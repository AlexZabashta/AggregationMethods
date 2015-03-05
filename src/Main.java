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

import misc.FeatureMiner;
import misc.SimpleMiner;
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

		Aggregation[] a = new Aggregation[] { new BordaCount(), new Stochastic() };

		for (Aggregation aggregation : a) {
			System.out.println(aggregation.aggregate(permutations));
		}

		System.out.println();
		FeatureMiner miner = new SimpleMiner();
		for (double f : miner.mine(permutations)) {
			System.out.println(f);
		}

	}
}
