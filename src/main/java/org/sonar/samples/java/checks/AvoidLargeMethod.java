package org.sonar.samples.java.checks;

import java.util.Collections;
import java.util.List;

import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.tree.MethodTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.samples.java.functioningClass.GetLines;

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
		
		//use the method of GetLines to find the number of MethodTreeLines
		MethodTree mt = (MethodTree) tree;
		int line = GetLines.getMethodTreeLines(mt);
		int maxLine = GetLines.getMaxMethodLines();
		if (line > maxLine) {
			reportIssue(mt, "Your method is too big with" + line + "lines");
		}
	}
}
