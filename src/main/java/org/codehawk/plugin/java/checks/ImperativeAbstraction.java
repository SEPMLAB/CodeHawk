package org.codehawk.plugin.java.checks;

import java.util.Collections;
import java.util.List;
import org.codehawk.smell.modler.ImperativeAbstractionDetector;
import org.codehawk.smell.modler.ImperativeAbstractionNode;
import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.tree.ClassTree;
import org.sonar.plugins.java.api.tree.Tree;

@Rule(key = "ImperativeAbstraction")
public class ImperativeAbstraction extends IssuableSubscriptionVisitor {

	@Override
	public List<Tree.Kind> nodesToVisit() {
		return Collections.singletonList(Tree.Kind.CLASS);// check every class
	}

	@Override
	public void visitNode(Tree tree) {
		ClassTree classTree = (ClassTree) tree;
		ImperativeAbstractionNode node = new ImperativeAbstractionNode(classTree.openBraceToken().line(),
				classTree.simpleName().name());
		ImperativeAbstractionDetector detector = new ImperativeAbstractionDetector();
		detector.detect(classTree);

		Boolean b = detector.check();
		if (Boolean.TRUE.equals(b)) {
			addIssue(node.getStartLine(),
					"Code smell \"Imperative Abstraction\" occurred in class \"" + node.getName() + "\" !");
		}
	}
}