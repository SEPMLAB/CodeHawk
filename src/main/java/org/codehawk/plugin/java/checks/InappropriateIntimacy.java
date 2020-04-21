package org.codehawk.plugin.java.checks;

import java.util.Collections;
import java.util.List;
import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.tree.IdentifierTree;
import org.sonar.plugins.java.api.tree.MethodTree;
import org.sonar.plugins.java.api.tree.Tree;

@Rule(key = "InappropriateIntimacy")
/**
 * To use subsctiption visitor, just extend the IssuableSubscriptionVisitor.
 */
public class InappropriateIntimacy extends IssuableSubscriptionVisitor {

	@Override
	public List<Tree.Kind> nodesToVisit() {
		// Register to the kind of nodes you want to be called upon visit.
		return Collections.singletonList(Tree.Kind.METHOD);
	}

	@Override
	public void visitNode(Tree tree) {

		MethodTree mt = (MethodTree) tree;
		List<IdentifierTree> trees = mt.symbol().usages();
		System.out.println("use:" + mt.symbol().usages());
		System.out.println("num:" + trees.size());
		for (Tree target : trees) {
			while (target.parent() != null) {
				
				if(target.parent().is(Tree.Kind.CLASS)) {
					System.out.println("use in:" + target.parent());
					break;
				} else {
					target = target.parent();
				}		
			}
		}
//		if(methodNum == 0) {
//			reportIssue(ct, "Class has 0 method");
//		}
	}

}