package fsel;

import weka.core.Instances;

public class IdentitySelection implements FeatureSelection {
	public Instances select(Instances data) {
		return data;
	}

	@Override
	public String toString() {
		return "IdentitySelection";
	}

}
