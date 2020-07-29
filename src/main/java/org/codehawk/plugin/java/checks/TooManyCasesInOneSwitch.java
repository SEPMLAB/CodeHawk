package org.codehawk.plugin.java.checks;

import java.util.Collections;
import java.util.List;

import org.codehawk.smell.modler.SwitchCaseDetector;
import org.codehawk.smell.modler.SwitchNode;
import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.tree.ClassTree;
import org.sonar.plugins.java.api.tree.Tree;

@Rule(key = "TooManyCasesInOneSwitch")
public class TooManyCasesInOneSwitch extends IssuableSubscriptionVisitor {

	@Override
	public List<Tree.Kind> nodesToVisit() {
		return Collections.singletonList(Tree.Kind.CLASS);//check every class
	}

	@Override
	public void visitNode(Tree tree) {
		SwitchCaseDetector detector = new SwitchCaseDetector();
		ClassTree classTree = (ClassTree) tree;
		List<Tree> treeList = classTree.members();
		
		//scan switch cases and register ones with over 10 cases
		detector.scanTrees(treeList);
		
		//report every switch statement with smell
		for(SwitchNode node: detector.getSmellNodes()) {
			addIssue(node.getStartLine(), "Too many cases in this switch statement!(over 10)");
		}
	}
}