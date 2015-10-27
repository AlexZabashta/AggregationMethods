package misc;

import java.util.ArrayList;
import java.util.List;

import perm.CanberraDistance;
import perm.Disagreement;
import perm.LAbs;
import perm.LMax;
import perm.LSquare;
import perm.Metric;
import perm.CayleyDistance;
import perm.KendallTau;
import perm.LevenshteinDistance;
import perm.Permutation;
import rank.Aggregation;
import rank.BordaCount;
import weka.core.Attribute;
import weka.core.Instance;

public class FastMiner extends AttributeMiner {

	public Metric[] metric;
	public Attribute[][] attribute;

	public FastMiner(List<Metric> metrics) {
		metric = metrics.toArray(new Metric[0]);
		attribute = new Attribute[metric.length][6];

		for (int i = 0; i < attribute.length; i++) {
			String name = metric[i].toString();
			attribute[i][0] = new Attribute(name + "Min");
			attribute[i][1] = new Attribute(name + "Max");
			attribute[i][2] = new Attribute(name + "Mean");
			attribute[i][3] = new Attribute(name + "StandardDeviation");
			attribute[i][4] = new Attribute(name + "Skewness");
			attribute[i][5] = new Attribute(name + "Kurtosis");
		}
	}

	private static final int m = 5;

	public double[] mine(Permutation[] permutations) {
		double[] features = new double[m];
		int n = permutations.length;

		Permutation[] invper = new Permutation[n];
		for (int i = 0; i < n; i++) {
			invper[i] = permutations[i].invert();
		}

		StatisticalValue dist = new StatisticalValue();

		for (int i = 0; i < n; i++) {
			for (int j = i + 1; j < n; j++) {
				for (int k = 0; k < invper[i].length(); k++) {
					dist.add(Math.abs(invper[i].get(k) - invper[j].get(k)));
				}
			}
		}

		int fp = 0;

		features[fp++] = dist.getMean();
		features[fp++] = dist.getStandardDeviation();
		features[fp++] = dist.getMax();
		// features[fp++] = dist.getMin();
		features[fp++] = dist.getSkewness();
		features[fp++] = dist.getKurtosis();

		return features;
	}

	public int length() {
		return m;
	}

	@Override
	public ArrayList<Attribute> getAttributes() {
		ArrayList<Attribute> attributes = new ArrayList<>();
		for (Attribute[] subArray : attribute) {
			for (Attribute a : subArray) {
				attributes.add(a);
			}
		}
		return attributes;
	}

	Aggregation aggregation = new BordaCount();

	@Override
	public void mine(Instance instance, Disagreement disagreement) {
		StatisticalValue[] val = new StatisticalValue[metric.length];

		for (int i = 0; i < val.length; i++) {
			val[i] = new StatisticalValue();
		}

		Permutation p = aggregation.aggregate(disagreement);

		for (Permutation q : disagreement) {
			for (int i = 0; i < metric.length; i++) {
				val[i].add(metric[i].distance(p, q));
			}
		}

		for (int i = 0; i < metric.length; i++) {
			instance.setValue(attribute[i][0], val[i].getMin());
			instance.setValue(attribute[i][1], val[i].getMax());
			instance.setValue(attribute[i][2], val[i].getMean());
			instance.setValue(attribute[i][3], val[i].getStandardDeviation());
			instance.setValue(attribute[i][4], val[i].getSkewness());
			instance.setValue(attribute[i][5], val[i].getKurtosis());
		}
	}

}
