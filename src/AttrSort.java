import fsel.EvalAndSearch;
import fsel.FeatureSelection;
import fsel.IdentitySelection;
import gen.*;
import rank.*;
import perm.*;
import miner.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import weka.core.*;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;
import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.ASSearch;
import weka.attributeSelection.BestFirst;
import weka.attributeSelection.CfsSubsetEval;
import weka.attributeSelection.ConsistencySubsetEval;
import weka.attributeSelection.CorrelationAttributeEval;
import weka.attributeSelection.GainRatioAttributeEval;
import weka.attributeSelection.GreedyStepwise;
import weka.attributeSelection.InfoGainAttributeEval;
import weka.attributeSelection.RankSearch;
import weka.attributeSelection.Ranker;
import weka.attributeSelection.ReliefFAttributeEval;
import weka.attributeSelection.SignificanceAttributeEval;
import weka.attributeSelection.WrapperSubsetEval;
import weka.classifiers.*;
import weka.classifiers.functions.SMO;
import weka.classifiers.lazy.IBk;
import static misc.MetricsCollection.getMetrics;
import static misc.ClassifierCollection.getClassifies;

public class AttrSort {

	static String slash = File.separator;

	public static void main(String[] args) throws Exception {

		String type = "SeveralSwapsGenerator";
		File arffFile = new File("data" + slash + "genm" + slash + type + ".arff");

		try (FileReader reader = new FileReader(arffFile)) {
			Instances instances = new Instances(reader);
			instances.setClassIndex(0);
			int n = instances.numAttributes();

			InfoGainAttributeEval eval = new InfoGainAttributeEval();
			eval.buildEvaluator(instances);

			double[] val = new double[n];
			Integer[] order = new Integer[n];

			for (int i = 0; i < n; i++) {
				order[i] = i;
				val[i] = eval.evaluateAttribute(i);
			}

			Arrays.sort(order, new Comparator<Integer>() {
				public int compare(Integer i, Integer j) {
					return Double.compare(val[j], val[i]);
				}
			});

			for (int i : order) {
				System.out.printf(Locale.ENGLISH,"%50s %.4f%n", instances.attribute(i).name(), val[i]);
			}
		}
	}
}
