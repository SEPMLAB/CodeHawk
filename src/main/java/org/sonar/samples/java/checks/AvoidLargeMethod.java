package org.sonar.samples.java.checks;

import java.util.Collections;
import java.util.List;
import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.tree.MethodTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.samples.java.functioningClass.treeLines;

@Rule(key = "AvoidLargeMethod")
/**
 * To use subsctiption visitor, just extend the IssuableSubscriptionVisitor.
 */
public class AvoidLargeMethod extends IssuableSubscriptionVisitor {

	@Override
	public List<Tree.Kind> nodesToVisit() {
		// Register to the kind of nodes you want to be called upon visit.
		return Collections.singletonList(Tree.Kind.METHOD);
	}

	@Override
	public void visitNode(Tree tree) {
		MethodTree mt = (MethodTree) tree;
		int line = treeLines.getMethodTreeLines(mt);
		int maxLine = treeLines.getMaxMethodLines();
		if (line > maxLine) {
			reportIssue(mt, "Your method is too big with" + line + "lines");
		}
	}
}
