package perm;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class SetOfPermutations implements Serializable, Iterable<Permutation> {

	private static final long serialVersionUID = 1L;
	private final int hashCode;
	private final double[] params;
	private final Permutation[] permutations;
	public final int size, permutationLength;

	public SetOfPermutations(double[] params, Permutation[] permutations) {
		this.params = params.clone();
		size = permutations.length;
		this.permutations = new Permutation[size];

		int n = -1;

		for (int i = 0; i < size; i++) {
			int m = permutations[i].length();
			if (n == -1) {
				n = m;
			}
			if (n != m) {
				throw new IllegalArgumentException("Permutations has different lengths.");
			}
			this.permutations[i] = permutations[i];
		}
		permutationLength = n;

		int[] positions = new int[permutationLength];

		for (int i = 0; i < size; i++) {
			for (int j = 0; j < permutationLength; j++) {
				positions[permutations[i].get(j)] += j;
			}
		}

		Arrays.sort(positions);
		hashCode = Arrays.hashCode(positions);
	}

	@Override
	public int hashCode() {
		return hashCode;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SetOfPermutations other = (SetOfPermutations) obj;
		if (hashCode != other.hashCode)
			return false;
		if (!Arrays.equals(params, other.params))
			return false;
		if (permutationLength != other.permutationLength)
			return false;
		if (!Arrays.equals(permutations, other.permutations))
			return false;
		if (size != other.size)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SetOfPermutations []";
	}

	public SetOfPermutations(Permutation... permutations) {
		this(new double[0], permutations);
	}

	public Permutation get(int index) {
		return permutations[index];
	}

	public Iterator<Permutation> iterator() {
		return new Iterator<Permutation>() {
			private int index = 0;

			public boolean hasNext() {
				return index < size;
			}

			public Permutation next() {
				if (index < size) {
					return permutations[index++];
				} else {
					throw new NoSuchElementException();
				}
			}

			public void remove() {
				throw new RuntimeException("You take iterator from immutable object.");
			}
		};
	}
}
