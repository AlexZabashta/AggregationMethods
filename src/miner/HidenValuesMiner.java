package miner;

import java.util.ArrayList;
import java.util.List;


import perm.Disagreement;
import weka.core.Attribute;
import weka.core.Instance;

public class HidenValuesMiner extends AttributeMiner {

	final int numberOfValues;
	final List<Attribute> attributes = new ArrayList<Attribute>();

	public HidenValuesMiner(int numberOfValues) {
		this.numberOfValues = numberOfValues;

		for (int i = 0; i < numberOfValues; i++) {
			attributes.add(new Attribute("hv" + i));
		}
	}

	@Override
	public ArrayList<Attribute> getAttributes() {
		ArrayList<Attribute> list = new ArrayList<Attribute>();
		list.addAll(attributes);
		return list;
	}

	@Override
	public void mine(Instance instance, Disagreement disagreement) {
		double[] hidenValues = disagreement.hidenValues();
		for (int i = 0; i < numberOfValues; i++) {
			if (i < hidenValues.length) {
				instance.setValue(attributes.get(i), hidenValues[i]);
			}
		}
	}

}
