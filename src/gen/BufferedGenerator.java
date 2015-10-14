package gen;

import java.util.Stack;

import perm.Disagreement;

public abstract class BufferedGenerator implements DisagreementsGenerator {

	private Stack<Disagreement> buffer = new Stack<Disagreement>();

	private int bufferSize = 32;
	private int permutationsInSet, permutationLength;

	public BufferedGenerator(int bufferSize) {
		this.bufferSize = bufferSize;
	}

	public abstract void fillBuffer(int permutationsInSet, int permutationLength, Stack<Disagreement> buffer, int bufferSize);

	public Disagreement generate(int permutationsInSet, int permutationLength) {
		if (buffer.isEmpty() || this.permutationLength != permutationLength || this.permutationsInSet != permutationsInSet) {
			this.permutationLength = permutationLength;
			this.permutationsInSet = permutationsInSet;

			buffer.clear();
			buffer.ensureCapacity(bufferSize);

			fillBuffer(permutationsInSet, permutationLength, buffer, bufferSize);
		}

		return buffer.pop();
	}
}
