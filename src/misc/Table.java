package misc;

import java.io.PrintWriter;

public class Table {
	int n, m;

	boolean[] daRow, daColumn;

	String[][] data;

	public Table(int n, int m) {
		this.n = n;
		this.m = m;
		data = new String[n][m];
		daRow = new boolean[n];
		daColumn = new boolean[m];
	}

	public void set(int i, int j, String str) {
		data[i][j] = str;
	}

	public String get(int i, int j) {
		return data[i][j];
	}

	public void setDelimiterAfterRow(int row, boolean value) {
		daRow[row] = value;
	}

	public boolean getDelimiterAfterRow(int row) {
		return daRow[row];
	}

	public void setDelimiterAfterColumn(int column, boolean value) {
		daColumn[column] = value;
	}

	public boolean getDelimiterAfterColumn(int column) {
		return daColumn[column];
	}

	public void print(PrintWriter writer) {
		int[] w = new int[m];

		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {
				if (data[i][j] == null) {
					w[j] = Math.max(w[j], 4);
				} else {
					w[j] = Math.max(w[j], data[i][j].length());
				}
			}
		}

		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {
				writer.printf("%" + w[j] + "s ", data[i][j]);
				if (daColumn[j]) {
					writer.print('|');
					if (j != m - 1) {
						writer.print(' ');
					}
				}
			}

			writer.println();
			if (daRow[i]) {
				for (int j = 0; j < m; j++) {
					for (int d = w[j]; 0 <= d; --d) {
						writer.print('-');
					}
					if (daColumn[j]) {
						writer.print('+');
						if (j != m - 1) {
							writer.print('-');
						}
					}
				}
				writer.println();
			}

		}

	}

}
