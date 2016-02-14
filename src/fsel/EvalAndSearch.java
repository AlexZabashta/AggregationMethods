package fsel;

import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.ASSearch;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;

public class EvalAndSearch implements FeatureSelection {

	final ASEvaluation evaluation;
	final ASSearch search;

	public EvalAndSearch(ASEvaluation evaluation, ASSearch search) {
		this.evaluation = evaluation;
		this.search = search;
	}

	public Instances select(Instances data) {
		try {
			AttributeSelection filter = new AttributeSelection();
			filter.setSearch(search);
			filter.setEvaluator(evaluation);
			filter.setInputFormat(data);
			return Filter.useFilter(data, filter);
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return data;
		}
	}

	@Override
	public String toString() {
		return evaluation.getClass().getSimpleName() + " " + search.getClass().getSimpleName();
	}

}
