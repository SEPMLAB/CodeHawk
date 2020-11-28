package org.codehawk.plugin.java.checks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.tree.BlockTree;
import org.sonar.plugins.java.api.tree.ClassTree;
import org.sonar.plugins.java.api.tree.MethodTree;
import org.sonar.plugins.java.api.tree.StatementTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.plugins.java.api.tree.VariableTree;

@Rule(key = "AvoidCyclicDependentModularization")
public class AvoidCyclicDependentModularization extends IssuableSubscriptionVisitor {

	private Map<String, ArrayList<String>> map = new HashMap<>();

	@Override
	public List<Tree.Kind> nodesToVisit() {
		// Register to the kind of nodes you want to be called upon visit.
		return Collections.singletonList(Tree.Kind.CLASS);
	}

	@Override
	public void visitNode(Tree tree) {
		ClassTree classTree = (ClassTree) tree;
		String className = classTree.simpleName().name();
		int classLine = classTree.openBraceToken().line();
		// System.out.println("ClassName: " + className + " ClassLine: " + classLine);

		ArrayList<String> varClass = new ArrayList<>(); // record
		for (Tree member : classTree.members()) {
			if (member.is(Tree.Kind.CONSTRUCTOR)) {
				MethodTree constructor = (MethodTree) member;
				BlockTree blockTree = constructor.block();
				for (StatementTree statementTree : blockTree.body()) {
					if (statementTree.is(Tree.Kind.VARIABLE)) {
						VariableTree constrVarClass = (VariableTree) statementTree;
						if (constrVarClass.type().symbolType().isClass()) {
							varClass.add(constrVarClass.type().symbolType().name());
						}
					}
				}
			}
			if (member.is(Tree.Kind.VARIABLE)) {
				VariableTree variableTree = (VariableTree) member;
				if (variableTree.type().symbolType().isClass()) {
					varClass.add(variableTree.type().symbolType().name());
				}
			}
		}

		map.put(className, varClass);

		if (checkCyclicDependent(className)) {
			addIssue(classLine, "Class has Cyclic Dependent");
		}
	}

	private boolean checkCyclicDependent(String className) {
		boolean cyclicDependent = false;
		for (String i : map.get(className)) {
			cyclicDependent = DFS(className, i);
		}
		return cyclicDependent;
	}

	private boolean DFS(String className, String tmpClass) {
		if (map.get(tmpClass) != null) {
			System.out.println("Classname: " + className + " DFS: " + tmpClass);
			for (String i : map.get(tmpClass)) {
				if (i.equals(className)) {
					System.out.println("Class has Cyclic Dependent");
					return true;
				} else {
					DFS(className, i);
				}
			}
		}
		return false;
	}
}
