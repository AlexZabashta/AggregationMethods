package misc;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;

import misc.MinCostFlow.Edge;
import perm.Metric;
import perm.Permutation;
import perm.Random;

public class FirstMinGenerator implements PermutationSetsGenerator {

	private Random rpg;

	private Metric metric;
	private int bufferSize;
	private int permutationsInSet, permutationLength;

	private Stack<Permutation[]> buffer;

	public FirstMinGenerator(int permutationsInSet, int permutationLength, Metric metric, java.util.Random rng) {
		this(permutationsInSet, permutationLength, metric, 128, new java.util.Random());
	}

	public FirstMinGenerator(int permutationsInSet, int permutationLength, Metric metric) {
		this(permutationsInSet, permutationLength, metric, new java.util.Random());
	}

	public FirstMinGenerator(int permutationsInSet, int permutationLength, Metric metric, int bufferSize) {
		this(permutationsInSet, permutationLength, metric, bufferSize, new java.util.Random());
	}

	public FirstMinGenerator(int permutationsInSet, int permutationLength, Metric metric, int bufferSize, java.util.Random rng) {

		if (permutationsInSet <= 1) {
			throw new IllegalArgumentException("Number of permutations in set = " + permutationsInSet + " <= 1");
		}

		this.permutationsInSet = permutationsInSet;
		this.permutationLength = permutationLength;
		this.metric = metric;
		this.bufferSize = bufferSize;
		this.rpg = new Random(rng);
		this.buffer = new Stack<Permutation[]>();
	}

	public void updateBuffer() {
		Permutation[][] p = new Permutation[bufferSize][permutationsInSet];

		for (int i = 0; i < bufferSize; i++) {
			p[i][0] = rpg.nextGaussian(permutationLength, 0.5);
		}

		int m = bufferSize * (permutationsInSet - 1);

		Permutation[] free = new Permutation[m];
		for (int i = 0; i < m; i++) {
			free[i] = rpg.nextGaussian(permutationLength, 0.5);
		}

		final Integer[][] order = new Integer[bufferSize][m];
		final double[][] dist = new double[bufferSize][m];

		for (int i = 0; i < bufferSize; i++) {
			for (int j = 0; j < m; j++) {
				order[i][j] = j;
				dist[i][j] = metric.distance(p[i][0], free[j]);
			}
			final int fi = i;
			Arrays.sort(order[i], new Comparator<Integer>() {
				public int compare(Integer x, Integer y) {
					return Double.compare(dist[fi][x], dist[fi][y]);
				}
			});
		}

		int[] pointer = new int[bufferSize];
		for (int k = 1; k < permutationsInSet; k++) {
			for (int i = 0; i < bufferSize; i++) {
				while (free[order[i][pointer[i]]] == null) {
					++pointer[i];
				}
				p[i][k] = free[order[i][pointer[i]]];
				free[order[i][pointer[i]]] = null;
			}
		}

		for (Permutation[] q : p) {
			buffer.add(q);
		}

	}

	public Permutation[] generate() {
		if (buffer.isEmpty()) {
			updateBuffer();
		}

		return buffer.pop();
	}

}
