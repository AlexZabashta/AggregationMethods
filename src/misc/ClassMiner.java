package misc;

import java.util.ArrayList;
import java.util.List;

import perm.Disagreement;
import perm.Permutation;
import rank.Aggregation;
import rank.LossFunction;
import weka.core.Attribute;
import weka.core.Instance;

public class ClassMiner extends AttributeMiner {

	final Attribute classAttribute;
	final List<Aggregation> aggregations = new ArrayList<Aggregation>();
	final LossFunction lossFunction;
	final List<String> classesNames = new ArrayList<String>();
	final int numberOfClasses;

	public ClassMiner(List<Aggregation> aggregations, LossFunction lossFunction) {
		this.aggregations.addAll(aggregations);
		this.lossFunction = lossFunction;
		numberOfClasses = aggregations.size();

		for (int c = 0; c < numberOfClasses; c++) {
			String name = aggregations.get(c).toString();
			classesNames.add(trimAttribute(name) + c);
		}

		classAttribute = new Attribute("class", classesNames);
	}

	public String getClassName(int index) {
		return classesNames.get(index);
	}

	public int getClassIndex(Disagreement disagreement) {
		return getClassIndex(disagreement, -1);
	}

	public int getClassIndex(Disagreement disagreement, double eps) {
		Permutation[] res = new Permutation[numberOfClasses];
		for (int c = 0; c < numberOfClasses; c++) {
			res[c] = aggregations.get(c).aggregate(disagreement);
		}
		return getClassIndex(disagreement, res, eps);
	}

	public int getClassIndex(Disagreement disagreement, Permutation[] res, double eps) {
		int classIndex = 0;
		double min1 = Double.POSITIVE_INFINITY, min2 = min1;

		if (numberOfClasses < 2) {
			return 0;
		}

		for (int c = 0; c < numberOfClasses; c++) {
			Permutation q = res[c];
			double currentLoss = lossFunction.getLoss(q, disagreement);

			if (currentLoss < min1) {
				min2 = min1;
				min1 = currentLoss;
				classIndex = c;
				continue;
			}

			if (currentLoss < min2) {
				min2 = currentLoss;
			}
		}

		if (min2 - min1 < eps) {
			return -1;
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
