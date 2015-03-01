package plot;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class PlotBuilder {
	public static BufferedImage simpleSplot(double[] d, int width, int height) {
		return simpleSplot(d, width, height, Color.BLACK, Color.WHITE);
	}

	static int getX(int width, int i, int n) {
		return ((width - 1) * i) / (n - 1);
	}

	static int getY(int height, double val, double max, double min) {
		int y = (int) (height * ((val - min) / (max - min)));
		if (y < 0) {
			return 0;
		}
		if (y >= height) {
			return height - 1;
		}
		return y;
	}

	public static BufferedImage simpleSplot(double[] d, int width, int height, Color foreground, Color background) {
		BufferedImage plot = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics g = plot.getGraphics();

		g.setColor(background);
		g.fillRect(0, 0, width, height);

		int n = d.length;

		double min = Double.POSITIVE_INFINITY, max = -min;
		for (double v : d) {
			min = Math.min(min, v);
			max = Math.max(max, v);
		}

		g.setColor(Color.BLACK);

		for (int i = 1; i < n; i++) {
			int ax = getX(width, i - 1, n);
			int bx = getX(width, i - 0, n);

			int ay = getY(height, d[i - 1], max, min);
			int by = getY(height, d[i - 0], max, min);

			g.drawLine(ax, height - 1 - ay, bx, height - 1 - by);

		}

		return plot;
	}
}
