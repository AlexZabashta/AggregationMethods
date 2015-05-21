import gen.FisherYatesShuffle;
import gen.PermutationGenerator;
import gen.SameSigmaGenerator;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import misc.AllMiner;
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
import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.ASSearch;
import weka.attributeSelection.AttributeSelection;
import weka.attributeSelection.CfsSubsetEval;
import weka.attributeSelection.GainRatioAttributeEval;
import weka.attributeSelection.GreedyStepwise;
import weka.attributeSelection.Ranker;
import weka.classifiers.Evaluation;
import weka.classifiers.meta.AttributeSelectedClassifier;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;
import weka.filters.Filter;

public class AttributeSelectionTest {

	final static String res = "results" + File.separator + AttributeSelectionTest.class.getSimpleName() + File.separator;

	public static void main(String[] args) throws Exception {

		int trainSetSzie = 128, testSetSize = 16;

		int permInSet = 25;
		int permLength = 25;

		int maxIter = 1000000;

		int numberOfSets = trainSetSzie + testSetSize;

		FeatureMiner miner = new AllMiner();

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

		int n = miner.length();
		// int n = 1;
		int m = aggregations.size();

		Painter painter = new Painter(aggregations, mu);

		List<double[]>[] features = new List[m];

		for (int i = 0; i < m; i++) {
			features[i] = new ArrayList<double[]>();
		}

		Permutation[][] debug = new Permutation[m][];

		int[] colorSize = new int[m];

		for (int curIter = 0, last = 0, minSize = 0; curIter < maxIter && minSize < numberOfSets; last = minSize, curIter++) {
			double sigma = rng.nextDouble();
			Permutation[] p = dsg.generate(permInSet, permLength, sigma);

			int color = painter.getColor(p, 0.0023);

			if (color == -1) {
				continue;
			}

			debug[color] = p;

			++colorSize[color];

			if (features[color].size() < numberOfSets) {
				features[color].add(miner.mine(p));
				// features[color].add(new double[] { sigma });
			} else {
				continue;
			}
			minSize = numberOfSets;
			for (List<double[]> fl : features) {
				minSize = Math.min(minSize, fl.size());
			}

			if (last < minSize) {
				System.out.println(minSize);
			}
		}

		boolean empt = false;
		for (List<double[]> fl : features) {
			if (fl.size() < numberOfSets) {
				empt = true;
			}
		}

		System.out.println(Arrays.toString(colorSize));

		if (empt) {
			System.out.println("empt");
			return;
		}

		String[] cn = new String[m];
		for (int i = 0; i < m; i++) {
			cn[i] = aggregations.get(i).getClass().getSimpleName() + i;
		}
		FastVector fvWekaAttributes = new FastVector(n + 1);

		for (int i = 0; i < n; i++) {
			fvWekaAttributes.addElement(new Attribute("atr" + i));
		}

		FastVector fvClassVal = new FastVector(m);
		for (int i = 0; i < m; i++) {
			fvClassVal.addElement(cn[i]);
		}
		fvWekaAttributes.addElement(new Attribute("class_v", fvClassVal));

		Instances data = new Instances("R", fvWekaAttributes, numberOfSets);

		data.setClassIndex(n);

		for (int i = 0; i < m; i++) {
			for (int j = 0; j < numberOfSets; j++) {

				Instance instance = new Instance(n + 1);

				for (int k = 0; k < n; k++) {
					instance.setValue((Attribute) fvWekaAttributes.elementAt(k), features[i].get(j)[k]);
				}
				instance.setValue((Attribute) fvWekaAttributes.elementAt(n), cn[i]);

				data.add(instance);
			}
		}

		useClassifier(data);

		 useFilter(data);

		useLowLevel(data);

	}

	protected static void useClassifier(Instances data) throws Exception {
		System.out.println("\n1. Meta-classfier");
		AttributeSelectedClassifier classifier = new AttributeSelectedClassifier();
		CfsSubsetEval eval = new CfsSubsetEval();
		GreedyStepwise search = new GreedyStepwise();
		search.setSearchBackwards(true);
		J48 base = new J48();
		classifier.setClassifier(base);
		classifier.setEvaluator(eval);
		classifier.setSearch(search);

		
		Evaluation evaluation = new Evaluation(data);
		evaluation.crossValidateModel(classifier, data, 10, new Random());
		System.out.println(evaluation.toSummaryString());
	}

	/**
	 * uses the filter
	 */
	protected static void useFilter(Instances data) throws Exception {
	//	System.out.println("\n2. Filter");
		weka.filters.supervised.attribute.AttributeSelection filter = new weka.filters.supervised.attribute.AttributeSelection();
		CfsSubsetEval eval = new CfsSubsetEval();
		GreedyStepwise search = new GreedyStepwise();
		search.setSearchBackwards(true);
		filter.setEvaluator(eval);
		filter.setSearch(search);
		filter.setInputFormat(data);
		Instances newData = Filter.useFilter(data, filter);
//		System.out.println(newData);
	}

	/**
	 * uses the low level approach
	 */
	protected static void useLowLevel(Instances data) throws Exception {
		System.out.println("\n3. Low-level");
		AttributeSelection attsel = new AttributeSelection();
		// CfsSubsetEval eval = new CfsSubsetEval();
		// GreedyStepwise search = new GreedyStepwise();

		ASEvaluation eval = new GainRatioAttributeEval();
		ASSearch search = new Ranker();

		attsel.setEvaluator(eval);
		attsel.setSearch(search);
		attsel.SelectAttributes(data);
		int[] indices = attsel.selectedAttributes();
		System.out.println("selected attribute indices (starting with 0):\n" + Utils.arrayToString(indices));

		double[][] dd = attsel.rankedAttributes();

		for (int i = 0; i < dd.length; i++) {
			System.out.print(i + ":   ");
			for (int j = 0; j < dd[i].length; j++) {
				System.out.print(dd[i][j] + " ");
			}
			System.out.println();
		}
	}

	protected static void rankAtr(Instances data) throws Exception {

	}
}
