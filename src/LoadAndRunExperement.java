import gen.*;
import rank.*;
import perm.*;
import miner.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.*;
import weka.core.*;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;
import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.ASSearch;
import weka.attributeSelection.CfsSubsetEval;
import weka.attributeSelection.GreedyStepwise;
import weka.classifiers.*;
import static misc.MetricsCollection.getMetrics;
import static misc.ClassifierCollection.getClassifies;

public class LoadAndRunExperement {

	protected static Instances useFilter(Instances data) {
		AttributeSelection filter = new AttributeSelection();
		ASEvaluation eval = new CfsSubsetEval();

		GreedyStepwise search = new GreedyStepwise();
		search.setSearchBackwards(true);

		filter.setEvaluator(eval);
		filter.setSearch(search);

		try {
			filter.setInputFormat(data);
			return Filter.useFilter(data, filter);
		} catch (Exception e) {
			return data;
		}
	}

	public static void main(String[] args) throws IOException {

		File dataFolder = new File("data/gen");

		for (File arffFile : dataFolder.listFiles()) {
			if (!arffFile.isFile()) {
				continue;
			}
			try (Reader reader = new FileReader(arffFile)) {
				Instances instances = new Instances(reader);
				instances.setClassIndex(0);			
				int s = instances.numClasses();
				
				
				
				
				System.out.println(arffFile.getName());				
				
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}

		}			
	}
}
