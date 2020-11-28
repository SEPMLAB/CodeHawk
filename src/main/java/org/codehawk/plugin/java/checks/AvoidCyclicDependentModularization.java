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

		ArrayList<String> varClass = new ArrayList<>(); // for recording variable class
		for (Tree member : classTree.members()) {
			if (member.is(Tree.Kind.CONSTRUCTOR)) { // if child tree is constructor
				MethodTree constructor = (MethodTree) member;
				BlockTree blockTree = constructor.block();
				for (StatementTree statementTree : blockTree.body()) {
					if (statementTree.is(Tree.Kind.VARIABLE)) { // check whether it is variableTree
						VariableTree constrVarClass = (VariableTree) statementTree;
						if (constrVarClass.type().symbolType().isClass()) { // and whether it is classTree
							if (!constrVarClass.type().symbolType().name().equals(classTree.simpleName().name())) {
								varClass.add(constrVarClass.type().symbolType().name()); // record
							}
						}
					}
				}
			}
			if (member.is(Tree.Kind.VARIABLE)) { // check is there any variableTree
				VariableTree variableTree = (VariableTree) member;
				if (variableTree.type().symbolType().isClass()) { // and check whether it is classTree
					if (!variableTree.type().symbolType().name().equals(classTree.simpleName().name())) {
						varClass.add(variableTree.type().symbolType().name());
					}
				}
			}
		}

		if (classTree.simpleName() != null) { // record className & it's variable classes
			String className = classTree.simpleName().name();
			map.put(className, varClass);

			for(int i = 0; i < map.size();i++) {
				System.out.println(map.get(i));
			}
			String dependentClass = checkCyclicDependent(className);
			if (dependentClass != null) {
				addIssue(classTree.openBraceToken().line(),
						"Class " + className + " has Cyclic Dependent with class " + dependentClass);
			}
		}
	}

	private String checkCyclicDependent(String className) {
		String cyclicDependentClass = null;
		for (String varClass : map.get(className)) {
			cyclicDependentClass = DFS(className, varClass);
		}
		return cyclicDependentClass;
	}

	private String DFS(String className, String tmpClass) {
		if (map.get(tmpClass) != null) {
			for (String varClass : map.get(tmpClass)) {
				if (className.equals(varClass)) {
					return tmpClass;
				} else if (!tmpClass.equals(varClass)) { // check to preventing class use itself in class
					return DFS(className, varClass);
				}
			}
		}
		return null;
	}
}
