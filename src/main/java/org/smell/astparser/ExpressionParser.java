package org.smell.astparser;

import org.smell.astmodeler.ClassNode;
import org.smell.metricruler.Metric;
abstract class ExpressionParser implements ASTTreeParser{
	protected ClassNode ownerClass;

	protected boolean notNull(Object object) {
		return object != null;	
	}
	
	protected boolean isFeatureEnvyMetrics(Metric metric) {
		return metric.is( Metric.Type.ATFD) || metric.is( Metric.Type.LAA) || metric.is( Metric.Type.FDP);
	}
}