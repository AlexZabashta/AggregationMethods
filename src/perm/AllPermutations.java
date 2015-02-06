package perm;

import java.util.Iterator;

public class AllPermutations implements Iterable<Permutation> {
	public final int length;

	public AllPermutations(int length) {
		this.length = length;
	}

	@Override
	public Iterator<Permutation> iterator() {
		return new Iterator<Permutation>() {

			private Permutation current = new Permutation(length);

			@Override
			public boolean hasNext() {
				return current != null;
			}

			@Override
			public Permutation next() {
				Permutation prev = current;
				current = current.next();
				return prev;
			}

		};
	}

}
