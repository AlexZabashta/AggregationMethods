package misc;

import java.util.ArrayList;

import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.Logistic;
import weka.classifiers.functions.SMO;
import weka.classifiers.lazy.IBk;
import weka.classifiers.lazy.KStar;
import weka.classifiers.meta.AdaBoostM1;
import weka.classifiers.meta.Bagging;
import weka.classifiers.meta.ClassificationViaClustering;
import weka.classifiers.meta.ClassificationViaRegression;
import weka.classifiers.meta.Decorate;
import weka.classifiers.meta.END;
import weka.classifiers.meta.LogitBoost;
import weka.classifiers.meta.MultiScheme;
import weka.classifiers.meta.RotationForest;
import weka.classifiers.meta.Stacking;
import weka.classifiers.meta.Vote;
import weka.classifiers.rules.DecisionTable;
import weka.classifiers.rules.OneR;
import weka.classifiers.trees.DecisionStump;
import weka.classifiers.trees.J48;

public class KnnClassifierCollection {

	public static ArrayList<Classifier> getClassifies() {
		ArrayList<Classifier> classifiers = new ArrayList<Classifier>();

		classifiers.add(new IBk(1));
		classifiers.add(new IBk(2));
		classifiers.add(new IBk(4));
		classifiers.add(new IBk(8));
		classifiers.add(new IBk(16));
		classifiers.add(new IBk(24));
		classifiers.add(new IBk(32));
		classifiers.add(new IBk(48));
		classifiers.add(new IBk(64));

		return classifiers;
	}
}
