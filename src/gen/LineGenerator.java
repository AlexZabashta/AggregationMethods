package gen;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Stack;

import perm.Metric;
import perm.Permutation;

public class LineGenerator extends BufferedGenerator {

	private Metric metric;
	private PermutationGenerator rpg;

	public LineGenerator(Metric metric, PermutationGenerator rpg, int bufferSize) {
		super(bufferSize);
		this.metric = metric;
		this.rpg = rpg;
	}

	public void fillBuffer(int permutationsInSet, int permutationLength, Stack<Permutation[]> buffer, int bufferSize) {

		int n = permutationsInSet * bufferSize;
		final Permutation[] free = new Permutation[n];
		final double[] dist = new double[n];
		final Integer[] order = new Integer[n];

		for (int i = 0; i < n; i++) {
			free[i] = rpg.generate(permutationLength, 1.0);
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
				p[k] = free[j];
			}
			buffer.add(p);
		}

	}
}
