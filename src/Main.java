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
import misc.FirstMinGenerator;
import misc.Painter;
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
import rank.PickAPerm;
import rank.Stochastic;
import rank.Vote;

public class Main {

	public static void main(String[] args) throws IOException {

		java.util.Random rng = new java.util.Random();

		List<Metric> metrics = new ArrayList<Metric>();
		metrics.add(new CanberraDistance());
		metrics.add(new KendallTau());
		metrics.add(new LevenshteinDistance());
		metrics.add(new CayleyDistance());
		metrics.add(new LSquare());

		List<Aggregation> aggregations = new ArrayList<Aggregation>();

		for (double d = 1; d < 10; d += 2) {
			aggregations.add(new Stochastic(d));
		}

		for (Metric metric : metrics) {

			Painter painter = new Painter(aggregations, metric);
			PermutationSetsGenerator psg = new FirstMinGenerator(10, 25, metric, 128);

			System.out.println(Arrays.toString(painter.getColorDistribution(psg, 512)));

		}

	}
}
