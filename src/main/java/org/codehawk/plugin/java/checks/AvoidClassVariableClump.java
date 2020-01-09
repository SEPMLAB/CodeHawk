package org.codehawk.plugin.java.checks;

import java.util.Collections;
import java.util.List;

import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.tree.ClassTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.plugins.java.api.tree.Tree.Kind;

public class AvoidClassVariableClump extends IssuableSubscriptionVisitor{
	private final int clumpThresh = 10;
	
	
	@Override
	public List<Kind> nodesToVisit() {
		List<Tree.Kind> visitList = Collections.singletonList(Tree.Kind.CLASS);
		
		return visitList;
	}
	
	@Override
	public void visitNode(Tree tree) {
		ClassTree classTree = (ClassTree) tree;
		
		for(Tree t: classTree.members()) {
			if(t.is(Tree.Kind.VARIABLE)) {
				
			}
		}
		
	}

}
