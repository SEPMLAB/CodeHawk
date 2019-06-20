package org.smell.astparser;

import org.smell.metricruler.Metric;
import org.sonar.plugins.java.api.tree.ExpressionTree;

public interface ASTTreeParser {
	public void parse(Metric metric , ExpressionTree expressionTree);
}