import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class FscoreTable {

	static String[] type = { "FisherYatesShuffle", "GaussGenerator", "SeveralSwapsGenerator" };
	static int tid;

	static Map<String, Integer> freq = new HashMap<String, Integer>();

	static void add(List<List<String>> table, File file, String name) throws IOException {

		name = name + file.getName() + " ";

		if (file.isDirectory()) {
			for (File subFile : file.listFiles()) {
				add(table, subFile, name);
			}
		} else {
			if (name.contains(type[tid])) {
				name = name.replace(type[tid] + " ", "").replace(".arff.txt", "").replace("fsel ", "");
				List<String> row = new ArrayList<>();
				row.add(name);

				try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
					String line;
					while ((line = reader.readLine()) != null) {
						if (line.isEmpty()) {
							continue;
						} else {
							Integer cnt = freq.get(line);
							if (cnt == null) {
								cnt = 0;
							}
							freq.put(line, cnt + 1);
						}

						if (line.length() != 5 || line.equals("class")) {
							continue;
						}
						row.add(line.replace("0.", ""));
					}
				}

				table.add(row);
			}
		}
	}

	public static void main(String[] args) throws IOException {
		String inFolder = "results\\fsel_mdata";

		for (tid = 0; tid < 3; tid++) {

			int[] cid = { 1, 2, 5, 7, 8 };

			List<List<String>> table = new ArrayList<>();
			add(table, new File(inFolder), "");

			int w = 0;

			for (List<String> row : table) {
				w = Math.max(w, row.get(0).length() + 3);
			}

			for (List<String> row : table) {
				System.out.printf("%" + w + "s ", row.get(0));

				for (int i : cid) {
					System.out.printf(" & 0.%s", row.get(i));
				}
				System.out.println();

				// boolean b = true;
				//
				// int max = 0;
				// for (String val : row) {
				//
				// if (b) {
				//
				// b = false;
				// } else {
				// System.out.printf("%" + 5 + "s ", val);
				// max = Math.max(max, Integer.parseInt(val));
				// }
				// }
				// System.out.println("  " + max);

			}

			System.out.println();
			System.out.println();

			List<Entry<String, Integer>> entries = new ArrayList<Entry<String, Integer>>();
			entries.addAll(freq.entrySet());

			Collections.sort(entries, new Comparator<Entry<String, Integer>>() {

				@Override
				public int compare(Entry<String, Integer> x, Entry<String, Integer> y) {
					return y.getValue().compareTo(x.getValue());
				}
			});

			for (Entry<String, Integer> e : entries) {
				if (e.getValue() > 3) {
					//System.out.println(e.getKey() + "     " + e.getValue());
				}
			}
		}

	}
}
