package perm;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
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

		return solve(other) != null;
	}

	public Permutation[] solve(SetOfPermutations other) {
		if (permutationLength != other.permutationLength)
			return null;
		if (size != other.size)
			return null;

		Permutation[] a = this.permutations;
		Permutation[] b = other.permutations;

		int n = size, m = permutationLength;

		if (n == 0 || m <= 1) {
			return new Permutation[] { new Permutation(n), new Permutation(m) };
		}

		Permutation q = b[0];

		if (n == 1) {
			return new Permutation[] { new Permutation(n), a[0].invert().product(q) };
		}

		for (Permutation p : a) {
			Permutation y = p.invert().product(q);

			Permutation[] c = new Permutation[n];
			Integer[] u = new Integer[n], v = new Integer[n];

			for (int i = 0; i < n; i++) {
				c[i] = a[i].product(y);
				v[i] = u[i] = i;
			}

			if (ord(c, b, 0, 0, n, m, u, v)) {
				Permutation x = new Permutation(v).invert().product(new Permutation(u));
				return new Permutation[] { x, y };
			}
		}

		return null;
	}

	private static class PermComp implements Comparator<Integer> {
		int radix;
		Permutation[] p;

		public int compare(Integer i, Integer j) {
			return Integer.compare(p[i].get(radix), p[j].get(radix));
		}
	}

	private static final PermComp pc = new PermComp();

	private static boolean ord(Permutation[] a, Permutation[] b, int radix, int from, int to, int m, Integer[] u, Integer[] v) {
		if (to <= from) {
			return true;
		}

		if (m <= radix) {
			return true;
		}

		pc.radix = radix;

		pc.p = a;
		Arrays.sort(u, from, to, pc);

		pc.p = b;
		Arrays.sort(v, from, to, pc);

		for (int i = from; i < to; i++) {
			if (a[u[i]].get(radix) != b[v[i]].get(radix)) {
				return false;
			}
		}

		boolean ok = true;

		int l = from;

		while (ok && l < to) {
			int r = l;

			while (r < to && a[u[l]].get(radix) == a[u[r]].get(radix)) {
				++r;
			}
			ok &= ord(a, b, radix + 1, l, r, m, u, v);
			l = r;
		}

		return ok;
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
