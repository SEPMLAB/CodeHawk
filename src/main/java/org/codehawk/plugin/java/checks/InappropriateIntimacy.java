package org.codehawk.plugin.java.checks;

import java.util.Collections;
import java.util.List;

import org.codehawk.smell.modler.PODetector;
import org.codehawk.smell.modler.POMethodNode;
import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.semantic.Type;
import org.sonar.plugins.java.api.tree.BlockTree;
import org.sonar.plugins.java.api.tree.ClassTree;
import org.sonar.plugins.java.api.tree.ExpressionTree;
import org.sonar.plugins.java.api.tree.IdentifierTree;
import org.sonar.plugins.java.api.tree.MethodTree;
import org.sonar.plugins.java.api.tree.Modifier;
import org.sonar.plugins.java.api.tree.ModifierKeywordTree;
import org.sonar.plugins.java.api.tree.ReturnStatementTree;
import org.sonar.plugins.java.api.tree.StatementTree;
import org.sonar.plugins.java.api.tree.Tree;

@Rule(key = "InappropriateIntimacy")
/**
 * To use subsctiption visitor, just extend the IssuableSubscriptionVisitor.
 */
public class InappropriateIntimacy extends IssuableSubscriptionVisitor {

	@Override
	public List<Tree.Kind> nodesToVisit() {
		// Register to the kind of nodes you want to be called upon visit.
		return Collections.singletonList(Tree.Kind.CLASS);
	}

	@Override
	public void visitNode(Tree tree) {
		String className;
		
		ClassTree ct = (ClassTree) tree;
		className = ct.simpleName().name();
		
		List<Tree> members = ct.members();
		for (Tree t : members) {
			if (t.is(Tree.Kind.METHOD)) {
				MethodTree mt = (MethodTree) t;
				BlockTree bt = mt.block();
				List<StatementTree> trees = bt.body();
				for (StatementTree target : trees) {
					if (target.is(Tree.Kind.RETURN_STATEMENT)) {
						ReturnStatementTree rst = (ReturnStatementTree) target;
						ExpressionTree expt = rst.expression();
						Type type = expt.symbolType();
						System.out.println(rst.returnKeyword().text());
						
					}
				}
			}
		}
			
		
//		MethodTree mt = (MethodTree) tree;
//		List<IdentifierTree> trees = mt.symbol().usages();
//		System.out.println("use:" + mt.symbol().usages());
//		System.out.println("num:" + trees.size());
//		for (Tree target : trees) {
//			while (target.parent() != null) {
//				
//				if(target.parent().is(Tree.Kind.CLASS)) {
//					System.out.println("use in:" + target.parent());
//					break;
//				} else {
//					target = target.parent();
//				}		
//			}
//		}

	}

}