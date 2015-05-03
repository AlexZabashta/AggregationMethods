package misc;

import java.util.List;
import java.util.Stack;

import misc.MinCostFlow.Edge;
import perm.Metric;
import perm.Permutation;
import perm.Random;

public class ClusterGenerator implements PermutationSetsGenerator {

	private Random rpg;

	private Metric metric;
	private int bufferSize;
	private int permutationsInSet, permutationLength;

	private Stack<Permutation[]> buffer;

	public ClusterGenerator(int permutationsInSet, int permutationLength, Metric metric, java.util.Random rng) {
		this(permutationsInSet, permutationLength, metric, 128, new java.util.Random());
	}

	public ClusterGenerator(int permutationsInSet, int permutationLength, Metric metric) {
		this(permutationsInSet, permutationLength, metric, new java.util.Random());
	}

	public ClusterGenerator(int permutationsInSet, int permutationLength, Metric metric, int bufferSize) {
		this(permutationsInSet, permutationLength, metric, bufferSize, new java.util.Random());
	}

	public ClusterGenerator(int permutationsInSet, int permutationLength, Metric metric, int bufferSize, java.util.Random rng) {

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
		int[] size = new int[bufferSize];
		Permutation[][] p = new Permutation[bufferSize][permutationsInSet];

		for (int i = 0; i < bufferSize; i++) {
			p[i][0] = rpg.next(permutationLength);
			size[i] = 1;
		}

		int m = bufferSize * (permutationsInSet - 1);

		Permutation[] free = new Permutation[m];
		for (int i = 0; i < m; i++) {
			free[i] = rpg.next(permutationLength);
		}

		int start = 0, finish = bufferSize + m + 1;

		List<Edge>[] graph = MinCostFlow.createGraph(finish + 1);

		for (int i = 1; i <= bufferSize; i++) {
			MinCostFlow.addEdge(graph, start, i, permutationsInSet - 1, 0);
		}

		for (int j = 1; j <= m; j++) {
			for (int i = 1; i <= bufferSize; i++) {
				int cost = Math.round(m * (float) metric.distance(p[i - 1][0], free[j - 1]));
				MinCostFlow.addEdge(graph, i, j + bufferSize, 1, Math.max(cost, 0));
			}
			MinCostFlow.addEdge(graph, j + bufferSize, finish, 1, 0);
		}

		MinCostFlow.minCostFlow(graph, start, finish, bufferSize * (m + 1));

		for (int i = 0; i < bufferSize; i++) {
			for (Edge edge : graph[i + 1]) {
				if (edge.f == 1) {
					p[i][size[i]++] = free[edge.to - 1 - bufferSize];
				}
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
