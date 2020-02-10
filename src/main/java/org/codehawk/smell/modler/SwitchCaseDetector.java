package org.codehawk.smell.modler;

import java.util.ArrayList;
import java.util.List;

import org.sonar.plugins.java.api.tree.BaseTreeVisitor;
import org.sonar.plugins.java.api.tree.CaseGroupTree;
import org.sonar.plugins.java.api.tree.SwitchStatementTree;
import org.sonar.plugins.java.api.tree.Tree;

public class SwitchCaseDetector extends BaseTreeVisitor implements Detector{
	private final int maxCases = 10;
	List<SwitchNode> smellNodes = new ArrayList<>();
	@Override
	public boolean detect(Node node) {
		if(smellNodes.contains(node))
			return true;
		
		return false;
	}
	
	public void scanTrees(List<? extends Tree> trees) {
		super.scan(trees);
	}
	
	@Override
	public void visitSwitchStatement(SwitchStatementTree tree) {
		if(countSwitch(tree) >= maxCases)
			smellNodes.add(new SwitchNode(tree.openBraceToken().line()));
		
		super.visitSwitchStatement(tree);
	}
	
	private int countSwitch(SwitchStatementTree switchStatementTree) {//count how many cases in this switch statement method 
		List<CaseGroupTree> caseGroupTreeList = switchStatementTree.cases();
		return caseGroupTreeList.size();
	}
	
	public List<SwitchNode> getSmellNodes() {
		return smellNodes;
	}	
}
