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
import weka.attributeSelection.ConsistencySubsetEval;
import weka.attributeSelection.CorrelationAttributeEval;
import weka.attributeSelection.GainRatioAttributeEval;
import weka.attributeSelection.GreedyStepwise;
import weka.attributeSelection.InfoGainAttributeEval;
import weka.attributeSelection.RankSearch;
import weka.attributeSelection.Ranker;
import weka.attributeSelection.ReliefFAttributeEval;
import weka.attributeSelection.SignificanceAttributeEval;
import weka.attributeSelection.WrapperSubsetEval;
import weka.classifiers.*;
import weka.classifiers.functions.SMO;
import weka.classifiers.lazy.IBk;
import static misc.MetricsCollection.getMetrics;
import static misc.ClassifierCollection.getClassifies;

public class AttrSelExperement {

	static String slash = File.separator;

	public static void main(String[] args) throws Exception {
		Random random = new Random();
		List<FeatureSelection> fsa = new ArrayList<FeatureSelection>();
		// fsa.add(new IdentitySelection());

		{
			Ranker ranker = new Ranker();
			ranker.setOptions(new String[] { "-T", "0.65" });
			SignificanceAttributeEval significanceAttributeEval = new SignificanceAttributeEval();
			fsa.add(new EvalAndSearch(significanceAttributeEval, ranker));
		}
		{
			RankSearch rankSearch = new RankSearch();
			ConsistencySubsetEval consistencySubsetEval = new ConsistencySubsetEval();
			// fsa.add(new EvalAndSearch(consistencySubsetEval, rankSearch));
		}
		{
			RankSearch rankSearch = new RankSearch();
			CfsSubsetEval cfsSubsetEval = new CfsSubsetEval();
			// fsa.add(new EvalAndSearch(cfsSubsetEval, rankSearch));

		}
		List<ASSearch> searchs = new ArrayList<ASSearch>();
		{
			{
				Ranker ranker = new Ranker();
				ranker.setNumToSelect(15);
				searchs.add(ranker);
			}
			{
				Ranker ranker = new Ranker();
				ranker.setNumToSelect(10);
				searchs.add(ranker);
			}
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
				// fsa.add(new EvalAndSearch(evaluation, search));
			}
		}
		// fsa.add(new EvalAndSearch(new CfsSubsetEval(), new
		// GreedyStepwise()));
		// fsa.add(new EvalAndSearch(new CfsSubsetEval(), new BestFirst()));

		File dataFolder = new File("data" + slash + "gen");

		for (FeatureSelection fs : fsa) {
			String resFolder = "results" + slash + "fsel_fix_rnd" + slash + fs.toString();
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

					if (!arffFile.getName().contains("SeveralSwapsGenerator")) {
						continue;
					}

					allAtrInstances.setClassIndex(0);

					Instances instances = fs.select(allAtrInstances);

					int s = instances.numClasses();
					System.out.println(resFolder);
					try (PrintWriter out = new PrintWriter(new File(resFolder + slash + arffFile.getName() + ".txt"))) {

						int na = instances.numAttributes();
						for (int i = 0; i < na; i++) {
							out.println(instances.attribute(i).name());
						}
						out.println();
						System.out.println(arffFile.getName() + " " + na);

						for (Classifier classifier : getClassifies()) {
							String curName = classifier.getClass().getSimpleName();
							out.println(curName);
							System.out.println("     " + curName);
							try {

								Evaluation evaluation = new Evaluation(instances);
								evaluation.crossValidateModel(classifier, instances, 10, new Random(31415926L));
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
