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
		return Collections.singletonList(Tree.Kind.CLASS);//check every class
	}

	@Override
	public void visitNode(Tree tree) {
		ClassTree classTree = (ClassTree) tree;
		List<Tree> treeList = classTree.members();
		for (Tree tempTree : treeList) {
			if (tempTree.is(Tree.Kind.METHOD) || tempTree.is(Tree.Kind.CONSTRUCTOR)) {//check every method(constructor) in this class
				MethodTree methodTree = (MethodTree) tempTree;
				BlockTree blockTree = methodTree.block();
				List<StatementTree> list = blockTree.body();
				if(!list.isEmpty()){
					checkStatementTree(list);
				}
			}
		}
	}

	private void checkStatementTree(List<StatementTree> list) {
		for (StatementTree statementTree : list) {
			switch (statementTree.kind()) {

			case SWITCH_STATEMENT://get switch statement in this method
				SwitchStatementTree switchStatementTree = (SwitchStatementTree) statementTree;
				if (countSwitch(switchStatementTree) >= 10){//check this switch statement, if >= 10 cases(default)
					reportIssue(switchStatementTree, "Too many cases in this switch statement !");//show this smell
				}
				break;

			case IF_STATEMENT://get if statement in this method
				IfStatementTree ifStatementTree = (IfStatementTree) statementTree;
				if (ifStatementTree.thenStatement().is(Tree.Kind.BLOCK)) {//if this if statement has curly brackets 
					checkInnerStatementTree(ifStatementTree.thenStatement());
				}
				else{//if this if statement doesn't have curly brackets 
					List<StatementTree> thenStatementTreeList = new ArrayList<>();
					thenStatementTreeList.add(ifStatementTree.thenStatement());
					checkStatementTree(thenStatementTreeList);
				}
				checkElseIfStatementTree(ifStatementTree);//check elseif part in this if statement
				break;

			case WHILE_STATEMENT://get while statement in this method
				WhileStatementTree whileStatementTree = (WhileStatementTree) statementTree;
				checkInnerStatementTree(whileStatementTree.statement());
				break;

			case FOR_STATEMENT://get for statement in this method
				ForStatementTree forStatementTree = (ForStatementTree) statementTree;
				checkInnerStatementTree(forStatementTree.statement());
				break;

			case DO_STATEMENT://get do_while statement in this method
				DoWhileStatementTree doWhileStatementTree = (DoWhileStatementTree) statementTree;
				checkInnerStatementTree(doWhileStatementTree.statement());
				break;

			case FOR_EACH_STATEMENT://get for_each statement in this method
				ForEachStatement forEachStatement = (ForEachStatement) statementTree;
				checkInnerStatementTree(forEachStatement.statement());
				break;

			case LABELED_STATEMENT://get labeled statement in this method
				LabeledStatementTree labeledStatementTree = (LabeledStatementTree) statementTree;
				List<StatementTree> labeledStatementTreeList = new ArrayList<>();
				labeledStatementTreeList.add(labeledStatementTree.statement());
				checkStatementTree(labeledStatementTreeList);
				break;

			case SYNCHRONIZED_STATEMENT://get synchronized statement in this method
				SynchronizedStatementTree synchronizedStatementTree = (SynchronizedStatementTree) statementTree;
				checkInnerBlockTree(synchronizedStatementTree.block());
				break;

			case TRY_STATEMENT://get try_catch statement in this method
				TryStatementTree tryStatementTree = (TryStatementTree) statementTree;//check try part in this try statement
				if(tryStatementTree.tryKeyword() != null){
					checkInnerBlockTree(tryStatementTree.block());
				}

				List<CatchTree> catchTreeList = tryStatementTree.catches();//check catch part in this try statement
				for (CatchTree catchtree : catchTreeList) {
					checkInnerBlockTree(catchtree.block());
				}

				if(tryStatementTree.finallyKeyword() != null){
					checkInnerBlockTree(tryStatementTree.finallyBlock());//check finally part in this try statement
				}
				break;
				
			default:
				break;
			}
		}
	}

	private void checkElseIfStatementTree(IfStatementTree tree) {//check inner elseif statement method
		if (tree.elseKeyword() != null) {
			StatementTree statementTree = tree.elseStatement();
			if (statementTree.is(Tree.Kind.IF_STATEMENT)) {
				IfStatementTree ifStatementTree = (IfStatementTree) statementTree;
				checkElseIfStatementTree(ifStatementTree);
				if (ifStatementTree.thenStatement().is(Tree.Kind.BLOCK)) {//if this elseif statement has curly brackets 
					checkInnerStatementTree(ifStatementTree.thenStatement());
				}else{//if this elseif statement doesn't have curly brackets 
					List<StatementTree> thenStatementTreeList = new ArrayList<>();
					thenStatementTreeList.add(ifStatementTree.thenStatement());
					checkStatementTree(thenStatementTreeList);	
				}				
			} else {
				checkInnerStatementTree(statementTree);
			}
		}
	}

	private void checkInnerStatementTree(StatementTree tree) {//check inner statementtree method
		BlockTree blockTree = (BlockTree) tree;
		List<StatementTree> list = blockTree.body();
		checkStatementTree(list);
	}

	private void checkInnerBlockTree(BlockTree tree) {//check inner blocktree method
		List<StatementTree> list = tree.body();
		checkStatementTree(list);
	}

	private int countSwitch(SwitchStatementTree temp) {//count how many cases in this switch statement method 
		List<CaseGroupTree> tempList = temp.cases();
		return tempList.size();
	}

}
