package misc;

import java.util.ArrayList;

import perm.Disagreement;
import weka.core.Attribute;
import weka.core.Instance;

public abstract class AttributeMiner {

	public abstract ArrayList<Attribute> getAttributes();

	public abstract void mine(Instance instance, Disagreement disagreement);

	public static String trimAttribute(String name) {
		StringBuilder builder = new StringBuilder();

		for (int i = 0; i < name.length() && builder.length() < 7; i++) {
			char c = name.charAt(i);
			if ('a' <= c && c <= 'z' || 'A' <= c && c <= 'Z') {
				builder.append(c);
			}
		}
		return builder.toString();
	}
}
