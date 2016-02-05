package miner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import perm.Disagreement;
import perm.Permutation;
import rank.Aggregation;
import rank.LossFunction;
import weka.core.Attribute;
import weka.core.Instance;

public class ArrClassMiner extends AttributeMiner {

	final Attribute classAttribute;
	final List<Aggregation> aggregations = new ArrayList<Aggregation>();
	final LossFunction lossFunction;
	final List<String> classesNames = new ArrayList<String>();
	final int numberOfClasses;
	final double acdd;

	public ArrClassMiner(double acdd, List<Aggregation> aggregations, LossFunction lossFunction) {
		this.acdd = acdd;

		this.aggregations.addAll(aggregations);
		this.lossFunction = lossFunction;
		numberOfClasses = aggregations.size();

		for (int c = 0; c < numberOfClasses; c++) {
			String name = aggregations.get(c).toString();
			classesNames.add(trimAttribute(name) + c);
		}

		classAttribute = new Attribute("class", classesNames);
	}

	public double arr(double ai, double aj, double ti, double tj, double acdd) {
		double a = aj / ai;
		double t = ti / tj;

		return a / (1 + acdd * Math.log(t));
	}

	public String getClassName(int index) {
		return classesNames.get(index);
	}

	public int getClassIndex(Disagreement disagreement) {
		return getClassIndex(disagreement, acdd);
	}

	public int getClassIndex(Disagreement disagreement, double acdd) {
		double[] a = new double[numberOfClasses], t = new double[numberOfClasses];

		for (int c = 0; c < numberOfClasses; c++) {
			Aggregation aggregation = aggregations.get(c);

			long start = System.nanoTime();
			Permutation p = aggregation.aggregate(disagreement);
			long finish = System.nanoTime();

			t[c] = finish - start;
			a[c] = lossFunction.getLoss(p, disagreement);
		}

		double val = 0; // Double.POSITIVE_INFINITY;
		int classIndex = -1;

		for (int i = 0; i < numberOfClasses; i++) {

			double arr = 0;

			for (int j = 0; j < numberOfClasses; j++) {
				arr += arr(a[i], a[j], t[i], t[j], acdd);
			}

			if (arr > val) {
				val = arr;
				classIndex = i;
			}
		}

		return classIndex;
	}

	public ArrayList<Attribute> getAttributes() {
		ArrayList<Attribute> attributes = new ArrayList<Attribute>(1);
		attributes.add(classAttribute);
		return attributes;
	}

	public Attribute getClassAttributes() {
		return classAttribute;
	}

	public void mine(Instance instance, Disagreement disagreement) {
		int index = getClassIndex(disagreement);
		String name = getClassName(index);
		instance.setClassValue(name);
	}

}
