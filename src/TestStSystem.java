import java.util.ArrayList;
import java.util.Random;

import gen.DisagreementsGenerator;
import gen.FisherYatesShuffle;
import gen.LineSigmaGenerator;
import gen.PermutationGenerator;
import perm.Disagreement;
import perm.Permutation;
import rank.Aggregation;
import rank.CopelandScore;
import rank.Stochastic;
import rank.MarkovChain;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.SMO;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public class TestStSystem {

	public static void main(String[] args) throws Exception {
		int n = 10, m = 10;
		Random random = new Random();

		PermutationGenerator pg = new FisherYatesShuffle(random);
		LineSigmaGenerator dg = new LineSigmaGenerator(pg, random);

		Disagreement d = dg.generate(n, m, 0, 1);

		Aggregation a = new MarkovChain(0);
		Aggregation b = new MarkovChain(1);
		Aggregation c = new MarkovChain(2);
		Aggregation v = new CopelandScore();

		for (Permutation p : d) {
			System.out.println(p);
		}

		System.out.println();

		System.out.println(a.aggregate(d));
		System.out.println(b.aggregate(d));
		System.out.println(c.aggregate(d));
		System.out.println(v.aggregate(d));

	}
}
