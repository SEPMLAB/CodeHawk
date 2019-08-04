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

		int methodNum = 0;
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
		
		/*MethodTree methodTree = (MethodTree) tree;  //rule4
		BlockTree bt = methodTree.block();
		List<StatementTree> trees = bt.body();
		for (Tree target : trees) {
			if (target.is(Tree.Kind.IF_STATEMENT)) {
				IfStatementTree ift = (IfStatementTree) target;
				StatementTree ift2 = ift.thenStatement();
				BlockTree bt2 = (BlockTree)ift2;
				List<StatementTree> trees2 = bt2.body();
				for (Tree target2 : trees2) {
					if (target2.is(Tree.Kind.IF_STATEMENT)) {
						IfStatementTree ift3 = (IfStatementTree) target2;
						reportIssue(ift3, "it is doubleif");
					}
				}
				//reportIssue(ift, "it is if");
			}
		}*/
		
		/*
		ClassTree classTree = (ClassTree) tree; //rule3
		List<Tree> trees = classTree.members();
		int num = 0 ;
		//VariableTree vt = null;
		//MethodTree mt  =null;
		for (Tree target : trees) {
			if (target.is(Tree.Kind.VARIABLE)) {
				VariableTree vt = (VariableTree) target;
				ModifiersTree modifiersOfVT = vt.modifiers();
				List<ModifierKeywordTree> modifiers = modifiersOfVT.modifiers();
				for (ModifierKeywordTree modifierKeyword : modifiers) {
					if (modifierKeyword.modifier().equals(Modifier.PUBLIC)) {
						num++;
					}
				}
			}
			else if (target.is(Tree.Kind.METHOD)) {
				MethodTree mt = (MethodTree) target;
				ModifiersTree modifiersOfVT = mt.modifiers();
				List<ModifierKeywordTree> modifiers = modifiersOfVT.modifiers();
				for (ModifierKeywordTree modifierKeyword : modifiers) {
					if (modifierKeyword.modifier().equals(Modifier.PUBLIC)) {
						num++;
					}
				}
			}
			
			if (num>=5) {
				reportIssue(target, "public variable should not use over five times");
			}
		}*/
		
		
		
		/*ClassTree classTree = (ClassTree) tree;  //rule2
		List<Tree> trees = classTree.members();
		for (Tree target : trees) {
			if (target.is(Tree.Kind.METHOD)) {
				MethodTree mt = (MethodTree) target;
				ModifiersTree modifiersOfVT = mt.modifiers();
				List<ModifierKeywordTree> modifiers = modifiersOfVT.modifiers();
				for (ModifierKeywordTree modifierKeyword : modifiers) {
					if (modifierKeyword.modifier().equals(Modifier.PUBLIC)) {
						reportIssue(mt, "variable should not be public");
					}
				}

			}
		}*/

		
		 /*MethodTree methodTree = (MethodTree) tree; //rule1
		 
		 IdentifierTree idt = methodTree.simpleName();
		 String name = idt.name();
		 
		 String message = "Method should not be begin with \"test \" "; 
		 String targetString = "test"; 
		 if (name.startsWith(targetString)) {
			 reportIssue(tree, message); 
		 }*/
	}

}
