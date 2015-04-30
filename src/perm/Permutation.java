package perm;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Random;

public class Permutation implements Comparable<Permutation>, Serializable {

	private static final long serialVersionUID = 1L;

	public static AllPermutations all(int length) {
		return new AllPermutations(length);
	}

	public static boolean nextPermutation(int[] permutation) {
		int n = permutation.length, a = n - 2;
		while (0 <= a && permutation[a] >= permutation[a + 1]) {
			a--;
		}
		if (a == -1) {
			return false;
		}

		int b = n - 1;
		while (permutation[b] <= permutation[a]) {
			b--;
		}

		swap(permutation, a, b);
		for (int i = a + 1, j = n - 1; i < j; i++, j--) {
			swap(permutation, i, j);
		}
		return true;
	}

	public static void swap(int[] array, int i, int j) {
		if (i == j)
			return;
		array[i] ^= array[j];
		array[j] ^= array[i];
		array[i] ^= array[j];
	}

	private final int hashCode;

	private final int[] permutation;

	public Permutation(int length) {
		permutation = new int[length];
		for (int i = 0; i < length; i++) {
			permutation[i] = i;
		}
		hashCode = Arrays.hashCode(this.permutation);
	}

	public Permutation(int... permutation) {
		int length = permutation.length;
		boolean[] unic = new boolean[length];
		this.permutation = new int[length];
		for (int i = 0; i < length; i++) {
			int j = permutation[i];
			if (j < 0 || length <= j || unic[j]) {
				throw new IllegalArgumentException("Error at permutation[" + i + "] = " + j);
			}
			this.permutation[i] = j;
			unic[j] = true;
		}
		hashCode = Arrays.hashCode(this.permutation);
	}

	public Permutation(Integer[] permutation) {
		int length = permutation.length;
		boolean[] unic = new boolean[length];
		this.permutation = new int[length];
		for (int i = 0; i < length; i++) {
			int j = permutation[i];
			if (j < 0 || length <= j || unic[j]) {
				throw new IllegalArgumentException("Error at permutation[" + i + "] = " + j);
			}
			this.permutation[i] = j;
			unic[j] = true;
		}
		hashCode = Arrays.hashCode(this.permutation);
	}

	@Override
	public int compareTo(Permutation permutation) {
		int cmp = Integer.compare(length(), permutation.length());
		if (cmp != 0) {
			return cmp;
		}

		int n = length();
		for (int i = 0; i < n; i++) {
			cmp = Integer.compare(get(i), permutation.get(i));
			if (cmp != 0) {
				return cmp;
			}
		}
		return 0;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Permutation other = (Permutation) obj;
		if (hashCode != other.hashCode)
			return false;
		return Arrays.equals(permutation, other.permutation);
	}

	public int get(int index) {
		return permutation[index];
	}

	@Override
	public int hashCode() {
		return hashCode;
	}

	public Permutation invert() {
		int length = permutation.length;
		int[] invert = new int[length];
		for (int i = 0; i < length; i++) {
			invert[permutation[i]] = i;
		}
		return new Permutation(invert);
	}

	public int length() {
		return permutation.length;
	}

	public Permutation next() {
		int[] permutation = this.permutation.clone();
		if (nextPermutation(permutation)) {
			return new Permutation(permutation);
		} else {
			return null;
		}
	}

	public Permutation product(Permutation permutation) {
		if (length() != permutation.length()) {
			throw new IllegalArgumentException("Permutations has different size.");
		}
		int n = length();
		int[] a = this.permutation, b = permutation.permutation, c = new int[n];

		for (int i = 0; i < n; i++) {
			c[i] = a[b[i]];
		}

		return new Permutation(c);
	}

	public Permutation swap(int i, int j) {
		int[] permutation = this.permutation.clone();
		swap(permutation, i, j);
		return new Permutation(permutation);
	}

	public int[] toArray() {
		return permutation.clone();
	}

	public int[][] toCycles() {
		int n = permutation.length, m = 0;
		int[] stack = new int[n * 2];
		boolean[] use = new boolean[n];
		for (int sp = 0, i = 0; i < n; i++) {
			if (!use[i]) {
				int s = sp++;
				int j = i;
				do {
					--stack[s];
					use[j] = true;
					stack[sp++] = j;
					j = permutation[j];
				} while (j != i);
				++m;
			}
		}

		int[][] cycles = new int[m][];
		for (int sp = 0, i = 0; i < m; i++) {
			cycles[i] = new int[-stack[sp++]];
			for (int j = 0; j < cycles[i].length; j++) {
				cycles[i][j] = stack[sp++];
			}
		}

		return cycles;
	}

	public int[] toInversions() {
		int n = permutation.length;
		int[] fenwickTree = new int[n], inversions = new int[n];

		for (int i = n - 1; i >= 0; i--) {
			for (int j = permutation[i]; 0 <= j; j = (j & (j + 1)) - 1) {
				inversions[i] += fenwickTree[j];
			}
			for (int j = permutation[i]; j < n; j |= j + 1) {
				fenwickTree[j] += 1;
			}
		}

		return inversions;
	}

	@Override
	public String toString() {
		return Arrays.toString(permutation);
	}

}
