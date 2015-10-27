package misc;

import java.util.ArrayList;
import java.util.List;

import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.SMO;
import weka.classifiers.lazy.IBk;
import weka.classifiers.meta.Bagging;
import weka.classifiers.meta.ClassificationViaRegression;
import weka.classifiers.meta.LogitBoost;
import weka.classifiers.rules.DecisionTable;
import weka.classifiers.trees.J48;

public class ClassifierCollection {

	public static List<Classifier> getClassifies() {

		ArrayList<Classifier> classifiers = new ArrayList<Classifier>();
		classifiers.add(new Bagging());
		// classifiers.add(new ClassificationViaClustering());
		classifiers.add(new DecisionTable());
		// classifiers.add(new Decorate());
		// classifiers.add(new END());
		classifiers.add(new IBk(24));
		classifiers.add(new J48());
		// classifiers.add(new KStar());
		classifiers.add(new LogitBoost());
		classifiers.add(new NaiveBayes());
		classifiers.add(new ClassificationViaRegression());
		// classifiers.add(new RotationForest());
		classifiers.add(new SMO());

		return classifiers;

	}
}
