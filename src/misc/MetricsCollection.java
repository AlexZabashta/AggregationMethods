package misc;

import java.util.ArrayList;
import java.util.List;

import perm.CanberraDistance;
import perm.CayleyDistance;
import perm.KendallTau;
import perm.LAbs;
import perm.LMax;
import perm.LSquare;
import perm.LevenshteinDistance;
import perm.Metric;

public class MetricsCollection {
	public static List<Metric> getMetrics() {

		List<Metric> metrics = new ArrayList<>();
		{
			metrics.add(new CanberraDistance());

			metrics.add(new KendallTau());
			metrics.add(new CayleyDistance());
			metrics.add(new LevenshteinDistance());

			metrics.add(new LMax());
			metrics.add(new LAbs());
			metrics.add(new LSquare());
		}

		return metrics;
	}
}
