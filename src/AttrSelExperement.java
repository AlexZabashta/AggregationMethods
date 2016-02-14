import fsel.EvalAndSearch;
import fsel.FeatureSelection;
import fsel.IdentitySelection;
import gen.*;
import rank.*;
import perm.*;
import miner.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import weka.core.*;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;
import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.ASSearch;
import weka.attributeSelection.BestFirst;
import weka.attributeSelection.CfsSubsetEval;
import weka.attributeSelection.CorrelationAttributeEval;
import weka.attributeSelection.GainRatioAttributeEval;
import weka.attributeSelection.GreedyStepwise;
import weka.attributeSelection.InfoGainAttributeEval;
import weka.attributeSelection.Ranker;
import weka.attributeSelection.ReliefFAttributeEval;
import weka.attributeSelection.WrapperSubsetEval;
import weka.classifiers.*;
import weka.classifiers.functions.SMO;
import weka.classifiers.lazy.IBk;
import static misc.MetricsCollection.getMetrics;
import static misc.ClassifierCollection.getClassifies;

public class AttrSelExperement {

	public static void main(String[] args) throws IOException {
		Random random = new Random();
		List<FeatureSelection> fsa = new ArrayList<FeatureSelection>();
		// fsa.add(new IdentitySelection());

		List<ASSearch> searchs = new ArrayList<ASSearch>();
		{
			Ranker ranker = new Ranker();
			ranker.setNumToSelect(15);
			searchs.add(ranker);

			// searchs.add( new GreedyStepwise());
			// searchs.add(new BestFirst());
		}

		List<ASEvaluation> evaluations = new ArrayList<ASEvaluation>();
		{
			evaluations.add(new CorrelationAttributeEval());
			evaluations.add(new GainRatioAttributeEval());
			evaluations.add(new InfoGainAttributeEval());
			evaluations.add(new ReliefFAttributeEval());
			
			// evaluations.add(new InfoGainAttributeEval());
			// WrapperSubsetEval wrapperSubsetEval = new WrapperSubsetEval();
			// wrapperSubsetEval.setClassifier(new SMO());
			// evaluations.add(wrapperSubsetEval);

			// evaluations.add(new CorrelationAttributeEval());
		}

		for (ASSearch search : searchs) {
			for (ASEvaluation evaluation : evaluations) {
				fsa.add(new EvalAndSearch(evaluation, search));
			}
		}

		File dataFolder = new File("data\\gen");

		for (FeatureSelection fs : fsa) {
			String resFolder = "results\\fsel\\" + fs.toString();
			File res = new File(resFolder);
			if (!res.exists()) {
				res.mkdirs();
			}

			for (File arffFile : dataFolder.listFiles()) {
				try (FileReader reader = new FileReader(arffFile)) {
					Instances allAtrInstances = new Instances(reader);

					if (allAtrInstances.numAttributes() < 10) {
						continue;
					}

					allAtrInstances.setClassIndex(0);

					Instances instances = fs.select(allAtrInstances);

					int s = instances.numClasses();
					System.out.println(resFolder);
					try (PrintWriter out = new PrintWriter(new File(resFolder + "\\" + arffFile.getName() + ".txt"))) {

						int na = instances.numAttributes();
						for (int i = 0; i < na; i++) {
							out.println(instances.attribute(i).name());
						}
						out.println();

						for (Classifier classifier : getClassifies()) {
							String curName = classifier.getClass().getSimpleName();
							out.println(curName);
							System.out.println("     " + curName);
							try {

								Evaluation evaluation = new Evaluation(instances);
								evaluation.crossValidateModel(classifier, instances, 10, random);
								double[][] cm = evaluation.confusionMatrix();
								for (double[] da : cm) {
									for (double val : da) {
										out.printf("%5.0f ", val);
									}
									out.println();
								}

								double arec = 0, apre = 0;
								for (int i = 0; i < s; i++) {
									double srec = 0, spre = 0;

									for (int j = 0; j < s; j++) {
										srec += cm[i][j];
										spre += cm[j][i];
									}

									if (cm[i][i] == 0.0) {
										srec += 1;
										spre += 1;
									}
									double rec = cm[i][i] / srec;
									double pre = cm[i][i] / spre;

									arec += rec / s;
									apre += pre / s;
								}

								double f = 2 * arec * apre / (arec + apre);

								out.printf(Locale.ENGLISH, "%.3f%n", f);
								out.printf(Locale.ENGLISH, "%.4f%n%.2f%%%n%n", evaluation.kappa(), (1 - evaluation.errorRate()) * 100);

							} catch (Exception e) {
								System.out.println(e.getMessage());
							}
						}
					}

				}
			}
		}
	}
}
