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

public class MergeDataSets {

	static String slash = File.separator;

	public static void main(String[] args) throws Exception {

		File dataFolder = new File("data" + slash + "gen");

		String type = "SeveralSwapsGenerator" + " ";

		List<Instances> dataSets = new ArrayList<Instances>();
		List<String> names = new ArrayList<String>();

		int n = 1000, m = 6, k = 0;

		for (File arffFile : dataFolder.listFiles()) {
			String name = arffFile.getName();

			if (!name.contains(type)) {
				continue;
			}

			name = name.replace(type, "").replace(".arff", "");

			try (FileReader reader = new FileReader(arffFile)) {
				Instances instances = new Instances(reader);
				dataSets.add(instances);
				++k;
				names.add(name);
			}
		}

		List<String> classNames = new ArrayList<String>();
		classNames.add("BordaCo0");
		classNames.add("Stochas1");
		classNames.add("Stochas2");
		classNames.add("Copelan3");
		classNames.add("LocalKe4");
		classNames.add("PickAPe5");
		Attribute classAttr = new Attribute("class", classNames);

		ArrayList<Attribute> attributes = new ArrayList<Attribute>();
		attributes.add(classAttr);

		for (int ds = 0; ds < k; ds++) {
			Instances instances = dataSets.get(ds);

			int na = instances.numAttributes();

			for (int aid = 1; aid < na; aid++) {
				Attribute attribute = new Attribute(names.get(ds) + instances.attribute(aid).name());
				attributes.add(attribute);
			}
		}

		Instances result = new Instances("testset", attributes, n * m);
		result.setClassIndex(0);

		for (int c = 0, i = 0; c < m; c++) {
			for (int q = 0; q < n; q++, i++) {
				Instance instance = new DenseInstance(attributes.size());
				instance.setDataset(result);
				instance.setClassValue(classNames.get(c));

				for (int ds = 0, p = 1; ds < k; ds++) {
					Instances instances = dataSets.get(ds);
					int na = instances.numAttributes();

					for (int aid = 1; aid < na; aid++, p++) {
						double val = instances.get(i).value(aid);
						instance.setValue(p, val);
					}
				}

				result.add(instance);
			}
		}
		try (PrintWriter out = new PrintWriter(new File(type.replace(" ", ".arff")))) {
			out.println(result);
		}

	}
}
