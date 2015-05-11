import gen.ClusterGenerator;
import gen.DataSetsGenerator;
import gen.FisherYatesShuffle;
import gen.GaussGenerator;
import gen.LineGenerator;
import gen.PermutationGenerator;
import gen.SameSigmaGenerator;
import gen.SeveralSwapsGenerator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import misc.ClassifierCollection;
import misc.FeatureMiner;
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
import weka.classifiers.Classifier;

public class Main {

	public static void main(String[] args) throws IOException {

		int permInSet = 10;
		int permLength = 100;

		int maxIter = 100;

		Metric mu = new CanberraDistance();

		Random rng = new Random();

		List<Metric> metrList = new ArrayList<Metric>();
		metrList.add(new CanberraDistance());
		metrList.add(new KendallTau());
		metrList.add(new LevenshteinDistance());
		metrList.add(new CayleyDistance());
		metrList.add(new LSquare());

		List<PermutationGenerator> permGenList = new ArrayList<PermutationGenerator>();
		{
			permGenList.add(new FisherYatesShuffle(0.83, 0.05, rng));
			permGenList.add(new GaussGenerator(0.38, 0.05, rng));
			permGenList.add(new SeveralSwapsGenerator(0.83, 0.05, rng));
		}

		List<DataSetsGenerator> dataGenList = new ArrayList<DataSetsGenerator>();
		{

			for (PermutationGenerator permGen : permGenList) {
				dataGenList.add(new SameSigmaGenerator(permGen, rng));

				for (Metric metric : metrList) {
					dataGenList.add(new LineGenerator(metric, permGen, 2048));
					dataGenList.add(new ClusterGenerator(metric, permGen, 64));
				}
			}
		}

		List<Aggregation> aggregations = new ArrayList<Aggregation>();
		{
			//for (int p = 1; p <= 15; p++) {
			for (int p = 15; p >= 1; p--) {
				Stochastic st = new Stochastic(8.3);
				st.testPow = p;
				aggregations.add(st);

			}

			
			// aggregations.add(new BordaCount());
			// aggregations.add(new PickAPerm(mu));
			// aggregations.add(new CopelandScore());
			// aggregations.add(new Stochastic(10));
		}

		int m = aggregations.size();

		Painter painter = new Painter(aggregations, mu);

		for (DataSetsGenerator dsg : dataGenList) {

			int[] colorSize = new int[m];
			for (int curIter = 0; curIter < maxIter; curIter++) {
				Permutation[] p = dsg.generate(permInSet, permLength);

				int color = painter.getColor(p);
				++colorSize[color];
			}

			System.out.println(dsg + " " + Arrays.toString(colorSize));
		}
	}
}
