import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Locale;

public class FMeasure {

	public static void main(String[] args) throws IOException {

		int n = 3;

		try (BufferedReader in = new BufferedReader(new InputStreamReader(System.in))) {
			String line;
			while ((line = in.readLine()) != null) {
				if (line.length() > 50) {
					double[][] cm = new double[n][n];
					int l = line.indexOf("[[");

					for (int i = 0; i < n; i++) {
						for (int j = 0; j < n; j++) {
							while (!('0' <= line.charAt(l) && line.charAt(l) <= '9')) {
								++l;
							}
							int r = l;
							while (('0' <= line.charAt(r) && line.charAt(r) <= '9')) {
								++r;
							}

							cm[i][j] = Integer.parseInt(line.substring(l, r));
							l = r;
						}
					}

					double arec = 0, apre = 0;
					for (int i = 0; i < n; i++) {
						double srec = 0, spre = 0;

						for (int j = 0; j < n; j++) {
							srec += cm[i][j];
							spre += cm[j][i];
						}

						if (cm[i][i] == 0.0) {
							srec += 1;
							spre += 1;
						}
						double rec = cm[i][i] / srec;
						double pre = cm[i][i] / spre;

						arec += rec / n;
						apre += pre / n;
					}

					double f = 2 * arec * apre / (arec + apre);

					System.out.printf(Locale.ENGLISH, "%.3f%n", f);

				}
			}
		}
	}

}
