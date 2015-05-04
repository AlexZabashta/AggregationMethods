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

		perm.Random rpg = new perm.Random();

		for (double sigma = 0.1; sigma < 4; sigma += 0.1) {
			
//			System.out.println(rpg.nextGaussian(25, sigma));
		}
		System.out.println(rpg.nextGaussian(25, 0.5));

	}

}
