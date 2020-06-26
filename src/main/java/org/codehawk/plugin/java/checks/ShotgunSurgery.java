package org.codehawk.plugin.java.checks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.JavaFileScannerContext;
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
import org.sonar.plugins.java.api.tree.StatementTree;
import org.sonar.plugins.java.api.tree.SwitchStatementTree;
import org.sonar.plugins.java.api.tree.SynchronizedStatementTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.plugins.java.api.tree.TryStatementTree;
import org.sonar.plugins.java.api.tree.VariableTree;
import org.sonar.plugins.java.api.tree.WhileStatementTree;

@Rule(key = "ShotgunSurgery")
public class ShotgunSurgery extends IssuableSubscriptionVisitor {
	private Map<String, Map<String, List<String>>> usedInMethod = new HashMap<>();
	private Map<String, Map<String, List<String>>> usedInVariable = new HashMap<>();
	private Map<String, Map<String, List<String>>> classCount = new HashMap<>();
	private Map<String, Map<String, List<String>>> methodCount = new HashMap<>();
	private Map<String, Map<MethodTree, JavaFileScannerContext>> location = new HashMap<>();
	private Map<String, List<String>> hasShowed = new HashMap<>();
	private List<List<String>> inHeritance = new ArrayList<>();
	private List<String> objectList = new ArrayList<>();
	private List<String> reusedList = new ArrayList<>();

	@Override
	public List<Tree.Kind> nodesToVisit() {
		return Collections.singletonList(Tree.Kind.CLASS);// check every class
	}

	@Override
	public void visitNode(Tree tree) {
		ClassTree classTree = (ClassTree) tree;
		if (classTree.superClass() != null) {
			putInheritance(classTree);
		}
		checkClassTree(classTree);
		comparedWithUsedInMethod();
		comparedWithUsedInVariable();
		showSmell();
	}

	private void putInheritance(ClassTree classTree) {// Establish an inheritance relationship
		List<String> innerInherit = new ArrayList<>();
		Boolean b = checkIsNewInheritance(classTree.simpleName().name(),
				classTree.superClass().symbolType().fullyQualifiedName());
		if (Boolean.FALSE.equals(b)) {
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

	private void checkClassTree(ClassTree classTree) {// Check each of class
		String className = null;
		if (classTree.simpleName() != null) {
			className = classTree.simpleName().name();
		}
		Map<String, List<String>> innerUsedInMethod = new HashMap<>();
		Map<String, List<String>> innerUsedInVariable = new HashMap<>();
		Map<MethodTree, JavaFileScannerContext> innerlocation = new HashMap<>();
		List<Tree> treeList = classTree.members();
		for (Tree tempTree : treeList) {// check every method (constructor) of each class
			if (tempTree.is(Tree.Kind.METHOD) || tempTree.is(Tree.Kind.CONSTRUCTOR)) {
				MethodTree methodTree = (MethodTree) tempTree;
				innerlocation.put(methodTree, context);
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

				case METHOD_INVOCATION:// get method invocation in each method
					MethodInvocationTree methodInvocationTree = (MethodInvocationTree) statementTree;
					checkMethodInvocationTree(methodInvocationTree);
					break;

				case VARIABLE:// get variable in each method
					VariableTree variableTree = (VariableTree) statementTree;
					checkVariableTree(variableTree);
					break;

				case EXPRESSION_STATEMENT:// get expression statement in each method
					ExpressionStatementTree expressionStatementTree = (ExpressionStatementTree) statementTree;
					checkExpressionTree(expressionStatementTree.expression());
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

				case TRY_STATEMENT:// get try_catch statement in each method
					TryStatementTree tryStatementTree = (TryStatementTree) statementTree;
					checkTryStatementTree(tryStatementTree);
					break;

				default:
					break;
			}
		}
	}

	// check each of method invocation
	private void checkMethodInvocationTree(MethodInvocationTree methodInvocationTree) {
		ExpressionTree expressionTree = methodInvocationTree.methodSelect();
		if (expressionTree.is(Tree.Kind.MEMBER_SELECT)) {
			MemberSelectExpressionTree memberSelectExpressionTree = (MemberSelectExpressionTree) expressionTree;
			if (memberSelectExpressionTree.expression().is(Tree.Kind.IDENTIFIER)) {
				IdentifierTree frontIdentifierTree = (IdentifierTree) memberSelectExpressionTree.expression();
				reusedList.add(checkIsObject(frontIdentifierTree.name()));
				reusedList.add(memberSelectExpressionTree.identifier().name());
			}
			checkExpressionTree(memberSelectExpressionTree.expression());
		}
	}

	private void checkVariableTree(VariableTree variableTree) {// check each of variable
		if (variableTree.initializer() != null && variableTree.initializer().is(Tree.Kind.NEW_CLASS)) {
			NewClassTree newClassTree = (NewClassTree) variableTree.initializer();
			if (newClassTree.identifier().is(Tree.Kind.IDENTIFIER)) {
				IdentifierTree identifierTree = (IdentifierTree) newClassTree.identifier();
				objectList.add(variableTree.simpleName().name());
				objectList.add(identifierTree.name());
			}
		} else if (variableTree.initializer() != null) {
			checkExpressionTree(variableTree.initializer());
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

	private void checkExpressionTree(ExpressionTree expressionTree) {
		if (expressionTree.is(Tree.Kind.METHOD_INVOCATION)) {
			MethodInvocationTree methodInvocationTree = (MethodInvocationTree) expressionTree;
			checkMethodInvocationTree(methodInvocationTree);
		} else if (expressionTree.is(Tree.Kind.MEMBER_SELECT)) {
			MemberSelectExpressionTree memberSelectExpressionTree = (MemberSelectExpressionTree) expressionTree;
			checkExpressionTree(memberSelectExpressionTree.expression());
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
			Map<String, List<String>> innerUsedInVariable, Map<MethodTree, JavaFileScannerContext> innerlocation) {
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
					String tempMethodName = entry2.getValue().get(i + 1);
					moveToClassCount(tempClassName, tempMethodName, className);
					moveToMethodCount(tempClassName, tempMethodName, className, methodName);
				}
			}
		}
	}

	// Calculate changing class
	private void comparedWithUsedInVariable() {
		for (Entry<String, Map<String, List<String>>> entry1 : usedInVariable.entrySet()) {
			String className = entry1.getKey();
			for (Entry<String, List<String>> entry2 : entry1.getValue().entrySet()) {
				for (int i = 0; i < entry2.getValue().size(); i += 2) {
					String tempClassName = entry2.getValue().get(i);
					String tempMethodName = entry2.getValue().get(i + 1);
					moveToClassCount(tempClassName, tempMethodName, className);
				}
			}
		}
	}

	// Store map of changing class quantity
	private void moveToClassCount(String tempClassName, String tempMethodName, String className) {
		Boolean b = checkInheritance(className, tempClassName);
		if (Boolean.TRUE.equals(b)) {
			if (!classCount.containsKey(tempClassName)) {
				Map<String, List<String>> innerClassCount = new HashMap<>();
				innerClassCount.put(tempMethodName, new ArrayList<>());
				classCount.put(tempClassName, innerClassCount);
			} else if (!classCount.get(tempClassName).containsKey(tempMethodName)) {
				classCount.get(tempClassName).put(tempMethodName, new ArrayList<>());
			}
			if (classCount.get(tempClassName).get(tempMethodName).indexOf(className) == -1) {
				classCount.get(tempClassName).get(tempMethodName).add(className);
			}
		}
	}

	// Store map of changing method quantity
	private void moveToMethodCount(String tempClassName, String tempMethodName, String className, String methodName) {
		Boolean b = checkInheritance(className, tempClassName);
		if (Boolean.TRUE.equals(b)) {
			if (!methodCount.containsKey(tempClassName)) {
				Map<String, List<String>> innerMethodCount = new HashMap<>();
				innerMethodCount.put(tempMethodName, new ArrayList<>());
				methodCount.put(tempClassName, innerMethodCount);
			} else if (!methodCount.get(tempClassName).containsKey(tempMethodName)) {
				methodCount.get(tempClassName).put(tempMethodName, new ArrayList<>());
			}
			if (methodCount.get(tempClassName).get(tempMethodName).indexOf(className + methodName) == -1) {
				methodCount.get(tempClassName).get(tempMethodName).add(className + methodName);
			}
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
		for (Entry<String, Map<String, List<String>>> entry1 : methodCount.entrySet()) {
			String className = entry1.getKey();
			for (Entry<String, List<String>> entry2 : entry1.getValue().entrySet()) {
				String methodName = entry2.getKey();
				for (Entry<String, Map<MethodTree, JavaFileScannerContext>> entry3 : location.entrySet()) {
					String className2 = entry3.getKey();
					for (Entry<MethodTree, JavaFileScannerContext> entry4 : entry3.getValue().entrySet()) {
						MethodTree methodtree = entry4.getKey();
						innerShowSmell(className, methodName, className2, methodtree, entry4.getValue());
					}
				}
			}
		}
	}

	// show bad smell inner part
	private void innerShowSmell(String className, String methodName, String className2, MethodTree methodtree,
			JavaFileScannerContext context) {
		String methodName2 = methodtree.simpleName().name();
		Boolean b = chcekConsistent(className, className2, methodName, methodName2)
				&& methodCount.get(className).get(methodName).size() > 7
				&& classCount.get(className).get(methodName).size() > 10;
		if (Boolean.TRUE.equals(b)) {// avoid repeating bad smell
			if (!hasShowed.containsKey(className)) {
				context.addIssue(methodtree.openParenToken().line(), this,
						"Code smell \"Shotgun Surgery\" occurred in method \"" + methodName + "\" !");
				hasShowed.put(className, new ArrayList<>());
			} else if (hasShowed.get(className).indexOf(methodName) == -1) {
				context.addIssue(methodtree.openParenToken().line(), this,
						"Code smell \"Shotgun Surgery\" occurred in method \"" + methodName + "\" !");
			}
			hasShowed.get(className).add(methodName);
		}
	}

	// avoid repeating bad smell
	private Boolean chcekConsistent(String className, String className2, String methodName, String methodName2) {
		return className.equals(className2) && methodName.equals(methodName2);
	}
}
