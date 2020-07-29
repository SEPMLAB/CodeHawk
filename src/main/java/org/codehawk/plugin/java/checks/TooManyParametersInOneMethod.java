package org.codehawk.plugin.java.checks;

import java.util.Collections;
import java.util.List;
import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.tree.ClassTree;
import org.sonar.plugins.java.api.tree.MethodTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.plugins.java.api.tree.VariableTree;

@Rule(key = "TooManyParametersInOneMethod")

public class TooManyParametersInOneMethod extends IssuableSubscriptionVisitor {

	@Override
	public List<Tree.Kind> nodesToVisit() {
		return Collections.singletonList(Tree.Kind.CLASS);// check every class
	}

	@Override
	public void visitNode(Tree tree) {
		ClassTree classTree = (ClassTree) tree;
		List<Tree> treeList = classTree.members();
		for (Tree tempTree : treeList) {
			// check every method(constructor) in this class
			if (tempTree.is(Tree.Kind.METHOD) || tempTree.is(Tree.Kind.CONSTRUCTOR)) {
				MethodTree methodTree = (MethodTree) tempTree;
				List<VariableTree> list = methodTree.parameters();
				if (!list.isEmpty() && list.size() >= 10) {// if this method has >= 10 parameters
					reportIssue(methodTree, "There are too many parameters in this method !");// show this smell
				}
			}
		}
	}
}