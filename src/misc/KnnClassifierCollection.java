package misc;

import java.util.ArrayList;

import weka.classifiers.Classifier;
import weka.classifiers.lazy.IBk;

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
