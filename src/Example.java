import java.util.Arrays;

public class Example {
	public static void main(String[] args) {

		double[] a = { 1, 2, 2, 2, 3, 3 };
		double[] b = { 6, 5, 4, 3, 2, 1 };

		System.out.println(SoftRanking.distance(a, b));
		
		
		double[][] u = new double[3][];

		u[0] = new double[] { 1, 2, 3, 4, 5 };
		u[1] = new double[] { 1, 1, 1, 1, 1 };
		u[2] = new double[] { 1, 1, 1, 1, 1 };

		double[] m = new double[] { 0.0021, 1.6, 1.8, 2.7, 5.1 };

		double[] w = SoftRanking.aggregate(u);

		// System.out.println(Arrays.toString(w));

		System.out.println(SoftRanking.distance(m, w));

		System.out.println(Arrays.toString(m));
		System.out.println(Arrays.toString(w));

		for (double[] v : u) {
			// System.out.println(Arrays.toString(v));
			// System.out.println(Arrays.toString(w));
			// System.out.println(SoftRanking.distance(v, w));
			// System.out.println();
		}

	}
}
