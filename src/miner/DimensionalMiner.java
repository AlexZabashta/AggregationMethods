package miner;

import java.util.ArrayList;
import java.util.List;

import perm.Disagreement;
import weka.core.Attribute;
import weka.core.Instance;

public class DimensionalMiner extends AttributeMiner {

	final List<Attribute> attributes = new ArrayList<Attribute>();

	public DimensionalMiner() {
		attributes.add(new Attribute("permutationsInSet"));
		attributes.add(new Attribute("permutationLength"));
	}

	@Override
	public ArrayList<Attribute> getAttributes() {
		ArrayList<Attribute> list = new ArrayList<Attribute>();
		list.addAll(attributes);
		return list;
	}

	@Override
	public void mine(Instance instance, Disagreement disagreement) {
		instance.setValue(attributes.get(0), disagreement.size);
		instance.setValue(attributes.get(1), disagreement.permutationLength);
	}

}
