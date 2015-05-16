import gen.FisherYatesShuffle;
import gen.PermutationGenerator;
import gen.SameSigmaGenerator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import misc.IOUtils;
import misc.Painter;
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

public class ColorDataGenerator {

	static String filePath = "data" + File.separatorChar;

	public static void main(String[] args) throws IOException {

		int permInSet = 25;
		int permLength = 25;

		int maxIter = 1000000;

		int numberOfSets = 20;

		Metric mu = new CanberraDistance();

		Random rng = new Random();

		List<Metric> metrList = new ArrayList<Metric>();
		metrList.add(new CanberraDistance());
		metrList.add(new KendallTau());
		metrList.add(new LevenshteinDistance());
		metrList.add(new CayleyDistance());
		metrList.add(new LSquare());

		// PermutationGenerator permGen = new GaussGenerator(0.5, 0.05, rng);
		PermutationGenerator permGen = new FisherYatesShuffle(0.9, 0.05, rng);
		SameSigmaGenerator dsg = new SameSigmaGenerator(permGen, rng);

		List<Aggregation> aggregations = new ArrayList<Aggregation>();
		{

			aggregations.add(new BordaCount());
			aggregations.add(new PickAPerm(mu));
			aggregations.add(new CopelandScore());
			aggregations.add(new Stochastic());
		}

		int numberOfColors = aggregations.size();

		Painter painter = new Painter(aggregations, mu);

		List<Permutation[]>[] data = new List[numberOfColors];

		for (int i = 0; i < numberOfColors; i++) {
			data[i] = new ArrayList<Permutation[]>();
		}

		int[] colorSize = new int[numberOfColors];

		for (int curIter = 0, last = 0, minSize = 0; curIter < maxIter && minSize < numberOfSets; last = minSize, curIter++) {
			double sigma = rng.nextDouble();
			Permutation[] p = dsg.generate(permInSet, permLength, sigma);

			int color = painter.getColor(p, 0.0023);

			if (color == -1) {
				continue;
			}

			++colorSize[color];

			if (data[color].size() < numberOfSets) {
				data[color].add(p);
			} else {
				continue;
			}
			minSize = numberOfSets;
			for (List<Permutation[]> pl : data) {
				minSize = Math.min(minSize, pl.size());
			}

			if (last < minSize) {
				System.out.println(minSize);
			}
		}

		boolean empt = false;
		for (List<Permutation[]> pl : data) {
			if (pl.size() < numberOfSets) {
				empt = true;
			}
		}

		if (empt) {
			System.out.println(Arrays.toString(colorSize));
		} else {
			IOUtils.writeObjectToFile(filePath + dsg.getClass().getSimpleName() + ".obj", data);
			System.out.println("OK");

		}
	}

}
