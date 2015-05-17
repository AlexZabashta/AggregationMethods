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

public class ClassifierCollection {

	public static ArrayList<Classifier> getClassifies() {
		ArrayList<Classifier> classifiers = new ArrayList<Classifier>();
		classifiers.add(new Bagging());
		classifiers.add(new ClassificationViaClustering());
		classifiers.add(new DecisionTable());
		//classifiers.add(new Decorate());
		classifiers.add(new END());
		classifiers.add(new IBk());
		classifiers.add(new J48());
		//classifiers.add(new KStar());
		classifiers.add(new LogitBoost());
		classifiers.add(new NaiveBayes());
		classifiers.add(new ClassificationViaRegression());
		classifiers.add(new RotationForest());
		classifiers.add(new SMO());

		return classifiers;
	}
}
