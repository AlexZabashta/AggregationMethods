package gen;

import java.util.Stack;

import perm.Permutation;

public abstract class BufferedGenerator implements DataSetsGenerator {

	public BufferedGenerator(int bufferSize) {
		this.bufferSize = bufferSize;
	}

	private int bufferSize = 32;
	private int permutationsInSet, permutationLength;
	private Stack<Permutation[]> buffer = new Stack<Permutation[]>();

	public abstract void fillBuffer(int permutationsInSet, int permutationLength, Stack<Permutation[]> buffer, int bufferSize);

	public Permutation[] generate(int permutationsInSet, int permutationLength) {

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
