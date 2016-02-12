package miner;

import java.util.ArrayList;
import java.util.List;

import misc.StatisticalValue;

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
import weka.core.Attribute;
import weka.core.Instance;

public class LMiner extends AttributeMiner {

	final public Attribute[] attribute = new Attribute[6];

	public LMiner() {
		attribute[0] = new Attribute("Min");
		attribute[1] = new Attribute("Max");
		attribute[2] = new Attribute("Mean");
		attribute[3] = new Attribute("StandardDeviation");
		attribute[4] = new Attribute("Skewness");
		attribute[5] = new Attribute("Kurtosis");
	}

	@Override
	public ArrayList<Attribute> getAttributes() {
		ArrayList<Attribute> attributes = new ArrayList<>();
		for (Attribute a : attribute) {
			attributes.add(a);
		}
		return attributes;
	}

	@Override
	public void mine(Instance instance, Disagreement disagreement) {
		StatisticalValue val = new StatisticalValue();

		for (int u = 0; u < disagreement.size; u++) {
			Permutation p = disagreement.get(u).invert();
			for (int v = u + 1; v < disagreement.size; v++) {
				Permutation q = disagreement.get(v).invert();
				for (int i = 0; i < disagreement.permutationLength; i++) {
					val.add(Math.abs(p.get(i) - q.get(i)));
				}
			}
		}

		instance.setValue(attribute[0], val.getMin());
		instance.setValue(attribute[1], val.getMax());
		instance.setValue(attribute[2], val.getMean());
		instance.setValue(attribute[3], val.getStandardDeviation());
		instance.setValue(attribute[4], val.getSkewness());
		instance.setValue(attribute[5], val.getKurtosis());

	}

}
