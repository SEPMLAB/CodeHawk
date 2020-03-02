package org.codehawk.plugin.java.checks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehawk.plugin.java.functioningClass.GetClass;
import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.tree.BinaryExpressionTree;
import org.sonar.plugins.java.api.tree.BlockTree;
import org.sonar.plugins.java.api.tree.CaseGroupTree;
import org.sonar.plugins.java.api.tree.ClassTree;
import org.sonar.plugins.java.api.tree.DoWhileStatementTree;
import org.sonar.plugins.java.api.tree.ExpressionTree;
import org.sonar.plugins.java.api.tree.ForEachStatement;
import org.sonar.plugins.java.api.tree.ForStatementTree;
import org.sonar.plugins.java.api.tree.IdentifierTree;
import org.sonar.plugins.java.api.tree.IfStatementTree;
import org.sonar.plugins.java.api.tree.MethodTree;
import org.sonar.plugins.java.api.tree.Modifier;
import org.sonar.plugins.java.api.tree.ModifierKeywordTree;
import org.sonar.plugins.java.api.tree.ModifiersTree;
import org.sonar.plugins.java.api.tree.StatementTree;
import org.sonar.plugins.java.api.tree.SwitchStatementTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.plugins.java.api.tree.TryStatementTree;
import org.sonar.plugins.java.api.tree.VariableTree;
import org.sonar.plugins.java.api.tree.WhileStatementTree;

@Rule(key = "refused bequest")
/**
 * To use subsctiption visitor, just extend the IssuableSubscriptionVisitor.
 */
public class RefusedBequest extends IssuableSubscriptionVisitor {
	// save two classNames which have Inheritance relationship
	private ArrayList<String> extendList = new ArrayList<String>();
	// save classTree in ExtendClassTree
	Map<String, ExtendClassTree> classList = new HashMap<String, ExtendClassTree>();
	private int classCount = 0;
	ExtendClassTree extendTree;

	@Override
	public List<Tree.Kind> nodesToVisit() {
		// Register to the kind of nodes you want to be called upon visit.
		return Collections.singletonList(Tree.Kind.CLASS);
	}

	@Override
	public void visitNode(Tree tree) {

		// to count the number of classTrees
		if (classCount == 0) {
			classCount = GetClass.setClassCount(context.getTree().types());
			//System.out.println("classCount: " + classCount);
		}

		/*
		 * use ExtendClassTree to save method & members and save className in classList
		 * if classtree has superClass ,save two name of the class in extendList
		 */
		ClassTree ct = (ClassTree) tree;
		if (ct.superClass() != null) {
			extendTree = new ExtendClassTree(ct, ct.superClass().symbolType().name(), checkAMW(ct));
			extendList.add(extendTree.getName());
			extendList.add(extendTree.getExtendClassName());
		} else {
			extendTree = new ExtendClassTree(ct, checkAMW(ct));
		}
		classList.put(ct.simpleName().name(), extendTree);
		//System.out.println(extendTree.getName() + " " + extendTree.getExtendClassName() + " " + extendTree.getAMV());

		// save the method & members of classtree into extendTree
		for (Tree t : ct.members()) {
			if (t.is(Tree.Kind.VARIABLE)) {
				VariableTree vt = (VariableTree) t;
				ModifiersTree modifiersOfVT = vt.modifiers();
				List<ModifierKeywordTree> modifiers = modifiersOfVT.modifiers();
				for (ModifierKeywordTree mtkt : modifiers) {
					if (mtkt.modifier().equals(Modifier.PROTECTED)) {
						// System.out.println("get variable");
						extendTree.addMember(vt);
					}
				}
			} else if (t.is(Tree.Kind.METHOD)) {
				MethodTree mt = (MethodTree) t;
				extendTree.addMethod(mt);
			}
		}

		//System.out.println(extendTree.getMembers().size() + " " + extendTree.getMethod().size());

		if (--classCount == 0) {
			// System.out.println(classList.size() + " " + extendList.size());
			if (extendList.size() != 0) {
				for (int i = 0; i < extendList.size(); i += 2) {
					// System.out.println(extendList.get(i) + " " + extendList.get(i + 1));
					if (classList.get(extendList.get(i)) != null && classList.get(extendList.get(i + 1)) != null) {
						if (classList.get(extendList.get(i)).getAMV()
								&& classList.get(extendList.get(i + 1)).getAMV()) {
							if (extendUse(classList.get(extendList.get(i)), classList.get(extendList.get(i + 1)))) {
								addIssue(classList.get(extendList.get(i)).getLine(), "this class refuse bequest");
							}
							//System.out.println("extendUse close");
						}
					}
				}
			}
		}
	}

	public boolean extendUse(ExtendClassTree classT, ExtendClassTree extendT) {
		//System.out.println("extendUse start");
		int BOvrThreshold = 0;
		int BURThreshold = 0;

		// check MethodUse
		for (String str1 : classT.getMethod()) {
			for (String str2 : extendT.getMethod()) {
				if (str1.equals(str2)) {
					BOvrThreshold++;
					break;
				}
			}
		}

		// check MemberUse
		for (VariableTree vt : extendT.getMembers()) {
			if (vt.symbol().usages() != null) {
				List<IdentifierTree> lit = vt.symbol().usages();
				for (IdentifierTree target : lit) {
					Tree target2 = target.parent();
					while (true) {
						// System.out.println(target2);
						if (target2.is(Tree.Kind.CLASS)) {
							//System.out.println("the vt uses on " + ((ClassTree) target2).simpleName().name());
							if (((ClassTree) target2).simpleName().name().equals(classT.getName())) {
								BURThreshold++;
							}
							break;
						}
						target2 = target2.parent();
					}
				}
			}
		}

//		System.out.println("methoduse: " + BOvrThreshold);
//		System.out.println(classT.getMethod().size());
//		System.out.println("Memberuse: " + BURThreshold);
//		System.out.println(classT.getMembers().size());
		if (classT.getMethod().size() > 7 && extendT.getMethod().size() > 7) {
			if (BOvrThreshold * 3 < extendT.getMethod().size() && BURThreshold * 3 < extendT.getMembers().size()) {
				return true;
			}
		}

		return false;
	}

	public boolean checkAMW(ClassTree ct) {
		int sum = 0;
		int methodNum = 0;
		for (Tree t : ct.members()) {
			if (t.is(Tree.Kind.METHOD)) {
				methodNum++;
				MethodTree mt = (MethodTree) t;
				BlockTree bt = mt.block();
				List<StatementTree> lst = bt.body();
				for (StatementTree st : lst) {
					sum = cycleCheck(sum, st);
				}
			}
		}
		System.out.println(sum + " " + methodNum);
		if (2 * methodNum <= sum) {
			return true;
		}
		return false;
	}

	public int cycleCheck(int num, StatementTree statementTree) {
		switch (statementTree.kind()) {

		case SWITCH_STATEMENT:
			SwitchStatementTree switchStatementTree = (SwitchStatementTree) statementTree;
			List<CaseGroupTree> switchList = switchStatementTree.cases();
			num += switchList.size();
			for (CaseGroupTree caseT : switchList) {
				List<StatementTree> switchSList = caseT.body();
				for (StatementTree st : switchSList) {
					num = cycleCheck(num, st);
				}
			}
			break;

		case IF_STATEMENT:
			StatementTree statementTree2 = ((IfStatementTree) statementTree).thenStatement();
			num += expressionTreeCheck(((IfStatementTree) statementTree).condition(), 1);
			num = cycleCheck(num, statementTree2);
			break;

		case WHILE_STATEMENT:
			StatementTree statementTree6 = ((WhileStatementTree) statementTree).statement();
			num += expressionTreeCheck(((WhileStatementTree) statementTree).condition(), 1);
			num = cycleCheck(num, statementTree6);
			break;

		case FOR_STATEMENT:
			num += 10;
			StatementTree statementTree7 = ((ForStatementTree) statementTree).statement();
			num = cycleCheck(num, statementTree7);
			break;

		case DO_STATEMENT:
			StatementTree statementTree8 = ((DoWhileStatementTree) statementTree).statement();
			num += expressionTreeCheck(((DoWhileStatementTree) statementTree).condition(), 1);
			num = cycleCheck(num, statementTree8);
			break;

		case FOR_EACH_STATEMENT:
			num++;
			StatementTree statementTree9 = ((ForEachStatement) statementTree).statement();
			num = cycleCheck(num, statementTree9);
			break;

		case TRY_STATEMENT:
			num++;
			BlockTree blockTree2 = ((TryStatementTree) statementTree).block();
			List<StatementTree> list = blockTree2.body();
			for (StatementTree st : list) {
				num = cycleCheck(num, st);
			}
			break;

		default:
			break;
		}

		return num;
	}

	public int expressionTreeCheck(ExpressionTree expt, int conditionNum) {
		if (expt.is(Tree.Kind.STRING_LITERAL) || expt.is(Tree.Kind.NULL_LITERAL) || expt.is(Tree.Kind.INT_LITERAL)
				|| expt.is(Tree.Kind.BOOLEAN_LITERAL)) {
			conditionNum++;
		} else if (expt.is(Tree.Kind.CONDITIONAL_AND) || expt.is(Tree.Kind.CONDITIONAL_OR)) {
			conditionNum++;
			conditionNum = expressionTreeCheck(((BinaryExpressionTree) expt).leftOperand(), conditionNum);
			conditionNum = expressionTreeCheck(((BinaryExpressionTree) expt).rightOperand(), conditionNum);
		}
		return conditionNum;
	}

}

//the tree to save the data in classtree
class ExtendClassTree {
	private String className;
	private String extendClassName;
	private boolean classAMV;
	private int startingLine;
	private ArrayList<VariableTree> classMembers = new ArrayList<VariableTree>();
	private ArrayList<String> classMethod = new ArrayList<String>();

	ExtendClassTree(ClassTree tree, String extendName, boolean AMV) {
		className = tree.simpleName().name();
		startingLine = tree.openBraceToken().line();
		extendClassName = extendName;
		classAMV = AMV;
	}

	ExtendClassTree(ClassTree tree, boolean AMV) {
		className = tree.simpleName().name();
		startingLine = tree.openBraceToken().line();
		classAMV = AMV;
	}

	public int getLine() {
		return startingLine;
	}

	public String getName() {
		return className;
	}

	public String getExtendClassName() {
		return extendClassName;
	}

	public boolean getAMV() {
		return classAMV;
	}

	public void addMember(VariableTree vt) {
		classMembers.add(vt);
	}

	public ArrayList<VariableTree> getMembers() {
		return classMembers;
	}

	public void addMethod(MethodTree mt) {
		classMethod.add(mt.simpleName().name());
	}

	public ArrayList<String> getMethod() {
		return classMethod;
	}

}