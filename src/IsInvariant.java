import java.util.ArrayList;
import java.util.Random;

import perm.KendallTau;
import perm.Metric;
import perm.Permutation;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.SMO;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public class IsInvariant {

	public static void main(String[] args) throws Exception {

		int n = 3;
		int m = 5;

		Random random = new Random();
		Permutation s = Permutation.random(m, random);

		Permutation[] p = new Permutation[n];

		Metric mu = new KendallTau();

		for (int i = 0; i < n; i++) {
			p[i] = Permutation.random(m, random);
		}

		for (Permutation q : p) {
			System.out.println(q);
		}
		System.out.println();

		for (Permutation q : p) {
			System.out.println(q.product(s));
		}
		System.out.println();

		for (Permutation q : p) {
			System.out.println(s.product(q));
		}
		System.out.println();

	}
}
