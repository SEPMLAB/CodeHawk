package org.codehawk.smell.modler;

import java.util.ArrayList;
import java.util.List;

import org.sonar.plugins.java.api.tree.ClassTree;
import org.sonar.plugins.java.api.tree.IdentifierTree;
import org.sonar.plugins.java.api.tree.MethodTree;
import org.sonar.plugins.java.api.tree.Modifier;
import org.sonar.plugins.java.api.tree.ModifierKeywordTree;
import org.sonar.plugins.java.api.tree.ReturnStatementTree;
import org.sonar.plugins.java.api.tree.StatementTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.plugins.java.api.tree.VariableTree;

public class DeficientEncapsulationDetector implements Detector {
	private int numOfPublicFields = 0;
	private List<String> getter = new ArrayList<>();

	@Override
	public boolean detect(Node node) {
		return false;
	}

	public void detect(ClassTree ct, DeficientEncapsulationNode node) {
		if (getModifier(ct) == Modifier.PUBLIC) {
			putInGetter(ct);
			checkClass(ct);
			if (numOfPublicFields > 0)
				showSmell(node);
		}
	}

	private void putInGetter(ClassTree classTree) {// put the return value of each method in the class into getter
		for (Tree tree : classTree.members()) {
			if (tree.is(Tree.Kind.METHOD)) {
				MethodTree methodTree = (MethodTree) tree;
				if (methodTree.block() != null) {
					innerPutInGetter(methodTree);
				}
			}
		}
	}

	private void innerPutInGetter(MethodTree methodTree) {//inner part of putInGetter method
		for (StatementTree statementTree : methodTree.block().body()) {
			if (statementTree.is(Tree.Kind.RETURN_STATEMENT)) {
				ReturnStatementTree returnStatementTree = (ReturnStatementTree) statementTree;
				if (returnStatementTree.expression().is(Tree.Kind.IDENTIFIER)) {
					IdentifierTree identifierTree = (IdentifierTree) returnStatementTree.expression();
					getter.add(identifierTree.name());
				}
			}
		}
	}

	private void checkClass(ClassTree ct) {// get each modifier is a public field and it is in getter
		for (Tree tree : ct.members()) {
			if (tree.is(Tree.Kind.VARIABLE)) {
				VariableTree variableTree = (VariableTree) tree;
				if (getModifier(variableTree) == Modifier.PUBLIC && getter.contains(variableTree.simpleName().name())) {
					numOfPublicFields++;
				}
			}
		}
	}

	private Modifier getModifier(Tree tree) {// get modifier of each variable or class
		Modifier modifier = Modifier.DEFAULT;
		if (tree.is(Tree.Kind.VARIABLE)) {
			VariableTree variableTree = (VariableTree) tree;
			for (ModifierKeywordTree modifierKeywordTree : variableTree.modifiers().modifiers()) {
				if (modifierKeywordTree.modifier() == Modifier.PUBLIC)
					modifier = Modifier.PUBLIC;
			}
		} else if (tree.is(Tree.Kind.CLASS)) {
			ClassTree classTree = (ClassTree) tree;
			for (ModifierKeywordTree modifierKeywordTree : classTree.modifiers().modifiers()) {
				if (modifierKeywordTree.modifier() == Modifier.PUBLIC)
					modifier = Modifier.PUBLIC;
			}
		}
		return modifier;
	}

	private void showSmell(DeficientEncapsulationNode node) {// show bad smell
		node.getContext().addIssue(node.getStartLine(), node.getCheck(),
				"Code smell \"Deficient Encapsulation\" occurred in Class \"" + node.getName() + "\" !");
	}
}