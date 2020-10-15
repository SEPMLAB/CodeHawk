package org.codehawk.smell.modler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.sonar.plugins.java.api.JavaCheck;
import org.sonar.plugins.java.api.JavaFileScannerContext;
import org.sonar.plugins.java.api.tree.AssertStatementTree;
import org.sonar.plugins.java.api.tree.AssignmentExpressionTree;
import org.sonar.plugins.java.api.tree.BinaryExpressionTree;
import org.sonar.plugins.java.api.tree.BlockTree;
import org.sonar.plugins.java.api.tree.CaseGroupTree;
import org.sonar.plugins.java.api.tree.CatchTree;
import org.sonar.plugins.java.api.tree.ClassTree;
import org.sonar.plugins.java.api.tree.DoWhileStatementTree;
import org.sonar.plugins.java.api.tree.ExpressionStatementTree;
import org.sonar.plugins.java.api.tree.ExpressionTree;
import org.sonar.plugins.java.api.tree.ForEachStatement;
import org.sonar.plugins.java.api.tree.ForStatementTree;
import org.sonar.plugins.java.api.tree.IdentifierTree;
import org.sonar.plugins.java.api.tree.IfStatementTree;
import org.sonar.plugins.java.api.tree.LabeledStatementTree;
import org.sonar.plugins.java.api.tree.MemberSelectExpressionTree;
import org.sonar.plugins.java.api.tree.MethodInvocationTree;
import org.sonar.plugins.java.api.tree.MethodTree;
import org.sonar.plugins.java.api.tree.NewClassTree;
import org.sonar.plugins.java.api.tree.ReturnStatementTree;
import org.sonar.plugins.java.api.tree.StatementTree;
import org.sonar.plugins.java.api.tree.SwitchStatementTree;
import org.sonar.plugins.java.api.tree.SynchronizedStatementTree;
import org.sonar.plugins.java.api.tree.ThrowStatementTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.plugins.java.api.tree.TryStatementTree;
import org.sonar.plugins.java.api.tree.VariableTree;
import org.sonar.plugins.java.api.tree.WhileStatementTree;

public class ShotgunSurgeryDetector implements Detector {
	private static final int CM = 7;
	private static final int CC = 10;
	private Map<String, Map<String, List<String>>> usedInMethod = new HashMap<>();
	private Map<String, Map<String, List<String>>> usedInVariable = new HashMap<>();
	private Map<String, Map<String, List<String>>> classCount = new HashMap<>();
	private Map<String, Map<String, List<String>>> methodCount = new HashMap<>();
	private Map<String, Map<Tree, ShotgunSurgeryNode>> location = new HashMap<>();
	private Map<String, List<String>> hasShowed = new HashMap<>();
	private List<List<String>> inHeritance = new ArrayList<>();
	private List<String> objectList = new ArrayList<>();
	private List<String> reusedList = new ArrayList<>();

	@Override
	public boolean detect(Node node) {
		return false;
	}

	public void detect(ClassTree classTree, ShotgunSurgeryNode shotgunSurgeryNode) {
		if (classTree.superClass() != null) {
			putInheritance(classTree);
		}
		checkClassTree(classTree, shotgunSurgeryNode);
		comparedWithUsedInMethod();
		comparedWithUsedInVariable();
		showSmell();
	}

	private void putInheritance(ClassTree classTree) {// Establish an inheritance relationship
		Boolean b = checkIsNewInheritance(classTree.simpleName().name(), classTree.superClass().symbolType().name());
		if (Boolean.FALSE.equals(b)) {
			List<String> innerInherit = new ArrayList<>();
			innerInherit.add(classTree.superClass().symbolType().fullyQualifiedName());
			innerInherit.add(classTree.simpleName().name());
			inHeritance.add(innerInherit);
		}
	}

	private Boolean checkIsNewInheritance(String thisClass, String superClass) {// Check if it is a new inheritance
		for (int i = 0; i < inHeritance.size(); i++) {
			if (inHeritance.get(i).indexOf(thisClass) != -1 && inHeritance.get(i).indexOf(superClass) == -1) {
				inHeritance.get(i).add(superClass);
				return true;
			} else if (inHeritance.get(i).indexOf(superClass) != -1 && inHeritance.get(i).indexOf(thisClass) == -1) {
				inHeritance.get(i).add(thisClass);
				return true;
			}
		}
		return false;
	}

	private void checkClassTree(ClassTree classTree, ShotgunSurgeryNode shotgunSurgeryNode) {// Check each of class
		String className = null;
		if (classTree.simpleName() != null) {
			className = classTree.simpleName().name();
		}
		Map<String, List<String>> innerUsedInMethod = new HashMap<>();
		Map<String, List<String>> innerUsedInVariable = new HashMap<>();
		Map<Tree, ShotgunSurgeryNode> innerlocation = new HashMap<>();
		List<Tree> treeList = classTree.members();
		for (Tree tempTree : treeList) {// check every method (constructor) of each class
			if (tempTree.is(Tree.Kind.METHOD) || tempTree.is(Tree.Kind.CONSTRUCTOR)) {
				MethodTree methodTree = (MethodTree) tempTree;
				innerlocation.put(methodTree, shotgunSurgeryNode);
				if (methodTree.block() != null) {
					List<StatementTree> statementTreeList = methodTree.block().body();
					checkStatementTree(statementTreeList);
				}
				if (!reusedList.isEmpty()) {
					innerUsedInMethod.put(methodTree.simpleName().name(), new ArrayList<>());
					innerUsedInMethod.get(methodTree.simpleName().name()).addAll(reusedList);
				}
			} else if (tempTree.is(Tree.Kind.VARIABLE)) {// check every variable of each class
				VariableTree variableTree = (VariableTree) tempTree;
				innerlocation.put(variableTree, shotgunSurgeryNode);
				checkVariableTree(variableTree);
				if (!reusedList.isEmpty()) {
					innerUsedInVariable.put(variableTree.simpleName().name(), new ArrayList<>());
					innerUsedInVariable.get(variableTree.simpleName().name()).addAll(reusedList);
				}
			}
			reusedList.clear();
		}
		putInHashMap(className, innerUsedInMethod, innerUsedInVariable, innerlocation);
		objectList.clear();
	}

	private void checkStatementTree(List<StatementTree> list) {// check each of statement
		for (StatementTree statementTree : list) {
			switch (statementTree.kind()) {
				case VARIABLE:// get variable in each method
					VariableTree variableTree = (VariableTree) statementTree;
					checkVariableTree(variableTree);
					break;

				case EXPRESSION_STATEMENT:// get expression statement in each method
					ExpressionStatementTree expressionStatementTree = (ExpressionStatementTree) statementTree;
					checkExpressionTree(expressionStatementTree.expression());
					break;

				case RETURN_STATEMENT:// get return statement in each method
					ReturnStatementTree returnStatementTree = (ReturnStatementTree) statementTree;
					checkReturnStatementTree(returnStatementTree);
					break;

				case ASSERT_STATEMENT:// get assert statement in each method
					AssertStatementTree assertStatementTree = (AssertStatementTree) statementTree;
					checkExpressionTree(assertStatementTree.condition());
					break;

				case THROW_STATEMENT:// get throw statement in each method
					ThrowStatementTree throwStatementTree = (ThrowStatementTree) statementTree;
					checkExpressionTree(throwStatementTree.expression());
					break;

				case SWITCH_STATEMENT:// get switch statement in each method
					SwitchStatementTree switchStatementTree = (SwitchStatementTree) statementTree;
					checkExpressionTree(switchStatementTree.expression());
					List<CaseGroupTree> caseGroupTreeList = switchStatementTree.cases();
					for (CaseGroupTree caseGroupTree : caseGroupTreeList) {
						List<StatementTree> statementTreeList = caseGroupTree.body();
						checkStatementTree(statementTreeList);
					}
					break;

				case IF_STATEMENT:// get if statement in each method
					IfStatementTree ifStatementTree = (IfStatementTree) statementTree;
					checkExpressionTree(ifStatementTree.condition());
					checkInnerStatementTree(ifStatementTree.thenStatement());
					checkElseIfStatementTree(ifStatementTree);
					break;

				case WHILE_STATEMENT:// get while statement in each method
					WhileStatementTree whileStatementTree = (WhileStatementTree) statementTree;
					checkExpressionTree(whileStatementTree.condition());
					checkInnerStatementTree(whileStatementTree.statement());
					break;

				case FOR_STATEMENT:// get for statement in each method
					ForStatementTree forStatementTree = (ForStatementTree) statementTree;
					checkExpressionTree(forStatementTree.condition());
					checkInnerStatementTree(forStatementTree.statement());
					break;

				case DO_STATEMENT:// get do_while statement in each method
					DoWhileStatementTree doWhileStatementTree = (DoWhileStatementTree) statementTree;
					checkExpressionTree(doWhileStatementTree.condition());
					checkInnerStatementTree(doWhileStatementTree.statement());
					break;

				case FOR_EACH_STATEMENT:// get for_each statement in each method
					ForEachStatement forEachStatement = (ForEachStatement) statementTree;
					checkExpressionTree(forEachStatement.expression());
					checkInnerStatementTree(forEachStatement.statement());
					break;

				case LABELED_STATEMENT:// get labeled statement in each method
					LabeledStatementTree labeledStatementTree = (LabeledStatementTree) statementTree;
					List<StatementTree> labeledStatementTreeList = new ArrayList<>();
					labeledStatementTreeList.add(labeledStatementTree.statement());
					checkStatementTree(labeledStatementTreeList);
					break;

				case SYNCHRONIZED_STATEMENT:// get synchronized statement in each method
					SynchronizedStatementTree synchronizedStatementTree = (SynchronizedStatementTree) statementTree;
					checkExpressionTree(synchronizedStatementTree.expression());
					checkInnerBlockTree(synchronizedStatementTree.block());
					break;

				case TRY_STATEMENT:// get try statement in each method
					TryStatementTree tryStatementTree = (TryStatementTree) statementTree;
					checkTryStatementTree(tryStatementTree);
					break;

				default:// get other statement in each method
					break;
			}
		}
	}

	// check each of method invocation
	private void checkMethodInvocationTree(MethodInvocationTree methodInvocationTree) {
		ExpressionTree expressionTree = methodInvocationTree.methodSelect();
		if (expressionTree.is(Tree.Kind.MEMBER_SELECT)) {
			MemberSelectExpressionTree memberSelectExpressionTree = (MemberSelectExpressionTree) expressionTree;
			checkMemberSelectExpressionTree(memberSelectExpressionTree);
		}
	}

	// check each of method select expressionTree
	private void checkMemberSelectExpressionTree(MemberSelectExpressionTree memberSelectExpressionTree) {
		if (memberSelectExpressionTree.expression().is(Tree.Kind.IDENTIFIER)) {
			IdentifierTree identifierTree = (IdentifierTree) memberSelectExpressionTree.expression();
			reusedList.add(checkIsObject(identifierTree.name()));
			reusedList.add(memberSelectExpressionTree.identifier().name());
		}
	}

	private void checkVariableTree(VariableTree variableTree) {// check each of variable
		if (variableTree.initializer() != null) {
			if (variableTree.initializer().is(Tree.Kind.NEW_CLASS)) {
				NewClassTree newClassTree = (NewClassTree) variableTree.initializer();
				if (newClassTree.identifier().is(Tree.Kind.IDENTIFIER)) {
					IdentifierTree identifierTree = (IdentifierTree) newClassTree.identifier();
					objectList.add(variableTree.simpleName().name());
					objectList.add(identifierTree.name());
				}
			} else if (variableTree.initializer().is(Tree.Kind.METHOD_INVOCATION)) {
				MethodInvocationTree methodInvocationTree = (MethodInvocationTree) variableTree.initializer();
				checkMethodInvocationTree(methodInvocationTree);
			} else {
				checkExpressionTree(variableTree.initializer());
			}
		}
	}

	private void checkTryStatementTree(TryStatementTree tryStatementTree) {// check each of try_catah statement
		if (tryStatementTree.tryKeyword() != null) {// check try part of each try statement
			checkInnerBlockTree(tryStatementTree.block());
		}
		List<CatchTree> catchTreeList = tryStatementTree.catches();// check catch part of each try statement
		for (CatchTree catchtree : catchTreeList) {
			checkInnerBlockTree(catchtree.block());
		}
		if (tryStatementTree.finallyKeyword() != null) {
			checkInnerBlockTree(tryStatementTree.finallyBlock());// check finally part of each try statement
		}
	}

	// check else if and else parts of each if statement
	private void checkElseIfStatementTree(IfStatementTree ifStatementTree) {
		if (ifStatementTree.elseKeyword() != null) {
			StatementTree elseStatementTree = ifStatementTree.elseStatement();
			if (elseStatementTree.is(Tree.Kind.IF_STATEMENT)) {
				IfStatementTree elseifStatementTree = (IfStatementTree) elseStatementTree;
				checkElseIfStatementTree(elseifStatementTree);// check next part is else if statement or else statement
				checkExpressionTree(elseifStatementTree.condition());
				checkInnerStatementTree(elseifStatementTree.thenStatement());// check else if part of each if statement
			} else {
				checkInnerStatementTree(elseStatementTree);// check else part of each if statement
			}
		}
	}

	private void checkReturnStatementTree(ReturnStatementTree returnStatementTree) {// check each of return statement
		if (returnStatementTree.expression() != null) {
			checkExpressionTree(returnStatementTree.expression());
		}
	}

	private void checkInnerStatementTree(StatementTree statementTree) {// check inner statement of each statement
		if (statementTree.is(Tree.Kind.BLOCK)) {
			BlockTree blockTree = (BlockTree) statementTree;
			checkInnerBlockTree(blockTree);
		}
	}

	private void checkInnerBlockTree(BlockTree blockTree) {// check inner block of each block
		List<StatementTree> statementTreeList = blockTree.body();
		checkStatementTree(statementTreeList);
	}

	private void checkExpressionTree(ExpressionTree expressionTree) {// check each of expression
		if (expressionTree.is(Tree.Kind.METHOD_INVOCATION)) {
			MethodInvocationTree methodInvocationTree = (MethodInvocationTree) expressionTree;
			checkMethodInvocationTree(methodInvocationTree);
		} else if (expressionTree.is(Tree.Kind.MEMBER_SELECT)) {
			MemberSelectExpressionTree memberSelectExpressionTree = (MemberSelectExpressionTree) expressionTree;
			checkMemberSelectExpressionTree(memberSelectExpressionTree);
		} else if (expressionTree.is(Tree.Kind.ASSIGNMENT) || expressionTree.is(Tree.Kind.AND_ASSIGNMENT)
				|| expressionTree.is(Tree.Kind.DIVIDE_ASSIGNMENT) || expressionTree.is(Tree.Kind.LEFT_SHIFT_ASSIGNMENT)
				|| expressionTree.is(Tree.Kind.MINUS_ASSIGNMENT) || expressionTree.is(Tree.Kind.MULTIPLY_ASSIGNMENT)
				|| expressionTree.is(Tree.Kind.OR_ASSIGNMENT) || expressionTree.is(Tree.Kind.PLUS_ASSIGNMENT)
				|| expressionTree.is(Tree.Kind.REMAINDER_ASSIGNMENT)
				|| expressionTree.is(Tree.Kind.RIGHT_SHIFT_ASSIGNMENT)
				|| expressionTree.is(Tree.Kind.UNSIGNED_RIGHT_SHIFT_ASSIGNMENT)
				|| expressionTree.is(Tree.Kind.XOR_ASSIGNMENT)) {
			AssignmentExpressionTree assignmentExpressionTree = (AssignmentExpressionTree) expressionTree;
			checkExpressionTree(assignmentExpressionTree.variable());
			checkExpressionTree(assignmentExpressionTree.expression());
		} else if (expressionTree.is(Tree.Kind.EQUAL_TO) || expressionTree.is(Tree.Kind.GREATER_THAN_OR_EQUAL_TO)
				|| expressionTree.is(Tree.Kind.LESS_THAN_OR_EQUAL_TO) || expressionTree.is(Tree.Kind.NOT_EQUAL_TO)
				|| expressionTree.is(Tree.Kind.AND) || expressionTree.is(Tree.Kind.CONDITIONAL_AND)
				|| expressionTree.is(Tree.Kind.CONDITIONAL_OR) || expressionTree.is(Tree.Kind.DIVIDE)
				|| expressionTree.is(Tree.Kind.GREATER_THAN) || expressionTree.is(Tree.Kind.LEFT_SHIFT)
				|| expressionTree.is(Tree.Kind.LESS_THAN) || expressionTree.is(Tree.Kind.MINUS)
				|| expressionTree.is(Tree.Kind.MULTIPLY) || expressionTree.is(Tree.Kind.OR)
				|| expressionTree.is(Tree.Kind.PLUS) || expressionTree.is(Tree.Kind.REMAINDER)
				|| expressionTree.is(Tree.Kind.RIGHT_SHIFT) || expressionTree.is(Tree.Kind.UNSIGNED_RIGHT_SHIFT)
				|| expressionTree.is(Tree.Kind.XOR)) {
			BinaryExpressionTree binaryExpressionTree = (BinaryExpressionTree) expressionTree;
			checkExpressionTree(binaryExpressionTree.leftOperand());
			checkExpressionTree(binaryExpressionTree.rightOperand());
		}
	}

	// Check if it is an object
	private String checkIsObject(String name) {
		for (int i = 0; i + 1 < objectList.size(); i += 2) {
			if (name.equals(objectList.get(i))) {
				return objectList.get(i + 1);
			}
		}
		return name;
	}

	private void putInHashMap(String className, Map<String, List<String>> innerUsedInMethod,
			Map<String, List<String>> innerUsedInVariable, Map<Tree, ShotgunSurgeryNode> innerlocation) {
		if (className != null && !innerUsedInMethod.isEmpty()) {
			usedInMethod.put(className, innerUsedInMethod);
		}
		if (className != null && !innerUsedInVariable.isEmpty()) {
			usedInVariable.put(className, innerUsedInVariable);
		}
		if (className != null && !innerlocation.isEmpty()) {
			location.put(className, innerlocation);
		}
	}

	// Calculate changing method & changing class
	private void comparedWithUsedInMethod() {
		for (Entry<String, Map<String, List<String>>> entry1 : usedInMethod.entrySet()) {
			String className = entry1.getKey();
			for (Entry<String, List<String>> entry2 : entry1.getValue().entrySet()) {
				String methodName = entry2.getKey();
				for (int i = 0; i < entry2.getValue().size(); i += 2) {
					String tempClassName = entry2.getValue().get(i);
					String tempName = entry2.getValue().get(i + 1);
					moveToClassCount(tempClassName, tempName, className, methodName);
					moveToMethodCount(tempClassName, tempName, className, methodName);
				}
			}
		}
	}

	// Calculate changing class
	private void comparedWithUsedInVariable() {
		for (Entry<String, Map<String, List<String>>> entry1 : usedInVariable.entrySet()) {
			String className = entry1.getKey();
			for (Entry<String, List<String>> entry2 : entry1.getValue().entrySet()) {
				String variableName = entry2.getKey();
				for (int i = 0; i < entry2.getValue().size(); i += 2) {
					String className2 = entry2.getValue().get(i);
					String name = entry2.getValue().get(i + 1);
					moveToClassCount(className2, name, className, variableName);
				}
			}
		}
	}

	// Store map of changing class quantity
	private void moveToClassCount(String className2, String name2, String className, String name) {
		Boolean b = checkInheritance(className, className2);
		if (Boolean.TRUE.equals(b)) {
			if (!classCount.containsKey(className2)) {
				Map<String, List<String>> innerClassCount = new HashMap<>();
				classCount.put(className2, innerClassCount);
			}
			if (!classCount.get(className2).containsKey(name2))
				classCount.get(className2).put(name2, new ArrayList<>());
			if (classCount.get(className2).get(name2).indexOf(className + name) == -1)
				classCount.get(className2).get(name2).add(className + name);
		}
	}

	// Store map of changing method quantity
	private void moveToMethodCount(String className2, String name2, String className, String methodName) {
		Boolean b = checkInheritance(className, className2);
		if (Boolean.TRUE.equals(b)) {
			if (!methodCount.containsKey(className2)) {
				Map<String, List<String>> innerMethodCount = new HashMap<>();
				methodCount.put(className2, innerMethodCount);
			}
			if (!methodCount.get(className2).containsKey(name2))
				methodCount.get(className2).put(name2, new ArrayList<>());
			if (methodCount.get(className2).get(name2).indexOf(className + methodName) == -1)
				methodCount.get(className2).get(name2).add(className + methodName);
		}
	}

	// Check for inheritance
	private Boolean checkInheritance(String thisClass, String superClass) {
		for (int i = 0; i < inHeritance.size(); i++) {
			if (inHeritance.get(i).indexOf(thisClass) != -1 && inHeritance.get(i).indexOf(superClass) != -1) {
				return false;
			}
		}
		return true;
	}

	private void showSmell() {// show bad smell
		for (Entry<String, Map<String, List<String>>> entry1 : classCount.entrySet()) {
			String className = entry1.getKey();
			for (Entry<String, List<String>> entry2 : entry1.getValue().entrySet()) {
				String name = entry2.getKey();
				for (Entry<String, Map<Tree, ShotgunSurgeryNode>> entry3 : location.entrySet()) {
					String className2 = entry3.getKey();
					for (Entry<Tree, ShotgunSurgeryNode> entry4 : entry3.getValue().entrySet()) {
						Tree tree = entry4.getKey();
						ShotgunSurgeryNode node = entry4.getValue();
						innerShowSmell(className, name, className2, tree, node);
					}
				}
			}
		}
	}

	// show bad smell inner part
	private void innerShowSmell(String className, String name, String className2, Tree tree, ShotgunSurgeryNode node) {
		String name2 = "";
		int line = 0;
		if (tree.is(Tree.Kind.METHOD)) {
			MethodTree methodtree = (MethodTree) tree;
			name2 = methodtree.simpleName().name();
			line = methodtree.openParenToken().line();
		} else if (tree.is(Tree.Kind.VARIABLE)) {
			VariableTree variableTree = (VariableTree) tree;
			name2 = variableTree.simpleName().name();
			line = variableTree.endToken().line();
		}
		Boolean b = isEqual(className, className2, name, name2) && checkMethodCount(className, name);
		if (Boolean.TRUE.equals(b)) {
			JavaFileScannerContext context = node.getContext();
			JavaCheck check = node.getCheck();
			if (methodCount.get(className).get(name).size() > CM && classCount.get(className).get(name).size() > CC) {
				if (!hasShowed.containsKey(className))
					hasShowed.put(className, new ArrayList<>());
				if (hasShowed.get(className).indexOf(name) == -1) {
					context.addIssue(line, check, "Code smell \"Shotgun Surgery\" occurred in \"" + name + "\" !");
					hasShowed.get(className).add(name);
				}
			}
		}
	}

	// check if the methodCount contains the same className and name
	private Boolean checkMethodCount(String className, String name) {
		return methodCount.containsKey(className) && methodCount.get(className).containsKey(name);
	}

	// avoid showing repeated bad smell
	private Boolean isEqual(String className, String className2, String name, String name2) {
		return className.equals(className2) && name.equals(name2);
	}
}
