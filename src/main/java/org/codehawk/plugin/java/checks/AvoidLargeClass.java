package org.codehawk.plugin.java.checks;

import java.util.Collections;
import java.util.List;

import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.tree.ClassTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.codehawk.smell.metricruler.GetLines;

@Rule(key = "AvoidLargeClass")
/**
 * To use subsctiption visitor, just extend the IssuableSubscriptionVisitor.
 */
public class AvoidLargeClass extends IssuableSubscriptionVisitor {

	@Override
	public List<Tree.Kind> nodesToVisit() {
		// Register to the kind of nodes you want to be called upon visit.
		return Collections.singletonList(Tree.Kind.CLASS);
	}

	@Override
	public void visitNode(Tree tree) {

		//use the method of GetLines to find the number of ClassTreeLines
		ClassTree ct = (ClassTree) tree;
		int line = GetLines.getClassTreeLines(ct);
		int maxLine = GetLines.getMaxClassLines();
		if (line > maxLine) {
			reportIssue(ct, "Your class is too big with " + line + " lines");
		}

	}

}
