package misc;

import java.io.Serializable;

public class StatisticalValue implements Serializable {

	private static final long serialVersionUID = 1L;

	private double min = Double.POSITIVE_INFINITY;
	private double max = Double.NEGATIVE_INFINITY;
	private double sum0, sum1, sum2, sum3, sum4;

	public void add(double d) {
		min = Math.min(min, d);
		max = Math.max(max, d);

		double val = 1.0;
		sum0 += val;
		val *= d;
		sum1 += val;
		val *= d;
		sum2 += val;
		val *= d;
		sum3 += val;
		val *= d;
		sum4 += val;
	}

	public void add(StatisticalValue sVal) {
		min = Math.min(min, sVal.min);
		max = Math.max(max, sVal.max);

		sum0 += sVal.sum0;
		sum1 += sVal.sum1;
		sum2 += sVal.sum2;
		sum3 += sVal.sum3;
		sum4 += sVal.sum4;
	}

	public StatisticalValue merge(StatisticalValue sVal) {
		StatisticalValue m = new StatisticalValue();
		m.add(this);
		m.add(sVal);
		return m;
	}

	public double getMax() {
		return max;
	}

	public double getMin() {
		return min;
	}

	public double getSumOfPows(int pow) {
		switch (pow) {
		case 0:
			return sum0;
		case 1:
			return sum1;
		case 2:
			return sum2;
		case 3:
			return sum3;
		case 4:
			return sum4;
		default:
			throw new IllegalArgumentException("The pow = " + pow + " is not in the interval from 0 to 4");
		}
	}

	public double getRawMoment(int momentNumber) {
		try {
			if (sum0 < 1) {
				return 0.0;
			} else {
				return getSumOfPows(momentNumber) / sum0;
			}
		} catch (IllegalArgumentException exception) {
			throw new IllegalArgumentException("The moment number = " + momentNumber + " is not in the interval from 0 to 4");
		}
	}

	public double getCentralMoment(int momentNumber) {
		if (sum0 < 1 || momentNumber == 0 || momentNumber == 1) {
			return 0.0;
		}

		double v1 = sum1 / sum0;
		double v2 = sum2 / sum0;
		if (momentNumber == 2) {
			return v2 - v1 * v1;
		}
		double v3 = sum3 / sum0;
		if (momentNumber == 3) {
			return v3 - 3 * v1 * v2 + 2 * v1 * v1 * v1;
		}
		double v4 = sum4 / sum0;
		if (momentNumber == 4) {
			v4 -= 4 * v1 * v3;
			v1 *= v1; // v1 = v1 ^ 2
			v4 += 6 * v1 * v2;
			v1 *= v1; // v1 = v1 ^ 4
			v4 -= 3 * v1;
			return v4;
		}
		throw new IllegalArgumentException("The moment number = " + momentNumber + " is not in the interval from 0 to 4");
	}

	public double getMean() {
		return getRawMoment(1);
	}

	public double getStandardDeviation() {
		return Math.sqrt(getCentralMoment(2));
	}

	public double getSkewness() {
		return getCentralMoment(3) / Math.pow(getCentralMoment(2), 1.5);
	}

	public double getKurtosis() {
		return getCentralMoment(4) / Math.pow(getCentralMoment(2), 1.5);
	}

	public void addToStringBuilde(StringBuilder sb) {
		sb.append(String.format("%11.5f", getRawMoment(1)));
		sb.append(' ');
		sb.append(String.format("%11.5f", getStandardDeviation()));

	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		addToStringBuilde(sb);
		return sb.toString();
	}

}
