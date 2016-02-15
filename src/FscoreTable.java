import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FscoreTable {

	static String type = "SeveralSwapsGenerator";

	static void add(List<List<String>> table, File file, String name) throws IOException {

		name = name + file.getName() + " ";

		if (file.isDirectory()) {
			for (File subFile : file.listFiles()) {
				add(table, subFile, name);
			}
		} else {
			if (name.contains(type)) {
				name = name.replace(type + " ", "").replace(".arff.txt", "").replace("fsel ", "");
				List<String> row = new ArrayList<>();
				row.add(name);

				try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
					String line;
					while ((line = reader.readLine()) != null) {
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
		String inFolder = "results/fsel";

		List<List<String>> table = new ArrayList<>();
		add(table, new File(inFolder), "");

		int w = 0;

		for (List<String> row : table) {
			w = Math.max(w, row.get(0).length() + 3);
		}

		for (List<String> row : table) {
			boolean b = true;
			for (String val : row) {
				if (b) {
					System.out.printf("%" + w + "s ", val);
					b = false;
				} else {
					System.out.printf("%" + 5 + "s ", val);
				}
			}
			System.out.println();

		}

	}
}
