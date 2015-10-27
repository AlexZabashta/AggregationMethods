import java.util.ArrayList;
import java.util.Random;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.SMO;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public class Test {

	public static void main(String[] args) throws Exception {

		int n = 256, m = 10, k = 4;

		ArrayList<Attribute> a = new ArrayList<Attribute>();
		for (int i = 0; i < m; i++) {
			a.add(new Attribute("atr" + i));
		}

		ArrayList<Attribute> b = new ArrayList<Attribute>();
		for (int i = 0; i < m; i++) {
			b.add(new Attribute("atr" + i));
		}

		ArrayList<String> c = new ArrayList<String>();
		for (int i = 0; i < k; i++) {
			c.add("class" + i);
		}

		Attribute clazz = new Attribute("class", c);

		Random rnd = new Random();

		a.add(clazz);
		Instances instances = new Instances("testset", a, n);
		instances.setClass(clazz);

		for (int i = 0; i < n; i++) {

			int y = rnd.nextInt(k);

			Instance instance = new DenseInstance(m + 1);

			for (int j = 0; j < m; j++) {
				double feature = (j + y + 1) * rnd.nextDouble();

				instance.setValue(a.get(j), feature);
			}

			instance.setDataset(instances);

			instance.setClassValue(c.get(y));

			instances.add(instance);
		}

		for (int i = 0; i < 10; i++) {
			Evaluation evaluation = new Evaluation(instances);
			Classifier classifier = new SMO();
			evaluation.crossValidateModel(classifier, instances, 10, rnd);
			double[][] cm = evaluation.confusionMatrix();
			for (double[] da : cm) {
				for (double val : da) {
					System.out.printf("%2.0f ", val);
				}
				System.out.println();
			}
			System.out.println(evaluation.kappa());
		}
	}
}
