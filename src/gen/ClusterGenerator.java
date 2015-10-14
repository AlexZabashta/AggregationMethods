package gen;

import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import misc.MinCostFlow;
import misc.MinCostFlow.Edge;
import perm.Disagreement;
import perm.Metric;
import perm.Permutation;

public class ClusterGenerator extends BufferedGenerator {

	private final double[] hidenValues;
	private Metric metric;
	private PermutationGenerator rpg;
	private double sigma = 1;

	public ClusterGenerator(Metric metric, PermutationGenerator rpg, int bufferSize) {
		super(bufferSize);
		this.metric = metric;
		this.rpg = rpg;
		hidenValues = rpg.hidenValues();
	}

	public void fillBuffer(int permutationsInSet, int permutationLength, Stack<Disagreement> buffer, int bufferSize) {
		int[] size = new int[bufferSize];
		Permutation[][] p = new Permutation[bufferSize][permutationsInSet];

		for (int i = 0; i < bufferSize; i++) {
			p[i][0] = rpg.generate(permutationLength, sigma);
			size[i] = 1;
		}

		int m = bufferSize * (permutationsInSet - 1);

		Permutation[] free = new Permutation[m];
		for (int i = 0; i < m; i++) {
			free[i] = rpg.generate(permutationLength, sigma);
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

			double[] hv = Arrays.copyOf(hidenValues, hidenValues.length + 3);
			hv[hv.length - 3] = 3.0;
			hv[hv.length - 2] = sigma;
			hv[hv.length - 1] = (1.0 * buffer.size()) / bufferSize;

			buffer.add(new Disagreement(hv, p[i]));
		}

	}

	@Override
	public String toString() {
		return "ClusterGenerator(" + metric + ", " + rpg + ")";
	}

}
