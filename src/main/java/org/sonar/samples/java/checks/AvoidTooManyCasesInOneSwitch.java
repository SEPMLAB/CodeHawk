package org.sonar.samples.java.checks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.tree.BlockTree;
import org.sonar.plugins.java.api.tree.CaseGroupTree;
import org.sonar.plugins.java.api.tree.CatchTree;
import org.sonar.plugins.java.api.tree.ClassTree;
import org.sonar.plugins.java.api.tree.DoWhileStatementTree;
import org.sonar.plugins.java.api.tree.ForEachStatement;
import org.sonar.plugins.java.api.tree.ForStatementTree;
import org.sonar.plugins.java.api.tree.IfStatementTree;
import org.sonar.plugins.java.api.tree.LabeledStatementTree;
import org.sonar.plugins.java.api.tree.MethodTree;
import org.sonar.plugins.java.api.tree.StatementTree;
import org.sonar.plugins.java.api.tree.SwitchStatementTree;
import org.sonar.plugins.java.api.tree.SynchronizedStatementTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.plugins.java.api.tree.TryStatementTree;
import org.sonar.plugins.java.api.tree.WhileStatementTree;

@Rule(key = "AvoidTooManyCasesInOneSwitch")
public class AvoidTooManyCasesInOneSwitch extends IssuableSubscriptionVisitor {

	@Override
	public List<Tree.Kind> nodesToVisit() {
		return Collections.singletonList(Tree.Kind.METHOD);
	}

	@Override
	public void visitNode(Tree tree) {
		MethodTree methodTree = (MethodTree) tree;
		BlockTree blockTree = methodTree.block();
		List<StatementTree> list = blockTree.body();
		checkStatementTree(list);
	}

	private void checkStatementTree(List<StatementTree> list) {
		for (StatementTree statementTree : list) {
			switch (statementTree.kind()) {

			case SWITCH_STATEMENT:
				SwitchStatementTree switchStatementTree = (SwitchStatementTree) statementTree;
				int count = countSwitch(switchStatementTree);
				if (count >= 10)
					reportIssue(switchStatementTree, "Too many cases in this switch statement !");
				break;

			case IF_STATEMENT:
				StatementTree statementTree2 = ((IfStatementTree) statementTree).thenStatement();
				checkInnerStatementTree(statementTree2);

				IfStatementTree ifStatementTree = ((IfStatementTree) statementTree);
				checkElseIfStatementTree(ifStatementTree);
				break;

			case WHILE_STATEMENT:
				StatementTree statementTree6 = ((WhileStatementTree) statementTree).statement();
				checkInnerStatementTree(statementTree6);
				break;

			case FOR_STATEMENT:
				StatementTree statementTree7 = ((ForStatementTree) statementTree).statement();
				checkInnerStatementTree(statementTree7);
				break;

			case DO_STATEMENT:
				StatementTree statementTree8 = ((DoWhileStatementTree) statementTree).statement();
				checkInnerStatementTree(statementTree8);
				break;

			case FOR_EACH_STATEMENT:
				StatementTree statementTree9 = ((ForEachStatement) statementTree).statement();
				checkInnerStatementTree(statementTree9);
				break;

			case LABELED_STATEMENT:
				LabeledStatementTree labeledStatementTree = (LabeledStatementTree) statementTree;
				StatementTree statementTree10 = labeledStatementTree.statement();
				List<StatementTree> list2 = new ArrayList<>();
				list2.add(statementTree10);
				checkStatementTree(list2);
				break;

			case SYNCHRONIZED_STATEMENT:
				BlockTree blockTree = ((SynchronizedStatementTree) statementTree).block();
				checkInnerBlockTree(blockTree);
				break;

			case TRY_STATEMENT:
				BlockTree blockTree2 = ((TryStatementTree) statementTree).block();
				checkInnerBlockTree(blockTree2);
				List<CatchTree> list3 = ((TryStatementTree) statementTree).catches();
				for (CatchTree catchtree : list3) {
					BlockTree blockTree3 = catchtree.block();
					checkInnerBlockTree(blockTree3);
				}
				BlockTree blockTree4 = ((TryStatementTree) statementTree).finallyBlock();
				checkInnerBlockTree(blockTree4);
				break;

			case CLASS:
				List<Tree> list5 = ((ClassTree) statementTree).members();
				for (Tree tree : list5) {
					if (tree.is(Tree.Kind.CONSTRUCTOR)) {
						BlockTree blockTree5 = ((MethodTree) tree).block();
						checkInnerBlockTree(blockTree5);
					}
				}
				break;

			default:
				break;
			}
		}
	}

	private void checkElseIfStatementTree(IfStatementTree tree) {
		if (tree.elseKeyword() != null) {
			StatementTree statementTree =  tree.elseStatement();
			if (statementTree.is(Tree.Kind.IF_STATEMENT)) {
				IfStatementTree ifStatementTree = (IfStatementTree) statementTree;
				checkElseIfStatementTree(ifStatementTree);
				StatementTree statementTree2 =  ifStatementTree.thenStatement();
				checkInnerStatementTree(statementTree2);
			}else {
				checkInnerStatementTree(statementTree);
			}
		}
	}

	private void checkInnerStatementTree(StatementTree tree) {
		BlockTree blockTree = (BlockTree) tree;
		List<StatementTree> list = blockTree.body();
		checkStatementTree(list);
	}

	private void checkInnerBlockTree(BlockTree tree) {
		List<StatementTree> list = tree.body();
		checkStatementTree(list);
	}

	private int countSwitch(SwitchStatementTree temp) {
		List<CaseGroupTree> tempList = temp.cases();
		return tempList.size();
	}

}