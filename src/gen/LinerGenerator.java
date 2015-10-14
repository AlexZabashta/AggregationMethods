package gen;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Stack;

import perm.Disagreement;
import perm.Metric;
import perm.Permutation;

public class LinerGenerator extends BufferedGenerator {

	private final double[] hidenValues;

	private Metric metric;
	private PermutationGenerator rpg;
	private double sigma = 1;
	public LinerGenerator(Metric metric, PermutationGenerator rpg, int bufferSize) {
		super(bufferSize);
		this.metric = metric;
		this.rpg = rpg;
		hidenValues = rpg.hidenValues();
	}

	@Override
	public void fillBuffer(int permutationsInSet, int permutationLength, Stack<Disagreement> buffer, int bufferSize) {

		int n = permutationsInSet * bufferSize;
		final Permutation[] free = new Permutation[n];
		final double[] dist = new double[n];
		final Integer[] order = new Integer[n];

		for (int i = 0; i < n; i++) {
			free[i] = rpg.generate(permutationLength, sigma);
			dist[i] = metric.distance(free[0], free[i]);
			order[i] = i;
		}

		Arrays.sort(order, new Comparator<Integer>() {
			@Override
			public int compare(Integer i, Integer j) {
				return Double.compare(dist[i], dist[j]);
			}
		});

		for (int i = 0, j = 0; i < bufferSize; i++) {
			Permutation[] p = new Permutation[permutationsInSet];
			for (int k = 0; k < permutationsInSet; k++, j++) {
				p[k] = free[order[j]];
			}
			double[] hv = Arrays.copyOf(hidenValues, hidenValues.length + 3);
			hv[hv.length - 3] = 4.0;
			hv[hv.length - 2] = sigma;
			hv[hv.length - 1] = bufferSize;

			buffer.add(new Disagreement(hv, p));
		}

	}

	@Override
	public String toString() {
		return "LineGenerator(" + metric + ", " + rpg + ")";
	}
}
