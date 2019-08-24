package org.sonar.samples.java.checks;

import java.util.Collections;
import java.util.List;
import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.tree.ClassTree;
import org.sonar.plugins.java.api.tree.Tree;

@Rule(key = "AvoidLazyClass")
/**
 * To use subsctiption visitor, just extend the IssuableSubscriptionVisitor.
 */
public class AvoidLazyClass extends IssuableSubscriptionVisitor {

	@Override
	public List<Tree.Kind> nodesToVisit() {
		// Register to the kind of nodes you want to be called upon visit.
		return Collections.singletonList(Tree.Kind.CLASS);
	}

	@Override
	public void visitNode(Tree tree) {

		int methodNum = 0; //Set the initial value of the method number
		
		/*get trees from classtree
		 *if there is no method in it,and it is lazyclass*/
		ClassTree ct = (ClassTree) tree; 
		List<Tree> trees = ct.members();
		for (Tree target : trees) {
			if (target.is(Tree.Kind.METHOD)) {
				methodNum++;
				break;
			}
		}
		if(methodNum == 0) {
			reportIssue(ct, "Class has 0 method");
		}
		

	}

}
