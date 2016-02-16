import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import weka.core.Instance;
import weka.core.Instances;

public class DrawHV {
	static String slash = File.separator;

	public static void main(String[] args) throws IOException {

		int w = 1024;
		int h = 1024;

		Random random = new Random();
		File dataFolder = new File("data" + slash + "gen");
		for (File arffFile : dataFolder.listFiles()) {
			if (!arffFile.getName().contains("HidenValuesMiner")) {
				continue;
			}
			try (FileReader reader = new FileReader(arffFile)) {
				Instances instances = new Instances(reader);
				instances.setClassIndex(0);
				int n = instances.numClasses();

				List<double[]> points = new ArrayList<double[]>();

				for (Instance instance : instances) {
					double[] point = new double[3];
					point[0] = instance.value(5);
					point[1] = instance.value(6);
					point[2] = instance.value(0);
					points.add(point);
				}

				Collections.shuffle(points, random);

				BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

				for (int x = 0; x < w; x++) {
					for (int y = 0; y < h; y++) {
						image.setRGB(x, y, -1);
					}
				}

				int r = 3;

				int[] color = { 0xFF0000, 0xFF8000, 0x00FF00, 0x00FFFF, 0x0000FF, 0xFF00E1 };

				for (double[] point : points) {
					int c = (int) point[2];

					int px = (int) (point[0] * (w - 1));
					int py = (int) (point[1] * (h - 1));

					for (int dx = -r; dx <= r; dx++) {
						for (int dy = -r; dy <= r; dy++) {
							if (Math.abs(dx) + Math.abs(dy) <= r) {
								int x = px + dx;
								int y = py + dy;
								if (0 <= x && x < w && 0 <= y && y < h) {
									image.setRGB(x, h - y - 1, color[c]);
								}

							}

						}
					}

				}

				for (int x = 0; x < w; x++) {
					image.setRGB(x, 1 - 1, 0);
					image.setRGB(x, h - 1, 0);
				}

				for (int y = 0; y < h; y++) {
					image.setRGB(1 - 1, y, 0);
					image.setRGB(w - 1, y, 0);
				}

				ImageIO.write(image, "png", new File(arffFile.getName() + ".png"));

			}
		}

	}
}
