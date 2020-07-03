package org.codehawk.plugin.java.checks;

import java.util.Collections;
import java.util.List;

import org.codehawk.smell.modler.DeficientEncapsulationDetector;
import org.codehawk.smell.modler.DeficientEncapsulationNode;
import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.tree.ClassTree;
import org.sonar.plugins.java.api.tree.Tree;

@Rule(key = "DeficientEncapsulation")
public class DeficientEncapsulation extends IssuableSubscriptionVisitor {

	@Override
	public List<Tree.Kind> nodesToVisit() {
		return Collections.singletonList(Tree.Kind.CLASS);// check every class
	}

	@Override
	public void visitNode(Tree tree) {
		ClassTree classTree = (ClassTree) tree;
		DeficientEncapsulationDetector detector = new DeficientEncapsulationDetector();
		DeficientEncapsulationNode node = new DeficientEncapsulationNode(classTree.openBraceToken().line(),
				classTree.simpleName().name(), context, this);
		detector.detect(classTree, node);
	}
}