package fsel;

import weka.core.Instances;

public interface FeatureSelection {
	public Instances select(Instances data);
}
